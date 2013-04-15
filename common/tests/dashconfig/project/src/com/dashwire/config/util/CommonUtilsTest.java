package com.dashwire.config.util;

import android.test.AndroidTestCase;
import junit.framework.Assert;

import java.util.Locale;

/**
 * Author: tbostelmann
 */
public class CommonUtilsTest extends AndroidTestCase {
    public void testSetLanguage() {
        String currentLanguage = Locale.getDefault().getLanguage();
        String expectedLanguage;
        if (currentLanguage.equals("en")) {
            CommonUtils.setLanguage("es", getContext());
            expectedLanguage = "es";
        }
        else {
            CommonUtils.setLanguage("en", getContext());
            expectedLanguage = "en";
        }

        Assert.assertEquals(expectedLanguage, Locale.getDefault().getLanguage());
        Assert.assertEquals("US", Locale.getDefault().getCountry());
    }
}
