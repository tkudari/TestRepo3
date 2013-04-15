package com.dashwire.config.launcher;

import android.content.ComponentName;
import android.content.ContentValues;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class AppWidgetInfo extends FavoriteInfo {
	AppWidgetInfo(ComponentName providerName) {
        super(providerName);
	}

	@Override
    Element asDomElement(Document document) {
        Element element = super.asDomElement(document);
        element.setAttribute("launcher:spanX", String.valueOf(spanX));
        element.setAttribute("launcher:spanY", String.valueOf(spanY));
        return element;
    }
}
