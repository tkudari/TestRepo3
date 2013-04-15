package com.dashwire.config;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.ui.AppInstallationActivity;
import com.dashwire.config.util.CommonConstants;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.lang.Process;

public class DownloadService extends Service {

    public class DownloadBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    private static final String TAG = DownloadService.class.getCanonicalName();
    private final IBinder binder = new DownloadBinder();
    private static ArrayList<ApplicationItem> applicationItems;
    private Hashtable<String, ApplicationItem> applicationMap;
    private Handler messageHandler;
    private int totalAppCount = 0;
    private ArrayList<String> cacheEntryUris;
    private int nextUri;
    private int BUFFER_SIZE = 50000;
    private AppInstallationActivity appInstallHandle;
    private AsyncTask<ApplicationItem, String, ApplicationItem> mediaDownloader;
    private ApplicationItem currentDownload;
    private int previousPercentComplete;

    @Override
    public void onCreate() {
        applicationMap = new Hashtable<String, ApplicationItem>();
        applicationItems = new ArrayList<ApplicationItem>();
        initCacheEntries();
        DashLogger.d( TAG, "Service: onCreate ----------------------------------------------------------------" );
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        DashLogger.d( TAG, "Service: onDestroy ----------------------------------------------------------------" );
        super.onDestroy();
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
    	DashLogger.d( TAG, "Service: onStartCommand ----------------------------------------------------------------" );
        if (intent != null)
        {
            String action = intent.getAction();
            DashLogger.d( TAG, "onStartCommand " + action + "----------------------------------------------------------------" );
            Bundle extras = intent.getExtras();
            if ( "download".equals( action ) ) {
                beginDownloading( intent.getStringExtra( "json" ) );
            } else if ( "installed".equals( action ) ) {
                notifyAppInstalled( extras.getString( "title" ), extras.getString( "packageName" ) );
                if(isCompleted()) {
                	DashLogger.d( TAG, "Service: Received DownloadService even with no more installs, calling clearDownloadService.");
                	clearDownloadService();
                }       
            }
            /* TODO: Ensure this removal is OK
            else if ( "uninstall".equals( action ) ) {
                uninstallAll();
            }*/
        }
        if(!isCompleted()) {
        	return START_STICKY; }
        else {
        	return START_NOT_STICKY ;
        }
    }
    
    

    @SuppressWarnings("static-access")
    private void beginDownloading( String json ) {
        this.startForeground( NotificationController.getInstance( this ).INSTALLING_APPLICATIONS, NotificationController.getInstance( this ).getNotification( NotificationController.getInstance( this ).INSTALLING_APPLICATIONS ));
        try {
            JSONArray apps = new JSONArray( json );
            for ( int index = 0; index < apps.length(); index++ ) {
                JSONObject jsonItem = apps.getJSONObject( index );
                ApplicationItem item = new ApplicationItem( this, jsonItem );
                if ( !applicationMap.containsKey( item.getPackageName() ) ) {
                    applicationItems.add( item );
                    applicationMap.put( item.getPackageName(), item );
                }
            }
            totalAppCount = applicationItems.size();
            startNextDownload();
        } catch ( JSONException je ) {
            DashLogger.e( TAG, "JSONException: " + je.toString() );
            DashLogger.e( TAG, "JSONException: " + json );
        }
        updateNotification( "beginDownloading" );
    }

    private synchronized void startNextDownload() {
        currentDownload = getNextDownload();
        if ( currentDownload != null ) {
            if ( mediaDownloader == null ) {
                DashLogger.d( TAG, "starting download : " + currentDownload.getUri() );
                mediaDownloader = new MediaDownloader( this ).executeOnExecutor(Executors.newSingleThreadExecutor(), currentDownload );
            } else {
                DashLogger.d( TAG, "startNextDownload called when mediaDownloader is not null" );
            }
        } else {
            DashLogger.d( TAG, "startNextDownload : no more downloads" );
        }
    }

    private ApplicationItem getNextDownload() {
        for ( int index = 0; index < applicationItems.size(); index++ ) {
            ApplicationItem item = applicationItems.get( index );
            if ( item.isQueued() ) {
                return item;
            }
        }
        return null;
    }

    private void notifyAppInstalled( String title, String packageName ) {

        if ( applicationMap.containsKey( packageName ) ) {
            updateItemStatus( packageName, ApplicationItem.INSTALLED );
            sendMessage( packageName );
            DashLogger.d( TAG, "Install complete: " + percentComplete() );

            // clear '/cache' of the downloaded file here, since we know that
            // the package has been installed:
            clearCacheOfFile();

            if ( completedCount() >= totalAppCount ) {
                try {
                    if ( appInstallHandle != null ) {
                        appInstallHandle.updateScreenStatus();
                    }
                } catch ( NullPointerException e ) {
                    e.printStackTrace();
                    // Could come here due to a race condition, but we're only
                    // trying to dismiss the button which will be gone otherwise
                }
                updateNotification( "notifyAppInstalled" );
            }
        }
    }

