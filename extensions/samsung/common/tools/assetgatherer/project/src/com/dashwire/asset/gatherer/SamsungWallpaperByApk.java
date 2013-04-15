package com.dashwire.asset.gatherer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.dashwire.asset.AssetGatherer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SamsungWallpaperByApk extends AssetGatherer {
    private static final String TAG = SamsungWallpaperByApk.class.getCanonicalName();

	private String apk;
	private String filter;
	private String uri_base;
	public SamsungWallpaperByApk(JSONObject config, File outputDir)
	{
		super(config,outputDir);
		this.assetName = "wallpapers";
		this.apk = config.optString( "apk_location","" );
		this.filter = config.optString( "filter","" );
		this.uri_base = config.optString( "uri_base","" );
				
	}
	@Override
	protected JSONArray getAssets(Context context) {
		Log.d(TAG, "FIND WALLPAPERS------------------");
        File b = new File( outputDir, assetName );
        if ( !b.exists() ) {
            b.mkdirs();
        }
        JSONArray array = new JSONArray();
        try {
            InputStream is = null;
            ZipEntry entry;
            ZipFile zipfile = new ZipFile( apk );
            Enumeration<?> e = zipfile.entries(); 

            while ( e.hasMoreElements() ) {
                entry = ( ZipEntry ) e.nextElement();
                Log.d(TAG, "entry = " + entry.getName());

                if ( ( ( entry.getName().contains( "drawable-xhdpi" ) || entry.getName().contains( "drawable-sw399dp-xhdpi" ) || entry.getName().contains( "drawable-hdpi" ) ) && entry.getName().contains( filter ) ) ) {
                    String entryFileName = entry.getName().substring( 1 + entry.getName().lastIndexOf( "/" ), entry.getName().length() );
                    Log.d(TAG, "File Name = " + entryFileName);
                    if ( (entryFileName.toLowerCase().startsWith( "hd_" ) || entryFileName.toLowerCase().startsWith( "wallpaper_" ) ||  entryFileName.toLowerCase().startsWith( "wvga_" ) || entryFileName.contains( "celox_" )) &&
                    		(entryFileName.toLowerCase().endsWith( ".jpg" ) || entryFileName.toLowerCase().endsWith( ".png" )) && 
                    		(!entryFileName.contains( "small" )) && 
                    		(!entryFileName.contains( "_preview"))) {
                    	try {
                            Log.d(TAG, "Extracting: " + entryFileName);
                            String uri = uri_base + entryFileName.replace( ".JPG", "" ).replace( ".jpg", "" ).replace( ".png", "" );
                            
                            is = context.getContentResolver().openInputStream( Uri.parse(uri));
                            
                            JSONObject object = new JSONObject();
                            object.put( "uri", uri );
                            object.put( "src", "/"+this.assetName+"/" + entryFileName );
                            array.put( object );
                            copyFileInputStream( is, new File( outputDir, assetName + "/" + entryFileName ) );
                        } catch ( JSONException je ) 
                        {
                            Log.e(TAG, "Error", je);
                        } catch ( Exception e1 ) {
                            Log.e(TAG, "Error", e1);
	                    }
                    }
                }
            }
        } catch ( Exception e ) {
            Log.e(TAG, "Error", e);
        }
        return array;
	}
	private void copyFileInputStream( InputStream fis, File dest ) {
        try {
            Log.v(TAG,"dest = " + dest.getPath());

            FileOutputStream fos = new FileOutputStream( dest );

            byte[] buffer = new byte[ 8192 ];

            while ( ( fis.read( buffer ) ) != -1 ) {
                fos.write( buffer );
            }
            fos.close();
            fis.close();
        } catch ( FileNotFoundException e ) {
            // TODO Auto-generated catch block
            Log.e(TAG, "Error", e);
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            Log.e(TAG, "Error", e);
        }
    }

}
