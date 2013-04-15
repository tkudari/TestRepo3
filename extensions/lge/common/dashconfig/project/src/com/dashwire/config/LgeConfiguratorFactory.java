package com.dashwire.config;

import com.dashwire.config.configuration.ConfiguratorFactory;
import com.dashwire.config.configuration.DefaultConfiguratorFactory;
import com.dashwire.config.configuration.ResourceConfigurator;
import com.dashwire.config.resources.LgeShortcutConfigurator;

public class LgeConfiguratorFactory implements ConfiguratorFactory {
	private LgeConfiguratorFactory() { }
	
	private static LgeConfiguratorFactory singleton = null;
	
	public static synchronized ConfiguratorFactory getInstance() {
		if(singleton == null) singleton = new LgeConfiguratorFactory();
		return singleton;
	}
	
	@Override
	public ResourceConfigurator getResourceConfigurator(String nodeName)
			throws IllegalArgumentException {
		if("shortcuts".equals(nodeName)) {
				return new LgeShortcutConfigurator();                   
			} 
		else {
			return DefaultConfiguratorFactory.getInstance().getResourceConfigurator(nodeName);
		}
	}


}
