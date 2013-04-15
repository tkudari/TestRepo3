package com.dashwire.config.launcher;

import android.content.ComponentName;
import com.dashwire.base.debug.DashLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TouchWizWidgetInfo extends AppWidgetInfo {
    private static final String TAG = TouchWizWidgetInfo.class.getCanonicalName();

    TouchWizWidgetInfo(ComponentName providerName) {
        super(providerName);
    }

    @Override
    Element asDomElement(Document document) {
        Element element = super.asDomElement(document);
        DashLogger.d(TAG, "Extra valuee:" + extras);
        if (extras != null) {
            int themeNameIndex = extras.indexOf("themename=");
            if (themeNameIndex == -1)
                themeNameIndex = extras.indexOf("themeName=");
            if (themeNameIndex != -1) {
                String themeName = extras.substring(themeNameIndex + "themename=".length());
                element.setAttribute("launcher:themeName", themeName);
            }
        }
        return element;
    }
}
