package com.dashwire.config.resources;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.dashwire.config.DebugTools;
import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ResourceConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import static android.provider.Browser.BookmarkColumns.TITLE;
import static android.provider.Browser.BookmarkColumns.URL;

public class BookmarkConfigurator implements ResourceConfigurator  {

	private static final Uri CONTENT_URI = android.provider.Browser.BOOKMARKS_URI;

	// TODO centeralize json constants
	public static final String DW_URL = "url";
	
	public static final String DW_NAME = "name";

	private HashSet<String> mExisingBookmarks;
	
	protected Context mContext;

	protected ConfigurationEvent mConfigurationEvent;
	
	protected ContentResolver mContentResolver;
	
	protected JSONArray mConfigArray;
	
	private boolean hasFailure;

	public BookmarkConfigurator() { }
	
	public String name() {
		return "bookmarks";
	}
	
	public boolean setBookmark(String url, String title) throws Exception {
		ContentValues values = new ContentValues();
		values.put("bookmark", 1);
		values.put(URL, url);
		values.put(TITLE, title);
		values.put("folder", 0);
		values.put("user_entered", 1);
		values.put("visits", 1);
		values.put("date", System.currentTimeMillis());
		values.put("created", System.currentTimeMillis());
		return mContentResolver.insert(CONTENT_URI, values) != null;
	}

    public int removeBookmark(String url) throws Exception {
        String[] args = {url};
        return mContentResolver.delete(CONTENT_URI, URL + "=?", args);
    }

	@SuppressWarnings("unused")
    private void setItems(JSONArray items) {

	}

	private void create(JSONObject data) {		
		// TODO validate
		boolean isFailure = false;
		try {
			String url = data.getString(DW_URL);
			String title = data.getString(DW_NAME);
			if (!bookmarkExists(url)) {
				if(!setBookmark(url, title)) {
					isFailure = true;
				}
			}
		} catch (Exception e) {
			mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.FAILED, e);
			return;
		}
		
		if(isFailure) {
			mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.FAILED);
		}
	}

	public boolean bookmarkExists(String url) {
		HashSet<String> exisingBookmarks = getExistingBookmarks();
		return exisingBookmarks.contains(url);
	}

	private HashSet<String> getExistingBookmarks() {
		if (mExisingBookmarks == null) {
			mExisingBookmarks = getBookmarkUrls();
		}
		return mExisingBookmarks;
	}

	private HashSet<String> getBookmarkUrls() {
		mExisingBookmarks = new HashSet<String>();
		Cursor cursor = mContext.getContentResolver().query(
				android.provider.Browser.BOOKMARKS_URI, null, "bookmark == 1",
				null, "date desc");
		int urlIndex = cursor.getColumnIndex("url");
		while (cursor.moveToNext()) {
			String url = cursor.getString(urlIndex);
			if (url != null) {
				mExisingBookmarks.add(url);
			}
		}
		cursor.close();
		return mExisingBookmarks;
	}

	public void dump() {
		DebugTools.dumpContentResolver(mContext, CONTENT_URI);
	}

    @Override
    public void setContext( Context context ) {
        mContext = context;
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void setConfigurationEvent( ConfigurationEvent configurationEvent ) {
       mConfigurationEvent = configurationEvent;
        
    }

    @Override
    public void setConfigDetails( JSONArray configArray ) {
        mConfigArray = configArray;
        
    }

    @Override
    public void configure() {
    	ArrayList<JSONObject> bookmarks = new ArrayList<JSONObject>();
    	
        for (int index = 0; index < mConfigArray.length(); index++) {
        	try {
				JSONObject jo = mConfigArray.getJSONObject(index);
				bookmarks.add(jo);
			} catch (JSONException e) {
				mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.FAILED, e);
				return;
			}
        }
        
        Collections.sort(bookmarks, new BookmarkComparator());
        for(JSONObject jo : bookmarks) {
            try {
                create(jo);
            } catch (Exception e) {
                mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.FAILED, e);
                return;
            }        	
        }

        
        mConfigurationEvent.notifyEvent(name(), hasFailure ? ConfigurationEvent.FAILED 
        		: ConfigurationEvent.CHECKED);
        
    }
    
    private class BookmarkComparator implements Comparator<JSONObject> {

		@Override
		public int compare(JSONObject lhs, JSONObject rhs) {
			
			String titleA;
			String titleB;
			try {
				titleA = lhs.getString(DW_NAME);
				titleB = rhs.getString(DW_NAME);
			} catch (JSONException e) {
				return 0;
			}
			
			return titleA.compareToIgnoreCase(titleB);
		}
    }
}
