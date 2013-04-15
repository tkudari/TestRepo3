package com.dashwire.config;

import com.dashwire.config.DefaultDeviceContext;
import com.dashwire.config.configuration.ConfiguratorFactory;

public class HtcDeviceContext extends DefaultDeviceContext {
	static boolean isCompatible() {
		//TODO: Add a real check here
		return true;
	}
	@Override
	public ConfiguratorFactory getConfiguratorFactory() {
		return HtcConfiguratorFactory.getInstance();
	}
}
