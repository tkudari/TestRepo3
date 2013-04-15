package com.dashwire.config.configuration;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.DashconfigApplication;
import com.dashwire.config.DeviceContext;
import com.dashwire.config.util.CommonUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Stack;

public abstract class ConfigurationService extends IntentService {

	public static final String[] sNodeNames = { "easy", "wallpapers",
			"contacts", "social", "bookmarks", "ringtones", "shortcuts",
			"networks", "notifications", "accounts" };

	protected static final String TAG = ConfigurationService.class.getCanonicalName();

	protected Context mContext;

	protected ConfigurationEvent mConfigurationEvent;

	private Stack<ResourceConfigurator> mResources = new Stack<ResourceConfigurator>();

	public ConfigurationService() {
		super("default");
	}

	public ConfigurationService(String name) {
		super(name);
	}

	protected abstract void configureFeatures(JSONObject configJSONObject);

	@Override
	protected void onHandleIntent(Intent intent) {
		mContext = this;
		DashLogger
				.v(TAG,
						"Service: onStartCommand ----------------------------------------");

		mConfigurationEvent = new ConfigurationEvent() {
			@Override
			public void notifyEvent(String name, int code) {
				notifyEvent(name, code, null);
			}

			@Override
			public void notifyEvent(String name, int code, Throwable e) {
				if (equals(e != null)) {
					e.printStackTrace();
				}
				Intent i = new Intent(ACTION_CONFIGURATION);
				i.setData(Uri.parse("dashconfig://?name=" + name + "&code="
						+ code));
				mContext.sendBroadcast(i, "com.dashwire.config.PERM_CONFIG");
			}
		};
		try {
			JSONObject configJSONObject = new JSONObject(
					intent.getStringExtra("config"));

			ContentResolver.setMasterSyncAutomatically(true);
			configureApplications(configJSONObject);
			configureFeatures(configJSONObject);
			for(ResourceConfigurator rc : mResources) {
				DashLogger.v(TAG, "Iterating over resources: " + rc.name());
				try {
					rc.configure();
				} catch (Exception e) {
					DashLogger.e(TAG, "Exception in ConfigurationTask = " + e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			DashLogger
					.v(TAG, "Exception in onStartCommand = " + e.getMessage());
			mConfigurationEvent.notifyEvent("general",
					ConfigurationEvent.FAILED);
		}
	}

	protected ResourceConfigurator pushResourceToStack(String nodeName,
			JSONArray configArray) {
		DeviceContext deviceContext = DashconfigApplication.getDeviceContext();
		ResourceConfigurator resource = deviceContext.getConfiguratorFactory()
				.getResourceConfigurator(nodeName);
		if (resource == null) {
			DashLogger
					.e(TAG,
							"No configurator defined for type "
									+ nodeName
									+ " for this device. This is a development of ProGuard issue.");
		}
		resource.setContext(mContext);
		resource.setConfigurationEvent(mConfigurationEvent);
		resource.setConfigDetails(configArray);
		mResources.push(resource);
		return resource;
	}

	private void configureApplications(JSONObject configJSONObject)
			throws JSONException {
		if (configJSONObject.has("apps")) {
			JSONArray applicationArray = configJSONObject.getJSONArray("apps");
			if (applicationArray != null && applicationArray.length() > 0) {
				CommonUtils.setDownloads(this, applicationArray.toString());
				mConfigurationEvent.notifyEvent("apps",
						ConfigurationEvent.CHECKED);
			}
		}
	}

	private class ConfigurationTask extends
			AsyncTask<ResourceConfigurator, Void, Void> {

		@Override
		protected Void doInBackground(ResourceConfigurator... params) {
			try {
				ResourceConfigurator resource = params[0];
				DashLogger.v(TAG, "doInBackground: " + resource.name());
				resource.configure();
			} catch (Exception e) {
				DashLogger.e(TAG,
                        "Exception in ConfigurationTask - doInBackground() = "
                                + e.getMessage(), e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (!mResources.isEmpty()) {
				new ConfigurationTask().execute(mResources.pop());
			}
		}
	}
}