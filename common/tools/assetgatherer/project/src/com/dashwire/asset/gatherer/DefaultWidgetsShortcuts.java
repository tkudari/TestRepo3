package com.dashwire.asset.gatherer;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.dashwire.asset.AssetGatherer;
import com.dashwire.asset.utility.ContentProviderFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class DefaultWidgetsShortcuts extends AssetGatherer {
    private static final String TAG = DefaultWidgetsShortcuts.class.getCanonicalName();
	private Uri favorites_uri;
	public DefaultWidgetsShortcuts(JSONObject config,File outputDir)
	{
		super(config,outputDir);
		this.assetName = "defaultWidgetsAndShortcuts";
		this.favorites_uri = Uri.parse(this.config.optString("favorites_uri",""));
		this.sortProp = "title";
	}
	@Override
	protected JSONArray getAssets(Context context) {
		Log.d(TAG, "FIND Default Widgets and shortcuts------------------");
        ContentResolver resolver = context.getContentResolver();
        JSONArray array = new JSONArray();
        Cursor cursor = resolver.query( favorites_uri, null, null, null, null );
        if ( cursor != null ) {
            int row = 0;
            while ( cursor.moveToNext() ) {
                row++;
                JSONObject object = new JSONObject();
                if ( row == 1 ) {
                	ContentProviderFunctions.dumpRowColumnNameCSVFormat( cursor );
                }

                
                String workspace_id = ContentProviderFunctions.getColumn( cursor, "workspace_id" ).toString();
                if ( "1".equalsIgnoreCase( workspace_id ) || workspace_id.equals( "" )) {
                    Log.v( TAG, "itemType = " + ContentProviderFunctions.getColumn( cursor, "itemType") );
                    try {
						object.put( "title", ContentProviderFunctions.getColumn( cursor, "title" ) );
	                    object.put( "screen", ContentProviderFunctions.getColumn( cursor, "screen" ) );
	                    object.put( "x", ContentProviderFunctions.getColumn( cursor, "cellX") );
	                    object.put( "y", ContentProviderFunctions.getColumn( cursor, "cellY" ) );
	                    object.put( "cols", ContentProviderFunctions.getColumn( cursor, "spanX") );
	                    object.put( "rows", ContentProviderFunctions.getColumn( cursor, "spanY" ) );
	                    if(ContentProviderFunctions.getColumn( cursor, "container" ) != null)
	                        object.put( "container_id", ContentProviderFunctions.getColumn( cursor, "container" ).toString() );
	                    String itemType = ContentProviderFunctions.getColumn( cursor, "itemType" ).toString();
	                    if ( "0".equalsIgnoreCase( itemType ) ) {
	                        object.put( "id", extractComponent( ContentProviderFunctions.getColumn( cursor, "intent" ).toString() ) );
	                        object.put( "category", "Shortcuts" );
	                    } else if ( "6".equalsIgnoreCase( itemType ) && Build.MANUFACTURER.equals( "HTC" )) {
	                        object.put( "id", ContentProviderFunctions.getColumn( cursor, "intent" ).toString() );
	                        object.put( "uri", ContentProviderFunctions.getColumn( cursor, "uri" ).toString() );
	                        object.put( "category", "Widgets" );
	                        object.put( "type", "htcw" );
	                    } else if ( "5".equalsIgnoreCase( itemType ) && Build.MANUFACTURER.equals( "samsung" ) ) {
	                        object.put( "id", ContentProviderFunctions.getColumn( cursor, "intent" ).toString() );
	                        object.put( "category", "Widgets" );
	                        object.put( "type", "tw" );
	                    } else if (  "5".equalsIgnoreCase( itemType ) && Build.MANUFACTURER.equals( "lge" ) ) {
	                        object.put( "category", "Folder" );
	                    }  else if ( "4".equalsIgnoreCase( itemType ) ) {
	                    	try
	                    	{
			                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( context );
			                    AppWidgetProviderInfo appWidget = appWidgetManager.getAppWidgetInfo(Integer.parseInt(ContentProviderFunctions.getColumn( cursor,"appWidgetId" ).toString()));
			                    			                    
			                    object.put( "title", appWidget.label );
			                    object.put( "id", appWidget.provider.flattenToString() );
			                    object.put( "category", "Widgets" );
			                    object.put( "type", "aw" );
	                    	}
	                    	catch(Exception e)
	                    	{
                                Log.e(TAG, "Error", e);
	                    	}
	               
	                    }
	                    array.put( object );
                    } catch (JSONException e) {
                        Log.e(TAG, "Error", e);
					}
                }

            
            }
        } else {
            Log.v( TAG, favorites_uri + " null cursor" );
        }
        return array;
	}

	
}
