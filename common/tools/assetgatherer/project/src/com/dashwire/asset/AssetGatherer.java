package com.dashwire.asset;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.TreeMap;

public abstract class AssetGatherer {
	protected JSONObject config;
	protected String assetName;
	protected File outputDir;
	protected String sortProp;
	private static final String TAG = AssetGatherer.class.getCanonicalName();
	public AssetGatherer(JSONObject object, File outputDir)
	{
		this.config = object;
		this.outputDir = outputDir;
	}
	public void extendAssets(JSONObject assets,Context context)
	{
		try
		{
			JSONArray originalAssets = assets.optJSONArray(assetName);
			JSONArray newAssets = this.getAssets(context);
			if(originalAssets != null)
			{
				for(int i = 0; i< newAssets.length(); i++)
				{
					originalAssets.put(newAssets.get(i));
				}
				assets.put(assetName,sortAssetArray(originalAssets));
				
			}
			else
			{
				assets.put(assetName, sortAssetArray(newAssets));
			}
		}
		catch(Exception e)
		{
            Log.e(TAG, "Error", e);
		}
	}
	protected abstract JSONArray getAssets(Context context);
	private JSONArray sortAssetArray(JSONArray array)
	{
		try {
			if(this.sortProp != null)
			{
				TreeMap<String,Integer> items = new TreeMap<String, Integer>();
		        Log.v( TAG,"starting sort" );
		        for(int i =0;i<array.length();i++){
		        	Log.v( TAG, array.getJSONObject(i).optString(this.sortProp,"") + " presort" );
					items.put(array.getJSONObject(i).optString(this.sortProp,"") + Integer.toString(i), i);
					
		        }
		        JSONArray array2 = new JSONArray();
		        for(Integer i : items.values())
		        {
		        	
	        		Log.v( TAG, array.getJSONObject(i).optString(this.sortProp,"") + " sorted" );
					array2.put(array.getJSONObject(i));
				
		        }
		        return array2;
			}
		} catch (JSONException e) {
            Log.e(TAG, "Error", e);
		}
		return array;
		
	}
	

	protected boolean hasMetadata( ActivityInfo ai ) {
        try {
            if ( ai.metaData == null || ai.metaData.keySet() == null ) {
                return false;
            }
        } catch ( Exception e1 ) {
            return false;
        }
        return true;
    }
	
	protected void copyFile( File src, File dest ) {
        try {
            FileInputStream fis = new FileInputStream( src );
            FileOutputStream fos = new FileOutputStream( dest );

            byte[] buffer = new byte[ 8192 ];

            while ( ( fis.read( buffer ) ) != -1 ) {
                fos.write( buffer );
            }
            fos.close();
            fis.close();
        } catch ( FileNotFoundException e ) {
            Log.e(TAG, "Error", e);
        } catch ( Exception e ) {
            Log.e(TAG, "Error", e);
        }
    }
	
	protected void copyBitmapToFile( Bitmap bm, File dest ) {
        try {
            FileOutputStream fos;
            fos = new FileOutputStream( dest );
            bm.compress( Bitmap.CompressFormat.PNG, 90, fos );
        } catch ( FileNotFoundException e ) {
            Log.v( TAG, "File not Found : " + e.getMessage() );
        }
    }

	public String extractComponent( String id ) {
        String component = id.substring( id.lastIndexOf( "=" ) + 1 );
        String componentId = component.substring( 0, component.lastIndexOf( ";" ) );
        ComponentName componentName = ComponentName.unflattenFromString(componentId);
        componentId = componentName.flattenToString();
        return componentId;
    }
	//EXTRA UNUSED BUT POSSIBLY USEFUL ASSET EXTRACTION FUNCTIONS
//	private void copyFile(InputStream in, OutputStream out) throws IOException {
//        byte[] buffer = new byte[1024];
//        int read;
//        while((read = in.read(buffer)) != -1){
//          out.write(buffer, 0, read);
//        }
//    }
//    
//    private static int getCell( float f ) {
//        return ( int ) ( f + 30 ) / 70;
//    }
//
//    private void printContentProviders() {
//        JSONArray array = new JSONArray();
//
//        List<PackageInfo> pis = context.getPackageManager().getInstalledPackages( PackageManager.GET_PROVIDERS | PackageManager.GET_META_DATA );
//        for ( PackageInfo pi : pis ) {
//            if ( pi.providers != null ) {
//                for ( ProviderInfo pr : pi.providers ) {
//                    Log.d( TAG,  pr.authority );
//                }
//            }
//        }
//
//    }
//    public static String extractPackage( String widgetId ) {
//        String packageName = widgetId.substring( 0, widgetId.lastIndexOf( "/" ) );
//        return packageName;
//    }
//    public static String extractProvider( String widgetId ) {
//        String providerClassName = widgetId.substring( widgetId.lastIndexOf( "/" ) + 1 );
//        String providerName = "";
//        String packageName = extractPackage( widgetId );
//        if ( ".".equalsIgnoreCase( providerClassName.substring( 0, 1 ) ) ) {
//            providerName = packageName + providerClassName;
//        } else {
//            providerName = providerClassName;
//        }
//        return providerName;
//    }
//
    
}
