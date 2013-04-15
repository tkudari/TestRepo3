package com.dashwire.config.launcher;

public class SceneSettings {
	
    public static interface BaseLauncherColumns  {

    	public static final String TITLE = "widgetName";
        
    	public static final String ITEM_TYPE = "itemType";

    	public static final int ITEM_TYPE_SHORTCUT = 0;
        
    	public static final int ITEM_TYPE_APPWIDGET = 4;  
    	
    	public static final int ITEM_TYPE_WALLPAPER_STATIC = 100;
    	
    	public static final int ITEM_TYPE_WALLPAPER_LIVE = 101;

    }

    public static final class Favorites implements BaseLauncherColumns {

    	public static final String PACKAGE_NAME = "packageName";
    	
    	public static final String CLASS_NAME = "className";
    	
    	public static final String PROVIDER_NAME = "providerName";
    	
    	public static final String WIDGET_NAME = "widgetName";

    	public static final String WALLPAPER_URI = "resFile";
    	
    	public static final String CONTAINER = "container";

    	public static final int CONTAINER_DESKTOP = -100;
    	
    	public static final int CONTAINER_HOTSEAT = -101;

    	public static final String SCREEN = "screen";

    	public static final String CELLX = "cell_x";

    	public static final String CELLY = "cell_y";

    	public static final String SPANX = "span_x";

    	public static final String SPANY = "span_y";

    }
}
