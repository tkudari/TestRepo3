package com.dashwire.config.test;

import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.resources.BookmarkConfigurator;

import android.test.AndroidTestCase;
import com.dashwire.config.util.CommonTestUtils;
import junit.framework.Assert;

public class BookmarkTest extends AndroidTestCase  {
    private static final String TAG = BookmarkTest.class.getCanonicalName();

	public void testBookmark() throws Exception {
		BookmarkConfigurator bc = new BookmarkConfigurator();
		bc.setContext(getContext());
        String url = "http://dashwire.com";
        String title = "Test: " + Math.random();
        DashLogger.d(TAG, "Test add Bookmark: " + url);
        if (bc.bookmarkExists(url)) {
            DashLogger.d(TAG, "Pre-test Bookmark exists: " + url);
            bc.removeBookmark(url);
            CommonTestUtils.waitForBookmarkToBeRemoved(url, getContext());
        }
        Assert.assertFalse(
                "Bookmark " + url + " exists - there was a problem removing the bookmark",
                bc.bookmarkExists(url));
		bc.setBookmark(url, title);
        CommonTestUtils.waitForBookmarkToBeCreated(url, getContext());
        Assert.assertTrue(
                "Failed to create bookmark " + url,
                bc.bookmarkExists(url));
        bc.removeBookmark(url);
	}
}
