package com.dashwire.config.launcher;

import android.content.ComponentName;
import com.dashwire.base.debug.DashLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class FavoriteInfo {
    private static final String TAG = FavoriteInfo.class.getCanonicalName();

    private static long mMaxId = 65535L;

    static final int NO_ID = -1;

    /**
     * The id in the settings database for this item
     */
    long id = NO_ID;

    int itemType;

    String itemTypeName;

    String className;

    String packageName;

    String extras;

    /**
     * The id of the container that holds this item. For the desktop, this will be
     * {@link LauncherSettings.Favorites#CONTAINER_DESKTOP}. For the all applications folder it
     * will be {@link #NO_ID} (since it is not stored in the settings DB). For user folders
     * it will be the id of the folder.
     */
    long container = NO_ID;

    /**
     * Iindicates the screen in which the shortcut appears.
     */
    int screen = -1;

    /**
     * Indicates the X position of the associated cell.
     */
    int cellX = -1;

    /**
     * Indicates the Y position of the associated cell.
     */
    int cellY = -1;

    /**
     * Indicates the X cell span.
     */
    int spanX = 1;

    /**
     * Indicates the Y cell span.
     */
    int spanY = 1;

    String intent;

    String title;

    ComponentName mComponentName;

    FavoriteInfo(ComponentName componentName) {
        mComponentName = componentName;
    }

    Element asDomElement(Document document) {
        Element element = document.createElement(itemTypeName);
//        element.setAttribute(SceneSettings.Favorites.ITEM_TYPE,
//                itemTypeName);
        String packageName = mComponentName.getPackageName();
        element.setAttribute("launcher:packageName", packageName);
        String className = mComponentName.getClassName();
        if (!className.startsWith(packageName) &&
                (this instanceof TouchWizWidgetInfo || container == -101)) {
            String completeName;
            if (className.startsWith("."))
                completeName = packageName + className;
            else
                completeName = packageName + "." + className;
            element.setAttribute("launcher:className", completeName);
        }
        else {
            element.setAttribute("launcher:className", className);
        }

        if (container != FavoritesInfo.HOT_SEAT_CONTAINER_ID) {
            element.setAttribute("launcher:x", String.valueOf(cellX));
            element.setAttribute("launcher:y", String.valueOf(cellY));
        }
        element.setAttribute("launcher:screen", String.valueOf(screen));
        DashLogger.d(TAG, "FavoriteInfo json: " + element.toString());


//        element.setAttribute(SceneSettings.Favorites.TITLE,
//                title);
        return element;
    }

    @Override
    public String toString() {
        return "Item(type=" + this.itemTypeName + " container=" + this.container
                + " screen=" + screen + " cellX=" + cellX + " cellY=" + cellY + " spanX=" + spanX
                + " spanY=" + spanY  + ")";
    }
}
