package com.dashwire.config.ui;

import com.dashwire.config.tracking.Tracker;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dashwire.config.R;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.util.CommonUtils;

public class NeedHelpActivity extends TaskListenerActivity {

    private TextView finishedStepsText;
    private TextView configuredOnlineText;
    private Button backButton;
    private static final String TAG = NeedHelpActivity.class.getCanonicalName();

    public void onCreate( Bundle savedInstanceState ) {
        setContentView( R.layout.need_help );
        super.onCreate( savedInstanceState );
        
        finishedStepsText = ( TextView ) findViewById( R.id.need_help_finished_steps_ans );
        CommonUtils.clickify( finishedStepsText,
			context.getResources().getString( R.string.need_help_finished_steps_ans ),
			context.getResources().getString( R.string.need_help_click_here_link ),
			new ClickSpan.OnClickListener() {
				public void onClick() {
					Tracker.track(context, "need_help/help_needed");
					finish();
					Toast.makeText( context, context.getResources().getString( R.string.toast_connect_server ), Toast.LENGTH_SHORT ).show();
					startActivity( CommonUtils.getDisplayCodeActivityIntent( context ) );
				}
        	}
		);

        configuredOnlineText = ( TextView ) findViewById( R.id.need_help_configured_online_ans );
        CommonUtils.clickify( configuredOnlineText,
			context.getResources().getString( R.string.need_help_configured_online_ans ),
			context.getResources().getString( R.string.need_help_click_here_link ),
			new ClickSpan.OnClickListener() {
            	public void onClick() {
					Tracker.track(context, "need_help/login");
					finish();
					startActivity( CommonUtils.getLoginActivityIntent( context ) );
            	}
        	}
		);

        backButton = ( Button ) findViewById( R.id.back_button );
        backButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view ) {
                Tracker.track(context, "need_help/back");
                back();
            }
        } );
    }

    protected void onPushClientTimeout() {
        CommonUtils.resetSession( context );
        finish();
        CommonUtils.startTimeoutActivity( CommonUtils.getDisplayCodeActivityIntent( context ).toURI(), "", context );
    }

    protected void processStatusMessage( JSONObject object ) {
        if ( object == null ) {
            DashLogger.e( TAG, "processStatusMessage called with null" );
        }
        if ( CommonUtils.isDataConnectionAvailable( context ) ) {
            if ( checkStatus( object, "ready" ) ) {
                CommonUtils.setPushClientStatus( "ready", context );
                finish();
                startActivity( CommonUtils.getConfigurationActivityIntent( context ) );
                CommonUtils.setPushClientStatus( "paired", context );
            } else if ( checkStatus( object, "paired" ) ) {
                finish();
                startActivity( CommonUtils.getConnectedActivityIntent( context ) );
            } else {
                finish();
                CommonUtils.startNotConnectedErrorActivity( CommonUtils.getDisplayCodeActivityIntent( context ).toURI(), "", context );
            }
        } else {
            finish();
            CommonUtils.startConnectivityErrorActivity( CommonUtils.getDisplayCodeActivityIntent( context ).toURI(), "", context );
        }
        finish();
    }
    
    @Override
    protected void onResume() {
        startPushClient( "ready" );
        super.onResume();
    }
    
    private void back() {
        CommonUtils.startActivityFromUri( getIntent().getStringExtra( "backTo" ), context );
        finish();
    }

    protected void onCheckinTimeout() {       
    }

    public void processCheckin( boolean success ) {
    }

}