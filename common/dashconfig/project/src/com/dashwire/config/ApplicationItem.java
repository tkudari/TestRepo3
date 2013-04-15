package com.dashwire.config;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class ApplicationItem {
    public static final int QUEUED = 0;
    public static final int DOWNLOADING = 1;
    public static final int INSTALLING = 2;
    public static final int INSTALLED = 3;
    public static final int CANCELED = 4;
    @SuppressWarnings("unused")
    private static String destinationPath;
    private String packageName;
    private String title;
    private long size;
    private String uri;
    private int status = QUEUED;
    private Context context;
    private HashSet<String> installedPackages;
    private int downloadProgress;

    public ApplicationItem( String title, String packageName ) {
        this.title = title;
        this.packageName = packageName;
    }

    public ApplicationItem( Context context, JSONObject item ) {
        try {
            this.context = context;
            this.size = item.getLong( "size" );
            this.uri = item.getString( "id" );
            this.title = item.getString( "title" );
            setPackageNameFromUri( uri );
            updateStatus();
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
    }

    public HashSet<String> getInstalledPackages() {
        if ( installedPackages == null ) {
            installedPackages = new HashSet<String>();
            PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> packageList = packageManager.getInstalledPackages( 0 );
            for ( int index = 0; index < packageList.size(); index++ ) {
                PackageInfo info = packageList.get( index );
                installedPackages.add( info.packageName );
            }
        }
        return installedPackages;
    }

    private void updateStatus() {
        HashSet<String> installed = getInstalledPackages();
        if ( installed.contains( getPackageName() ) ) {
            status = INSTALLED;
        }
    }

    private void setPackageNameFromUri( String uri ) {
        int index = uri.lastIndexOf( "/" );
        packageName = uri.substring( index + 1, uri.length() - 4 );
    }

    public String getTitle() {
        return title;
    }

    public String getPackageName() {
        return packageName;
    }
    
    public long getSize() {
        return size;
    }

    public void setStatus( int status ) {
        this.status = status;
    }

    public boolean isInstalled() {
        return status == INSTALLED;
    }

    public boolean isQueued() {
        return status == QUEUED;
    }

    public boolean isCanceled() {
        return status == CANCELED;
    }

    public boolean isDownloading() {
        return status == DOWNLOADING;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        int index = uri.lastIndexOf( "/" );
        String name = uri.substring( index + 1 );
        return getDestinationPath(context) + "/" + name;
    }

    public static String getDestinationPath(Context context) {
// TODO stopped working in late celox build.  unhardcode later.
//        if ( destinationPath == null ) {
//            File downloadCache = Environment.getDownloadCacheDirectory();
//            File path = new File( downloadCache.getAbsolutePath() );
//            destinationPath = path.getAbsolutePath();
//        }
//        return destinationPath;
    	
    	String downloadLocation = DashconfigApplication.getDeviceContext().getStringConst(context, "APP_DOWNLOAD_LOCATION");
    	//set to old default if nothing found from constant settings
    	if(downloadLocation == null || downloadLocation.length() == 0)
    	{
    		downloadLocation = "/data/data/com.dashwire.config/install";
    	}
        return downloadLocation;
    }

    public int getStatus() {
        return status;
    }

    public int getDownloadProgress() {
        if ( isCanceled() || isInstalled() ) {
            return 100;
        } else {
            return this.downloadProgress;
        }
    }

    public void setDownloadProgress( int progress ) {
        this.downloadProgress = progress;
    }

    public void requeue() {
        setStatus( ApplicationItem.QUEUED );
        setDownloadProgress( 0 );
    }

    public boolean validate( long expectedSize ) {
        File file = new File( getPath() ); 
        return file.length() == expectedSize;
    }

}
