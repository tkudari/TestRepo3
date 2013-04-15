package com.dashwire.config;

import android.app.Activity;
import android.content.*;

import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.util.CommonUtils;

public class EasyReceiver extends BroadcastReceiver
{

    protected static final String TAG = EasyReceiver.class.getCanonicalName();

    private Context mContext;

    public static boolean changeModeFlag;
    public static boolean setToEasyModeFlag;
    public static boolean setToStandardModeFlag;

    @Override
    public void onReceive( Context context, Intent intent )
    {
        mContext = context;

        DashLogger.v( TAG, "EasyReceiver onReceive Called." );

        DashLogger.v( TAG, "changeMode = " + changeModeFlag );
        DashLogger.v(TAG, "setToEasyMode = " + setToEasyModeFlag);
        DashLogger.v( TAG, "setToStandardMode = " + setToStandardModeFlag );

        if ( changeModeFlag )
        {
            if ( setToEasyModeFlag )
            {
                DashLogger.v( TAG, "Easy Mode and mode value is 1" );
                setPantechExperience( 1 );
            } else if ( setToStandardModeFlag )
            {
                DashLogger.v( TAG, "Standard Mode and mode value is 0" );
                setPantechExperience( 0 );
            }
        } else
        {
            DashLogger.v( TAG, "Keep current mode and mode value is 2" );
            setPantechExperience( 2 );
        }
        resetFlags();
    }

    public void setPantechExperience( final int modeValue )
    {
        Intent modeChangeIntent = new Intent( "com.pantech.intent.action.MODE_CHANGED_R2G" );

        modeChangeIntent.putExtra( "mode", modeValue );
        modeChangeIntent.putExtra( "from", 1 ); // 1 : intent from ready2go
        if ( CommonUtils.isFirstLaunch( mContext ) )
        {
            modeChangeIntent.putExtra( "when", 1 ); // 1 : on first boot, 2 :
                                                    // after boot
        } else
        {
            modeChangeIntent.putExtra( "when", 2 ); // 1 : on first boot, 2 :
                                                    // after boot
        }

        DashLogger.v( TAG, "EasyReceiver modeValue = " + modeValue );
        BroadcastReceiver resultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive( Context context, Intent intent )
            {

                Intent completedIntent = new Intent( "com.dashwire.config.EASY_FINISHED" );
                DashLogger.d( TAG, "Sending reply back to CompletedActivity from resultReceiver.." );
                context.sendBroadcast( completedIntent );
            }
        };

        DashLogger.d( TAG, "setEasyMode sending " + modeChangeIntent );
        mContext.sendOrderedBroadcast( modeChangeIntent, null, resultReceiver, null, Activity.RESULT_OK, null, null );
        DashLogger.d( TAG, "setEasyMode sent " + modeChangeIntent );
    }
    
    private void resetFlags()
    {
        changeModeFlag = false;
        setToEasyModeFlag = false;
        setToStandardModeFlag = false;
    }
}
