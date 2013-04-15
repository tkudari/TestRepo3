package com.dashwire.config.tracking;

import com.dashwire.base.device.DashSettings;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.dashwire.config.tasks.TrackerTask;

public class Tracker {

    private static String trackingUri;
    private static boolean trackingUriLoaded = false;
    private static String trackingId;
    private static boolean trackingIdLoaded = false;
    private static String deviceId;
    private static boolean deviceIdLoaded = false;
    private static HttpClient httpClient;

    public static void track( Context context, String tag ) {
        String trackingUri = getTrackingUri( context );
        String imei = getDeviceId( context );
        if ( trackingUri != null ) {
            String uri = trackingUri + "/" + tag + "?tid=" + getTrackingId( context ) ;
            if (!"".equalsIgnoreCase( imei ))
            {
                uri = uri + "?did=" + imei;
            }
            new TrackerTask( getHttpClient() ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uri);
        }
    }

	private static HttpClient getHttpClient() {
		if (httpClient == null) {
			HttpParams httpParams = new BasicHttpParams();
			httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);
			HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
			HttpConnectionParams.setSoTimeout(httpParams, 3000);
			httpClient = new DefaultHttpClient(httpParams);
			ClientConnectionManager mgr = httpClient.getConnectionManager();
			HttpParams params = httpClient.getParams();
			httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params,
					mgr.getSchemeRegistry()), params);

		}
		return httpClient;
	}

    private static String getTrackingUri( Context context ) {
        if ( !trackingUriLoaded  ) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
            trackingUri = settings.getString( DashSettings.PROPERTY_STRING_TRACKING_URI, null );
            if (trackingUri != null)
            {
                trackingUriLoaded = true;
            }    
        }
        return trackingUri;
    }
    
    private static String getTrackingId( Context context ) {
        if ( !trackingIdLoaded  ) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
            trackingId = settings.getString( DashSettings.PROPERTY_STRING_TRACKING_ID, null );
            if (trackingId != null)
            {
                trackingIdLoaded = true;
            } 
        }
        return trackingId;
    }
    
    private static String getDeviceId( Context context ) {
        if ( !deviceIdLoaded  ) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
            deviceId = settings.getString( DashSettings.PROPERTY_STRING_DEVICE_ID, null );
            if (deviceId != null)
            {
                deviceIdLoaded = true;
            } 
        }
        return deviceId;
    }
    
    public static void resetTrackingId()
    {
        trackingUriLoaded = false;
        trackingIdLoaded = false;
    }
}
