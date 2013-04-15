package com.dashwire.asset;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.dashwire.asset.event.BusProvider;
import com.dashwire.asset.gatherer.events.ExtractionStateChangedEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;



public class AssetExtractionTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = AssetExtractionTask.class.getCanonicalName();

    private static String manufacturer = Build.MANUFACTURER;
    private static String device = Build.DEVICE;
    private static String version = VERSION.INCREMENTAL;
    private static String version_release = VERSION.RELEASE;

    private Context context;
    private Display display;

    public AssetExtractionTask(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        this.context = context;
        this.display = windowManager.getDefaultDisplay();
    }

    @Override
    protected String doInBackground( Void... voids ) {
    	Log.v(TAG,"AssetExtraction getting started");
    	JSONObject assetJSON = new JSONObject();
    	
        postStateChange( ExtractionStateChangedEvent.State.IN_PROGRESS );

        try {
            assetJSON = extractDeviceContent();
        } catch(Exception e) {
            Log.d(TAG,"Exception = " + e.getMessage());
            e.printStackTrace();
            broadcastAssetResult("failed", e.getMessage());
            return "FAILURE";
        }
        
        broadcastAssetResult("success", assetJSON.toString());

        postStateChange( ExtractionStateChangedEvent.State.COMPLETED );

        return "SUCCESS";
    }
    
    public static String replaceDots( String name ) {
        return name.replace( ".", "_" );
    }

    private void postStateChange( ExtractionStateChangedEvent.State state ) {
        BusProvider.getInstance().post(
            new ExtractionStateChangedEvent( state )
        );
    }

    private JSONObject extractDeviceContent() {
        String path = "/data/data/"+context.getPackageName()+"/";
        
        String outputfileName = manufacturer + "_" + device +"_"+version_release+"_" + version;
        outputfileName = replaceDots( outputfileName );
        File pinfodir = new File( path );
        pinfodir.setReadable(true, false);
        File outputDir = new File( path + outputfileName + "/" );
        File outputZipFile = new File( path + outputfileName + ".zip" );

        
        outputDir.setReadable(true,false);
        outputZipFile.setWritable(true,false);
        outputDir.setWritable(true,false);
        outputDir.setExecutable(true,false);
        
        File output = outputDir;
        if ( !output.exists() ) {
            output.mkdir();
        }

        JSONObject root = new JSONObject();

        try {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            
            JSONArray assets = Manufacturer.loadManufacturerAssets( context, Build.MANUFACTURER );
            if(manufacturer != null)
            {
            	android.graphics.Point displaySize = new android.graphics.Point();
            	display.getSize(displaySize);
                root.put( "build-release", VERSION.RELEASE );
                root.put( "build-incremental", VERSION.INCREMENTAL );
                root.put( "build-sdk", VERSION.SDK_INT );
                root.put( "build-device", Build.DEVICE );
                root.put( "build-manufacturer", Build.MANUFACTURER );
                root.put( "screen_width", displaySize.x );
                root.put( "screen_height", displaySize.y );
                root.put( "screen_dpi", metrics.densityDpi);
                root.put( "screen_xdpi", metrics.xdpi);
                root.put( "screen_ydpi", metrics.ydpi);
                root.put( "screen_density", metrics.density);
                Resources r = context.getResources();
                float one_dip_in_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
                root.put( "screen_density_real", one_dip_in_px);
                
                for(int i = 0; i < assets.length();	i++)
                {
                	JSONObject asset = assets.getJSONObject(i);
                	Class c = Class.forName("com.dashwire.asset.gatherer."+asset.getString("type"));
                	Constructor constructor = c.getConstructor(JSONObject.class,File.class);
                	AssetGatherer assetGatherer = (AssetGatherer)constructor.newInstance(asset,outputDir);
                	Log.v("Assets","Extracting "+ asset.toString(4));
                	assetGatherer.extendAssets(root,context);
                }
                
            }
            else
            {
                Log.e("ConfigError","No config settings for '"+Build.MANUFACTURER+"'");
            }
            
        } catch ( JSONException e ) {
            Log.e(TAG, "Error", e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Error", e);
		} catch (SecurityException e) {
            Log.e(TAG, "Error", e);
		} catch (NoSuchMethodException e) {
            Log.e(TAG, "Error", e);
		} catch (IllegalArgumentException e) {
            Log.e(TAG, "Error", e);
		} catch (IllegalAccessException e) {
            Log.e(TAG, "Error", e);
		} catch (InvocationTargetException e) {
            Log.e(TAG, "Error", e);
		} catch (InstantiationException e) {
            Log.e(TAG, "Error", e);
		}
        

        
        try {
            FileOutputStream fos = new FileOutputStream( new File( output, "assets.json" ) );
            fos.write( root.toString(4).getBytes() );
            fos.close();
        } catch ( Exception e ) {
            Log.e(TAG, "Error", e);
        }

        ZipTask zt = new ZipTask();

        zt.execute( outputDir, outputZipFile );
        ReadableRecursive(outputDir);
        
        return root;
    }
    void ReadableRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
            	ReadableRecursive(child);

        fileOrDirectory.setReadable(true, false);
        fileOrDirectory.setWritable(true, false);
        fileOrDirectory.setExecutable(true, false);
    }
    
   private void broadcastAssetResult(String status, String root)
    {
    	Log.v(TAG,"Broadcasting device asset result");
    	Intent assetResultIntent = new Intent();
    	assetResultIntent.setAction("com.dashwire.asset.gatherer.intent.action.EXTRACTION_RESULT");
    	assetResultIntent.putExtra("assetResult", root);
    	assetResultIntent.putExtra("result", status);
        context.sendBroadcast(assetResultIntent);
    }
}
