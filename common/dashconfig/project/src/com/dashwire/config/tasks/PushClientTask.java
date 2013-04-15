package com.dashwire.config.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.RestClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

public class PushClientTask extends AsyncTask<String, Void, JSONObject> {
    private static final String TAG = PushClientTask.class.getCanonicalName();
    private PushClientHandler handler;
    private Context context;
    private DefaultHttpClient httpClient;

    public PushClientTask( Context context, PushClientHandler handler ) {
        this.context = context;
        this.handler = handler;
        setupClient();
    }

    private void setupClient() {
        HttpParams basicParams = new BasicHttpParams();
        basicParams.setParameter( CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1 );
        this.httpClient = new DefaultHttpClient( basicParams );
    }

    private String loadNotificationUri() {
        // TODO verify code expiration
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getString( "notification_uri", null );
    }

    @Override
    protected void onCancelled() {
        DashLogger.v( TAG, "cancelled " + toString() + " ---------------------------------------------------" );
        new HttpShutdownTask().execute( this.httpClient );
        super.onCancelled();
    }

    @Override
    protected JSONObject doInBackground( String... params ) {
        JSONObject response = null;
        String check = params[ 0 ];
        String uri = loadNotificationUri();
        if ( uri != null ) {
            int statusCode = -1;
            while ( !isCancelled() && statusCode != 200 /* TODO and not timeout */ ) {
                response = executeRequest( uri + "&on=" + check );
                statusCode = RestClient.getStatusCode( response );
            }
        }
        return response;
    }

    private JSONObject executeRequest( String uri ) {
        JSONObject json = null;
        HttpResponse response = null;
        try {
            DashLogger.d(TAG,"uri = " + uri);
            HttpGet request = new HttpGet( uri );
            DashLogger.d(TAG,"Downloading: " + uri);
            response = httpClient.execute( request );
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            String result = RestClient.convertStreamToString( instream );
            DashLogger.v(TAG, "result = " + result);
            json = new JSONObject( result );
        } catch ( HttpHostConnectException e ) {
            backoff();
        } catch ( SocketException e ) {
            backoff();
        } catch ( UnsupportedEncodingException e ) {
            // do nothing...
        } catch ( ClientProtocolException e ) {
            // do nothing...
        } catch ( IOException e ) {
            // do nothing...
        } catch ( JSONException e ) {
            DashLogger.e( TAG, e.toString() );
        }

        if ( json == null ) {
            json = new JSONObject();
        }
        if ( response != null ) {
            try {
                json.put( RestClient.JSON_HTTP_STATUS_CODE, response.getStatusLine().getStatusCode() );
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
        }
        return json;
    }

    private void backoff() {
        try {
            Thread.sleep( 5000 );
        } catch ( InterruptedException e ) {
        }
    }

    @Override
    protected void onPostExecute( JSONObject result ) {
        handler.handlePushMessage( result );
        super.onPostExecute( result );
    }
    
    public class HttpShutdownTask extends AsyncTask<HttpClient, Void, Void> {
        @Override
        protected Void doInBackground( HttpClient... params ) {
            HttpClient httpclient = params[ 0 ];
            if (httpclient != null)
            {
                httpclient.getConnectionManager().shutdown();
            }
            return null;
        }
    }
}
