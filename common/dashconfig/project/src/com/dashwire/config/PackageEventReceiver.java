package com.dashwire.config;

import com.dashwire.base.debug.DashLogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class PackageEventReceiver extends BroadcastReceiver {

	private static final String TAG = PackageEventReceiver.class.getCanonicalName();
	
    @Override
    public void onReceive( Context context, Intent intent ) {
    	if(context == null) {
    		return;
    	}
        String packageString = intent.getDataString();
        int index = packageString.lastIndexOf( ":" );
        String name = packageString.substring( index + 1 );
        String title = getPackageTitle( context, name );
        
        Intent newIntent = new Intent( context, DownloadService.class );
        newIntent.setAction( "installed" );
        Bundle extras = new Bundle();
        extras.putString( "title", title );
        extras.putString( "packageName", name );
        newIntent.putExtras( extras );
        DashLogger.d(TAG, "Service: startService called for com.dashwire.config.DownloadService");
        context.startService( newIntent );
    }

    public static String getPackageTitle( Context context, String name ) {
        String title = "untitled";
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo( name, PackageManager.GET_ACTIVITIES | PackageManager.GET_INTENT_FILTERS );
            Context packageContext = context.createPackageContext( name, 0 );

            if ( info.applicationInfo.nonLocalizedLabel != null ) {
                title = ( String ) info.applicationInfo.nonLocalizedLabel;
            } else {
                int resId = info.applicationInfo.labelRes;
                if ( resId != 0 ) {
                    title = packageContext.getString( resId );
                }
            }
        } catch ( NameNotFoundException e ) {
        }
        return title;
    }
}
