package com.dashwire.config.configuration;

import android.content.Context;

public class ConfigurationItem {
    
    public static final int UNCHECKED = 0;
    public static final int CHECKED = 1;
    public static final int FAILED = 2;

    private final String mLabel;
    private int mState;
    @SuppressWarnings("unused")
    private Context context;
    private String configDetails;
    
    private final String mName;

    public ConfigurationItem( Context context, String label, String name ) {
        this.context = context;
        this.mName = name;
        this.mLabel = label;
        this.mState = UNCHECKED;
    }

    public String getName() {
    	return mName;
    }
    
    public String getLabel() {
        return mLabel;
    }

    public int getState() {
        return mState;
    }

    public void setState( int state ) {
        mState = state;
    }
    
    public void setConfigDetails( String configDetails) {
        this.configDetails = configDetails;
    }
    
    public String getConfigDetails() {
        return configDetails;
    }
}