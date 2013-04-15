package com.dashwire.config.launcher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.net.Uri;

public class WallpaperInfo {

	String mWallpaperUri;

	String mItemTypeName;

	WallpaperInfo(int itemType, String wallpaperUri) {
		mItemTypeName = (itemType == SceneSettings.Favorites.ITEM_TYPE_WALLPAPER_STATIC) ? "static_wallpaper"
				: "live_wallpaper";
		mWallpaperUri = Uri.parse(wallpaperUri).getPath();
	}

	public Element asDomElement(Document document) {
		Element element = document.createElement("WallpaperSetting");
		element.setAttribute(SceneSettings.Favorites.ITEM_TYPE, mItemTypeName);
		element.setAttribute(SceneSettings.Favorites.WALLPAPER_URI,
				mWallpaperUri);
		return element;
	}
}
