package com.dashwire.asset.gatherer;

import android.content.Context;
import com.dashwire.asset.AssetGatherer;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class WallpaperByFolder extends AssetGatherer {
    private static final String TAG = WallpaperByFolder.class.getCanonicalName();
	public String directory;
	public WallpaperByFolder(JSONObject config,File outputDir)
	{
		super(config,outputDir);
		this.assetName = "wallpapers";
		this.directory = this.config.optString("directory","");
	}
	@Override
	protected JSONArray getAssets(Context context) {
		Log.d(TAG, "FIND WALLPAPERS------------------");
		    
		  File b = new File( outputDir, assetName );
		  if ( !b.exists() ) {
		      b.mkdirs();
		  }
		  JSONArray array = new JSONArray();
		  File parent = new File( directory );
		  File[] files = parent.listFiles();
		  for ( File f : files ) {
		      String fileName = f.getName();
		      JSONObject object = new JSONObject();
		      if ( ( !fileName.contains( "_small" ) ) ) {
		      try {
		          object.put( "uri", "file://" + f.getAbsolutePath() );
		          object.put( "src", "/"+this.assetName+"/" + fileName );
		          array.put( object );
		          copyFile( f, new File( outputDir, assetName + "/" + f.getName() ) );
		          } catch ( JSONException e ) {
                    Log.e(TAG, "Error", e);
		          }
		      }
		  }
		  return array;
	}

}