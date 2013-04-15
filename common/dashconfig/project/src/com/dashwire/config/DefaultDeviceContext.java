package com.dashwire.config;


import android.content.Context;

import com.dashwire.config.configuration.ConfiguratorFactory;
import com.dashwire.config.configuration.DefaultConfiguratorFactory;

public class DefaultDeviceContext extends DeviceContext {
	static boolean isCompatible() {
		return false;
	}
	@Override
	public ConfiguratorFactory getConfiguratorFactory() {
		return DefaultConfiguratorFactory.getInstance();
	}
	@Override
    public boolean sendNotification(Context context, Notification notification) {
        return true;
    }
	
	@Override
	public boolean isBlackListedDevice() {
		return false;
	}
	
	@Override
	public long getCustomDefinedTextSize(Context context) {
		return -1;
	}
	
    
}
