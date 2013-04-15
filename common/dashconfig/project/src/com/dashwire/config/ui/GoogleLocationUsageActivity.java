package com.dashwire.config.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

import com.dashwire.config.R;
import com.dashwire.config.tasks.GoogleLocationUpdateHandler;
import com.dashwire.config.tasks.GoogleLocationUpdateTask;
import com.dashwire.config.tracking.Tracker;
import com.dashwire.config.util.CommonUtils;

public class GoogleLocationUsageActivity extends BaseActivity implements GoogleLocationUpdateHandler {

    private GoogleLocationUpdateTask googleLocationUpdateTask;
    private CheckBox allowLocationCheckBox;

    public void onCreate( Bundle savedInstanceState ) {
        setContentView( R.layout.google_location_usage );
        super.onCreate( savedInstanceState );
        
        allowLocationCheckBox = (CheckBox) findViewById(R.id.allow_loction_check_box);
        if (!allowLocationCheckBox.isChecked()) {
            allowLocationCheckBox.setChecked(true);
        }
        
        Button backButton = ( Button ) findViewById( R.id.back_button );
        backButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view ) {
                Tracker.track(context, "google_location_usage/back");
                updateGoogleLocationUsage();
                back();
            }
        } );

        Button nextButton = ( Button ) findViewById( R.id.next_button );
        nextButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view ) {
                Tracker.track(context, "google_location_usage/next");
                CommonUtils.setGoogleLocationUsageCompletedFlag( true, context );
                updateGoogleLocationUsage();
                CommonUtils.scheduleDownloader( GoogleLocationUsageActivity.this );
                CommonUtils.releaseDevice();
            }
        } );
    }

    private void updateGoogleLocationUsage() {
        showGoogleLocationUpdateProgress();
        googleLocationUpdateTask = new GoogleLocationUpdateTask( context, this );
        if ( allowLocationCheckBox.isChecked() ) {
            googleLocationUpdateTask.execute( true );
        } else {
            googleLocationUpdateTask.execute( false );
        }
    }

    private void showGoogleLocationUpdateProgress() {
        CommonUtils.showProgressDialog( context.getResources().getString( R.string.progress_dialog_google_location ), context );
    }

    private void hideGoogleLocationUpdateProgress() {
        if ( CommonUtils.isProgressDialogVisible() ) {
            CommonUtils.hideProgressDialog();
        }
    }

    private void back() {
        CommonUtils.startActivityFromUri( getIntent().getStringExtra( "back" ), context );
        finish();
    }

    public void processGoogleLocationUpdateStatus( boolean success ) {
        if ( success ) {
            hideGoogleLocationUpdateProgress();
            finish();
        }
    }
}