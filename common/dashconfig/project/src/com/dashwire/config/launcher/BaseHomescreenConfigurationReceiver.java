package com.dashwire.config.launcher;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.dashwire.base.debug.DashLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseHomescreenConfigurationReceiver extends BroadcastReceiver {
    private static final String TAG = BaseHomescreenConfigurationReceiver.class.getCanonicalName();

	public static final String OPTIONS_TYPE = "type";
	public static final String OPTIONS_OEM_TYPE= "oemType";
	public static final String OPTIONS_TITLE = "title";// Not used
	public static final String OPTIONS_PACKAGE_NAME = "packageName";
	public static final String OPTIONS_CLASS_NAME = "className";
    public static final String OPTIONS_EXTRAS = "extras";
	public static final String OPTIONS_CONTAINER = "container";
	public static final String OPTIONS_SCREEN = "screen";
	public static final String OPTIONS_X = "x";
	public static final String OPTIONS_Y = "y";
	public static final String OPTIONS_ROWS = "rows";
	public static final String OPTIONS_COLUMNS = "cols";
	public static final String OPTIONS_URI = "uri";
	public static final String ITEM_TYPE_WIDGET = "widget";
	public static final String ITEM_TYPE_SHORTCUT = "shortcut";


	/**
	 * Version of this API
	 */
	public static final String OPTIONS_VERSION = "version";

	/**
	 * The protocol version of this service is incorrect
	 */
	public static final String RESULT_INVALID_VERSION = "RESULT_INVALID_VERSION";

	public static final String RESULT_MISSING_REQUIRED_PARAMETER = "RESULT_MISSING_REQUIRED_PARAMETER";

	public static final String RESULT_INVALID_JSON = "RESULT_INVALID_JSON";

	/**
	 * Unknown email failure
	 */
	public static final String RESULT_UNKNOWN = "RESULT_UNKNOWN";

	/**
	 * Email account creation success
	 */
	public static final int RESULT_CODE_SUCCESS = 0x0;

	/**
	 * Email account creation failure
	 */
	public static final int RESULT_CODE_FAILURE = 0x1;

	private static final Float SUPPORTED_VERSION = 1.0f;// TODO: supported
														// version	
	
	protected Context mContext;

	protected LauncherModel mLauncherModel;

	
	public abstract LauncherModel getLauncherModel();

	@Override
	public void onReceive(Context context, Intent intent) {
		DashLogger.i("Homescreen", "starting configuration");
		mContext = context;
		mLauncherModel = getLauncherModel();
		
		if(intent.hasExtra("wallpaperUri")) {
			installWallpaper(context, intent.getStringExtra("wallpaperUri"));
		}
		
		List<Bundle> bundles = null;
		if (intent.hasExtra("homescreen")) {
			bundles = fromIntentToBundles(intent);

			if (bundles.isEmpty() && !intent.hasExtra("wallpaperUri")) {
				sendResponse(context, null, false, RESULT_INVALID_JSON);
			}
		} else {
			sendResponse(context, null, false,
					RESULT_MISSING_REQUIRED_PARAMETER);
			return;
		}
		mLauncherModel.killLauncher();
		mLauncherModel.clearDesktop();
		
		boolean hasError = false;
 
		JSONArray messages = new JSONArray();
		
		for (Bundle options : bundles) {
			JSONObject payload = null;
			try {
				payload = new JSONObject(options.getString("jsonObject"));
			} catch (JSONException e) {
				hasError = true;
				continue;
			}

			if (hasRequiredOptions(payload)) {
                String containerType = "desktop";
                String itemType = null;
				String oemType = null;
				try {
                    containerType = payload.getString(OPTIONS_CONTAINER);
                    itemType = payload.getString(OPTIONS_TYPE);
					if(payload.has(OPTIONS_OEM_TYPE)) {
						oemType =  payload.getString(OPTIONS_OEM_TYPE);
					}
				} catch (JSONException e1) {
					hasError = true;
					continue;
				}

                String extras = null;
                if (options.containsKey(OPTIONS_EXTRAS))
                    extras = options.getString(OPTIONS_EXTRAS);
                DashLogger.d(TAG, "Extra value is set to: " + extras);
				
				ComponentName cn = new ComponentName(
						options.getString(OPTIONS_PACKAGE_NAME),
						options.getString(OPTIONS_CLASS_NAME));

				if (itemType.equals(ITEM_TYPE_WIDGET)) {
					installWidget(context, cn, extras,
							options.getString(OPTIONS_URI, null),
							options.getInt(OPTIONS_SCREEN, -1), containerType,
							options.getInt(OPTIONS_X, -1),
							options.getInt(OPTIONS_Y, -1),
							options.getInt(OPTIONS_COLUMNS, -1),
							options.getInt(OPTIONS_ROWS, -1), oemType, true);
				} else if (itemType.equals(ITEM_TYPE_SHORTCUT)) {
					installShortCut(context, cn,
							options.getInt(OPTIONS_SCREEN, -1), containerType,
							options.getInt(OPTIONS_X, -1),
							options.getInt(OPTIONS_Y, -1));
				}

				messages.put(buildResponse(options, true, 0, "OK"));
			} else {
				hasError = true;
				messages.put(buildResponse(options, true, 0,
						RESULT_MISSING_REQUIRED_PARAMETER));
			}
		}
		mLauncherModel.killLauncher();
		mLauncherModel.store();

        if (!mLauncherModel.usesExternalConfigurator())
		    sendResponse(context, messages, !hasError, null);
        else
            mLauncherModel.activateExternalConfigurator();
	}

	private void sendResponse(Context context, JSONArray items,
			boolean isSuccess, String errorMessage) {
		Intent result = new Intent(
				"com.dashwire.config.launcher.CONFIGURE_HOMESCREEN_RESULT");
		result.putExtra("success", isSuccess);
		result.putExtra("version", "1.0");// TODO - hard-coded
		result.putExtra("intent", "com.android.homescreen.CONFIGURE_HOMESCREEN");

		if (items != null) {
		    DashLogger.d("Homescreen", items.toString());
			result.putExtra("homescreen", items.toString());
		} else {
			result.putExtra("errorCode", 0);
			if (errorMessage != null) {
				result.putExtra("errorMessage", errorMessage);
				DashLogger.i("Homescreen", errorMessage);
			}
		}

		context.sendBroadcast(result);
	}

	private JSONObject buildResponse(Bundle options, boolean isSuccess,
			int errorCode, String errorMessage) {

		try {
			JSONObject payload = new JSONObject(options.getString("jsonObject"));
			payload.put("success", (isSuccess ? RESULT_CODE_SUCCESS
					: RESULT_CODE_FAILURE));
			payload.put("errorCode", errorCode);
			payload.put("errorMessage", errorMessage);
			return payload;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean hasRequiredOptions(JSONObject options) {
		boolean hasOptions = options.has(OPTIONS_TYPE)
				&& options.has(OPTIONS_PACKAGE_NAME)
				&& options.has(OPTIONS_CLASS_NAME)
				&& options.has(OPTIONS_CONTAINER);

		if (hasOptions && options.has(OPTIONS_CONTAINER)) {
			return options.has(OPTIONS_SCREEN);
		}
		
		return hasOptions;
	}

	private List<Bundle> fromIntentToBundles(Intent intent) {
		try {
			JSONArray bundledArray = new JSONArray(
					intent.getStringExtra("homescreen"));
			DashLogger.d("fromIntentToBundles",
					"bundledArray = " + bundledArray.toString());
			if (bundledArray != null) {
				return fromJsonToBundles(bundledArray);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new ArrayList<Bundle>();
	}

	private List<Bundle> fromJsonToBundles(JSONArray items) {
		ArrayList<Bundle> bundles = new ArrayList<Bundle>();

		try {
			for (int index = 0; index < items.length(); index++) {
				JSONObject item = items.getJSONObject(index);
				Bundle bundle = new Bundle();

				bundle.putString("jsonObject", item.toString());
				addOptionString(OPTIONS_TYPE, item, bundle);
				addOptionString(OPTIONS_PACKAGE_NAME, item, bundle);
				addOptionString(OPTIONS_CLASS_NAME, item, bundle);
                addOptionString(OPTIONS_EXTRAS, item, bundle);
				addOptionString(OPTIONS_URI, item, bundle);
				
				addOptionInt(OPTIONS_CONTAINER, item, bundle);
				addOptionInt(OPTIONS_SCREEN, item, bundle);
				addOptionInt(OPTIONS_X, item, bundle);
				addOptionInt(OPTIONS_Y, item, bundle);
				addOptionInt(OPTIONS_ROWS, item, bundle);
				addOptionInt(OPTIONS_COLUMNS, item, bundle);
				
				bundles.add(bundle);
			}
		} catch (JSONException je) {
			je.printStackTrace();
		}
		DashLogger.d("Homescreen", "fromJsonToBundles.bundles = " + bundles.toString());
		return bundles;

	}

	private void addOptionString(String option, JSONObject item, Bundle bundle) {
		if (!item.isNull(option)) {
			try {
				bundle.putString(option, item.getString(option));
			} catch (JSONException e) {
			}
		}
	}

	private void addOptionInt(String option, JSONObject item, Bundle bundle) {
		if (!item.isNull(option)) {
			try {
				bundle.putInt(option, item.getInt(option));
			} catch (JSONException e) {
			}
		}
	}

	private void installWidget(Context context,
                               ComponentName cn, String extras, String uri, int screen, String container, int xCoOd,
                               int yCoOd, int spanX, int spanY, String oemType, boolean notify) {

		mLauncherModel.addAppWidget(context, cn, extras, uri, container, screen,
				xCoOd, yCoOd, spanX, spanY, oemType);

	}

	/**
	 * Returns true if version protocol is supported, otherwise false
	 * 
	 * @param options
	 *            email account options
	 * @return true if version protocol is supported, otherwise false
	 */
	private static boolean isVersionProtocolSupported(Bundle options) {
		// TODO: Wouldn't this be better as a float: options.getFloat?
		try {
			Float version = Float.valueOf(options.getString(OPTIONS_VERSION,
					"0.0"));
			return !version.equals(0.0f) && (version <= SUPPORTED_VERSION);
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private boolean installShortCut(Context context, ComponentName cn,
			int screen, String container, int xCoOd, int yCoOd) {
		mLauncherModel.addShortcut(context, cn, container,
					screen, xCoOd, yCoOd);
		return true;

	}
	
	private boolean installWallpaper(Context context, String wallpaperUri) {
		mLauncherModel.addWallpaper(context, wallpaperUri);
		return true;

	}
}
