package com.dashwire.configurator3000;

import java.io.File;
import java.io.FileOutputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings.Secure;

public class Configruator3000PackageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent ) {
    	if(context == null) {
    		return;
    	}
        String packageString = intent.getDataString();
        int index = packageString.lastIndexOf( ":" );
        String name = packageString.substring( index + 1 );
        
        if ("com.dashwire.configurator3000".equalsIgnoreCase( name ))
        {
            writeToFile(getAndroidId(context));
        }
    }

    public static String getPackageTitle( Context context, String name ) {
        String title = "untitled";
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo( name, PackageManager.GET_ACTIVITIES | PackageManager.GET_INTENT_FILTERS );
            Context packageContext = context.createPackageContext( name, 0 );

            if ( info.applicationInfo.nonLocalizedLabel != null ) {
                title = ( String ) info.applicationInfo.nonLocalizedLabel;
            } else {
                int resId = info.applicationInfo.labelRes;
                if ( resId != 0 ) {
                    title = packageContext.getString( resId );
                }
            }
        } catch ( NameNotFoundException e ) {
        }
        return title;
    }
    
    private void writeToFile(String androidId){
        String path = "/data/data/com.dashwire.configurator3000/files";
        File outputDir = new File( path + "/" );

        File output = outputDir;
        if ( !output.exists() ) {
            output.mkdir();
        }
        
        try {
            FileOutputStream fos = new FileOutputStream( new File( output, "android_id.txt" ) );
            fos.write( androidId.getBytes() );
            fos.close();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
    private String getAndroidId(Context context) {
        String androidId = Secure.getString( context.getContentResolver(), Secure.ANDROID_ID );
        return androidId;
    }
}
