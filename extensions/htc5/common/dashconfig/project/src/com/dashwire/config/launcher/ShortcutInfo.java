package com.dashwire.config.launcher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.ComponentName;

class ShortcutInfo extends ItemInfo {

	ComponentName providerName;

	ShortcutInfo(ComponentName componentName) {
		spanX = 1;
		spanY = 1;
		providerName = componentName;
	}

	@Override
	public Element asDomElement(Document document) {
		Element element = super.asDomElement(document);
		element.setAttribute(SceneSettings.Favorites.CONTAINER, 		
				String.valueOf(container));
		element.setAttribute(
				SceneSettings.Favorites.CLASS_NAME, 				
				providerName.getShortClassName().startsWith(".") ? providerName.getPackageName()
						+ providerName.getShortClassName() : providerName.getShortClassName());
		
		element.setAttribute(SceneSettings.Favorites.PACKAGE_NAME,
				providerName.getPackageName());
		return element;
	}
	
	
}
