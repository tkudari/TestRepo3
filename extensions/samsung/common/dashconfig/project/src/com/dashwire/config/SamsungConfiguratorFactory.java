package com.dashwire.config;

import com.dashwire.config.configuration.ConfiguratorFactory;
import com.dashwire.config.configuration.DefaultConfiguratorFactory;
import com.dashwire.config.configuration.ResourceConfigurator;

public class SamsungConfiguratorFactory implements ConfiguratorFactory {
	private SamsungConfiguratorFactory() {
	}

	private static SamsungConfiguratorFactory singleton = null;

	public static synchronized SamsungConfiguratorFactory getInstance() {
		if (singleton == null)
			singleton = new SamsungConfiguratorFactory();
		return singleton;
	}

	@Override
	public ResourceConfigurator getResourceConfigurator(String nodeName)
			throws IllegalArgumentException {
		return DefaultConfiguratorFactory.getInstance()
				.getResourceConfigurator(nodeName);
	}
}