    private int percentComplete() {
        int total = 0;
        for ( int index = 0; index < applicationItems.size(); index++ ) {
            ApplicationItem item = applicationItems.get( index );
            total += item.getDownloadProgress();
        }
        if ( applicationItems.size() != 0 ) {
            return total / applicationItems.size();
        } else {
            return 0;
        }
    }

    private int completedCount() {
        int count = 0;
        for ( int index = 0; index < applicationItems.size(); index++ ) {
            ApplicationItem item = applicationItems.get( index );
            int status = item.getStatus();
            if ( status == ApplicationItem.INSTALLED || status == ApplicationItem.CANCELED ) {
                count++;
            }
        }
        return count;
    }

    private void updateItemStatus( String packageName, int status ) {
        ApplicationItem item = applicationMap.get( packageName );
        if ( item != null ) {
            item.setStatus( status );
        }
    }

    private void sendMessage( String packageName ) {
        updateNotificationProgress();
        if ( messageHandler != null ) {
            Message message = Message.obtain( messageHandler, 0, null );
            message.obj = packageName;
            messageHandler.sendMessage( message );
        }
    }

    private void updateNotificationProgress() {
        int percent = percentComplete();
        if ( previousPercentComplete != percent ) {
            previousPercentComplete = percent;
            updateNotification( "UpdateNotificaitonProgress" );
        }
    }

    @Override
    public IBinder onBind( Intent intent ) {
    	//TODO: SECURITY Add caller check.
        return binder;
    }

    public ArrayList<ApplicationItem> getApplicationList() {
        return applicationItems;
    }

    private class MediaDownloader extends AsyncTask<ApplicationItem, String, ApplicationItem> {

        private static final String TAG = "com.dashwire.config.DownloadService.MediaDownloader";
        private DefaultHttpClient httpClient;

        public MediaDownloader( Context context ) {
        }

        @Override
        protected void onPostExecute( ApplicationItem item ) {
            downloaderCompleted( item );
            super.onPostExecute( item );
        }

        @Override
        protected ApplicationItem doInBackground( ApplicationItem... items ) {
            ApplicationItem item = items[ 0 ];
            if ( !item.isInstalled() ) {
                item.setStatus( ApplicationItem.DOWNLOADING );
                publishProgress( item.getPackageName() );
                HttpResponse response = download( item.getUri(), item.getPath(), item );
                dumpDownloadDetails( response, item.getUri(), item.getPath() );
                if ( !isCancelled() ) {
                    if ( response != null && response.getStatusLine().getStatusCode() == 200 ) {
                        long contentLength = response.getEntity().getContentLength();
                        if ( item.validate( contentLength ) ) {
                            setReadable( item.getPath() );
                            item.setStatus( ApplicationItem.INSTALLING );
                            cacheEntryUris.add( item.getPath() );
                            install( item.getPath() );
                            publishProgress( item.getPackageName() );
                        } else {
                            item.requeue();
                        }
                    } else {
                        item.requeue();
                    }
                }
            }
            return item;
        }

