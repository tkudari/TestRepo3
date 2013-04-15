package com.dashwire.config.tasks;

import android.os.AsyncTask;
import com.dashwire.base.debug.DashLogger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;

public class TrackerTask extends AsyncTask<String, Void, Void> {
    private static final String TAG = TrackerTask.class.getCanonicalName();
    private HttpClient httpClient;

    public TrackerTask( HttpClient httpClient ) {
        this.httpClient = httpClient;
    }

    @Override
    protected Void doInBackground( String... params ) {
        String uri = params[ 0 ];
        HttpGet request = new HttpGet( uri );
        try {
        	DashLogger.d(TAG,"Uri: " + uri);      
        	HttpResponse response = httpClient.execute( request );
            HttpEntity entity = response.getEntity();
            entity.consumeContent();
        } catch ( ConnectTimeoutException e ) {
            // do nothing...
        } catch ( IllegalStateException e ) {
            // do nothing...
        } catch ( ClientProtocolException e ) {
            DashLogger.e( TAG, e.toString() );
        } catch ( IOException e ) {
            DashLogger.e( TAG, e.toString() );
        }
        return null;
    }
}
