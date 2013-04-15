package com.dashwire.config.launcher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.util.CommonUtils;
import org.w3c.dom.Document;

import java.io.File;

public class HtcLauncherModel implements LauncherModel {
    private static final String TAG = HtcLauncherModel.class.getCanonicalName();

	private final SceneInfo mSceneInfo;

	private final Context mContext;

	public HtcLauncherModel(Context context) {
		mSceneInfo = new SceneInfo(context, CommonUtils.getAndroidOSProperty(context, "ro.build.sense.version"));
		mContext = context;
	}

	public void store() {
		File file = mSceneInfo.writeDocument();
        DashLogger.d(TAG, "Schene Xml File = " + mSceneInfo.toString());
        //System.out.print("XML File = " + file.getAbsolutePath());
		Intent clientIntent = new Intent(
				"com.htc.home.personalize.ACTION_READY2GO");
		clientIntent.putExtra("EXTRA_SCENE_PATH", file.getAbsolutePath());
		mContext.sendBroadcast(clientIntent);
	}

    /**
     * Builds a DOM Document representing the current state of this model.
     *
     * @see com.dashwire.config.launcher.SceneInfo#buildDocument()
     * @return document
     */
    Document buildDocument() {
        return mSceneInfo.buildDocument();
    }

	@Override
	public boolean usesExternalConfigurator() {
		return false;
	}

	@Override
	public void activateExternalConfigurator() {
		// Not used for HTC
	}

	public void addShortcut(Context context, ComponentName cn,
			String container, int screen, int cellX, int cellY) {
		ShortcutInfo info = new ShortcutInfo(cn);
		info.title = getAppLabel(context, cn.getPackageName());
		info.itemTypeName = "application";
		info.cellX = cellX;
		info.cellY = cellY;
		info.screen = screen;
		info.container = Integer
				.valueOf("desktop".equalsIgnoreCase(container) ? -100 : -101);
		mSceneInfo.addItem(info);
	}

	public void addAppWidget(Context context, ComponentName cn, String extras,
			String uri, String container, int screen, int cellX, int cellY,
			int spanX, int spanY, String oemType) {

		ItemInfo info = AppWidgetFactory.getInstance(context).createItemInfo(
				oemType, cn, uri);
		if (info == null) {//Bad data
			DashLogger.d("Launcher",
					"Failed to add widget: missing info: "
							+ cn.flattenToString());
			return;
		}
		info.spanX = spanX;
		info.spanY = spanY;
		info.cellX = cellX;
		info.cellY = cellY;
		info.screen = screen;
		mSceneInfo.addItem(info);
	}

	@Override
	public void addWallpaper(Context context, String wallpaperUri) {
		mSceneInfo.addWallpaper(new WallpaperInfo(
				SceneSettings.Favorites.ITEM_TYPE_WALLPAPER_STATIC,
				wallpaperUri));
	}

	public void clearDesktop() {
		// noop
	}

	@Override
	public void killLauncher() {
		// noop
	}

	private static String getAppLabel(Context context, String packageName) {
		final PackageManager pm = context.getPackageManager();
		android.content.pm.ApplicationInfo ai;
		try {
			ai = pm.getApplicationInfo(packageName, 0);
		} catch (final NameNotFoundException e) {
			ai = null;
		}
		return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
	}

}
