package com.dashwire.config;

import com.dashwire.config.DefaultDeviceContext;
import com.dashwire.config.configuration.ConfiguratorFactory;

public class MotorolaDeviceContext extends DefaultDeviceContext {
	@Override
	public ConfiguratorFactory getConfiguratorFactory() {
		return MotorolaConfiguratorFactory.getInstance();
	}
	
	static public Object emailConfigMutex = new Object();
}
