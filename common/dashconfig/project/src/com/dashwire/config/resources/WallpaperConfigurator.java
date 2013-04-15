package com.dashwire.config.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ResourceConfigurator;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;


public class WallpaperConfigurator implements ResourceConfigurator {
	
	private static final String TAG = WallpaperConfigurator.class.getCanonicalName();

    protected Context mContext;

	protected ConfigurationEvent mConfigurationEvent;
	
	   protected JSONArray mConfigArray;
	
	public WallpaperConfigurator() { }
	
	@Override
	public String name() {
		return "wallpapers";
	}

    void setFileWallpaper( Uri uri ) throws IOException {
        InputStream in = mContext.getContentResolver().openInputStream( uri );
        try {
            WallpaperManager.getInstance( mContext ).setStream( in );
        } finally {
            in.close();
        }
    }

    void setResourceWallpaper( Uri uri ) throws NameNotFoundException, IOException {
        String packageName = uri.getHost();
        List<String> pathSegments = uri.getPathSegments();
        String resourceType = pathSegments.get( 0 );
        String resourceName = pathSegments.get( 1 );
        PackageManager packageManager = mContext.getPackageManager();

        Context appContext = mContext.createPackageContext( packageName, Context.CONTEXT_IGNORE_SECURITY );
        final Resources resources = packageManager.getResourcesForApplication( packageName );

        int res = resources.getIdentifier( resourceName, resourceType, packageName );
        setResourceWallpaper(appContext, res);
    }
    
    public void setResourceWallpaper(Context ctx, int resource) throws IOException {
    	WallpaperManager.getInstance( ctx ).setResource( resource );
    }
    
    @Override
    public void setContext( Context context ) {
        mContext = context;
    }

    @Override
    public void setConfigurationEvent( ConfigurationEvent configurationEvent ) {
       mConfigurationEvent = configurationEvent;
        
    }

    @Override
    public void setConfigDetails( JSONArray configArray ) {
        mConfigArray = configArray;
        
    }
    
    @Override
    public void configure() {
        if ( mConfigArray != null && mConfigArray.length() > 0 ) {
            try {
                JSONObject data = mConfigArray.getJSONObject( 0 );
                Uri uri = Uri.parse( data.get( "uri" ).toString() );
                String scheme = uri.getScheme(); 
                if ( scheme.equals( "file" ) ) {
                    setFileWallpaper( uri );
                } else if ( scheme.equals( "android.resource" ) ) {
                    setResourceWallpaper( uri );
                }
            } catch ( Exception e ) {
                mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.FAILED);
                return;
            }

            mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.CHECKED);
        }
        
    }

}
