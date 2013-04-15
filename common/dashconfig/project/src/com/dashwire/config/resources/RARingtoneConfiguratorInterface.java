package com.dashwire.config.resources;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ResourceConfigurator;

public class RARingtoneConfiguratorInterface implements ResourceConfigurator
{

	private static final String TAG = RARingtoneConfiguratorInterface.class.getCanonicalName();

	protected Context mContext;

	protected ConfigurationEvent mConfigurationEvent;

	protected JSONArray mConfigArray;
	
	public RARingtoneConfiguratorInterface()
	{
	}
	
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
			Log.v(TAG,"mConfigArray = " + mConfigArray.toString());
			try
			{
				Log.v(TAG,"Inside RARingtoneConfiguratorInterface configure()");

				Intent featureConfigurationRequestIntent = new Intent("com.dashwire.feature.intent.CONFIGURE");
				featureConfigurationRequestIntent.putExtra("featureId", "NotDefinedYet");
				featureConfigurationRequestIntent.putExtra("featureName", "Ringtone");
				featureConfigurationRequestIntent.putExtra("featureData", mConfigArray.toString());
				featureConfigurationRequestIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
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
		return "ringtones";
	}
}
