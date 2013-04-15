package com.dashwire.config.tasks;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.dashwire.config.RestClient;
import com.dashwire.base.debug.DashLogger;

public class StatusTask extends AsyncTask<String, Void, JSONObject> {

    private static final String TAG = StatusTask.class.getCanonicalName();
    private Context context;
    private StatusHandler handler;

    public StatusTask( Context context, StatusHandler handler ) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected JSONObject doInBackground( String... params ) {
        JSONObject response = null;
        String uri = loadStatusUri();
        response = RestClient.get( uri, context );
        if ( response != null ) {
            DashLogger.v( TAG, response.toString() + " " + toString() );
        }
        return response;
    }

    @Override
    protected void onPostExecute( JSONObject result ) {
        handler.handleStatus( result );
        super.onPostExecute( result );
    }

    private String loadStatusUri() {
        // TODO verify code expiration
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        String uri = settings.getString( "notification_uri", null );
        // TODO make this hack less ugly, maybe generate from host uri and notificiation id
        return uri.replace( "http:", "https:" ).replace( "/wait", "/status" ).replace( ":8000", "" );
    }

}
