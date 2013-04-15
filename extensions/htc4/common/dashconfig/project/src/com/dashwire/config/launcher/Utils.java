package com.dashwire.config.launcher;

import android.content.ComponentName;

public class Utils {
	
	public static ComponentName normalizeComponent(ComponentName cn) {
		String className = cn.getClassName().startsWith(".") ? cn
				.getPackageName() + cn.getClassName() : cn.getClassName();
		return new ComponentName(cn.getPackageName(), className);
	}
}
