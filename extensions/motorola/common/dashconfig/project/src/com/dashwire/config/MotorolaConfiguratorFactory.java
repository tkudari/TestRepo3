package com.dashwire.config;

import com.dashwire.config.configuration.ConfiguratorFactory;
import com.dashwire.config.configuration.DefaultConfiguratorFactory;
import com.dashwire.config.configuration.ResourceConfigurator;
import com.dashwire.config.resources.MotorolaShortcutConfigurator;

public class MotorolaConfiguratorFactory implements ConfiguratorFactory {
	private MotorolaConfiguratorFactory() { }
	
	private static MotorolaConfiguratorFactory singleton = null;
	
	public static synchronized ConfiguratorFactory getInstance() {
		if(singleton == null) singleton = new MotorolaConfiguratorFactory();
		return singleton;
	}
	
	@Override
	public ResourceConfigurator getResourceConfigurator(String nodeName)
			throws IllegalArgumentException {
		if("shortcuts".equals(nodeName)) {
				return new MotorolaShortcutConfigurator();                   
			} 
		else {
			return DefaultConfiguratorFactory.getInstance().getResourceConfigurator(nodeName);
		}
	}


}
