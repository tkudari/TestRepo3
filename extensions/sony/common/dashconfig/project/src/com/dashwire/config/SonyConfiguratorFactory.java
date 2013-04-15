package com.dashwire.config;

import com.dashwire.config.configuration.ConfiguratorFactory;
import com.dashwire.config.configuration.DefaultConfiguratorFactory;
import com.dashwire.config.configuration.ResourceConfigurator;

public class SonyConfiguratorFactory implements ConfiguratorFactory {
	private SonyConfiguratorFactory() {
	}

	private static SonyConfiguratorFactory singleton = null;

	public static synchronized ConfiguratorFactory getInstance() {
		if (singleton == null)
			singleton = new SonyConfiguratorFactory();
		return singleton;
	}

	@Override
	public ResourceConfigurator getResourceConfigurator(String nodeName)
			throws IllegalArgumentException {
		return DefaultConfiguratorFactory.getInstance()
				.getResourceConfigurator(nodeName);
	}
}
