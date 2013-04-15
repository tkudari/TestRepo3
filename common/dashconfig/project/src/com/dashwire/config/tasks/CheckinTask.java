package com.dashwire.config.tasks;

import com.dashwire.base.debug.DashLogger;
import com.dashwire.base.device.DashSettings;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.dashwire.config.RestClient;
import com.dashwire.config.tracking.Tracker;
import com.dashwire.base.debug.OverridePreferences;

public class CheckinTask extends AsyncTask<Void, Void, JSONObject> {
    private static final String TAG = CheckinTask.class.getCanonicalName();
    
    private CheckinHandler handler;
    private Context context;
    private String deviceId;
    private String whenStarted;

    public CheckinTask( Context context, CheckinHandler handler, String sWhenStarted ) {
        DashLogger.v( TAG, "CheckinTask ---------------------------------------------------------------" );
        this.handler = handler;
        this.context = context;
        this.whenStarted = sWhenStarted;
    }

    @Override
    protected void onPreExecute() {
        DashLogger.v( TAG, "CheckinTask - onPreExecute ---------------------------------------------------------------" );
        super.onPreExecute();
    }


    @Override
    protected JSONObject doInBackground( Void... params ) {
        CharSequence currentTimeString = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date());
        DashLogger.v( TAG, "CheckinTask - When Started = " + whenStarted + " doInBackground ----------------------------------------------------" );
        int attemptsRemaining = 1;
        JSONObject response = null;
        while ( response == null && attemptsRemaining > 0 ) {
            DashLogger.v(TAG, "CheckinTask - When Started = " + whenStarted + " time : " + currentTimeString + " requesting checkin ------------------------------------------------");
            response = RestClient.get( RestClient.getDefaultHost( context ) + "/checkin", context );

            if ( response != null ) {
                CharSequence responseTimeString = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date());
                DashLogger.v( TAG, "CheckinTask - When Started = " + whenStarted + " time : " + responseTimeString + " checkin response: " + response.toString() );
            } 
            attemptsRemaining--;
        }
        return response;
    }

    @Override
    protected void onPostExecute( JSONObject result ) {
        boolean checkinStatus = processCheckin( result );
        DashLogger.v(TAG,"onPostExecute - When Started = " + whenStarted + "checkinStatus = " + checkinStatus);
        handler.processCheckin( checkinStatus );
        super.onPostExecute( result );
    }

    public boolean processCheckin( JSONObject result ) {
        boolean checkinStatus = false;
        if ( result != null ) {
            try {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();

                //this is overridden by the next if
                if ( result.has( "tracking_uri" ) ) {
                    editor.putString( "tracking_uri", result.getString( "tracking_uri" ) );
                    DashLogger.v( TAG, "tracking_uri: " + result.getString( "tracking_uri" ) );
                    Uri host_uri = Uri.parse( result.getString( "tracking_uri" ));
                    RestClient.overrideUri( host_uri);
                }
                //this will override the previous if                
                if ( result.has( "host_uri" ) ) {
                    DashLogger.v( TAG, "host_uri: " + result.getString( "host_uri" ) );
                    Uri host_uri = Uri.parse( result.getString( "host_uri" ));
                    RestClient.overrideUri( host_uri );
                }
                
                if ( result.has( DashSettings.PROPERTY_STRING_TRACKING_ID ) ) {
                    editor.putString( DashSettings.PROPERTY_STRING_TRACKING_ID, result.getString( DashSettings.PROPERTY_STRING_TRACKING_ID ) );
                    DashLogger.v( TAG, "tracking_id: " + result.getString( DashSettings.PROPERTY_STRING_TRACKING_ID ) );
                }
                if ( result.has( "notification_uri" ) ) {
                    editor.putString( "notification_uri", result.getString( "notification_uri" ) );
                    DashLogger.v( TAG, "notification_uri: " + result.getString( "notification_uri" ) );
                }
                if ( result.has( "config_id" ) ) {
                    String configId = result.getString( "config_id" );
                    editor.putString( "config_id", configId );
                }
                if ( result.has( "ttl" ) ) {
                    int ttl = result.getInt( "ttl" );
                    ttl = getTTL( ttl );
                    editor.putInt( "ttl", ttl );
                    editor.putLong( "expiration", ttl * 1000 + System.currentTimeMillis() );
                }
                if ( result.has( "code" ) ) {
                    editor.putString( "code", result.getString( "code" ) );
                    checkinStatus = true;
                }
                if ( result.has( "key" ) ) {
                    editor.putString( "key", result.getString( "key" ) );
                }
                if ( result.has( "pairing_uri" ) ) {
                    editor.putString( "pairing_uri", result.getString( "pairing_uri" ) );
                    
                }
                if ( result.has( "package_waiting" ) ) {
                    editor.putBoolean( "package_waiting", result.getBoolean( "package_waiting" ) );
                }
                
                editor.putString( DashSettings.PROPERTY_STRING_DEVICE_ID, deviceId );

                editor.commit();
                Tracker.resetTrackingId();
            } catch ( JSONException je ) {
                DashLogger.v( TAG, "CheckinTask - When Started = " + whenStarted + " malformed json" );
            }
        } else {
            DashLogger.v( TAG, "CheckinTask - When Started = " + whenStarted + " checkin failed ----------------------------------------------" );
        }
        return checkinStatus;
    }

    private int getTTL( long ttl ) {
        ttl *= 1000;
        if ( OverridePreferences.getBoolean( context, "overrideCodeExpiration", false ) ) {
            ttl = OverridePreferences.getStringAsLong( context, "overrideCodeExpirationValue", ttl );
        }
        return ( int ) ( ttl / 1000 );
    }

}
