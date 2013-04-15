package com.dashwire.config.resources;

import android.content.ContentResolver;
import android.content.Context;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ResourceConfigurator;
import com.dashwire.config.util.CommonUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MotorolaShortcutConfigurator implements ResourceConfigurator {

    public static final String TAG = MotorolaShortcutConfigurator.class.getCanonicalName();

    private long mMaxId = 65535L;

    protected Context mContext;

    protected ConfigurationEvent mConfigurationEvent;

    protected ContentResolver mContentResolver;

    protected JSONArray mConfigArray;

    public MotorolaShortcutConfigurator() {
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
    	try
    	{
	        mContentResolver = mContext.getContentResolver();
	        if ( mConfigArray != null && mConfigArray.length() > 0 ) {
	            processWidgetsAndShortcuts( mConfigArray );
	        } else {
	            DashLogger.v( TAG, "Did not find any items for shortcuts or Widgets" );
	        }
    	}
    	catch(Exception e)
    	{
    		DashLogger.e(TAG, e.getMessage());
    		eForNotify = e;
    		iForNotify = ConfigurationEvent.FAILED;
    	}
    	finally
    	{
    		mConfigurationEvent.notifyEvent( name(), iForNotify, eForNotify );
    	}
    }

    private void processWidgetsAndShortcuts( JSONArray items ) {
        DashLogger.v(TAG, "processWidgetsAndShortcuts starts");
        
        //don't kill launcher app, because it will require 
        //CommonUtils.killLauncherApp( mContext );
        //truncateFavoritesDB();
        
        ArrayList<JSONObject> widgetsAndShortcutsList = extractWidgetsAndShortcuts( items );
        if ( widgetsAndShortcutsList.size() > 0 ) { 
            Widget widgetsAndShortcuts = new Widget( mContext, widgetsAndShortcutsList );
            widgetsAndShortcuts.createWidgetsAndShortcuts();
            if ( widgetsAndShortcuts.isAnyFailed()) {
                mConfigurationEvent.notifyEvent( name(), ConfigurationEvent.FAILED );
                DashLogger.v( TAG, "Failed Items = " + widgetsAndShortcuts.getFailedItems().toString());
            } else {
                mConfigurationEvent.notifyEvent( name(), ConfigurationEvent.CHECKED );
            }
        }
        DashLogger.v( TAG, "processWidgetsAndShortcuts end" );
    }

    private ArrayList<JSONObject> extractWidgetsAndShortcuts( JSONArray items ) {
        ArrayList<JSONObject> widgetsAndShortcutsList = new ArrayList<JSONObject>();

        try {
            for ( int index = 0; index < items.length(); index++ ) {
                JSONObject item = items.getJSONObject( index );
                if ( CommonUtils.isTouchWizWidget( item ) || CommonUtils.isAndroidWidget( item ) || CommonUtils.isShortcut( item ) ) {
                    widgetsAndShortcutsList.add( item );
                }
            }
        } catch ( JSONException je ) {

        }
        return widgetsAndShortcutsList;
    }

    public long generateNewId() {
        if ( this.mMaxId < 0L )
            throw new RuntimeException( "Error: max id was not initialized" );
        long l = this.mMaxId + 1L;
        this.mMaxId = l;
        return this.mMaxId;
    }
}
