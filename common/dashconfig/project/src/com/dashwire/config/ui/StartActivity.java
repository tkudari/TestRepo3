package com.dashwire.config.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dashwire.config.R;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.tracking.Tracker;
import com.dashwire.config.util.CommonConstants;
import com.dashwire.config.util.CommonUtils;

public class StartActivity extends BaseStartActivity {
    
	private static final String TAG = StartActivity.class.getCanonicalName();
	
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        Tracker.track(context, "StartActivity");
        
        Button cancelButton = ( Button ) findViewById( R.id.cancel_button );
        if ( cancelButton != null ) {
            cancelButton.setOnClickListener( new OnClickListener() {
                public void onClick( View view ) {
                    Tracker.track(context, "startActivity/cancel");
                    CommonUtils.setCanceledFlag( true, context );
                    processCanceledFlow();
                }
            } );
        }
        
        Button selectLangButton = ( Button ) findViewById( R.id.select_lang_button );
        selectLangButton.setVisibility( View.VISIBLE );
        if ( selectLangButton != null ) {
            selectLangButton.setOnClickListener( new OnClickListener() {
                public void onClick( View view ) {
                    Tracker.track(context, "startActivity/selectLanguage");
                    startActivity(CommonUtils.getLanguageSelectionActivityIntent( context ));
                }
            } );
        }
        //CommonUtils.broadcastStartSetupWizard(context);
    }

    void setLauncherFlag() {
    	DashLogger.d(TAG,  "Setting launcher flag from class " + TAG); 
        CommonUtils.setLaunchFromIndicator( CommonConstants.LAUNCH_FROM_OTHER_WIZARD_IND, context );
    }
}