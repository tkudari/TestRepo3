package com.dashwire.config.resources;

import com.dashwire.base.debug.DashLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import com.dashwire.config.EasyReceiver;
import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ResourceConfigurator;
import com.dashwire.config.util.CommonUtils;

public class PantechShortcutConfigurator implements ResourceConfigurator {

    public static final String TAG = PantechShortcutConfigurator.class.getCanonicalName();
 
    protected Context mContext;

    protected ConfigurationEvent mConfigurationEvent;

    protected ContentResolver mContentResolver;

    protected JSONArray mConfigArray;

    public PantechShortcutConfigurator() {
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
        return "shortcuts";
    }
    
    public void configure() {
    	Exception eForNotify = null;
    	int iForNotify = ConfigurationEvent.CHECKED; 		
		
		DashLogger.v("EasyModeLog","Trying to change to Standard Mode");
		
        EasyReceiver.changeModeFlag = true;
        EasyReceiver.setToEasyModeFlag = false;
        EasyReceiver.setToStandardModeFlag = true;
				
		//create API JSON from config
		if(mConfigArray != null && mConfigArray.length() >0) {
					
			String configForAPI = "{\"homescreen\":[";
			for(int i = 0; i < mConfigArray.length(); i++) {	
				try {
					
					JSONObject item = mConfigArray.getJSONObject(i);
					
					String packageName = CommonUtils.extractPackage( item.getString("id") );
		            String providerName = CommonUtils.extractProvider(item.getString("id") );
		            
		            int containerId = -100;
		            if ( item.has( "container_id" ) ) {
		                containerId = item.getInt( "container_id" );
		            }
		            
		            String category = item.getString("category");
		            if(category.equalsIgnoreCase("widgets")) {
		            	category = "widget";
		            } else if(category.equalsIgnoreCase("shortcuts")) {
		            	category = "shortcut";
		            } else {
		            	DashLogger.d(TAG,"Invalid class for widget: " + category);
		            	category = null;
		            }
		            if(category != null) {
					           
						String itemConfigString = "{" +
										"\"type\": \"" + category + "\"" +
										",\"title\": \"" + item.getString("title") + "\"" +
										",\"packageName\": \"" + packageName + "\"" +
										",\"className\": \"" + providerName + "\"" +
										",\"container\": " + containerId +
										",\"screen\": " + item.getInt("screen") +
										",\"x\": " + item.getInt("x") +
										",\"y\": " + item.getInt("y") +
										",\"rows\": " + item.getInt("rows") +
										",\"cols\": " + item.getInt("cols") + "}";
						if(i > 0) configForAPI += ",";
						configForAPI += itemConfigString;
		            }
				} catch (JSONException jse) {
					DashLogger.d(TAG, "Invalid widget configuration: " + jse.getMessage());
				}
			}	
			configForAPI += "]}";	
			
			Intent homescreenIntent = new Intent();
			homescreenIntent.setAction("com.dashwire.homescreen.CONFIGURE_HOMESCREEN");
			homescreenIntent.putExtra("version", "1.0"); 
		    homescreenIntent.putExtra("homescreen", configForAPI);
		    DashLogger.d(TAG,"homescreen json blob = " + configForAPI);
		    mContext.sendBroadcast( homescreenIntent,"com.dashwire.homescreen.permission.CONFIGURE_HOMESCREEN" );
			
		} else {
			DashLogger.d(TAG,"Empty array in Shortcut configuration");
			
			mConfigurationEvent.notifyEvent( name(), iForNotify, eForNotify);
		}
    }
}
