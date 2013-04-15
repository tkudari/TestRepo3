package com.dashwire.configurator3000;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive( Context context, Intent intent ) {
    	Intent settings = new Intent("com.dashwire.configurator.SETTINGS");
    	settings.setClass(context, SettingsActivity.class);
    	settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
    	context.startActivity(settings);
    }
}
