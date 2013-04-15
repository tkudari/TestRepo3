package com.pantech.modetestapp;

import com.pantech.modetestapp.homeswitch.HomeSwitch;
import android.app.Activity;
import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.View;
import android.widget.Button;

public class ModeTestAppActivity extends Activity {
    
    private boolean mIsEasyMode;
    
    private Button mEasyButton;
    private Button mStandardButton;    

    private static final String TAG = "ModeTestAppActivity";
    private static final float LARGE_FONT = 1.15f;    
    private final Configuration mCurrentConfig = new Configuration();
    private Context mContext;    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mContext = this;        
        
        mEasyButton = (Button)findViewById(R.id.easy_button);
        mEasyButton.setOnClickListener(new View.OnClickListener() {            
            public void onClick(View v) {
                // TODO Auto-generated method stub                
                mIsEasyMode = true;               

                //set the value
                SystemProperties.set("sys.mode_simple", mIsEasyMode ? "1" : "0");
                Settings.System.putInt(getContentResolver(),Settings.System.DEVICE_SCREEN_MODE, mIsEasyMode ? 1 : 0);
                                        
                //set the font size
                setFontSizeLarge(mIsEasyMode);

                //go to home screen so that other apps will be paused. 
                HomeSwitch.switchPantechHome(mContext, mIsEasyMode);
                
                //send broadcast intent
                Intent modeChangeIntent = new Intent(Intent.ACTION_MODE_CHANGED);
                modeChangeIntent.putExtra("mode", mIsEasyMode ? 1 : 0);
                sendBroadcast(modeChangeIntent);
            }
        });
                
        
        mStandardButton = (Button)findViewById(R.id.standard_button);       
        mStandardButton.setOnClickListener(new View.OnClickListener() {            
            public void onClick(View v) {
                // TODO Auto-generated method stub                
                // TODO Auto-generated method stub                
                mIsEasyMode = false;               

                //set the setting value
                SystemProperties.set("sys.mode_simple", mIsEasyMode ? "1" : "0");
                Settings.System.putInt(getContentResolver(),Settings.System.DEVICE_SCREEN_MODE, mIsEasyMode ? 1 : 0);
                                        
                //set the font size
                setFontSizeLarge(mIsEasyMode);

                //go to home screen so that other apps will be paused. 
                HomeSwitch.switchPantechHome(mContext, mIsEasyMode);
                
                //send broadcast intent
                Intent modeChangeIntent = new Intent(Intent.ACTION_MODE_CHANGED);
                modeChangeIntent.putExtra("mode", mIsEasyMode ? 1 : 0);
                sendBroadcast(modeChangeIntent);
                
            }
        });
    
    }
    

    private void setFontSizeLarge(boolean modechange) {
        try {
            mCurrentConfig.fontScale = modechange ? LARGE_FONT : 1;
            ActivityManagerNative.getDefault().updatePersistentConfiguration(mCurrentConfig);
        } catch (RemoteException e) {

        }
    }
    
    private boolean getModeState()
    {        
        try {
            int mode = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.DEVICE_SCREEN_MODE);
            boolean isEasyMode = mode == 1 ? true : false;
            return isEasyMode;
        } catch (SettingNotFoundException e) {
        }
        
        return false;
    }
    
}