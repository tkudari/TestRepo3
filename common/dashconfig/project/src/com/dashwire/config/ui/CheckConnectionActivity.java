package com.dashwire.config.ui;

import com.dashwire.base.debug.DashLogger;
import com.dashwire.base.device.DeviceInfo;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dashwire.config.DashconfigApplication;
import com.dashwire.config.R;
import com.dashwire.config.util.CommonUtils;

public class CheckConnectionActivity extends TaskListenerActivity {
    private static final String TAG = CheckConnectionActivity.class.getCanonicalName();
    private static final int MENU_AIRPLANE_ON = 0;
    private static final int MENU_AIRPLANE_OFF = 1;
    private BroadcastReceiver connectivityReceiver;
    private BroadcastReceiver mCheckinResultReceiver;
    private Handler retryHandler = new Handler();
    private Runnable retryRunnable;
    private TextView needHelpLink;
    private Button backButton;
    private final int FONT_SCALE = 3;
    


    @Override
    public void onCreate( Bundle savedInstanceState ) {
        setContentView( R.layout.check_connection );
        registerReceivers();
        super.onCreate( savedInstanceState );
        
        needHelpLink = ( TextView ) findViewById( R.id.need_help_link);
		String linkText = context.getResources().getString( R.string.check_conn_need_help_link );
        CommonUtils.clickify( needHelpLink, linkText, linkText, new ClickSpan.OnClickListener() {
            public void onClick() {
                finish();
                startActivity( CommonUtils.getCheckConnNeedHelpActivityIntent( context ) );
            }
        } );
        
        TextView title = ( TextView ) findViewById( R.id.title);
        TextView gotoLinkView = ( TextView ) findViewById( R.id.goto_link );
        gotoLinkView.setText( CommonUtils.getGotoLinkText(getApplicationContext()) );
        // This is a fix for when the text for the gotoLinkView & title overflows in the Pantech Magnus in EZ mode.
        // Override/modify  the getCustomDefinedTextSize() for getting OEM specific font sizes.
              
        if (DashconfigApplication.getDeviceContext().getCustomDefinedTextSize(this) != -1) { // != -1 only for Pantech EZ mode
        	gotoLinkView.setTextSize(DashconfigApplication.getDeviceContext().getCustomDefinedTextSize(this));
        	title.setTextSize(DashconfigApplication.getDeviceContext().getCustomDefinedTextSize(this) + FONT_SCALE);
        }
        
        backButton = ( Button ) findViewById( R.id.back_button );
        backButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view ) {
                finish();
            }
        } );
    }

    @Override
    protected void onDestroy() {
        if ( retryRunnable != null ) {
            retryHandler.removeCallbacks( retryRunnable );
        }
        unregisterReceivers();
        super.onDestroy();
    }

    private void registerReceivers() {
        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive( Context context, Intent intent ) {
                processConnectivityEvent( intent );
            }
        };

        IntentFilter filter = new IntentFilter( "android.net.conn.CONNECTIVITY_CHANGE" );
        registerReceiver( connectivityReceiver, filter );
        
        mCheckinResultReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("com.dashwire.config.checkin.result".equalsIgnoreCase( action ))
                {
                    boolean success = intent.getBooleanExtra( "success", false );
                    if ( success ) {
                        finish();
                        Intent DisplayCodeintent = CommonUtils.getDisplayCodeActivityIntent( getApplicationContext() );
                        CommonUtils.setPostPairFlag(context, true);
                        startActivity( DisplayCodeintent );
                    } else {
                        backoff();
                    }
                }
            }
        };
        
        IntentFilter checkinResultfilter = new IntentFilter();
        checkinResultfilter.addAction("com.dashwire.config.checkin.result");
        this.registerReceiver( mCheckinResultReceiver, checkinResultfilter );
    }

    private void unregisterReceivers() {
        if ( connectivityReceiver != null ) {
            unregisterReceiver( connectivityReceiver );
            connectivityReceiver = null;
        }
        if (mCheckinResultReceiver != null )
        {
            unregisterReceiver(mCheckinResultReceiver);
            mCheckinResultReceiver = null;
        }
    }

    private void processConnectivityEvent( Intent intent ) {
        ConnectivityManager connectivityManager = ( ConnectivityManager ) getSystemService( Context.CONNECTIVITY_SERVICE );
        DashLogger.v( TAG, intent.toURI() + " ----------------------------------------------------------------" );
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if ( info != null ) {
            DashLogger.v( TAG, "network is active! ----------------------------------------------------------------" );
            if ( info.isConnected() ) {
                DashLogger.v( TAG, "network is connected! ----------------------------------------------------------------" );
                DashLogger.v(TAG,"isDataConnectionAvailable = " + CommonUtils.isDataConnectionAvailable( this ));
                startCheckin("CheckConnectionActivity processConnectivityEvent");
            } else {
                DashLogger.v( TAG, "network is not connected! ----------------------------------------------------------------" );
            }
        } else {
            DashLogger.v(TAG, "no active network ----------------------------------------------------------------");
        }
    }

    public void processCheckin( boolean success ) {
        super.processCheckin( success );
    }

    private void backoff() {
        retryRunnable = new Runnable() {
            public void run() {
                startCheckin("CheckConnectionActivity backoff");
            }
        };
        retryHandler.postDelayed( retryRunnable, 5000 );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        if (DeviceInfo.isConfigurator3000Installed(context))
        {
            menu.add( 0, MENU_AIRPLANE_ON, 0, "Airplane On" );
            menu.add( 0, MENU_AIRPLANE_OFF, 0, "Airplane Off" );
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch ( item.getItemId() ) {
            case MENU_AIRPLANE_ON:
                airplaneMode( true );
                return true;
            case MENU_AIRPLANE_OFF:
                CommonUtils.airplaneMode( this, false );
                return true;
        }
        return false;
    }
    


    private void airplaneMode( boolean state ) {
        CommonUtils.airplaneMode( this, state );
    }

    protected long getPushClientTimeout() {
        return 0;
    }

    protected void onCheckinTimeout() {
    }

    protected void onPushClientTimeout() {   
    }

    protected void processStatusMessage( JSONObject object ) {   
    }

    public void finish(){
        super.finish();
    }
}