package com.dashwire.config.launcher;

import android.content.ComponentName;
import android.content.ContentValues;

class ShortcutInfo extends ItemInfo {

	ShortcutInfo(ComponentName componentName) {
		intent = "#Intent;action=android.intent.action.MAIN;category=android.intent.category.LAUNCHER;launchFlags=0x10200000;component="
				+ componentName.flattenToString() + ";end";
		itemType = LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT;
		spanX = 1;
		spanY = 1;
	}

	@Override
	void onAddToDatabase(ContentValues values) {
		super.onAddToDatabase(values);
	}
}
