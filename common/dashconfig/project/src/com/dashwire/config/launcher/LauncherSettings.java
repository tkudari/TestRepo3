/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dashwire.config.launcher;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Settings related utilities.
 */
public class LauncherSettings {
    public static interface BaseLauncherColumns extends BaseColumns {
        /**
         * Descriptive name of the gesture that can be displayed to the user.
         * <P>Type: TEXT</P>
         */
    	public static final String TITLE = "title";

        /**
         * The Intent URL of the gesture, describing what it points to. This
         * value is given to {@link android.content.Intent#parseUri(String, int)} to create
         * an Intent that can be launched.
         * <P>Type: TEXT</P>
         */
    	public static final String INTENT = "intent";
        
        /**
         * The type of the gesture
         *
         * <P>Type: INTEGER</P>
         */
    	public static final String ITEM_TYPE = "itemType";

        /**
         * The gesture is an application created shortcut
         */
    	public static final int ITEM_TYPE_SHORTCUT = 0;
        
        /**
         * The favorite is a widget
         */
    	public static final int ITEM_TYPE_APPWIDGET = 4;
        
        /**
         * Samsung Touch Wizard Widget
         */
    	public static final int ITEM_TYPE_TOUCHWIZ = 5;
        
        /**
         * The favorite is a clock
         */
    	public static final int ITEM_TYPE_WIDGET_CLOCK = 1000;        

        /**
         * The icon type.
         * <P>Type: INTEGER</P>
         */
    	public static final String ICON_TYPE = "iconType";

        /**
         * The icon is a resource identified by a package name and an integer id.
         */
    	public static final int ICON_TYPE_RESOURCE = 0;

    }

    /**
     * Favorites.
     */
    public static final class Favorites implements BaseLauncherColumns {
        /**
         * The content:// style URL for this table
         */
    	public static final Uri CONTENT_URI_LEGACY = 
        		Uri.parse("content://com.sec.android.app.twlauncher.settings/favorites");

    	public static final Uri CONTENT_URI = 
        		Uri.parse("content://com.sec.android.app.launcher.settings/favorites");
        
        /**
         * The container holding the favorite
         * <P>Type: INTEGER</P>
         */
    	public static final String CONTAINER = "container";

        /**
         * The icon is a resource identified by a package name and an integer id.
         */
    	public static final int CONTAINER_DESKTOP = -100;
    	public static final int CONTAINER_HOTSEAT = -101;

        /**
         * The screen holding the favorite (if container is CONTAINER_DESKTOP)
         * <P>Type: INTEGER</P>
         */
    	public static final String SCREEN = "screen";

        /**
         * The X coordinate of the cell holding the favorite
         * (if container is CONTAINER_HOTSEAT or CONTAINER_HOTSEAT)
         * <P>Type: INTEGER</P>
         */
    	public static final String CELLX = "cellX";

        /**
         * The Y coordinate of the cell holding the favorite
         * (if container is CONTAINER_DESKTOP)
         * <P>Type: INTEGER</P>
         */
    	public static final String CELLY = "cellY";

        /**
         * The X span of the cell holding the favorite
         * <P>Type: INTEGER</P>
         */
    	public static final String SPANX = "spanX";

        /**
         * The Y span of the cell holding the favorite
         * <P>Type: INTEGER</P>
         */
    	public static final String SPANY = "spanY";

        /**
         * The appWidgetId of the widget
         *
         * <P>Type: INTEGER</P>
         */
    	public static final String APPWIDGET_ID = "appWidgetId";

        /**
         * The URI associated with the favorite. It is used, for instance, by
         * live folders to find the content provider.
         * <P>Type: TEXT</P>
         */
    	public static final String URI = "uri";

        /**
         * The display mode if the item is a live folder.
         * <P>Type: INTEGER</P>
         *
         * @see android.provider.LiveFolders#DISPLAY_MODE_GRID
         * @see android.provider.LiveFolders#DISPLAY_MODE_LIST
         */
    	public static final String DISPLAY_MODE = "displayMode";
    }
}
