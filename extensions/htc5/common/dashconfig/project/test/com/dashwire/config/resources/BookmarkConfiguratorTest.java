package com.dashwire.config.resources;

import com.dashwire.robolectric.DashRobolectricTestRunner;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.parsers.ParserConfigurationException;

/**
 * User: tbostelmann
 * Date: 1/8/13
 */
@RunWith(DashRobolectricTestRunner.class)
public class BookmarkConfiguratorTest {
    BookmarkConfigurator bookmarkConfigurator;

    @Before
    public void setUp() throws ParserConfigurationException {
        bookmarkConfigurator = new BookmarkConfigurator();
    }

    @Test
    public void testName() {
        Assert.assertEquals("bookmarks", bookmarkConfigurator.name());
    }
}
