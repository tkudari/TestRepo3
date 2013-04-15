package com.dashwire.config.resources;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.util.CommonConstants;
import com.dashwire.config.util.CommonUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Widget {

    public static final String TAG = Widget.class.getCanonicalName();
    private Context context;
    //IWidgetService widgetService;
    private ArrayList<JSONObject> widgetsAndShortcutsList;
    private long mMaxId = 65535L;
    protected ContentResolver mContentResolver;
    public JSONArray failedList = new JSONArray();

    private JSONArray motoList = new JSONArray();
    
    public Widget( Context context ) {
        this.context = context;
        ;
    }

    public Widget( Context context, ArrayList<JSONObject> widgetsAndShortcuts ) {
        DashLogger.d( TAG, "Widget()" );
        this.context = context;
        this.widgetsAndShortcutsList = widgetsAndShortcuts;
        this.mContentResolver = context.getContentResolver();
    }

    public int bindWidgetsOrShortcuts( JSONObject item ) {
        int result = CommonConstants.SUCCESS;
    	try {

    		result = createMotoWidgetOrShortcut(  item );

        } catch ( Exception e ) {
            DashLogger.e( TAG, "Exception in bindWidget " + e.toString() );
            return CommonConstants.FAILED;
        }
        return result;
    }

    protected void createWidgetsAndShortcuts() {

        for ( int index = 0; index < widgetsAndShortcutsList.size(); index++ ) {
            JSONObject item = widgetsAndShortcutsList.get( index );
            DashLogger.d(TAG, "item = " + item.toString());
            if(bindWidgetsOrShortcuts( item ) != CommonConstants.SUCCESS) {
            	DashLogger.d( TAG, "bindWidgetsOrShortcuts returned failure" );
            }       
        }
        
        //TODO make the moto intent call with json array
        Intent homescreenIntent = new Intent();
        homescreenIntent.setAction("com.android.launcher.action.CHANGE_DEFAULT_WORKSPACE");
        homescreenIntent.putExtra("extra_workspace_config", motoList.toString());
        context.sendBroadcast( homescreenIntent );  
        
        //notify any content listeners of the change(s)
        //Uri favoritesUri = Uri.parse( DeviceConstants.FAVORITES_URI );
        //mContentResolver.notifyChange(favoritesUri, null);

        synchronized ( this ) {
            DashLogger.d( TAG, "before notify" );
            notify();
            DashLogger.d( TAG, "after notify" );
        }
    }
    
    public int createMotoWidgetOrShortcut( JSONObject item ) {
        int result = CommonConstants.SUCCESS;
    	try {
        	JSONObject motoItem = new JSONObject();
        	
        	String category = item.getString( "category" );
            DashLogger.d(TAG,"category = " + category);
            if ( "Widgets".equalsIgnoreCase( category ) ) {
            	motoItem.put("itemType","appwidget");
            } else if ( "Shortcuts".equalsIgnoreCase( category ) ) {
            	motoItem.put("itemType","favorite" );
            } else {
            	DashLogger.e(TAG,"Category is neither widgets nor shortcuts.");
            	return CommonConstants.FAILED;
            }
            
            String component = item.getString( "id" );     
            String packageName = CommonUtils.extractPackage( component );
            String providerName = CommonUtils.extractProvider( component );
            motoItem.put("packageName",packageName );
            motoItem.put("className",providerName );
            
            int containerId = -100;
            if ( item.has( "container_id" ) ) {
                containerId = item.getInt( "container_id" );
            }
            motoItem.put("container",containerId );
            
        	int screen = item.getInt( "screen" );
        	motoItem.put("screen",screen );
        	 
            int x = item.getInt( "x" );
            int y = item.getInt( "y" );
            motoItem.put("x",x );
            motoItem.put("y",y );

            if(item.has("rows") && item.has("cols")) 
            {
            	int cols = item.getInt( "cols" );
                int rows = item.getInt( "rows" );
                motoItem.put("spanX",cols );
                motoItem.put("spanY",rows );
            }
            
            motoList.put(motoItem);
            
        } catch ( JSONException e ) {
            DashLogger.e( TAG, "JSONException in createMotoWidgetOrShortcut" + e.getMessage() );
            result = CommonConstants.FAILED;
        }
    	return result;
    }


    public long generateNewId() {
        if ( this.mMaxId < 0L )
            throw new RuntimeException( "Error: max id was not initialized" );
        long l = this.mMaxId + 1L;
        this.mMaxId = l;
        return this.mMaxId;
    }

    public boolean isAnyFailed() {
        if ( failedList.length() != 0 ) {
            return true;
        } else {
            return false;
        }
    }

    public JSONArray getFailedItems() {
        return failedList;
    }
}