package com.dashwire.config.configuration;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dashwire.base.debug.DashLogger;

public class DefaultConfigurationService extends ConfigurationService {

	protected void configureFeatures(JSONObject configJSONObject) {
		try {
			for (String nodeName : sNodeNames) {
				if (configJSONObject.has(nodeName)) {
					JSONArray configArray = new JSONArray();
					if ("easy".equalsIgnoreCase(nodeName)) {
						if (configJSONObject.getBoolean("easy")) {
							JSONObject easyModeJSONObject = new JSONObject(
									"{easy:[true]}");
							easyModeJSONObject.toJSONArray(configArray);
						}
					} else {
						configArray = configJSONObject.getJSONArray(nodeName);
					}
					pushResourceToStack(nodeName, configArray);
				}
			}
		} catch (Exception e) {
			DashLogger.v(TAG,
					"Exception in configureFeatures = " + e.getMessage());
			mConfigurationEvent.notifyEvent("general",
					ConfigurationEvent.FAILED);
		}
	}
}
