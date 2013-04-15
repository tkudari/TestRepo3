package com.dashwire.config.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.base.debug.OverridePreferences;
import com.dashwire.config.R;
import com.dashwire.config.tasks.*;
import com.dashwire.config.tracking.Tracker;
import com.dashwire.config.util.CommonConstants;
import com.dashwire.config.util.CommonUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;

public abstract class TaskListenerActivity extends BaseActivity implements PushClientHandler, StatusHandler, CheckinHandler {

    protected static final String TAG = TaskListenerActivity.class.getCanonicalName();
    private PushClientTask pushClient;
    private CheckinTask checkin;
    private Handler checkinTimeoutHandler;
    private Runnable checkinTiemoutRunnable;
    private boolean pushClientTimeoutCancelled;
    private boolean checkinTimeoutCancelledFlag = false;

    abstract protected void onPushClientTimeout();
    abstract protected void onCheckinTimeout();
    abstract protected void processStatusMessage( JSONObject object );

    protected void requestStatus() {
        CommonUtils.showProgressDialog( context.getResources().getString( R.string.progress_dialog_status_check ), context );
        new StatusTask( this, this ).executeOnExecutor(Executors.newCachedThreadPool());
    }

    protected void startPushClient( String check ) {
        cancelPushClient();
        pushClient = new PushClientTask( this, this );
        pushClient.executeOnExecutor(Executors.newCachedThreadPool(), check );
    }

    protected void startCheckinWithProgressDialog(String whenStarted) {
        showCheckinProgress();
        setCheckinTimeout();
        startCheckin(whenStarted);
    }

    protected void startCheckin(String whenStarted) {
        if (!CommonUtils.getCheckinInProgressFlag( getApplicationContext() ))
        {
            cancelCheckin();
            CommonUtils.setCheckinInProgressFlag( getApplicationContext(), true );
            checkin = new CheckinTask( context, this, whenStarted );
            checkin.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); 
        }
    }

    protected void cancelPushClient() {
        if ( pushClient != null ) {
            pushClient.cancel( true );
            pushClient = null;
        }
    }

    protected void cancelCheckin() {
        if ( checkin != null ) {
            checkin.cancel( true );
            checkin = null;
        }
    }

    protected boolean isCheckinSuccess() {
        if ( checkin == null ) {
            return false;
        }
        return true;
    }

    protected void onResume() {
        setPushClientTimeout();
        super.onResume();
    }

    @Override
    protected void onPause() {
        cancelPushClientTimeout();
        hideCheckinProgress();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        cancelPushClient();
        cancelCheckin();
        hideCheckinProgress();
        super.onDestroy();
    }

    @Override
    public void finish() {
        cancelCheckin();
        hideCheckinProgress();
        if ( checkinTiemoutRunnable != null ) {
            checkinTimeoutHandler.removeCallbacks( checkinTiemoutRunnable );
        }
        super.finish();
    }

    protected void cancelPushClientTimeout() {
        this.pushClientTimeoutCancelled = true;
    }

    protected void setPushClientTimeout() {
        Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            public void run() {
                timeoutPushClient();
            }
        }, getPushClientTimeout() );
    }

    protected void timeoutPushClient() {
        cancelPushClient();
        if ( !pushClientTimeoutCancelled ) {
            onPushClientTimeout();
        }
    }

    protected void setCheckinTimeout() {
        checkinTimeoutHandler = new Handler();
        checkinTiemoutRunnable = new Runnable() {
            public void run() {
                timeoutCheckin();
            }   
        };
        checkinTimeoutHandler.postDelayed( checkinTiemoutRunnable, getCheckinTimeout() );
    }

    protected void timeoutCheckin() {
        if ( !checkinTimeoutCancelledFlag && CommonUtils.getCheckinInProgressFlag( context )) {
            Tracker.track( context, "CheckIn_Timeout" );
            cancelCheckin();
            onCheckinTimeout();
        }
    }

    protected void cancelCheckinTimeout() {
        this.checkinTimeoutCancelledFlag = true;
    }

    protected boolean checkStatus( JSONObject object, String key ) {
        if ( object != null ) {
            try {
                boolean b = object.getBoolean( key );
                if(b) {
                	DashLogger.d(TAG, "JSON Status returned: Code = " + key);
                }
                return b;
            } catch ( JSONException e ) {
                // drop through to return false;
            }
        }
        return false;
    }

    public void handlePushMessage( JSONObject object ) {
        processStatusMessage( object );
    }

    public void handleStatus( JSONObject object ) {
        CommonUtils.hideProgressDialog();
        processStatusMessage( object );
    }

    private long getCheckinTimeout() {
        long checkinTimeout = CommonConstants.THIRTY_SECONDS;
        if ( OverridePreferences.getBoolean( this, "checkinTimeout", false ) ) {
            checkinTimeout = OverridePreferences.getStringAsLong( this, "checkinTimeoutValue", checkinTimeout );
        }
        return checkinTimeout;
    }

    synchronized public void processCheckin( boolean success ) {
        CommonUtils.setCheckinInProgressFlag( getApplicationContext(), false );
        hideCheckinProgress();
        Intent checkinResultIntent = new Intent("com.dashwire.config.checkin.result");
        checkinResultIntent.putExtra( "success", success );
        getApplicationContext().sendBroadcast( checkinResultIntent );
        
        if ( success ) {
            cancelCheckinTimeout();
        }
        else
        {
            cancelCheckin();
        }
    }
    
    private void showCheckinProgress()
    {
        CommonUtils.showProgressDialog( context.getResources().getString( R.string.progress_dialog_session ), context );
    }
    
    private void hideCheckinProgress()
    {
        if ( CommonUtils.isProgressDialogVisible() ) {
            CommonUtils.hideProgressDialog();
        }
    }
    
    private long getPushClientTimeout() {
        long pushClientTimeout = CommonConstants.SIXTY_MINUTES;
        if ( OverridePreferences.getBoolean( this, "overrideConnectedTimeout", false ) ) {
            pushClientTimeout = OverridePreferences.getStringAsLong( this, "overrideConnectedTimeoutValue", pushClientTimeout );
        }
        return pushClientTimeout;
    }
}
