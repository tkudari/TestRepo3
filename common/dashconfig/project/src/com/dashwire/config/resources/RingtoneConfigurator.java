package com.dashwire.config.resources;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;

import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ResourceConfigurator;
import com.dashwire.base.debug.DashLogger;

public class RingtoneConfigurator implements ResourceConfigurator {
    protected static final String TAG = RingtoneConfigurator.class.getCanonicalName();
    
	protected JSONArray mItems;

	protected Context mContext;

	protected ConfigurationEvent mConfigurationEvent;
	
	protected ContentResolver mContentResolver;
	
	   protected JSONArray mConfigArray;
    
    public RingtoneConfigurator() {
    }
    
    public String name() {
        return "ringtones";
    }

    public Uri getAudioContentUri( Uri uri ) {
        String path = uri.getPath();
        Uri contentUri = MediaStore.Audio.Media.getContentUriForPath( path );
        String[] proj = {
            MediaStore.Audio.Media._ID
        };
        int idIndex = 0;
        String selection = MediaStore.Audio.Media.DATA + " LIKE '" + path + "'";
        Cursor cursor = mContentResolver.query( contentUri, proj, selection, null, null );
        try {
            if ( cursor.moveToNext() ) {
                return ContentUris.withAppendedId( contentUri, cursor.getInt( idIndex ) );
            }
        } finally {
            cursor.close();
        }
        throw new IllegalArgumentException( uri.toString() );
    }
    
    @Override
    public void setContext( Context context ) {
        mContext = context;
        mContentResolver = context.getContentResolver();
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
        
        DashLogger.v( TAG, "Inise Ringtone - Set method");
        
        if ( mConfigArray != null && mConfigArray.length() > 0 ) {
            try {
                
                for (int i=0;i<mConfigArray.length();i++)
                {
                    JSONObject data = mConfigArray.getJSONObject( i );
                    
                    // TODO uri for silent?
                    String uriString = data.get( "uri" ).toString();
                    Uri uri = Uri.parse( uriString );
                    
                    if ( !ContentResolver.SCHEME_CONTENT.equals( uri.getScheme() ) ) {
                        uri = getAudioContentUri( uri );
                    }
                    
                    String type = data.getString( "type" );
                    setRingtone(type, uri);
                }
            } catch ( Exception ex ) {
                mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.FAILED, ex);
                return;
                //throw new IllegalArgumentException( items.toString() );
            }
        }
        mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.CHECKED);
    }
    
    public void setRingtone(String type, Uri uri) {
              
        if (type.equalsIgnoreCase( "call" ))
        {
            RingtoneManager.setActualDefaultRingtoneUri( mContext, RingtoneManager.TYPE_RINGTONE, uri );
        }
        else if (type.equalsIgnoreCase( "sms" ))
        {
            RingtoneManager.setActualDefaultRingtoneUri( mContext, RingtoneManager.TYPE_NOTIFICATION, uri );
        }
    }
}
