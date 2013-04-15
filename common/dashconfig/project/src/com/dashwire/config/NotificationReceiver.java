package com.dashwire.config;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.util.CommonUtils;

public class NotificationReceiver extends BroadcastReceiver {
    
 public void onReceive(Context context, Intent intent) {
     
		if ("SETUP_GOOGLE_ACCOUNT".equals(intent.getAction())) {
			NotificationController.getInstance(context).showNotification(
					NotificationController.SETUP_GOOGLE_ACCOUNT);

		}
	 
     if ( CommonUtils.isNotificationAlarmFlagEnabled( context ) )
     {
         NotificationController.getInstance( context ).showNotification( NotificationController.CONFIGURE_YOUR_DEVICE );
     }
     DashLogger.v("NotificationReceiver","Notification Received");
     String action = intent.getStringExtra( "action" );
     
     DashLogger.v("NotificationReceiver", "action = " + action);
     
     if ( "notify_to_reboot".equalsIgnoreCase( action))
     {
         NotificationController.getInstance( context ).showNotification( NotificationController.RESTART_DEVICE );
     }
 }
}
