package com.dashwire.config;

import android.content.Context;
import android.provider.Settings.SettingNotFoundException;

import com.dashwire.config.configuration.ConfiguratorFactory;
import com.dashwire.base.debug.DashLogger;

public class PantechDeviceContext extends DefaultDeviceContext {
	@Override
	public ConfiguratorFactory getConfiguratorFactory() {
		return PantechConfiguratorFactory.getInstance();
	}
	
	static public Object emailConfigMutex = new Object();
    private static final String DEVICE_SCREEN_MODE = "device_screen_mode";
    private static final int CUSTOM_FONT_SIZE = 15;
    private static final String TAG = PantechDeviceContext.class.getCanonicalName();

	
	@Override
	public long getCustomDefinedTextSize(Context context) {
		// This is to fix the problem in EZ mode where the text would overflow from a TextView
		int mode = 0; //mode = 1 => EZ mode; 0 => standard mode
		
		 try {
				mode = android.provider.Settings.System.getInt(context.getContentResolver(), DEVICE_SCREEN_MODE);
				DashLogger.d(TAG, "Easymode setting = " + mode);
			} catch (SettingNotFoundException e) {
				DashLogger.d(TAG, "Easymode setting not found??");
				e.printStackTrace();
			}
		 if (mode == 1) {
			 return CUSTOM_FONT_SIZE;
		 } else {
			 return -1;
		 }
	}	
	
	
}
