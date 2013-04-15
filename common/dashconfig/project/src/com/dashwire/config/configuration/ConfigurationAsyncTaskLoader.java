package com.dashwire.config.configuration;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.base.debug.OverridePreferences;
import com.dashwire.config.R;
import com.dashwire.config.RestClient;
import org.json.JSONObject;

import java.util.Hashtable;

public class ConfigurationAsyncTaskLoader extends AsyncTaskLoader<Integer> {
	
	public JSONObject configuration;
	private final String TAG = ConfigurationAsyncTaskLoader.class.getCanonicalName();
	protected Context mContext;
	
	public ConfigurationAsyncTaskLoader(Context context) {
		super(context);
		mContext = context;
	}
	
	@Override
	protected void onStartLoading() {
		forceLoad();
		super.onStartLoading();
	}

	@Override
	public Integer loadInBackground() {
        try {           
            Hashtable<String, String> headers = new Hashtable<String, String>();
            headers.put( "config-id", getConfigId() );
            String encryptedConfiguration = RestClient.get( RestClient.getHost() + mContext.getResources().getString( R.string.uri_config ), headers, mContext ).getString("config");
            configuration = ConfigurationDecrypter.decryptConfiguration(
					encryptedConfiguration,
					mContext);   

        } catch ( Exception e ) {
            DashLogger.v(TAG, "ConfigurationDownloader - doInBackground()");
            return -1;
        }
        return 200;
	}
	
    private String getConfigId() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(  mContext );
        String configId = settings.getString( "config_id", null );
        if ( OverridePreferences.getBoolean( mContext, "overrideConfig", false ) ) {
            configId = OverridePreferences.getString( mContext, "overrideConfigId", configId );
        }
        return configId;
    }

}
