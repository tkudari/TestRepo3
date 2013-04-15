package com.dashwire.config.ui;

import com.dashwire.config.tracking.Tracker;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dashwire.config.R;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.util.CommonUtils;

public class ConnectedActivity extends TaskListenerActivity {

    public static final String TAG = ConnectedActivity.class.getCanonicalName();
    private TextView clickHereLink;
    
    public void onCreate( Bundle savedInstanceState ) {
        setContentView( R.layout.connected );
        super.onCreate( savedInstanceState );
        
        clickHereLink = ( TextView ) findViewById( R.id.connected_body2 );
        if ( clickHereLink != null)
        {
            CommonUtils.clickify(
					clickHereLink,
					context.getResources().getString( R.string.connected_body2 ),
					context.getResources().getString( R.string.connected_click_here_link ),
					new ClickSpan.OnClickListener() {
						public void onClick() {
							Tracker.track(context, "connected/click_here");
							finish();
							startActivity( CommonUtils.getNeedHelpActivityIntent( CommonUtils.getConnectedActivityIntent( context ).toURI(), context ) );
                		}
            		}
			);
        }

       
        Button cancelButton = ( Button ) findViewById( R.id.cancel_button );
        cancelButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view ) {
                Tracker.track(context, "connected/reset_session");
                CommonUtils.setCanceledFlag( true, context );
                CommonUtils.broadcastDashconfigCanceled( context );
                finish();
            }
        } );
    }

    protected void processStatusMessage( JSONObject object ) {
        if ( object == null ) {
            DashLogger.e( TAG, "handlePushMessage called with null" );
        }
        if ( CommonUtils.isDataConnectionAvailable( context ) ) {
            if ( checkStatus( object, "ready" ) ) {
                CommonUtils.setPushClientStatus( "ready", context );
                startActivity( CommonUtils.getConfigurationActivityIntent( context ) );
            } else {
                // TODO display
                // "please complete configuring your device on the web message"
                finish();
                CommonUtils.startConnectivityErrorActivity( CommonUtils.getConnectedActivityIntent( context ).toURI(), "", context );
            }
        } else {
            finish();
            CommonUtils.startConnectivityErrorActivity( CommonUtils.getConnectedActivityIntent( context ).toURI(), "", context );
        }
        finish();
    }

    @Override
    protected void onResume() {
        startPushClient( "ready" );
        super.onResume();
    }

    @Override
    protected void onPushClientTimeout() {
        finish();
        CommonUtils.startTimeoutActivity( CommonUtils.getConnectedActivityIntent( context ).toURI(), "", context );
    }

    protected void onCheckinTimeout() {       
    }

    public void processCheckin( boolean success ) {        
    }
}
