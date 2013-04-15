package com.dashwire.config.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dashwire.config.R;
import com.dashwire.config.tracking.Tracker;
import com.dashwire.config.util.CommonUtils;

public class AboutActivity extends BaseActivity {

    private Button backButton;
    private Button nextButton;

    public void onCreate( Bundle savedInstanceState ) {
        setContentView( R.layout.about );
        super.onCreate( savedInstanceState );

        backButton = ( Button ) findViewById( R.id.back_button );
        backButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view ) {
                Tracker.track(context, "about/back");
                finish();
            }
        } );

        nextButton = ( Button ) findViewById( R.id.next_button );
        nextButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view ) {
                Tracker.track(context, "about/next");
                finish();
                startNextActivity();
            }
        } );
    }

    private void startNextActivity() {
        if ( CommonUtils.isDataConnectionAvailable( this ) ) {
            if ( CommonUtils.packageWaiting( context ) ) {
                startActivity( CommonUtils.getLoginActivityIntent( context ) );
            } else {
                startActivity( CommonUtils.getDisplayCodeActivityIntent( context ) );
            }
        } else {
            startActivity( CommonUtils.getCheckConnectionActivityIntent( context ) );
        }
    }
}