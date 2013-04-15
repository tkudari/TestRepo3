package com.dashwire.config.launcher;

import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.base.debug.DashLogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SceneResultsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent ) {    	   
        boolean isOk = intent.getExtras().getBoolean("KEY_PARSING_RESULT"); 
        if(!isOk) {
            DashLogger.i("Shortcuts", "Failed to install shortcuts/widgets");
        } else {
            DashLogger.i("Shortcuts", "OK shortcuts/widgets");
        }
         
		Intent i = new Intent(ConfigurationEvent.ACTION_CONFIGURATION);
		i.setData(Uri.parse("dashconfig://?name=shortcuts&code=" + 
				(isOk ?  ConfigurationEvent.CHECKED : ConfigurationEvent.FAILED)));
		context.sendBroadcast(i);
    }

}
