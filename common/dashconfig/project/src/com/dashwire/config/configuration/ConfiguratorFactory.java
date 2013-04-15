package com.dashwire.config.configuration;


public interface ConfiguratorFactory {
	public ResourceConfigurator getResourceConfigurator(String nodeName) throws IllegalArgumentException;
}
