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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.ContentValues;

/**
 * Represents an item in the launcher.
 */
class ItemInfo {
	
	private static long mMaxId = 65535L;
    
    static final int NO_ID = -1;
    
    /**
     * The id in the settings database for this item
     */
    long id = NO_ID;
    
    /**
     * One of {@link LauncherSettings.Favorites#ITEM_TYPE_APPLICATION},
     * {@link LauncherSettings.Favorites#ITEM_TYPE_SHORTCUT},
     * {@link LauncherSettings.Favorites#ITEM_TYPE_FOLDER}, or
     * {@link LauncherSettings.Favorites#ITEM_TYPE_APPWIDGET}.
     */
    int itemType;
    
    String itemTypeName;
    
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

    ItemInfo() {
    	id = generateNewId();
    }
    
    public Element asDomElement(Document document) {
    	Element element = document.createElement("item");
    	element.setAttribute(SceneSettings.Favorites.ITEM_TYPE, 
    			itemTypeName);
    	element.setAttribute(SceneSettings.Favorites.SCREEN, 
    			String.valueOf(screen));
    	element.setAttribute(SceneSettings.Favorites.CELLX, 
    			String.valueOf(cellX));   
    	element.setAttribute(SceneSettings.Favorites.CELLY, 
    			String.valueOf(cellY));     
    	element.setAttribute(SceneSettings.Favorites.SPANX, 
    			String.valueOf(spanX));       
    	element.setAttribute(SceneSettings.Favorites.SPANY, 
    			String.valueOf(spanY));  	    	
    	return element;
    }

    /**
     * Write the fields of this item to the DB
     * 
     * @param values
     */
    void onAddToDatabase(ContentValues values) {  	
    	values.put(LauncherSettings.Favorites._ID, id);
    	values.put(LauncherSettings.Favorites.ITEM_TYPE, itemType);
    	values.put(LauncherSettings.Favorites.CONTAINER, container);
    	values.put(LauncherSettings.Favorites.SCREEN, screen);
    	values.put(LauncherSettings.Favorites.CELLX, cellX);
    	values.put(LauncherSettings.Favorites.CELLY, cellY);
    	values.put(LauncherSettings.Favorites.SPANX, spanX);
    	values.put(LauncherSettings.Favorites.SPANY, spanY);
    	if(intent != null) {
        	values.put(LauncherSettings.Favorites.INTENT, intent);   		
    	}
    	values.put(LauncherSettings.Favorites.TITLE, title);
    }

	public static long generateNewId() {
		if (mMaxId < 0L)
			throw new RuntimeException("Error: max id was not initialized");
		long l = mMaxId + 1L;
		mMaxId = l;
		return mMaxId;
	}
	
    @Override
    public String toString() {
        return "Item(id=" + this.id + " type=" + this.itemType + " container=" + this.container
            + " screen=" + screen + " cellX=" + cellX + " cellY=" + cellY + " spanX=" + spanX
            + " spanY=" + spanY  + ")";
    }
}
