package com.dashwire.config.resources;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.DashconfigApplication;
import com.dashwire.config.util.CommonConstants;
import com.dashwire.config.util.CommonUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Widget {

	public static final String TAG = Widget.class.getCanonicalName();
	private Context context;

	private ArrayList<JSONObject> widgetsAndShortcutsList;
	private long mMaxId = 65535L;
	protected ContentResolver mContentResolver;
	private int resultStatus = CommonConstants.FAILED;
	public JSONArray failedList = new JSONArray();

	public Widget(Context context) {
		this.context = context;
	}

	public Widget(Context context, ArrayList<JSONObject> widgetsAndShortcuts) {
		DashLogger.v(TAG, "Widget()");
		this.context = context;
		this.widgetsAndShortcutsList = widgetsAndShortcuts;
		this.mContentResolver = context.getContentResolver();
	}

	public int bindSamsungWidgetsOrShortcuts(JSONObject item) {
		try {

			String category = item.getString("category");
			if ("Widgets".equalsIgnoreCase(category)) {
				String widgetId = item.getString("id");
				Context packageContext = context.createPackageContext(
						DashconfigApplication.getDeviceContext()
								.getStringConst(context, "TWLAUNCHER_COMP"), 0);
				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(packageContext);
				String packageName = CommonUtils.extractPackage(widgetId);
				String providerName = CommonUtils.extractProvider(widgetId);
				ComponentName widget = new ComponentName(packageName,
						providerName);
				int id = allocateWidgetId();
				item.put("widgetId", id);
				if (CommonUtils.isAndroidWidget(item)) {
					appWidgetManager.bindAppWidgetId(id, widget);
					createAndroidWidget(item);
				} else if (CommonUtils.isTouchWizWidget(item)) {
					createTouchWizWidget(item);
				}

			} else if ("Shortcuts".equalsIgnoreCase(category)) {
				createShortcut(item);
			}

		} catch (NameNotFoundException e) {
			DashLogger.e(TAG, "unknown package: " + e.toString());
			return CommonConstants.FAILED;
		} catch (JSONException e) {
			DashLogger.e(TAG, "JSONException in bindWidget " + e.toString());
			return CommonConstants.FAILED;
		} catch (Exception e) {
			DashLogger.e(TAG, "Exception in bindWidget " + e.toString());
			return CommonConstants.FAILED;
		}
		return CommonConstants.SUCCESS;
	}

	int allocateWidgetId() {
		try {
            Context packageContext = context.createPackageContext(
                    DashconfigApplication.getDeviceContext()
                            .getStringConst(context, "TWLAUNCHER_COMP"), 0);
			AppWidgetHost host = new AppWidgetHost(packageContext, 1024);

			return host.allocateAppWidgetId();
		} catch (NameNotFoundException e) {
			DashLogger.e(TAG, "NameNotFoundException: " + e.getMessage());
		} catch (Exception e) {
			DashLogger.e(TAG, "Exception in allocateWidgetId : " + e.getMessage());
		}
		return -1;
	}

	protected void createWidgetsAndShortcuts() {

		for (int index = 0; index < widgetsAndShortcutsList.size(); index++) {
			JSONObject item = widgetsAndShortcutsList.get(index);
			resultStatus = bindSamsungWidgetsOrShortcuts(item);
			if (resultStatus != CommonConstants.SUCCESS) {
				failedList.put(item);
			}
		}

		synchronized (this) {
			DashLogger.v(TAG, "before notify");
			notify();
			DashLogger.v(TAG, "after notify");
		}
	}

	public void createAndroidWidget(JSONObject item) {
		try {
			int screen = item.getInt("screen");
			int cellX = item.getInt("x");
			int cellY = item.getInt("y");
			int width = item.getInt("cols");
			int height = item.getInt("rows");
			int widgetId = item.getInt("widgetId");
			String component = item.getString("id");
			String packageName = CommonUtils.extractPackage(component);
			String providerName = CommonUtils.extractProvider(component);
			String intent = packageName + "/" + providerName;
			ContentValues values = new ContentValues();
			values.put("_id", generateNewId());
			values.putNull("title");
			values.put("intent", intent);
			values.put("container", -100);//
			values.put("screen", screen);//
			values.put("cellX", cellX);//
			values.put("cellY", cellY);//
			values.put("spanX", width);//
			values.put("spanY", height);//
			values.put("itemType", 4);//
			values.put("appWidgetId", widgetId);
			values.putNull("isShortcut");
			values.putNull("iconType");
			values.putNull("iconPackage");
			values.putNull("iconResource");
			values.putNull("icon");
			values.putNull("uri");
			values.putNull("displayMode");
			values.putNull("multipleSize");
			Uri favoritesUri = Uri.parse(DashconfigApplication
					.getDeviceContext()
					.getStringConst(context, "FAVORITES_URI"));
			@SuppressWarnings("unused")
			Uri addedUri = mContentResolver.insert(favoritesUri, values);
		} catch (JSONException e) {
			DashLogger.e(TAG,
					"JSONException in createAndroidWidget" + e.getMessage());
		}
	}

	private void createShortcut(JSONObject item) {
		try {
			int screen = item.getInt("screen");
			int cellX = item.getInt("x");
			int cellY = item.getInt("y");
			String component = item.getString("id");
			String title = item.getString("title");
			String intent = "#Intent;action=android.intent.action.MAIN;category=android.intent.category.LAUNCHER;launchFlags=0x10200000;component="
					+ component + ";end";
			int containerId = -100;
			if (item.has("container_id")) {
				containerId = item.getInt("container_id");
			}
			ContentValues values = new ContentValues();
			values.put("cellX", cellX);
			values.put("cellY", cellY);
			values.put("screen", screen);
			values.put("intent", intent);
			values.put("title", title);
			values.put("container", containerId);
			values.put("spanX", 1);
			values.put("spanY", 1);
			values.put("itemType", 0);
			values.put("iconType", 0);
			values.put("iconPackage", CommonUtils.extractPackage(component));
			values.put("_id", generateNewId());
			Uri uri = Uri.parse(DashconfigApplication.getDeviceContext()
					.getStringConst(context, "FAVORITES_URI"));
			@SuppressWarnings("unused")
			Uri uriadded = mContentResolver.insert(uri, values);
		} catch (JSONException e) {
			DashLogger.d(TAG,
					"JSONException in createShortcut: " + e.getMessage());
		}
	}

	public long generateNewId() {
		if (this.mMaxId < 0L)
			throw new RuntimeException("Error: max id was not initialized");
		long l = this.mMaxId + 1L;
		this.mMaxId = l;
		return this.mMaxId;
	}

	public void createTouchWizWidget(JSONObject item) {
		try {
			int screen = item.getInt("screen");
			int x = item.getInt("x");
			int y = item.getInt("y");
			int width = item.getInt("cols");
			int height = item.getInt("rows");
			String id = item.getString("id");
			String intent = "intent=#Intent;component=" + id + ";end";
			ContentValues values = new ContentValues();
			values.put("cellX", x);
			values.put("cellY", y);
			values.put("screen", screen);
			values.put("container", -100);
			values.put("spanX", width);
			values.put("spanY", height);
			values.put("itemType", 5);
			values.put("appWidgetId", -1);
			values.put("intent", intent);
			values.putNull("title");
			values.putNull("iconType");
			values.putNull("isShortcut");
			values.putNull("iconPackage");
			values.putNull("iconResource");
			values.putNull("icon");
			values.putNull("uri");
			values.putNull("displayMode");
			values.putNull("multipleSize");
			Uri uri = Uri.parse(DashconfigApplication.getDeviceContext()
					.getStringConst(context, "FAVORITES_URI"));
			ContentResolver resolver = context.getContentResolver();
			@SuppressWarnings("unused")
			Uri insertedRow = resolver.insert(uri, values);
		} catch (JSONException je) {
			DashLogger
					.d(TAG,
                            "JSONException in createTouchWizWidget: "
                                    + je.getMessage());
		}
	}

	public boolean isAnyFailed() {
		if (failedList.length() != 0) {
			return true;
		} else {
			return false;
		}
	}

	public JSONArray getFailedItems() {
		return failedList;
	}
}
