package com.dashwire.config.resources;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ResourceConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public final class WifiConfigurator implements ResourceConfigurator {
    protected static final String TAG = WifiConfigurator.class.getCanonicalName();
    private JSONArray mNetworks;
    private BroadcastReceiver mWifiReceiver;

    protected Context mContext;

    protected ConfigurationEvent mConfigurationEvent;

    protected JSONArray mConfigArray;

    private final int retryCount = 2;
    private boolean tryResult = false;

    public WifiConfigurator() {
    }

    public String name() {
        return "networks";
    }

    @SuppressWarnings( "unused" )
    private void setItems( JSONArray items ) {
        this.mNetworks = items;
        enableWifi();
    }

    public void enableWifi() {

        WifiManager manager = ( WifiManager ) mContext.getSystemService( Context.WIFI_SERVICE );
        int state = manager.getWifiState();

        if ( state == WifiManager.WIFI_STATE_ENABLED ) {
            setupNetworks( retryCount );
        } else {
            manager.setWifiEnabled( true );
            mWifiReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive( Context context, Intent intent ) {
                    WifiManager manager = ( WifiManager ) context.getSystemService( Context.WIFI_SERVICE );
                    DashLogger.v( TAG, "checking for wifi" );
                    if ( manager.isWifiEnabled() ) {
                        DashLogger.v( TAG, "wifi enabled" );
                        setupNetworks( retryCount );
                    } else {
                        DashLogger.v( TAG, "wifi not enabled" );
                        mConfigurationEvent.notifyEvent( name(), ConfigurationEvent.FAILED );
                    }
                }
            };
            // TODO: SECURITY Add component or permission filter.
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction( WifiManager.WIFI_STATE_CHANGED_ACTION );

            mContext.registerReceiver( mWifiReceiver, intentFilter );
            // TODO: SECURITY Change back to this one once the sysuid and
            // paltform key is done
            // mContext.registerReceiver(mWifiReceiver, intentFilter,
            // "android.permission.CHANGE_NETWORK_STATE", null);

            DashLogger.v(TAG, "done");
        }
    }

    private void setupNetworks( int retryCount ) {
        if ( mWifiReceiver != null ) {
            mContext.unregisterReceiver( mWifiReceiver );
            mWifiReceiver = null;
        }
        try 
        {
            for ( int index = 0; index < mNetworks.length(); index++ ) 
            {
                JSONObject item = mNetworks.getJSONObject( index );
                int tryCount = 0;
                do
                {
                    tryCount ++;
                    tryResult = createNetwork( item, index == 0 );
                }while( !tryResult && tryCount <= retryCount);
            }
        } catch ( JSONException je ) {
            DashLogger.v( TAG, "JSON Exception : " + je.getMessage() );
            mConfigurationEvent.notifyEvent( name(), ConfigurationEvent.FAILED, je );
            return;
        }
        mConfigurationEvent.notifyEvent( name(), tryResult ? ConfigurationEvent.CHECKED : ConfigurationEvent.FAILED );
    }

    private boolean createNetwork( JSONObject item, boolean enable ) {
        try {
            String ssid = item.getString( "ssid" );
            String security = item.getString( "security" );
            String key = item.getString( "key" );

            return setupWifi( ssid, key, security, enable );
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setupWifi( String ssid, String key, String security, boolean enable ) {
        WifiManager manager = ( WifiManager ) mContext.getSystemService( Context.WIFI_SERVICE );
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";

        List<WifiConfiguration> configuredNetworks = manager.getConfiguredNetworks();

        for ( WifiConfiguration configuredNetwork : configuredNetworks ) {
            if ( config.SSID.equalsIgnoreCase( configuredNetwork.SSID ) ) {
                manager.removeNetwork( configuredNetwork.networkId );
            }
        }

        // TODO wep security
        if ( "none".equals( security ) ) {
            config.allowedKeyManagement.set( KeyMgmt.NONE );
        } else if ( "wep".equals( security ) ) {
            config.wepKeys[ 0 ] = key;
            config.wepTxKeyIndex = 0;
            config.priority = 40;
            config.allowedKeyManagement.set( WifiConfiguration.KeyMgmt.NONE );
            config.allowedProtocols.set( WifiConfiguration.Protocol.RSN );
            config.allowedProtocols.set( WifiConfiguration.Protocol.WPA );
            config.allowedAuthAlgorithms.set( WifiConfiguration.AuthAlgorithm.OPEN );
            config.allowedAuthAlgorithms.set( WifiConfiguration.AuthAlgorithm.SHARED );
            config.allowedGroupCiphers.set( WifiConfiguration.GroupCipher.WEP40 );
            config.allowedGroupCiphers.set( WifiConfiguration.GroupCipher.WEP104 );
            config.allowedGroupCiphers.set( WifiConfiguration.GroupCipher.CCMP );
            config.allowedGroupCiphers.set( WifiConfiguration.GroupCipher.TKIP );
            config.allowedPairwiseCiphers.set( WifiConfiguration.PairwiseCipher.CCMP );
            config.allowedPairwiseCiphers.set( WifiConfiguration.PairwiseCipher.TKIP );
        } else {
            config.allowedKeyManagement.set( KeyMgmt.WPA_PSK );
            config.allowedAuthAlgorithms.set( AuthAlgorithm.OPEN );
            config.preSharedKey = "\"" + key + "\"";
        }

        int newNetworkId = manager.addNetwork( config );


        if ( newNetworkId != -1 ) {
            boolean newNetworkEnabledFlag = false;
            if ( manager.startScan() ) {
                List<ScanResult> accessPoints = manager.getScanResults();
                if ( accessPoints != null ) {
                    for ( ScanResult accessPoint : accessPoints ) {
                        String newSSID = removeChar( config.SSID, '"' );
                        if ( newSSID.equalsIgnoreCase( accessPoint.SSID ) ) {
                            newNetworkEnabledFlag = manager.enableNetwork( newNetworkId, true );
                        }
                    }
                }
            }
            if ( !newNetworkEnabledFlag ) {
                DashLogger.d( TAG, "Wifi not enabled: id =  " + newNetworkId );
                manager.enableNetwork( newNetworkId, false );
            }
            manager.reassociate();
            DashLogger.d( TAG, "Saving wifi configuration: security = " + security );
            return manager.saveConfiguration();
        } else {
            return false;
        }
    }

    public static String removeChar( String s, char c ) {
        String r = "";
        for ( int i = 0; i < s.length(); i++ ) {
            if ( s.charAt( i ) != c )
                r += s.charAt( i );
        }
        return r;
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
        this.mNetworks = mConfigArray;
        enableWifi();
    }
}
