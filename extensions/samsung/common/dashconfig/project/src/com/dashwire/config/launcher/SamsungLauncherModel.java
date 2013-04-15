package com.dashwire.config.launcher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.dashwire.base.debug.DashLogger;

import java.io.File;

public class SamsungLauncherModel implements LauncherModel {

    protected final String TAG = SamsungLauncherModel.class.getCanonicalName();

    private Context mContext;
    private FavoritesInfo mFavoritesInfo;

    public SamsungLauncherModel(Context context) {
        mContext = context;
        mFavoritesInfo = new FavoritesInfo(context);
    }

    public void store() {
        File file = mFavoritesInfo.writeDocument();
        DashLogger.d(TAG, "XML File = " + file.getAbsolutePath());
        //TODO: send to service
    }

    @Override
    public boolean usesExternalConfigurator() {
        return true;
    }

    public void activateExternalConfigurator() {
        final Intent i = new Intent();
        i.putExtra("Favorites", "/sdcard/favorites.xml");
        i.setClassName("com.sec.android.app.launcher", "com.android.launcher2.WidgetCreationService");
        DashLogger.d(TAG, "SamsungLauncherModel.activateExternalConfigurator()");
        mContext.startService(i);
    }

    public void addShortcut(Context context, ComponentName cn,
                            String container, int screen, int cellX, int cellY) {
        FavoriteInfo info = new FavoriteInfo(cn);
        info.title = getAppName(context, cn.getPackageName());
        info.itemTypeName = FavoritesInfo.SHORTCUT_ELEMENT_NAME;
        info.cellX = cellX;
        info.cellY = cellY;
        info.screen = screen;
        if (container.equals("-101")) {
            mFavoritesInfo.addHotSeatFavorite(info);
            info.container = -101;
        }
        else {
            mFavoritesInfo.addHomeFavorite(info);
            info.container = -100;
        }
    }

    public void addAppWidget(Context context, ComponentName cn,
                             String extras, String uri, String container, int screen, int cellX, int cellY, int spanX,
                             int spanY, String oemType) {
        DashLogger.d(TAG, "Setting extras value: " + extras);
        FavoriteInfo info;
        if ("aw".equals(oemType)) {
            info = new AppWidgetInfo(cn);
            info.itemTypeName = "appwidget";

        } else {
            info = new TouchWizWidgetInfo(cn);
            info.itemTypeName = "sactivitywidget";
        }
        info.title = getAppName(context, cn.getPackageName());
        info.className = cn.getClassName();
        info.packageName = cn.getPackageName();
        info.extras = extras;
        info.spanX = spanX;
        info.spanY = spanY;
        info.cellX = cellX;
        info.cellY = cellY;
        info.screen = screen;
        //TODO: what about htc_fx_widget?
        mFavoritesInfo.addHomeFavorite(info);
    }

    public void clearDesktop() {
        // nothing to do here
    }

    private static String getAppName(Context context, String packageName) {
        final PackageManager pm = context.getPackageManager();
        android.content.pm.ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }

    @Override
    public void killLauncher() {
        // nothing to do here
    }


    private static boolean isInstalled(String packageName, Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        try {
            return pm.getPackageInfo(packageName, PackageManager.GET_META_DATA) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void addWallpaper(Context context, String wallpaperUri) {
        //noop
    }
}
