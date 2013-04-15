package com.dashwire.config.configuration;

import org.json.JSONObject;
import com.dashwire.base.debug.DashLogger;

public class NewConfigurationService extends ConfigurationService {

    // Rover code had shortcuts removed from NodeNames list
    // Sense 4.0 requires shortcuts
    // What do we need to to support. Defaulting to having it in both lists
    // Also, does this break Rover?
	private static final String[] sLNodeNames = { "wallpapers", "contacts", "social",
			"bookmarks", "ringtones", "networks", "notifications", "accounts", "htc_feeds", "shortcuts" };

	@Override
	protected void configureFeatures(JSONObject configJSONObject) {
		try {

			for (String nodeName : sLNodeNames) {
				if (configJSONObject.has(nodeName)) {
					pushResourceToStack(nodeName,
							configJSONObject.getJSONArray(nodeName));
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
