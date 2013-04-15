package com.dashwire.config.test;

import com.dashwire.config.resources.BookmarkConfigurator;

import android.test.AndroidTestCase;

public class BookmarkTest extends AndroidTestCase  {

	public void testBookmark() throws Exception {
		BookmarkConfigurator bc = new BookmarkConfigurator();
		bc.setContext(mContext);
		
		bc.setBookmark("http://dashwire.com", "Test: " + Math.random());
	}
}
