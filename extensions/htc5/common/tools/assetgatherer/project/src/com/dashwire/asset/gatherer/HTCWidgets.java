package com.dashwire.asset.gatherer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;

import com.dashwire.asset.AssetGatherer;
import com.dashwire.asset.utility.ContentProviderFunctions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class HTCWidgets extends AssetGatherer {
    private static final String TAG = HTCWidgets.class.getCanonicalName();

	public HTCWidgets(JSONObject config,File outputDir)
	{
		super(config,outputDir);
		this.assetName = "htc_widgets";
	}
	@Override
	protected JSONArray getAssets(Context context) {
		Log.d(TAG, "FIND HTC Widgets------------------");
        String HTC_WIDGETS_URI = "content://com.htc.launcher.widget.fxwidget/fxwidgets";
        ContentResolver resolver = context.getContentResolver();
        JSONArray array = new JSONArray();
        
        File outputDirectory = new File( outputDir, "widgets" );
        if ( !outputDirectory.exists() ) {
            outputDirectory.mkdirs();
        }
        
        Cursor cursor = resolver.query( Uri.parse( HTC_WIDGETS_URI ), null, null, null, null );
        if ( cursor != null ) {
            int row = 0;
            while ( cursor.moveToNext() ) {
                row++;
                JSONObject object = new JSONObject();
                if ( row == 1 ) {
                	ContentProviderFunctions.dumpRowColumnNameCSVFormat( cursor );
                }
                
                Resources packageResource;
                try {
                    packageResource = context.getPackageManager().getResourcesForApplication( ContentProviderFunctions.getColumn( cursor, "provider" ).toString().split("/")[0] );

                
                    Bitmap bitmap = ( ( BitmapDrawable ) packageResource.getDrawable( Integer.parseInt(ContentProviderFunctions.getColumn( cursor, "style_preview_res" ).toString()) )).getBitmap();
                    copyBitmapToFile( bitmap, new File( outputDirectory , "/"+ ContentProviderFunctions.getColumn( cursor, "style_component_name" ).toString().replaceAll("/", "_") + ".png" ) );
                    object.put( "src", "/widgets/" + ContentProviderFunctions.getColumn( cursor, "style_component_name" ).toString().replaceAll("/", "_")  + ".png" );

                    object.put( "title", packageResource.getString(Integer.parseInt(ContentProviderFunctions.getColumn( cursor, "style_name_res" ).toString())) );
                    object.put( "cols", ContentProviderFunctions.getColumn( cursor, "style_span_x" ) );
                    object.put( "rows", ContentProviderFunctions.getColumn( cursor, "style_span_y" ) );
                    object.put( "category", "Widgets" );
                    object.put( "type", "htcw" );
                    object.put( "id", ContentProviderFunctions.getColumn( cursor, "provider" ));
                    object.put( "uri", ContentProviderFunctions.getColumn( cursor, "style_component_name" ));

                    array.put( object );

                } catch (NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    Log.e(TAG, "Error", e);
                } catch (JSONException e) {
					// TODO Auto-generated catch block
                    Log.e(TAG, "Error", e);
				}
            }
        } else {
            Log.v(TAG, Uri.parse(HTC_WIDGETS_URI) + " null cursor");
        }
        return array;
	}

}