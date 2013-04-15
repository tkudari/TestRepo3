package com.dashwire.config;

import com.dashwire.config.configuration.ConfiguratorFactory;
import com.dashwire.config.configuration.DefaultConfiguratorFactory;
import com.dashwire.config.configuration.ResourceConfigurator;
import com.dashwire.config.resources.EasyConfigurator;
import com.dashwire.config.resources.PantechShortcutConfigurator;

public class PantechConfiguratorFactory implements ConfiguratorFactory {
	private PantechConfiguratorFactory() { }
	
	private static PantechConfiguratorFactory singleton = null;
	
	public static synchronized ConfiguratorFactory getInstance() {
		if(singleton == null) singleton = new PantechConfiguratorFactory();
		return singleton;
	}
	
	@Override
	public ResourceConfigurator getResourceConfigurator(String nodeName)
			throws IllegalArgumentException {
		if("easy".equals(nodeName)) {
				return new EasyConfigurator();                   
		} 
		else if("shortcuts".equals(nodeName)) {
			return new PantechShortcutConfigurator();                   
		} 
		else {
			return DefaultConfiguratorFactory.getInstance().getResourceConfigurator(nodeName);
		}
	}


}