        @Override
        protected void onCancelled() {
            DashLogger.w( TAG, "cancelled " + toString() + " ---------------------------------------------------" );
            if ( httpClient != null ) {
                httpClient.getConnectionManager().shutdown();
            }
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate( String... values ) {
            sendMessage( values[ 0 ] );
            super.onProgressUpdate( values );
        }

        private void dumpDownloadDetails( HttpResponse response, String uri, String destinationPath ) {
            DashLogger.v( TAG, "download complete: " + uri );
            if ( response != null ) {
                Header contentLength = response.getFirstHeader( "Content-Length" );
                DashLogger.v( TAG, "status: " + response.getStatusLine() );
                DashLogger.v( TAG, "size: " + contentLength );
            }
            File file = new File( destinationPath );
            DashLogger.v(TAG, "path: " + destinationPath);
            DashLogger.v( TAG, "exists: " + file.exists() );
            DashLogger.v( TAG, "size: " + file.length() );
        }

        private HttpResponse download( String uri, String path, ApplicationItem item ) {
            createInstallPath( item );
            File file = new File( path );
            if ( file.exists() ) {
                file.delete();
            }
            if ( !file.exists() ) {
                try {
                    HttpParams httpParams = new BasicHttpParams();
                    // httpParams.setParameter(
                    // CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1
                    // );
                    // TODO 3g / wifi timeouts
                    HttpConnectionParams.setConnectionTimeout( httpParams, 5000 );
                    HttpConnectionParams.setSoTimeout( httpParams, 5000 );

                    httpClient = new DefaultHttpClient( httpParams );
                    
                    DashLogger.d(TAG,"Uri: " + uri);

                    HttpGet request = new HttpGet( uri );
                    DashLogger.d(TAG,"Downloading: " + uri);                  
                    HttpResponse response = httpClient.execute( request );
                    long downloadSize = ( long ) item.getSize();
                    InputStream inputStream = response.getEntity().getContent();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream( inputStream, BUFFER_SIZE );
                    //TODO: File Permission Issue
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream( new FileOutputStream( file, false ), BUFFER_SIZE );

                    byte buffer[] = new byte[ BUFFER_SIZE ];
                    int size = 0;
                    double bytesRead = 0;
                    int progressValue = 0;
                    DashLogger.v( TAG, "" + Integer.toHexString( System.identityHashCode( this ) ) + "starting download: " + uri );
                    int previousProgress = 0;
                    while ( !isCancelled() && -1 != ( size = bufferedInputStream.read( buffer ) ) ) {
                        bufferedOutputStream.write( buffer, 0, size );
                        bytesRead += size;

                        progressValue = ( int ) Math.min( ( bytesRead / downloadSize ) * 100.0, 100.0 );
                        if ( progressValue != previousProgress ) {
                            previousProgress = progressValue;
                            // DashLogger.v( TAG, "" +
                            // Integer.toHexString(System.identityHashCode( this
                            // ) ) + "download: " + progressValue + " " + uri );
                            item.setDownloadProgress( progressValue );
                            publishProgress( item.getPackageName() );
                        }
                    }

                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                    bufferedInputStream.close();

                    item.setDownloadProgress( 100 );
                    publishProgress( item.getPackageName() );

                    DashLogger.v( TAG, "" + Integer.toHexString( System.identityHashCode( this ) ) + "completed download: " + uri );

                    return response;
                } catch ( SocketException e ) {
                    // cancel will cause this so just ignore
                } catch ( UnknownHostException e ) {
                    DashLogger.e( TAG, e.toString() );
                } catch ( SocketTimeoutException e ) {
                    DashLogger.e( TAG, e.toString() );
                } catch ( ConnectTimeoutException e ) {
                    DashLogger.e( TAG, e.toString() );
                } catch ( Exception e ) {
                    // showErrorDialog();
                    DashLogger.e( TAG, e.toString() );
                    e.printStackTrace();
                }
            }
            if ( isCancelled() ) {
                item.setDownloadProgress( 0 );
                item.setStatus( ApplicationItem.CANCELED );
            }
            return null;
        }

        private void createInstallPath( ApplicationItem item ) {
            String path = ApplicationItem.getDestinationPath(DownloadService.this.getApplicationContext());
            File dir = new File( path );
            if ( !dir.exists() ) {
                dir.mkdirs();
                setExecutable( path );
                DashLogger.v(TAG, "");
            }
        }
    }

