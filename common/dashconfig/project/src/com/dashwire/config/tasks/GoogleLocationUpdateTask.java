package com.dashwire.config.tasks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;

import com.dashwire.config.DashconfigApplication;
import com.dashwire.base.debug.DashLogger;


public class GoogleLocationUpdateTask extends AsyncTask<Boolean, Void, Boolean> {
    private static final String TAG = GoogleLocationUpdateTask.class.getCanonicalName();
    private Context context;
    private GoogleLocationUpdateHandler handler;

    public GoogleLocationUpdateTask( Context context, GoogleLocationUpdateHandler handler ) {
        DashLogger.v( TAG, "GoogleLocationUpdateTask ---------------------------" );
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected Boolean doInBackground( Boolean... params ) {
        try {
            boolean googleLocationUsageFlag = params[ 0 ];
            updateGoogleSettings( googleLocationUsageFlag );
            return true;
        } catch ( Exception e ) {
            DashLogger.v( TAG, "GoogleLocationUsageUpdater - Exception in doInBackground() : " +e.getMessage() );
            return false;
        }
    }
    
    @Override
    protected void onPostExecute( Boolean result ) {
        handler.processGoogleLocationUpdateStatus( result );
        super.onPostExecute( result );
    }
    
    private void updateGoogleSettings( boolean googleLocationUsageFlag ) {
        try {
            Uri uri = Uri.parse( DashconfigApplication.getDeviceContext().getStringConst(context, "GOOGLE_SETTINGS_PARTNER_URI" ) );
            ContentResolver resolver = context.getContentResolver();

            ContentValues useLocationValues = new ContentValues();
            useLocationValues.put( "name", "use_location_for_services" );
            if ( googleLocationUsageFlag ) {
                useLocationValues.put( "value", "1" );
            } else {
                useLocationValues.put( "value", "0" );
            }

            ContentValues networkLoacationOptInValues = new ContentValues();
            networkLoacationOptInValues.put( "name", "network_location_opt_in" );
            if ( googleLocationUsageFlag ) {
                networkLoacationOptInValues.put( "value", "1" );
            } else {
                networkLoacationOptInValues.put( "value", "0" );
            }

            resolver.insert( uri, useLocationValues );
            resolver.insert( uri, networkLoacationOptInValues );

            if ( googleLocationUsageFlag ) {
                Settings.Secure.setLocationProviderEnabled( context.getContentResolver(), LocationManager.NETWORK_PROVIDER, true );
            } else {
                Settings.Secure.setLocationProviderEnabled( context.getContentResolver(), LocationManager.NETWORK_PROVIDER, false );
            }
            
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
