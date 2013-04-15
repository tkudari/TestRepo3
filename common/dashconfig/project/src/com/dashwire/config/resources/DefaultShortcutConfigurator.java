package com.dashwire.config.resources;

import com.dashwire.base.debug.DashLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ResourceConfigurator;

public class DefaultShortcutConfigurator implements ResourceConfigurator {

    public static final String TAG = DefaultShortcutConfigurator.class.getCanonicalName();

    protected Context mContext;

    protected ConfigurationEvent mConfigurationEvent;

    protected JSONArray mConfigArray;
    
    protected Bundle mExtras;

    public DefaultShortcutConfigurator() {

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
    
    public void addExtras(Bundle extras) {
    	mExtras = extras;
    }

    public void configure() {
		try {
			createWidgetsAndShortcuts(mConfigArray);
		} catch (JSONException e) {
			mConfigurationEvent.notifyEvent( name(), ConfigurationEvent.FAILED );
			return;
		}
		
		if (mConfigArray == null || mConfigArray.length() == 0) {
			if(mExtras == null) {
				mConfigurationEvent.notifyEvent( name(), ConfigurationEvent.CHECKED );
			}	
		} 
    }
    
    private void createWidgetsAndShortcuts(JSONArray items ) throws JSONException {
        JSONArray outputItems = new JSONArray();
        if(items != null) {
        	for (int index = 0; index < items.length(); index++) {
    			JSONObject item;
    			try {
    				item = toExternalJson(items.getJSONObject(index));
    			} catch (Exception e) {
    			    DashLogger.i(TAG, "Bad data");
    				e.printStackTrace();//TODO: bad data
    				continue;
    			}
    			outputItems.put(item);
    		}        	
        }
	
		final Intent createWidgets = new Intent(
				"com.dashwire.homescreen.CONFIGURE_HOMESCREEN");
		createWidgets.putExtra("version", "1.0");
		createWidgets.putExtra("homescreen", outputItems.toString());
		if(mExtras != null) {
			createWidgets.putExtras(mExtras);
		}
		mContext.sendBroadcast(createWidgets);
    }
    
    protected JSONObject toExternalJson(JSONObject item) throws JSONException {
        JSONObject object = new JSONObject();
    	if(!item.has("id")) {
    		throw new JSONException("No id specified");
    	}
    	
        String[] id = item.getString("id").split("[/]");

        if(id.length != 2) {
        	throw new JSONException("Invalid id");
        }
        int semiColonIndex = id[1].indexOf(';');
        String extras;
        if (semiColonIndex != -1) {
            extras = id[1].substring(semiColonIndex);
            id[1] = id[1].substring(0, semiColonIndex);
        }
        else
            extras = null;
        DashLogger.d(TAG, "Extra value: " + extras);

        object.put("type", "shortcuts".equalsIgnoreCase(item
				.getString("category")) ? "shortcut" : "widget");
		if(item.has("type")) {
			object.put("oemType", item.getString("type"));
		}
        object.put( "title", item.getString("title") );
        object.put( "packageName", id[0] );
        object.put( "className", id[1] );
        if (extras != null)
            object.put("extras", extras);

        if (item.has("container_id") && item.getInt("container_id") == -101) {
            DashLogger.d(TAG, "container_id value: " + item.getString("container_id"));
            object.put( "container", item.getString("container_id"));
        }
        else {
            object.put( "container", "desktop");
        }
        object.put( "screen", item.getInt("screen") );
        if (item.has("x"))
            object.put( "x", item.getInt("x") );
        if (item.has("y"))
            object.put( "y", item.getInt("y") );
        object.put( "rows", item.getInt("rows") );
        object.put( "cols", item.getInt("cols") );
        if(item.has("uri")) {
        	object.put("uri", item.getString("uri"));
        }
        
    	return object;
    }
}
