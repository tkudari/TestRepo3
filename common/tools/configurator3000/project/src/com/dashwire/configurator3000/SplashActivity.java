package com.dashwire.configurator3000;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

public class SplashActivity extends Activity implements OnClickListener {

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.splash );
        View view = ( View ) findViewById( R.id.splash_view );
        view.setOnClickListener( this );

        Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            public void run() {
                start();
            }
        }, 600 );
    }

    protected void start() {
        startActivity( new Intent( this, SettingsActivity.class ) );
        finish();
    }

    public void onClick( View v ) {
        start();
    }

}