    private void setReadable( String path ) {
        try {
            Process permissionsHandle = Runtime.getRuntime().exec( "chmod 644 " + path );
            try {
                // wait for the native process to finish; our calling thread is
                // blocked till then
                permissionsHandle.waitFor();
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private void setExecutable( String path ) {
        try {
            Process permissionsHandle = Runtime.getRuntime().exec( "chmod 751 " + path );
            try {
                // wait for the native process to finish; our calling thread is
                // blocked till then
                permissionsHandle.waitFor();
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public synchronized void downloaderCompleted( ApplicationItem item ) {
        if ( item != null ) {
            DashLogger.v( TAG, "downloaderCompleted: " + item.getPackageName() + " " + item.getStatus() );
            if ( item.isDownloading() ) {
                item.requeue();
            }
        } else {
            DashLogger.e( TAG, "downloaderCompleted called with null item" );
        }
        mediaDownloader = null;
        startNextDownload();
        if ( item != null )
        {
            sendMessage( item.getPackageName() );
        }
    }

    private void install( String path ) {
        Uri uri = Uri.parse( "file://" + path );
        File file = new File( path );
        if ( file.exists() ) {
            PackageManager packageManager = getPackageManager();
            try {
                Class<?>[] parameters = new Class[] {
                    Uri.class,
                    Class.forName( "android.content.pm.IPackageInstallObserver" ),
                    int.class,
                    String.class
                };
                Method method = packageManager.getClass().getDeclaredMethod( "installPackage", parameters );
                @SuppressWarnings("unused")
                Object result = method.invoke( packageManager, new Object[] {
                    uri,
                    null,
                    0,
                    CommonConstants.PACKAGE_INSTALLER_COMP
                } );
            } catch ( SecurityException e ) {
                e.printStackTrace();
            } catch ( NoSuchMethodException e ) {
                e.printStackTrace();
            } catch ( IllegalArgumentException e ) {
                e.printStackTrace();
            } catch ( IllegalAccessException e ) {
                e.printStackTrace();
            } catch ( InvocationTargetException e ) {
                e.printStackTrace();
            } catch ( ClassNotFoundException e ) {
                e.printStackTrace();
            }
        }
    }

    /*
    private void uninstall( String packageName ) {
        PackageManager packageManager = getPackageManager();
        try {
            Class<?>[] parameters = new Class[] {
                String.class,
                Class.forName( "android.content.pm.IPackageDeleteObserver" ),
                int.class
            };
            Method method = packageManager.getClass().getDeclaredMethod( "deletePackage", parameters );
            @SuppressWarnings("unused")
            Object result = method.invoke( packageManager, new Object[] {
                packageName,
                null,
                0
            } );
        } catch ( SecurityException e ) {
            e.printStackTrace();
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        } catch ( IllegalArgumentException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        }
    }

    public void uninstallAll() {
        // TODO nix debugging code
        uninstall( "com.rovio.angrybirds" );
        uninstall( "com.att.android.mobile.attmessages" );
        uninstall( "com.uievolution.client.rbt" );
        uninstall( "com.espn.score_center" );
        uninstall( "com.facebook.katana" );
        uninstall( "com.openfeint.spotlightwla" );
        uninstall( "com.google.android.stardroid" );
        uninstall( "com.att.android.JustUs" );
        uninstall( "com.lookout" );
        uninstall( "com.pandora.android" );
        uninstall( "ca.jamdat.flight.scrabblefree" );
        uninstall( "com.shazam.android" );
        uninstall( "com.tripadvisor.tripadvisor" );
        uninstall( "com.aws.android" );
        uninstall( "com.zynga.words" );
        
    }
    */

    public void setMessageHandler( Handler messageHandler ) {
        this.messageHandler = messageHandler;
    }

    private void clearCacheOfFile() {
        if ( cacheEntryUris != null && cacheEntryUris.size() > nextUri ) {
            File entryToBeDeleted = new File( cacheEntryUris.get( nextUri ) );
            if ( entryToBeDeleted.exists() ) {
                try {
                    entryToBeDeleted.delete();
                } catch ( SecurityException e ) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                ++nextUri;
            }
        }
    }

    private void initCacheEntries() {
        cacheEntryUris = new ArrayList<String>();
        nextUri = 0;

    }

    public void cancelAll() {
        clearDownloadService();
    }

    public synchronized void cancelDownload( ApplicationItem item ) {
        item.setStatus( ApplicationItem.CANCELED );
        // if ( currentDownload != null && item.getPackageName().equals(
        // currentDownload.getPackageName() ) ) {
        if ( item == currentDownload ) {
            cancelDownloader();
            startNextDownload();
            updateNotification( "CancelDownload" );
        }
    }

    private synchronized void cancelDownloader() {
        if ( mediaDownloader != null ) {
            mediaDownloader.cancel( true );
            mediaDownloader = null;
        }
    }

    public void clearDownloadService() {
        cancelDownloader();
        totalAppCount = 0;
        updateNotification( "clearDownloadService" );
        applicationItems.clear();
        DashLogger.d(TAG, "Service: Calling stopSelf on DownloadService");
        DownloadService.this.stopSelf();
    }

    private void updateNotification(String from) {

        if ( percentComplete() == 0 && totalAppCount != 0 || "beginDownloading".equalsIgnoreCase( from )) {
            NotificationController.getInstance( this ).showNotification( NotificationController.INSTALLING_APPLICATIONS );
        } 
        
        if ( totalAppCount == 0 ) {
            NotificationController.clearNotification( NotificationController.INSTALLING_APPLICATIONS );
        } else if ( completedCount() < totalAppCount ) {
            NotificationController.getInstance( this ).updateNotification( NotificationController.INSTALLING_APPLICATIONS, this.getResources().getString( R.string.notification_ins_app_title ),
                    percentComplete() );
        } else if ( completedCount() >= totalAppCount ) {
            NotificationController.getInstance( this ).updateNotification( NotificationController.INSTALLING_APPLICATIONS, this.getResources().getString( R.string.notification_ins_app_body_complete ),
                    percentComplete() );
        }
    }

    public void setAppInstallHandle( AppInstallationActivity handle ) {
        appInstallHandle = handle;
    }

    public boolean isCompleted() {
        if ( completedCount() >= totalAppCount ) {
            return true;
        } else {
            return false;
        }
    }
}
