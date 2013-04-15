package com.dashwire.config.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.dashwire.config.NotificationController;
import com.dashwire.config.R;
import com.dashwire.base.debug.OverridePreferences;
import com.dashwire.config.util.CommonConstants;

public class SocialHubRequestService extends Service {

	protected static final String TAG = SocialHubRequestService.class.getCanonicalName();
    public String type = "";
    private boolean timeoutCancelled = false;
    private Context context = this;

    public class SocialHubRequestServiceBinder extends Binder {
        SocialHubRequestService getService() {
            return SocialHubRequestService.this;
        }
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        setTimeout();
        
        type = intent.getStringExtra( CommonConstants.SNS_TYPE );
        String login = intent.getStringExtra( CommonConstants.SNS_USERNAME );
        String password = intent.getStringExtra( CommonConstants.SNS_PASSWORD );

        Intent samsungSocialHubIntent = new Intent();        
        samsungSocialHubIntent.setClassName( "com.sec.android.socialhub", "com.sec.android.socialhub.service.AutomaticSetupService" );
        samsungSocialHubIntent.putExtra( CommonConstants.SNS_TYPE, type );
        samsungSocialHubIntent.putExtra( CommonConstants.SNS_USERNAME, login );
        samsungSocialHubIntent.putExtra( CommonConstants.SNS_PASSWORD, password );
        startService( samsungSocialHubIntent );
        
        return START_STICKY;
    }

    public void onDestroy() {
        cancelTimeout();
    }

    public IBinder onBind(Intent intent) {
    	//TODO: SECURITY Add caller check.
		return mBinder;
    }

    private final IBinder mBinder = new SocialHubRequestServiceBinder();
    
    protected void setTimeout() {
        Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            public void run() {
                timeout();
            }
        }, getTimeout() );
    }
    
    protected void timeout() {
        if ( !timeoutCancelled ) {
            onTimeout();
        }
    }
    
    public long getTimeout() {
        long snstimeout = CommonConstants.NINTY_SECONDS;
        //TODO correct the override Timeout
        
        if ( OverridePreferences.getBoolean( this, "overridePairingTimeout", false ) ) {
            snstimeout = OverridePreferences.getStringAsLong( this, "overridePairingTimeoutValue", snstimeout );
        }
        return snstimeout;
    }
    
    protected void onTimeout() {
        
        String notificationTitle = context.getResources().getString( R.string.notification_social_login_fail_title );
        String notificationBody = context.getResources().getString( R.string.notification_social_login_fail_body );
        Intent notificationIntent = new Intent();
        notificationIntent.setComponent( new ComponentName( "com.android.providers.subscribedfeeds", "com.android.settings.ManageAccountsSettings" ) );

        NotificationController.getInstance( context ).createNotification( NotificationController.SOCIAL_NETWORK_RESULT , type, notificationTitle, notificationBody, notificationIntent);
    }
    

    protected void cancelTimeout() {
        this.timeoutCancelled = true;
    }
}
