package com.dashwire.config.resources;

import com.dashwire.base.debug.DashLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ResourceConfigurator;
import com.dashwire.config.util.CommonConstants;

public class SocialNetworkConfigurator implements ResourceConfigurator {

    private long mMaxId = 65535L;

    protected Context mContext;

    protected ConfigurationEvent mConfigurationEvent;

    protected ContentResolver mContentResolver;

    protected JSONArray mConfigArray;
    
    protected static final String TAG = SocialNetworkConfigurator.class.getCanonicalName();
    protected Intent socialHubListenerIntent = new Intent();

    public SocialNetworkConfigurator( ) {
    }

    public void setContext( Context context ) {
        mContext = context;
    }

    public void setConfigurationEvent( ConfigurationEvent configurationEvent ) {
        mConfigurationEvent = configurationEvent;
    }

    public void setConfigDetails( JSONArray configArray ) {
        mConfigArray = configArray;
    }


    public String name() {
        return "social";
    }

    public void configure() {
        mContentResolver = mContext.getContentResolver();
        if ( mConfigArray != null && mConfigArray.length() > 0 ) {
            set( mConfigArray );
        } else {
            DashLogger.v( TAG, "Did not find any items for social networks" );
        }
    }
    
    public void set( JSONArray items ) {
        try {
            for ( int index = 0; index < items.length(); index++ ) {
                JSONObject item = items.getJSONObject( index );
                linkSocialNetworks( item );
                mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.CHECKED);
            }
        } catch ( JSONException je ) {
            DashLogger.v( TAG, "Exception on set() : " + je.getMessage() );

            mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.FAILED);
        }
    
    }

    public void linkSocialNetworks( JSONObject item ) throws JSONException {

        String type = item.has( "type" ) ? item.getString( "type" ) : "";
        String login = item.has( "login" ) ? item.getString( "login" ) : "";
        String password = item.has( "password" ) ? item.getString( "password" ) : "";

        Intent result = new Intent();
        result.setClassName( "com.dashwire.config", "com.dashwire.config.services.SocialHubRequestService" );
        result.putExtra( CommonConstants.SNS_TYPE, type );
        result.putExtra( CommonConstants.SNS_USERNAME, login );
        result.putExtra( CommonConstants.SNS_PASSWORD, password );
        DashLogger.d(TAG, "Service: startService called for com.dashwire.config.services.SocialHubRequestService");
        
        mContext.startService( result );
    }

    public long generateNewId() {
        if ( this.mMaxId < 0L )
            throw new RuntimeException( "Error: max id was not initialized" );
        long l = this.mMaxId + 1L;
        this.mMaxId = l;
        return this.mMaxId;
    }
}
