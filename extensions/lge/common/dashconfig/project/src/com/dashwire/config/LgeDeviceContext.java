package com.dashwire.config;

import com.dashwire.config.DefaultDeviceContext;
import com.dashwire.config.configuration.ConfiguratorFactory;

public class LgeDeviceContext extends DefaultDeviceContext {
	@Override
	public ConfiguratorFactory getConfiguratorFactory() {
		return LgeConfiguratorFactory.getInstance();
	}
}
