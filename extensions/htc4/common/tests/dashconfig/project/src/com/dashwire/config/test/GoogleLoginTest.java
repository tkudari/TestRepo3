package com.dashwire.config.test;

import android.test.AndroidTestCase;

import com.dashwire.base.device.NetworkStatusAndroid;
import com.dashwire.config.resources.GoogleLoginServiceConfigurator;
import com.dashwire.config.util.CommonTestUtils;
import com.dashwire.config.util.CommonUtils;
import junit.framework.Assert;

public class GoogleLoginTest extends AndroidTestCase {
    public void setUp() throws Exception {
        NetworkStatusAndroid networkStatusAndroid = new NetworkStatusAndroid(getContext());
        Assert.assertTrue(
                "Need a data connection for these tests to run",
                networkStatusAndroid.isNetworkAvailable());
    }

	public void testGoogleLogin() throws Exception {
		final String email = "dashconfig2@gmail.com";
		final GoogleLoginServiceConfigurator login = new GoogleLoginServiceConfigurator();
		login.setContext(mContext);

        if (login.accountExists(email)) {
            CommonUtils.removeAccount(email, getContext(), null, null);
            CommonTestUtils.waitForAccountToBeRemoved(email, getContext());
        }
        Assert.assertFalse(
                "Gmail account " + email + " exists - please delete the account before running this test",
                login.accountExists(email));

		login.provisionGoogleLoginCredential(email, "abababab",
                true, true);

        CommonTestUtils.waitForAccountToBeCreated(email, getContext());
        Assert.assertTrue(
                "Account " + email + " failed to install",
                CommonUtils.accountExists(email, getContext()));
        CommonUtils.removeAccount(email, getContext(), null, null);
	}
}
