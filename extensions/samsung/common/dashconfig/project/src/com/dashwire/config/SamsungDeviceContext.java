package com.dashwire.config;

import com.dashwire.config.DefaultDeviceContext;
import com.dashwire.config.configuration.ConfiguratorFactory;

public class SamsungDeviceContext extends DefaultDeviceContext {

	@Override
	public ConfiguratorFactory getConfiguratorFactory() {
		return SamsungConfiguratorFactory.getInstance();
	}
}
