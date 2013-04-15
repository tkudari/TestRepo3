package com.dashwire.config.resources;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ResourceConfigurator;

public class RAWidgetsShortcutsConfiguratorInterface implements ResourceConfigurator {
	
	private static final String TAG = RAWidgetsShortcutsConfiguratorInterface.class.getCanonicalName();

	protected Context mContext;

	protected ConfigurationEvent mConfigurationEvent;

	protected JSONArray mConfigArray;

	@Override
	public void setContext(Context context) {
		mContext = context;
	}

	@Override
	public void setConfigurationEvent(ConfigurationEvent configurationEvent) {
		mConfigurationEvent = configurationEvent;		
	}

	@Override
	public void setConfigDetails(JSONArray configArray) {
		mConfigArray = configArray;		
	}

	@Override
	public void configure() {
	       if (mConfigArray != null && mConfigArray.length() > 0)
	        {
	            Log.v(TAG, "mConfigArray = " + mConfigArray.toString());
	            try
	            {
	                Log.v(TAG,"Inside RAWidgetsShortcutsConfiguratorInterface configure()");

	                Intent featureConfigurationRequestIntent = ConfiguratorIntent.generateIntent("widgets_shortcuts", mConfigArray);
	                mContext.sendBroadcast(featureConfigurationRequestIntent);

	                Log.v(TAG,"broadcasted com.dashwire.feature.intent.CONFIGURE : " + mConfigArray.toString());

	            } catch (Exception e)
	            {
	                mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.FAILED);
	                return;
	            }
	        }
		
	}

	@Override
	public String name() {
		return "widgets_shortcuts";
	}

	  public static class ConfiguratorIntent extends Intent {

	        public ConfiguratorIntent(String name) {
	            super(name);
	        }

	        public static ConfiguratorIntent generateIntent(String featureName, JSONArray configArray) {
	            if (featureName == null || configArray == null) {
	                return null;
	            }

	            ConfiguratorIntent featureConfigurationRequestIntent = new ConfiguratorIntent("com.dashwire.feature.intent.CONFIGURE");
	            featureConfigurationRequestIntent.putExtra("featureId", "NotDefinedYet");
	            featureConfigurationRequestIntent.putExtra("featureName", featureName);
	            featureConfigurationRequestIntent.putExtra("featureData", configArray.toString());
	            featureConfigurationRequestIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
	            return featureConfigurationRequestIntent;
	        }
	    }
}
