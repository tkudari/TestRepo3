package com.dashwire.config.configuration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class WidgetsShortcutsBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive( final Context context, Intent intent ) {
        int code = (intent.getExtras().getBoolean("success")) ?
        		ConfigurationEvent.CHECKED : ConfigurationEvent.FAILED;

        Intent i = new Intent( ConfigurationEvent.ACTION_CONFIGURATION );
        i.setData( Uri.parse( "dashconfig://?name=shortcuts&code=" + code ) );
        context.sendBroadcast( i, "com.dashwire.config.PERM_CONFIG" );
        
    }

}
