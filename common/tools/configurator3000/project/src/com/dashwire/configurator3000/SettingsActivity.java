package com.dashwire.configurator3000;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getCanonicalName();
    
    private boolean forceClose = false;

    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
       
        PreferenceManager preferenceManager = getPreferenceManager();
        String preferencesName = preferenceManager.getSharedPreferencesName();
        preferenceManager.setSharedPreferencesMode( MODE_WORLD_READABLE | MODE_MULTI_PROCESS );
        preferenceManager.setSharedPreferencesName( "configurator3000" );
        Log.v( TAG, "getSharedPreferencesName: " + preferencesName );
        addPreferencesFromResource( R.xml.settings );  
        
        Preference pref = findPreference("Communications");
        pref.setEnabled(true);
        
        SharedPreferences sp = pref.getSharedPreferences();
        Bundle extras = getIntent().getExtras();
        
        if(extras != null && extras.containsKey("env")) {
        	String env = extras.getString("env");
        	String server = null;
        	if("dev".equalsIgnoreCase(env)) {
        		server = "http://ready2go.dev.dashlab.net";
        	} else if("qa".equalsIgnoreCase(env)) {
        		server = "http://ready2go.qa.dashlab.net";
        	} else if("demo".equalsIgnoreCase(env)) {
        		server = "https://ready2go.demo.dashlab.net";       		
        	} else if("prod".equalsIgnoreCase(env)) {
        		server = "https://dashconfig.com";
        	}
        	Log.d(TAG, "Detected environment change: " + server);
        	forceClose = true;
        	writeTargetServer(server, sp);
        }
        
    }
    
    private void writeTargetServer(String server, SharedPreferences sp) {
        sp.edit().putString("overrideServerHost", 
        		server).commit();        
    }
    
    @Override
	protected void onStart() {
		super.onStart();

        if(forceClose || "com.dashwire.configurator.SETTINGS"
        		.equals(getIntent().getAction())) {
        	finish();
        }
	}

	private void setSummary( String key ) {
        ListPreference list = ( ListPreference ) findPreference( key );
        list.setSummary( list.getEntry() ); 
    }

    @Override
    protected void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener( this );
        super.onPause();
    }

    @Override
    protected void onResume() {
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );
        setSummary( "overrideServerHost" );
        setSummary( "overrideConfigId" );
        setSummary( "overridePhoneNumber" );
        setSummary( "overridePairingTimeoutValue" );
        setSummary( "overrideConnectedTimeoutValue" );
        setSummary( "overrideCodeExpirationValue" );
        setSummary( "OverrideFeatureValue" );
        super.onResume();
    }

    public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key ) {
        Preference preference = findPreference( key );
        if ( preference instanceof ListPreference ) {
            setSummary( key );
        }
    }
}
