package com.dashwire.config.launcher;

import android.content.ComponentName;
import android.content.Context;

public interface LauncherModel {
	
	void addShortcut(Context context, ComponentName cn,
			String container, int screen, int cellX, int cellY);
	
	void addAppWidget(Context context, ComponentName cn,
                      String extras, String uri, String container, int screen, int cellX, int cellY, int spanX,
                      int spanY, String oemType);
	
	void addWallpaper(Context context, String wallpaperUri);
	
	void clearDesktop();
	
	void killLauncher();
	
	void store();
	
	boolean usesExternalConfigurator();

    void activateExternalConfigurator();
}
