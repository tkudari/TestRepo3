package com.dashwire.config.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dashwire.config.R;
import com.dashwire.config.util.CommonUtils;

public class GenericErrorActivity extends BaseActivity {
    
    private boolean continueButtonFlag = false;
    
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        setContentView( R.layout.generic_error );
        super.onCreate( savedInstanceState );
        
        Intent intent = getIntent();
        
        customizeTextFields( intent );

        Button cancelButton = ( Button ) findViewById( R.id.cancel_button );
        cancelButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view ) {
                cancel();
            }
        } );

        Button tryAgainButton = ( Button ) findViewById( R.id.try_again_button );
        tryAgainButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view ) {
                retry();
            }
        } );

        continueButtonFlag = ('T' == getIntent().getCharExtra( "ContinueButton", 'F' ))?true:false;
        if (continueButtonFlag)
        {
            tryAgainButton.setText( context.getResources().getString( R.string.button_continue ) );
        }
    }

    private void customizeTextFields( Intent intent ) {
        
        if ( intent != null ) {
            setTextViewText( R.id.title, intent.getStringExtra( "title" ) );
            setTextViewText( R.id.body, intent.getStringExtra( "body" ) );
            setButtonText( R.id.cancel_button, intent.getStringExtra( "cancel_text" ) );
            setButtonText( R.id.try_again_button, intent.getStringExtra( "retry_text" ) );
            if (!intent.getBooleanExtra( "QuitButton", true ))
            {
                View topBar = (View) findViewById(R.id.error_top_bar);
                topBar.setVisibility( View.GONE );
            }
        }
    }

    private void setButtonText( int id, String text ) {
        if ( text != null ) {
            Button button = ( Button ) findViewById( id );
            if ( button != null ) {
                button.setText( text );
            }
        }
    }

    private void setTextViewText( int id, String text ) {
        if ( text != null ) {
            TextView textView = ( TextView ) findViewById( id );
            if ( textView != null ) {
                textView.setText( text );
            }
        }
    }

    private void retry() {
        CommonUtils.startActivityFromUri( getIntent().getStringExtra( "retry" ), context );
        finish();
    }

    private void cancel() {
        String cancelUri = getIntent().getStringExtra( "cancel" );
        if ( cancelUri != null && cancelUri.length() > 0 ) {
            CommonUtils.startActivityFromUri( cancelUri, context );
        }
        CommonUtils.setCanceledFlag( true, context );
        CommonUtils.broadcastDashconfigCanceled( getApplicationContext() );
        finish();
    }

}
