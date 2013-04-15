package com.dashwire.config.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.NotificationController;
import com.dashwire.config.R;
import com.dashwire.config.tracking.Tracker;
import com.dashwire.config.util.CommonUtils;

public class CompletedActivity extends BaseActivity
{

    protected static final String TAG = CompletedActivity.class.getCanonicalName();
    private Button nextButton;
    private BroadcastReceiver finalFinishReceiver = new FinalFinishReceiver();

    public void onCreate( Bundle savedInstanceState )
    {
        setContentView( R.layout.completed );
        super.onCreate( savedInstanceState );

        initReceiver();
        CommonUtils.setConfigCompletedFlag( true, this );
        CommonUtils.setConfiguredFlag( true, this );

        if ( CommonUtils.isNotificationAlarmFlagEnabled( this ) )
        {
            CommonUtils.setNotificationAlarmFlag( false, this );
            NotificationController.stopNotificationAlarms( this );
            NotificationController.clearNotification( NotificationController.CONFIGURE_YOUR_DEVICE );
        }

        nextButton = ( Button ) findViewById( R.id.next_button );
        if ( !CommonUtils.isFirstLaunch( context ) )
        {
            nextButton.setText( context.getResources().getString( R.string.button_finish ) );
        }
        nextButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view )
            {
                Tracker.track(context, "completed/finish");
                CommonUtils.scheduleDownloader( CompletedActivity.this );
                CommonUtils.releaseDevice();
                CommonUtils.broadcastDashconfigSuccess( context );
                if ( CommonUtils.hasFinishBroadcastReceiver( context ) )
                {

                    Intent intent = new Intent( "com.dashwire.config.FINISH" );
                    intent.setPackage( "com.dashwire.config" );
                    DashLogger.d( TAG, "Sending broadcast to EasyReceiver.." );
                    sendBroadcast( intent );

                } else
                {
                    DashLogger.v(TAG, "CompletedActivity onClick - calling finish()");
                    finish();
                }
            }
        } );
        OverrideBackKeyLayout.setUIActivity( this );
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    protected void onDestroy()
    {
        try
        {
            unregisterReceiver( finalFinishReceiver );
        } catch ( Exception e )
        {
            DashLogger.e( TAG, "Exception on unregistering final finish receiver and config finish receiver" );
        }
        super.onDestroy();
    }

    public class FinalFinishReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive( Context context, Intent intent )
        {
            DashLogger.d( TAG, "FinalFinishReceiver calling disableSetupWizard() after receiving result from EasyReceiver.." );
            finish();
            CommonUtils.disableSetupWizard( context );
        }
    }

    private void initReceiver()
    {
        IntentFilter easyFinishfilter = new IntentFilter();
        easyFinishfilter.addAction( "com.dashwire.config.EASY_FINISHED" );
        registerReceiver( finalFinishReceiver, easyFinishfilter );
    }

    public void onBackPressed()
    {
    };
}