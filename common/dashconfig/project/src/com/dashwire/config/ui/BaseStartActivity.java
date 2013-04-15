package com.dashwire.config.ui;

import android.provider.Settings;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.base.device.DeviceInfo;
import com.dashwire.config.tracking.Tracker;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.dashwire.config.DashconfigApplication;
import com.dashwire.config.NotificationController;
import com.dashwire.config.R;
import com.dashwire.config.util.CommonConstants;
import com.dashwire.config.util.CommonUtils;

public abstract class BaseStartActivity extends TaskListenerActivity
{
    abstract void setLauncherFlag();

    protected static final String TAG = BaseStartActivity.class.getCanonicalName();
    private static final int MENU_DEBUG = 0;
    private static final int MENU_AIRPLANE_ON = 1;
    private static final int MENU_AIRPLANE_OFF = 2;
    private static final int MENU_SETTINGS = 3;
    private TextView learnMoreLink;
    private ImageView getStartedButton;
    private BroadcastReceiver mDashconfigCancelReceiver;
    private BroadcastReceiver mDashconfigSuccessReceiver;

    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        if(DashconfigApplication.getDeviceContext().isBlackListedDevice()) {
        	processCanceledFlow();
        }

        CommonUtils.resetSession( context );
        setLauncherFlag();
        setContentView( R.layout.start );
        CommonUtils.acquireDevice( context );
        DashLogger.v( TAG, "Starting Dashconfig Ready2Go-----------------" );
        registerReceivers();

        boolean isLaunchFromNotification = getIntent().getBooleanExtra( CommonConstants.LAUNCHFROMNOTIFICATION, false );

        if ( isLaunchFromNotification )
        {
            CommonUtils.setAlreadyNotifiedFlag( true, context );
            CommonUtils.setNotificationAlarmFlag( false, this );
            NotificationController.stopNotificationAlarms( this );
            NotificationController.clearNotification( NotificationController.CONFIGURE_YOUR_DEVICE );
        }

        if ( CommonUtils.isDataConnectionAvailable( this ) )
        {
            startCheckin("BaseStartActivity onCreate");
        }

        learnMoreLink = ( TextView ) findViewById( R.id.learn_more_link );

