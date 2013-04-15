package com.sec.android.socialhub.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AutomaticSetupService extends IntentService {

    public AutomaticSetupService() {
        super( "Test SNS Hub Service" );
        // TODO Auto-generated constructor stub
    }

    public AutomaticSetupService( String name ) {
        super( name );
        // TODO Auto-generated constructor stub
    }

    @Override
    public IBinder onBind( Intent arg0 ) {
    	//TODO: SECURITY Add caller check.
		return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.e( "Service Example", "SNS Hub Service Started.. " );
        // pushBackground();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.e( "Service Example", "SNS Hub Service Destroyed.. " );
    }

    protected void onHandleIntent( Intent arg0 ) {

        String sns_type = arg0.getStringExtra( "SNS_TYPE" );

        int sns_result = 0;

        if ( sns_type.equalsIgnoreCase( "Facebook" ) ) {
            sns_result = 31110;
        } else if ( sns_type.equalsIgnoreCase( "Twitter" ) ) {
            sns_result = 31111;
        } else if ( sns_type.equalsIgnoreCase( "LinkedIn" ) ) {
            sns_result = 31112;
        }
        
        Log.v("AutomaticSetupService", "sns_type = " + sns_type);
        Log.v("AutomaticSetupService", "sns_result = " + sns_result);
        
        if (sns_type.equalsIgnoreCase( "Facebook" ) || sns_type.equalsIgnoreCase( "Twitter" ))
        {
            Intent result = new Intent();
            result.setClassName( "com.dashwire.config", "com.dashwire.config.services.SocialHubIntentService" );
            result.putExtra( "SNS_TYPE", sns_type );
            result.putExtra( "SNS_RESULT", sns_result );
            startService( result );
            Log.v("AutomaticSetupService", "Started SocialHubIntentService");
        }
    }
}
