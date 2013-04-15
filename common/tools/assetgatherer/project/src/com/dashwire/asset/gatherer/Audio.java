package com.dashwire.asset.gatherer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import com.dashwire.asset.AssetGatherer;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Audio extends AssetGatherer {
    private static final String TAG = Audio.class.getCanonicalName();

	private String path;
	
	public Audio(JSONObject config,File outputDir)
	{
		super(config,outputDir);
		try {
			this.assetName = this.config.getString("output");
			this.path = this.config.getString("uri");
		} catch (JSONException e) {
            Log.e(TAG, "Error", e);
		}
	}
	@Override
	protected JSONArray getAssets(Context context) {
		Log.d(TAG, "FIND Audio " + path + "------------------");
        File b = new File( outputDir, assetName );
        if ( !b.exists() ) {
            b.mkdirs();
        }
        JSONArray array = new JSONArray();

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        File parent = new File( path );
        File[] files = parent.listFiles();
        for ( File f : files ) {
            String abp = f.getAbsolutePath();
            if ( abp.contains( ".mp3" ) || abp.contains( ".ogg" )) {
                mmr.setDataSource( f.getAbsolutePath() );
                JSONObject object = new JSONObject();
                try {
                    String title = mmr.extractMetadata( MediaMetadataRetriever.METADATA_KEY_TITLE ) ;
                    if(title == null)
                    {
                        title = f.getName().replace(".ogg","").replace(".mp3","").replace('_',' ').replace('-',' ');
                    }
                    title.replace("\\r\\n","");
                    object.put( "uri", "file://"+path+f.getName() );
                    object.put( "title", title );
                    object.put( "src", "/" + assetName + "/" + f.getName() );
                    if (assetName.equalsIgnoreCase("ringtones"))
                    {
                    	object.put("type", "call");
                    }else if (assetName.equalsIgnoreCase("notifications"))
                    {
                    	object.put("type", "sms");
                    }
                    array.put( object );
                } catch ( JSONException e ) {
                    Log.e(TAG, "Error", e);
                }
                Log.d(TAG, "Extracting: " + "file://" + path + f.getName());
                copyFile( f, new File( outputDir, assetName + "/" + f.getName() ) );
            }
        }
        return array;
	}
}
