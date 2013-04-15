package com.dashwire.config.configuration;

import org.json.JSONArray;

import android.content.Context;

public interface ResourceConfigurator {

    String JSON_ARRAY = null;

    void setContext( Context context );
    void setConfigurationEvent( ConfigurationEvent configurationEvent );
    void setConfigDetails( JSONArray configArray);
    void configure();
    
    String name();
}
