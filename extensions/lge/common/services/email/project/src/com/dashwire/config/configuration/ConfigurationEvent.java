package com.dashwire.config.configuration;

public interface ConfigurationEvent {
	
    static final int UNCHECKED = 0;
    
    static final int CHECKED = 1;
    
    static final int FAILED = 2;
       
    static final String ACTION_CONFIGURATION = "com.dashwire.config.ACTION_CONFIGURATION";
	
	void notifyEvent(String name, int code);
	
	public void notifyEvent(String name, int code, Throwable e);
}
