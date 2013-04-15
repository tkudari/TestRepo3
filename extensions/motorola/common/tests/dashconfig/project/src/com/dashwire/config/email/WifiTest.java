package com.dashwire.config.email;

import com.dashwire.config.resources.WifiConfigurator;

import android.test.AndroidTestCase;

public class WifiTest extends AndroidTestCase {

	public void testSetup() throws Exception {
		WifiConfigurator wifi = new WifiConfigurator();
		wifi.setContext(mContext);
		assertTrue(wifi.setupWifi("test-" + Math.random(), "1A648C9FE2", "wep", true));
	}
}
