package com.dashwire.config.integration;

import android.test.AndroidTestCase;
import com.dashwire.base.device.DashSettingsAndroid;

/**
 * Author: tbostelmann
 */
public class DashSettingsAndroidTest extends AndroidTestCase {
    DashSettingsAndroid dashSettingsAndroid;

    public void setUp() {
        dashSettingsAndroid = new DashSettingsAndroid(getContext());
    }

    public void testGetPhoneNumber() {
        String phoneNumber = dashSettingsAndroid.getPhoneNumber();
        if (phoneNumber != null) {
            assertTrue("invalid phone number:" + phoneNumber, phoneNumber.length() == "12065551212".length());
        }
    }

    public void testGetIMEI() {
        String imei = dashSettingsAndroid.getIMEI();
        assertTrue(imei.toString() + " is not lenght of " + "359691040015763".length(), imei.length() == "359691040015763".length());
    }

    public void testGetAndroidId() {
        String androidId = dashSettingsAndroid.getAndroidId();
        assertTrue(androidId.length() == "6066f0834913baa0".length());
    }

    public void testGetBuildDevice() {
        assertNotEmpty(dashSettingsAndroid.getBuildDevice());
    }

    public void testGetBuildIncremental() {
        assertNotEmpty(dashSettingsAndroid.getBuildIncremental());
    }

    public void testGetBuildManufacturer() {
        assertNotEmpty(dashSettingsAndroid.getBuildManufacturer());
    }

    public void testGetBuildRelease() {
        assertNotEmpty(dashSettingsAndroid.getBuildRelease());
    }

    public void testGetClientVersion() {
        assertNotEmpty(dashSettingsAndroid.getClientVersion());
    }

    public void testGetBuildSDK() {
        assertNotEmpty(dashSettingsAndroid.getBuildSDK());
    }

    public void testGetServerHostname() {
        assertNotEmpty(dashSettingsAndroid.getServerHostname());
    }

    public void assertNotEmpty(String value) {
        assertNotNull(value);
        assertTrue(value.length() > 0);
    }
}
