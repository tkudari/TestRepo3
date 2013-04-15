package com.dashwire.config.configuration;

import com.dashwire.base.debug.DashLogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class EmailSetupBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {	
	    DashLogger.d("EmailSetupBroadcastReceiver", "errorMessage = " + intent.getStringExtra("errorMessage"));
	    DashLogger.d("EmailSetupBroadcastReceiver", "errorCode = " + intent.getIntExtra("errorCode", 1));
	    DashLogger.d("EmailSetupBroadcastReceiver", "success = " + intent.getBooleanExtra("success", false));
	    DashLogger.d("EmailSetupBroadcastReceiver", "email = " + intent.getStringExtra("email"));
	}

}