        getStartedButton = ( ImageView ) findViewById( R.id.get_started_button );
        getStartedButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view ) {
                Tracker.track(context, "base_start/get_started");
                    start();
            }
        } );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        if ( DeviceInfo.isConfigurator3000Installed(context) )
        {
            menu.add( 0, MENU_DEBUG, 0, "DEBUG" );
            menu.add( 0, MENU_AIRPLANE_ON, 0, "Airplane On" );
            menu.add( 0, MENU_AIRPLANE_OFF, 0, "Airplane Off" );
            menu.add( 0, MENU_SETTINGS, 0, "Settings" );
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch ( item.getItemId() )
        {
            case MENU_AIRPLANE_ON:
                CommonUtils.airplaneMode( this, true );
                return true;
            case MENU_AIRPLANE_OFF:
                CommonUtils.airplaneMode( this, false );
                return true;
            case MENU_SETTINGS:
                startActivity( new Intent( Settings.ACTION_SETTINGS ) );
                return true;
        }
        return false;
    }

    synchronized private void start()
    {
        DashLogger.v( TAG, "start() ---------------------------------------------------------------------" );
        if ( CommonUtils.isDataConnectionAvailable( this ) )
        {
            startNextActivity();
        } else
        {
            startActivity( CommonUtils.getCheckConnectionActivityIntent( context ) );
        }
    }

    private void startNextActivity()
    {
        if ( CommonUtils.packageWaiting( context ) )
        {
            startActivity( CommonUtils.getLoginActivityIntent( context ) );
        } else
        {
            startActivity( CommonUtils.getDisplayCodeActivityIntent( context ) );
        }
    }

    protected void onResume()
    {
        if ( ( CommonUtils.getGoogleLocationUsageCompletedFlag( context ) && DashconfigApplication.getDeviceContext()
                .getStringConst( context, "FIRST_BOOT_LAUNCHER" )
                .equalsIgnoreCase( CommonUtils.getLaunchFromIndicator( context ) ) )
                || CommonUtils.isCanceledFlagEnabled( context ) )
        {
            processCanceledFlow();
        } else if ( CommonUtils.isConfigCompletedFlagEnabled( context ) )
        {
            processCompletedFlow();
        }
        super.onResume();
		String linktext = context.getResources().getString( R.string.start_learn_more_link );
		CommonUtils.clickify( learnMoreLink, linktext, linktext, new ClickSpan.OnClickListener() {
			public void onClick() {
				Tracker.track(context, "base_start/learn_more");
				startActivity( CommonUtils.getAboutActivityIntent( context ) );
			}
		} );
    }

    protected void addQuitButton( View quitButton )
    {
        if ( quitButton != null )
        {
            quitButton.setOnClickListener( new OnClickListener() {
                public void onClick( View view )
                {
                    CommonUtils.setQuitFlag( true, context );
                    processCanceledFlow();
                }
            } );
        } else
        {
            DashLogger.v( TAG, "quit button is null in BaseStartActivity" );
        }
    }

    private void setFlagsInResult()
    {
        Intent intent = new Intent();

        if ( CommonUtils.isQuitFlagEnabled( this ) )
        {
            intent.putExtra( CommonConstants.QUIT_FLAG, true );
        } else
        {
            intent.putExtra( CommonConstants.QUIT_FLAG, false );
        }

        if ( CommonUtils.isConfiguredFlagEnabled( this ) )
        {
            intent.putExtra( CommonConstants.CONFIGURED_FLAG, true );
        } else
        {
            intent.putExtra( CommonConstants.CONFIGURED_FLAG, false );
        }

        setResult( RESULT_OK, intent );

        if ( CommonUtils.isFirstLaunch( context ) )
        {
            DashLogger.d( TAG, "First launch." );
            CommonUtils.disableStatusBarManager( context );

            CommonUtils.setLauncherRefreshFlag( true, context );

            if ( CommonUtils.isConfiguredFlagEnabled( this ) )
            {
                CommonUtils.broadcastDashconfigConfigured( context );
            } else
            {
                CommonUtils.broadcastDashconfigNotConfigured( context );
            }
        } else
        {
            DashLogger.d(TAG, "Not first launch.");
        }

        CommonUtils.releaseDevice();
        CommonUtils.resetSession( context );
        
        if (CommonUtils.isFirstLaunch( context ) && !CommonUtils.isGoogleAccountSetOnFirstBoot( context ))      
        {
        	NotificationController.setGoogleLoginAlarm(context);
        }
        
        if ( CommonUtils.isFirstLaunch( context ))
        {
            CommonUtils.disableSetupWizard( context ); 
        }
    }

    protected void processCanceledFlow()
    {

        DashLogger.v( TAG, "processCanceledFlow ---------------------------------------------------------" );
        if ( CommonUtils.isFirstLaunch( context ) )
        {
            if ( CommonUtils.isCanceledFlagEnabled( context ) )
            {
                if ( !CommonUtils.isAlreadyNotified( context ) )
                {
                    CommonUtils.setNotificationAlarmFlag( true, context );
                    NotificationController.startNotificationAlarms( System.currentTimeMillis(), context );
                }
            }
        }

        setFlagsInResult();
        finish();
    }

    private void processCompletedFlow()
    {
        DashLogger.v( TAG, "processCompletedFlow ---------------------------------------------------------" );
        setFlagsInResult();
        finish();
    }
    
    private void registerReceivers() {
        
        mDashconfigSuccessReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("com.dashwire.config.success".equalsIgnoreCase( action ))
                {
                    processCompletedFlow();
                }
            }
        };
        
        IntentFilter dashconfigSuccessfilter = new IntentFilter();
        dashconfigSuccessfilter.addAction("com.dashwire.config.success");
        this.registerReceiver( mDashconfigSuccessReceiver, dashconfigSuccessfilter );
        
        
        mDashconfigCancelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                DashLogger.d( TAG, " --- CANCELED!");
                String action = intent.getAction();
                if ("com.dashwire.config.cancel".equalsIgnoreCase( action ))
                {
                    processCanceledFlow();
                }
            }
        };
        
        IntentFilter dashconfigCancelfilter = new IntentFilter();
        dashconfigCancelfilter.addAction("com.dashwire.config.cancel");
        this.registerReceiver( mDashconfigCancelReceiver, dashconfigCancelfilter );
    }
    
    private void unregisterReceivers() {
        
        if (mDashconfigSuccessReceiver != null )
        {
            unregisterReceiver(mDashconfigSuccessReceiver);
            mDashconfigSuccessReceiver = null;
        }
        
        if (mDashconfigCancelReceiver != null )
        {
            unregisterReceiver(mDashconfigCancelReceiver);
            mDashconfigCancelReceiver = null;
        }
    }

    synchronized public void processCheckin( boolean success )
    {
        super.processCheckin( success );
    }

    protected void onCheckinTimeout()
    {
    }

    protected void onPushClientTimeout()
    {
    }

    protected long getPushClientTimeout()
    {
        return 0;
    }

    protected void processStatusMessage( JSONObject object )
    {
    }

    @Override
    public void onDestroy()
    {
        unregisterReceivers();
        CommonUtils.releaseDevice();
        super.onDestroy();
    }

}