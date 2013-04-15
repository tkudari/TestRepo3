package com.dashwire.config.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

public class OverrideBackKeyLayout extends RelativeLayout {

    private static Activity mUIActivity;;

    public OverrideBackKeyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverrideBackKeyLayout(Context context) {
        super(context);
    }

    public static void setUIActivity(Activity uiActivity) {
        mUIActivity = uiActivity;
    }

    /**
     * Overrides the handling of the back key to move back to the 
     * previous sources
     */
    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        
        if (mUIActivity != null && 
                    event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            
            KeyEvent.DispatcherState state = getKeyDispatcherState();
            
            if (state != null) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getRepeatCount() == 0) {
                    state.startTracking(event, this);
                    return true;
                } else if (event.getAction() == KeyEvent.ACTION_UP
                        && !event.isCanceled() && state.isTracking(event)) {
                    mUIActivity.onBackPressed();
                    return true;
                }
            }
        }

        return super.dispatchKeyEventPreIme(event);
    }
}