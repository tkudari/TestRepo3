package com.dashwire.config.resources;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.configuration.ConfigurationEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RAConfiguratorResultReceiver extends BroadcastReceiver {

    private static final String TAG = RAConfiguratorResultReceiver.class.getCanonicalName();
    
    private ConfigurationEvent mConfigurationEvent;
    
    private static Map<String, String> sNodeFeatureMapping;
    
    static
    {
    	sNodeFeatureMapping = new HashMap<String, String>();
    	sNodeFeatureMapping.put( "EasyMode", "easy" );
    	sNodeFeatureMapping.put( "Accounts", "accounts" );
    	sNodeFeatureMapping.put( "Emails", "emails" );
    	sNodeFeatureMapping.put( "Contacts", "contacts" );
     	sNodeFeatureMapping.put( "Wallpaper", "wallpapers" );
    	sNodeFeatureMapping.put( "Bookmarks", "bookmarks");
    	sNodeFeatureMapping.put( "Shortcuts", "shortcuts" );
    	sNodeFeatureMapping.put( "Ringtone", "ringtones" );
    	sNodeFeatureMapping.put( "Wifi", "networks" );
    	sNodeFeatureMapping.put( "Social", "social");
    	sNodeFeatureMapping.put( "ExchangeAccounts", "exchange_accounts" );
    	sNodeFeatureMapping = Collections.unmodifiableMap( sNodeFeatureMapping );
    }

    @Override
    public final void onReceive(final Context context, Intent intent) 
    {   	
    	Log.v(TAG,"onReceive of RAConfiguratorResultReceiver : " + intent.getAction());
    	mConfigurationEvent = new ConfigurationEvent() {
    		@Override
    		public void notifyEvent(String name, int code) {
    			notifyEvent(name, code, null);
    		}

    		@Override
    		public void notifyEvent(String name, int code, Throwable e) {
    			if (equals(e != null)) {
    				e.printStackTrace();
    			}
    			Intent i = new Intent(ACTION_CONFIGURATION);
    			i.setData(Uri.parse("dashconfig://?name=" + name + "&code="
    					+ code));
    			context.sendBroadcast(i, "com.dashwire.config.PERM_CONFIG");
    		}
    	};
    	
        String action = intent.getAction();
        if (action.equals("com.dashwire.feature.intent.CONFIGURATION_RESULT")) 
        {
         	String featureName = intent.getStringExtra("featureName");
        	String featureResult = intent.getStringExtra("result");
        	
        	Log.v(TAG,"RAConfiguratorResultReceiver onReceive : ");
        	Log.v(TAG,"RAConfiguratorResultReceiver featureName : " + featureName);
        	Log.v(TAG,"RAConfiguratorResultReceiver result : " + featureResult);
        	
        	if (featureName != null && featureResult != null)
        	{
        		if (featureResult.equalsIgnoreCase("success"))
        		{
        			Log.v(TAG,"Notifying checked event");
        			Log.v(TAG,"Feature Node = " + getFeatureNode(featureName));
        			mConfigurationEvent.notifyEvent(getFeatureNode(featureName), ConfigurationEvent.CHECKED);
        			
        		}else if (featureResult.equalsIgnoreCase("failed"))
        		{
        			Log.v(TAG,"Notifying failed event");
        			mConfigurationEvent.notifyEvent(getFeatureNode(featureName), ConfigurationEvent.FAILED);
        		}
        	} else
        	{
        		DashLogger.e(TAG,"Data missing");
        	}
        }
    }
    
    private String getFeatureNode(String featureName)
    {
    	return sNodeFeatureMapping.get(featureName);
    }
}
