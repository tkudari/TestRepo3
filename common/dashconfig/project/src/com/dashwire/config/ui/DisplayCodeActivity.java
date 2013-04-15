package com.dashwire.config.ui;

import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.tracking.Tracker;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dashwire.config.DashconfigApplication;
import com.dashwire.config.R;
import com.dashwire.config.util.CommonUtils;

public class DisplayCodeActivity extends TaskListenerActivity {

    private static final String TAG = DisplayCodeActivity.class.getCanonicalName();

    private TextView title;
    private TextView body;
    private TextView needHelpLink;
    private Handler expireHandler;
    private Runnable expireRunnable;
    private BroadcastReceiver mCheckinResultReceiver;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        setContentView( R.layout.display_code );
        super.onCreate( savedInstanceState );
        registerReceivers();

        title = ( TextView ) findViewById( R.id.display_code_title );
        body = ( TextView ) findViewById( R.id.display_code_body );
        if ( CommonUtils.isPostPairEnabled(context) ) {
            title.setText( context.getResources().getString( R.string.display_code_title_s3 ) );
            body.setText( context.getResources().getString( R.string.display_code_body_s3 ) );
        } else {
            title.setText( context.getResources().getString( R.string.display_code_title_s1 ) );
            body.setText( context.getResources().getString( R.string.display_code_body_s1 ) );
        }
        
        if (DashconfigApplication.getDeviceContext().getCustomDefinedTextSize(this) != -1) { // != -1 only for Pantech EZ mode
        	title.setTextSize(DashconfigApplication.getDeviceContext().getCustomDefinedTextSize(this));
        }

        needHelpLink = ( TextView ) findViewById( R.id.need_help_link );
		String linkText = context.getResources().getString( R.string.display_code_need_help_link );
        CommonUtils.clickify( needHelpLink, linkText, linkText, new ClickSpan.OnClickListener() {
            public void onClick() {
                Tracker.track(context, "display_code/need_help");
                finish();
                startActivity( CommonUtils.getNeedHelpActivityIntent( CommonUtils.getDisplayCodeActivityIntent( context ).toURI(), context ) );
            }
        } );

        Button backButton = ( Button ) findViewById( R.id.back_button );
        backButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view ) {
                finish();
            }
        } );
    }

    @Override
    protected void onResume() {
        setupCode();
        super.onResume();
    }

    protected void onPushClientTimeout() {
        CommonUtils.resetSession( context );
        finish();
        CommonUtils.startTimeoutActivity( CommonUtils.getDisplayCodeActivityIntent( context ).toURI(), "", context );
    }

    protected String getCode() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( this );
        String code = settings.getString( "code", null );
        return code;
    }

    protected void setupCode() {
        DashLogger.v(TAG,"isDataConnectionAvailable = " + CommonUtils.isDataConnectionAvailable( this ));
        if ( CommonUtils.isDataConnectionAvailable( this ) ) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( this );
            String code = settings.getString( "code", null );
            if ( code != null ) {
                TextView codeView = ( TextView ) findViewById( R.id.code );
                codeView.setText( code );
                String pairingUri = "";
                if ( CommonUtils.isPostPairEnabled(context) ) {
                    pairingUri = CommonUtils.getGotoLinkText(getApplicationContext());
                } else {
                    pairingUri = settings.getString( "pairing_uri", "http://att.com/pair" );
                }
                TextView gotoLinkView = ( TextView ) findViewById( R.id.goto_link );
                gotoLinkView.setText( pairingUri );
                if (DashconfigApplication.getDeviceContext().getCustomDefinedTextSize(this) != -1) { // != -1 only for Pantech EZ mode
                	gotoLinkView.setTextSize(DashconfigApplication.getDeviceContext().getCustomDefinedTextSize(this));
                }
                startPushClient( "paired" );
                startExpirationTimer();
            } else {
                CharSequence currentTimeString = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date());
                startCheckinWithProgressDialog("DisplayCodeActivity - setupCode - " + currentTimeString);
            }
        } else if ( "paired".equalsIgnoreCase( CommonUtils.getPushClientStatus( context )) ) {
            finish();
            CommonUtils.startConnectivityErrorActivity( CommonUtils.getDisplayCodeActivityIntent( context ).toURI(), "", context );
        } else {
            finish();
            startActivity( CommonUtils.getCheckConnectionActivityIntent( context ) );
        }
    }

    private void startExpirationTimer() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( this );
        long expiration = settings.getLong( "expiration", System.currentTimeMillis() );
        long millis = expiration - System.currentTimeMillis();
        if ( millis > 0 ) {
            expireHandler = new Handler();
            expireRunnable = new Runnable() {
                public void run() {
                    expireCode();
                }
            };
            expireHandler.postDelayed( expireRunnable, millis );
        } else {
            expireCode();
        }
    }

    @Override
    protected void onDestroy() {
        if ( expireHandler != null ) {
            expireHandler.removeCallbacks( expireRunnable );
        }
        unregisterReceivers();       
        super.onDestroy();
    }

    protected void expireCode() {
        CommonUtils.resetSession( context );
        finish();
        CommonUtils.startCodeExpiredActivity( CommonUtils.getDisplayCodeActivityIntent( context ).toURI(), "", context );
    }

    protected void processStatusMessage( JSONObject object ) {
        if ( object == null ) {
            DashLogger.e(TAG, "processStatusMessage called with null");
        }
        
        if ( CommonUtils.isDataConnectionAvailable( context ) ) {
            if ( checkStatus( object, "ready" ) ) {
                CommonUtils.setPushClientStatus( "ready", context );
                startActivity( CommonUtils.getConfigurationActivityIntent( context ) );
            } else if ( checkStatus( object, "paired" ) ) {
                CommonUtils.setPushClientStatus( "paired", context );
                startActivity( CommonUtils.getConnectedActivityIntent( context ) );
            } else {
                finish();
                CommonUtils.startNotConnectedErrorActivity( CommonUtils.getDisplayCodeActivityIntent( context ).toURI(), "", context );
            }
        } else {
            finish();
            CommonUtils.startConnectivityErrorActivity( CommonUtils.getDisplayCodeActivityIntent( context ).toURI(), null, context );
        }
        finish();
    }

    synchronized public void processCheckin( boolean success ) {
        super.processCheckin( success );
    }

    protected void onCheckinTimeout() {
        finish();
        startActivity( CommonUtils.getCheckConnectionActivityIntent( context ) );
    }
    
    private final BroadcastReceiver mCheckinSuccessReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if ("com.dashwire.config.checkin.result".equalsIgnoreCase( action ))
            {
                boolean success = intent.getBooleanExtra( "success", false );
                if ( success ) {
                    setupCode();
                } else {
                    finish();
                    startActivity ( CommonUtils.getCheckConnectionActivityIntent( context ));
                }
            }
        }
    };
    
    private void registerReceivers() {
        mCheckinResultReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("com.dashwire.config.checkin.result".equalsIgnoreCase( action ))
                {
                    boolean success = intent.getBooleanExtra( "success", false );
                    if ( success ) {
                        setupCode();
                    } else {
                        finish();
                        startActivity ( CommonUtils.getCheckConnectionActivityIntent( context ));
                    }
                }
            }
        };
        
        IntentFilter checkinResultfilter = new IntentFilter();
        checkinResultfilter.addAction("com.dashwire.config.checkin.result");
        this.registerReceiver( mCheckinResultReceiver, checkinResultfilter );
    }
    
    private void unregisterReceivers() {
        if (mCheckinResultReceiver != null )
        {
            unregisterReceiver(mCheckinResultReceiver);
            mCheckinResultReceiver = null;
        }
    }
}
