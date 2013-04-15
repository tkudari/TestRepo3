package com.dashwire.config.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.TextView;
import com.dashwire.config.R;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.util.CommonConstants;

public class DifferentPhoneConfirmActivity extends BaseActivity {

    private static final String TAG = DifferentPhoneConfirmActivity.class.getCanonicalName();

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        setContentView( R.layout.diff_phone_confirm );
        super.onCreate( savedInstanceState );

        TextView body = (TextView) findViewById( R.id.body );
        String messageBodyFormat = getResources().getString( R.string.diff_phone_confirm_body );
        String messageBodyText = String.format(messageBodyFormat, Build.MODEL);
        body.setText(messageBodyText);

        Button cancelButton = ( Button ) findViewById( R.id.cancel_button );
        cancelButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view ) {
                cancel();
            }
        } );
    }

    private void cancel() {
        setResult( RESULT_CANCELED );
        finish();
    }

    protected void addQuitButton( View quitButton ) {
        if ( quitButton != null ) {
            quitButton.setOnClickListener( new OnClickListener() {
                public void onClick( View view ) {
                    Intent intent = new Intent();
                    intent.putExtra( CommonConstants.OVERRIDE_DEVICE_FLAG, false );
                    intent.putExtra( CommonConstants.QUIT_FLAG, true );
                    setResult( RESULT_CANCELED, intent );
                    finish();
                }
            } );
        } else {
            DashLogger.v( TAG, "null quit button" );
        }
    }

}
