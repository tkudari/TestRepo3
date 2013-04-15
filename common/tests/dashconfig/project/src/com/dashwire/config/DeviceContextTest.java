package com.dashwire.config;

import android.test.AndroidTestCase;
import junit.framework.Assert;

/**
 * Author: tbostelmann
 */
public class DeviceContextTest extends AndroidTestCase {
    public void testGetStringConstant() {
        DefaultDeviceContext deviceContext = new DefaultDeviceContext();
        Assert.assertNotNull(deviceContext.getStringConst(getContext(), "APP_DOWNLOAD_LOCATION"));
        Assert.assertNull(deviceContext.getStringConst(getContext(), "SOME_KEY_THAT_DOES_NOT_EXIST"));
        Assert.assertNotSame("", deviceContext.getStringConst(getContext(), "SOME_KEY_THAT_DOES_NOT_EXIST"));
    }
}
