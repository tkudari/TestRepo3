package com.dashwire.config.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dashwire.config.R;
import com.dashwire.config.util.CommonUtils;

public class CheckConnNeedHelpActivity extends BaseActivity {

    private Button cancelButton;
    private Button tryAgainButton;

    public void onCreate( Bundle savedInstanceState ) {
        setContentView( R.layout.check_conn_need_help);
        super.onCreate( savedInstanceState );

        cancelButton = ( Button ) findViewById( R.id.cancel_button );
        cancelButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view ) {
                CommonUtils.setCanceledFlag( true, context );
                CommonUtils.broadcastDashconfigCanceled( context );
                finish();
            }
        } );

        tryAgainButton = ( Button ) findViewById( R.id.try_again_button );
        tryAgainButton.setOnClickListener( new OnClickListener() {
            public void onClick( View view ) {
                finish();
                startActivity( CommonUtils.getCheckConnectionActivityIntent( context ) );
            }
        } );
    }
}