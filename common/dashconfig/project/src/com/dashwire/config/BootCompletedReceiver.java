package com.dashwire.config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dashwire.config.util.CommonUtils;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive( Context context, Intent intent ) {

        if ( !CommonUtils.isConfiguredFlagEnabled( context ) && !CommonUtils.isAlreadyNotified( context )) {
            CommonUtils.setNotificationAlarmFlag( true, context );
            NotificationController.startNotificationAlarms( CommonUtils.getNotificationStartTime( context ), context );
        }
    }
}
