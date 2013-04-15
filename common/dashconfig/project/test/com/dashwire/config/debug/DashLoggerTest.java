package com.dashwire.config.debug;

import com.dashwire.base.debug.DashLogger;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Author: tbostelmann
 */
public class DashLoggerTest extends TestCase {
    public void testIsDebugModeDefaultsToFalse() {
        Assert.assertFalse("Default setting for isDebugMode should be false", DashLogger.isDebugMode());
    }
}
