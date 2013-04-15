package com.dashwire.config.resources;

import com.dashwire.base.debug.DashLogger;
import org.json.JSONArray;

import android.content.ContentResolver;
import android.content.Context;

import com.dashwire.config.EasyReceiver;
import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ResourceConfigurator;

public class EasyConfigurator implements ResourceConfigurator {
    protected static final String TAG = EasyConfigurator.class.getCanonicalName();
    
	protected JSONArray mItems;

	protected Context mContext;

	protected ConfigurationEvent mConfigurationEvent;
	
	protected ContentResolver mContentResolver;
	
	protected JSONArray mConfigArray;
	  
	   
    @Override
    public void setContext( Context context ) {
        mContext = context;
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void setConfigurationEvent( ConfigurationEvent configurationEvent ) {
       mConfigurationEvent = configurationEvent;
        
    }

    @Override
    public void setConfigDetails( JSONArray configArray ) {
        mConfigArray = configArray;
        
    }

	@Override
	public void configure() {

		DashLogger.v("EasyModeLog", "Trying to change to Easy Mode");
		
		EasyReceiver.changeModeFlag = true;
		EasyReceiver.setToEasyModeFlag = true;
		EasyReceiver.setToStandardModeFlag = false;
		
		mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.CHECKED);
	}

	@Override
	public String name() {
		return "easy";
	}
}
