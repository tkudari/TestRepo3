package com.dashwire.config.services;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.dashwire.config.NotificationController;
import com.dashwire.config.R;


public class SocialHubIntentService extends IntentService {

    public final String TAG = SocialHubIntentService.class.getCanonicalName();
    public final Context context = this;

    private final IBinder binder = new SocialHubIntentServiceBinder();

    public SocialHubIntentService() {
        super( "SocialHubIntentService" );
        
    }

    public SocialHubIntentService( String name ) {
        super( name );
    }

    public class SocialHubIntentServiceBinder extends Binder {
        public SocialHubIntentService getService() {
            return SocialHubIntentService.this;
        }
    }

    public IBinder onBind( Intent arg0 ) {
    	//TODO: SECURITY Add caller check.
		return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void onHandleIntent( Intent arg0 ) {

        String sns_type = arg0.getStringExtra( "SNS_TYPE" );
        int sns_result = arg0.getIntExtra( "SNS_RESULT", 0 );
        
        String notificationTitle = "";
        String notificationBody = "";
        Intent notificationIntent = new Intent();

        switch ( sns_result ) {
            case 31111:
            case 31112:
            case 31113:
            case 31114:
            case 31116:
                notificationTitle = context.getResources().getString( R.string.notification_social_login_fail_title );
                notificationBody = context.getResources().getString( R.string.notification_social_login_fail_body );
                notificationIntent.setComponent( new ComponentName( "com.android.providers.subscribedfeeds", "com.android.settings.ManageAccountsSettings" ) );
                break;
            case 31115:
                notificationTitle = context.getResources().getString( R.string.notification_social_conn_error_title );
                notificationBody = context.getResources().getString( R.string.notification_social_conn_error_body );
                notificationIntent.setComponent( new ComponentName( "com.android.settings", "com.android.settings.WirelessSettings" ) );
                break;
        }

        if ( sns_result != 31110 ) {
            NotificationController.getInstance( this ).createNotification( NotificationController.SOCIAL_NETWORK_RESULT, sns_type, notificationTitle,
                    notificationBody, notificationIntent );
        }

        Intent result = new Intent();
        result.setClassName( "com.dashwire.config", "com.dashwire.config.services.SocialHubRequestService" );
        stopService( result );
    }
}
