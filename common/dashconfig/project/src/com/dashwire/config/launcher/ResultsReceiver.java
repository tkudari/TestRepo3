package com.dashwire.config.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.configuration.ConfigurationEvent;

public class ResultsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent ) {    	   
        boolean isOk = intent.getExtras().getBoolean("success"); 
        if(!isOk) {
            DashLogger.i("Shortcuts", "Failed to install shortcuts/widgets");
        }
         
		Intent i = new Intent(ConfigurationEvent.ACTION_CONFIGURATION);
		i.setData(Uri.parse("dashconfig://?name=shortcuts&code=" + 
				(isOk ?  ConfigurationEvent.CHECKED : ConfigurationEvent.FAILED)));
		context.sendBroadcast(i);
    }

}
