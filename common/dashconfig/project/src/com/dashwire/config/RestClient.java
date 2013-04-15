package com.dashwire.config;

import android.content.Context;
import android.net.Uri;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.base.debug.OverridePreferences;
import com.dashwire.base.device.DashSettings;
import com.dashwire.base.device.DashSettingsAndroid;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class RestClient {

    public static final String JSON_HTTP_STATUS_CODE = "http_status_code";
    public static final String JSON_CONFIG_ID = "config_id";
    private static final String TAG = RestClient.class.getCanonicalName();
    private static String deviceId;
    private static String host = "https://ready2go.att.com";
    private static Hashtable<String, String> headers;
    
//    RestClient(){
//        headers.put( "build-release", VERSION.RELEASE );
//        headers.put( "build-incremental", VERSION.INCREMENTAL );
//        headers.put( "build-sdk", VERSION.SDK );
//        headers.put( "build-device", Build.DEVICE );
//        headers.put( "build-manufacturer", Build.MANUFACTURER );
//    }
//

    
    public static String convertStreamToString( InputStream is ) {
        BufferedReader reader = new BufferedReader( new InputStreamReader( is ) );
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ( ( line = reader.readLine() ) != null ) {
                sb.append( line + "\n" );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static JSONObject get( String uri, Context context ) {
    	DashLogger.d(TAG,"Uri: " + uri);
        HttpGet request = new HttpGet( uri );
        return generalRequest( request, context );
    }

    public static JSONObject get( String uri, Hashtable<String, String> headers, Context context ) {
    	DashLogger.d(TAG,"Uri: " + uri);
        HttpGet request = new HttpGet( uri );
        addRequestHeaders( request, headers );
        return generalRequest( request, context );
    }

    public static JSONObject post( String uri, String body, Context context ) {
        try {
        	DashLogger.d(TAG,"Downloading: " + uri);                  
            HttpPost request = new HttpPost( uri );
            StringEntity entity = new StringEntity( body );
            entity.setContentType( new BasicHeader( HTTP.CONTENT_TYPE, "application/json" ) );
            request.setEntity( entity );
            return generalRequest( request, context );
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject generalRequest( HttpRequestBase request, Context context ) {
        addHeadersToRequest( request, context );
        JSONObject json = null;
        HttpResponse response = null;
        try {
            DashLogger.v(TAG, "Http Host = " + request.getURI().getHost());
            HttpParams basicParams = new BasicHttpParams();
            basicParams.setParameter( CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1 );
            HttpClient httpClient = new DefaultHttpClient( basicParams );
            response = httpClient.execute( request );
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            String result = convertStreamToString( instream );
            json = new JSONObject( result );
        } catch ( java.net.UnknownHostException e ) {
            DashLogger.e(TAG, e.getMessage(), e);// no connectivity
        } catch ( UnsupportedEncodingException e ) {
            DashLogger.e(TAG, e.getMessage(), e);
        } catch ( ClientProtocolException e ) {
            DashLogger.e(TAG, e.getMessage(), e);
        } catch ( IOException e ) {
            DashLogger.e(TAG, e.getMessage(), e);
        } catch ( JSONException e ) {
            DashLogger.e(TAG, e.getMessage(), e);
        }

        if ( json == null ) {
            json = new JSONObject();
        }
        if ( response != null ) {
            try {
                json.put( JSON_HTTP_STATUS_CODE, response.getStatusLine().getStatusCode() );
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
        }
        return json;
    }

    private static void addRequestHeaders( HttpRequest request, Hashtable<String, String> headers ) {
        Enumeration<String> keys = headers.keys();
        while ( keys.hasMoreElements() ) {
            String key = ( String ) keys.nextElement();
            request.addHeader( key, ( String ) headers.get( key ) );
        }
    }
    
    private static void addHeadersToRequest( HttpRequest request, Context context ) {
//        Hashtable<String, String> headers = setupHeaders(context);
//        Enumeration<String> keys = headers.keys();
//        while ( keys.hasMoreElements() ) {
//            String key = ( String ) keys.nextElement();
//            request.addHeader( key, ( String ) headers.get( key ) );
//        }
        addHeaders(request, context);
    }

    @SuppressWarnings("unused")
    private static void addRequestParams( HttpRequest request, Hashtable<String, String> params ) {
        HttpParams basicParams = new BasicHttpParams();
        Enumeration<String> keys = params.keys();
        while ( keys.hasMoreElements() ) {
            String key = ( String ) keys.nextElement();
            basicParams.setParameter( key, ( String ) params.get( key ) );
        }
        request.setParams( basicParams );
    }

    public static String getDefaultHost(Context context) {
        if ( OverridePreferences.getBoolean( context, DashSettings.PROPERTY_BOOLEAN_OVERRIDE_SERVER, false ) ) {
            String overrideHost = OverridePreferences.getString( context, DashSettings.PROPERTY_STRING_OVERRID_SERVER_HOST, "" );
            if(overrideHost != null && overrideHost.length() > 0) {
                host = overrideHost;
            }
        }
        return host;
    }
    
    public static String getHost()
    {
        return host;
    }
    
    public static void overrideUri(Uri overridingUri) {
    	host = overridingUri.getScheme() + "://" + overridingUri.getHost();
    }

    public static int getStatusCode( JSONObject response ) {
        try {
            return response.getInt( RestClient.JSON_HTTP_STATUS_CODE );
        } catch ( JSONException e ) {
            return 500;
        }
    }

    static void addHeaders(HttpRequest request, Context context) {
        DashSettings dashSettings = new DashSettingsAndroid(context);
        String value = dashSettings.getPhoneNumber();
        if (value != null && !value.equals(""))
            request.setHeader("phone-number", value);

        value = dashSettings.getClientVersion();
        if (value != null && !value.equals(""))
            request.setHeader("User-Agent", "Dashconfig " + value);
        else
            request.setHeader("User-Agent", "Dashconfig");

        value = dashSettings.getIMEI();
        if (value != null && !value.equals(""))
            request.setHeader("device-id", value);

        value = dashSettings.getBuildRelease();
        if (value != null && !value.equals(""))
            request.setHeader("build-release", value);

        value = dashSettings.getBuildIncremental();
        if (value != null && !value.equals(""))
            request.setHeader("build-incremental", value);

        value = dashSettings.getBuildSDK();
        if (value != null && !value.equals(""))
            request.setHeader("build-sdk", value);

        value = dashSettings.getBuildDevice();
        if (value != null && !value.equals(""))
            request.setHeader("build-device", value);

        value = dashSettings.getBuildManufacturer();
        if (value != null && !value.equals(""))
            request.setHeader("build-manufacturer", value);

        value = dashSettings.getAndroidId();
        if (value != null && !value.equals(""))
            request.setHeader("android-id", value);

        value = dashSettings.getTrackingId();
        if (value != null && !value.equals(""))
            request.setHeader("tracking-id", value);
    }

//    private static Hashtable<String, String> setupHeaders(Context context) {
//        DashLogger.v( TAG, "setupHeaders ---------------------------------------------------------------" );
//        headers = new Hashtable<String, String>();
//        String phoneNumber = loadPhoneNumber( context );
//        if ( phoneNumber != null ) {
//            headers.put( "phone-number", phoneNumber );
//        }
//        String androidId = Secure.getString( context.getContentResolver(), Secure.ANDROID_ID );
//        if ( androidId == null ) {
//            androidId = "";
//        }
//
//        String versionName = "";
//        try {
//            PackageInfo packageInfo = context.getPackageManager().getPackageInfo( context.getPackageName(), 0 );
//            versionName = packageInfo.versionName;
//        } catch ( NameNotFoundException ex ) {
//            DashLogger.e( TAG, ex.toString() );
//        }
//
//        TelephonyManager telephonyManager = ( TelephonyManager ) context.getSystemService( Context.TELEPHONY_SERVICE );
//        String imei = telephonyManager.getDeviceId();
//
//        if ( imei != null ) {
//            deviceId = imei;
//        } else {
//            deviceId = "";
//        }
//
//        headers.put( "User-Agent", "Dashconfig " + versionName );
//        headers.put( "device-id", deviceId );
//        headers.put( "build-release", VERSION.RELEASE );
//        headers.put( "build-incremental", VERSION.INCREMENTAL );
//        headers.put( "build-sdk", VERSION.SDK );
//        headers.put( "build-device", Build.DEVICE );
//        headers.put( "build-manufacturer", Build.MANUFACTURER );
//        headers.put( "android-id", androidId );
//        DashLogger.v( TAG, "HTTP Headers = " + headers.toString() );
//        DashLogger.v( TAG, "setupHeaders end ---------------------------------------------" );
//        return headers;
//    }
//
//    private static String loadPhoneNumber( Context context ) {
//        TelephonyManager telephonyManager = ( TelephonyManager ) context.getSystemService( Context.TELEPHONY_SERVICE );
//        String phoneNumber = telephonyManager.getLine1Number();
//        if ( OverridePreferences.getBoolean( context, "overridePhone", false ) ) {
//            phoneNumber = OverridePreferences.getString( context, "overridePhoneNumber", phoneNumber );
//        }
//        return phoneNumber;
//    }


}
