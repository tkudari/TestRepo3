package com.dashwire.config.test;


import android.content.ComponentName;
import android.content.Context;
import com.dashwire.config.debug.DashLogger;
import com.dashwire.config.email.HtcEmailCreator;

import android.content.Intent;
import android.test.AndroidTestCase;
import com.dashwire.config.util.CommonTestUtils;
import com.dashwire.config.util.CommonUtils;
import junit.framework.Assert;
import org.json.JSONObject;

import java.util.concurrent.*;

public class EmailTest extends AndroidTestCase {
	private static final String TAG = EmailTest.class.getCanonicalName();

    public void testAttSetup() throws Exception {
        final String account = "ljhalleran@att.net";
        DashLogger.d(TAG, "Test email account: " + account);
        if (CommonUtils.accountExists(account, getContext())) {
            DashLogger.w(TAG, "Pre-test email account exists: " + account);
            CommonUtils.removeAccount(account, getContext(), null, null);
            CommonTestUtils.waitForAccountToBeRemoved(account, getContext());
        }
        Assert.assertFalse(
                "Account " + account + " exists.  Please delete before running this test",
                CommonUtils.accountExists(account, getContext()));
        assertTrue(new HtcEmailCreator().saveAccount(getContext(), account, "ready2go", "auto test"));
        CommonTestUtils.waitForAccountToBeCreated(account, getContext());
        Assert.assertTrue(
                "Account " + account + " failed to install",
                CommonUtils.accountExists(account, getContext()));
        CommonUtils.removeAccount(account, getContext(), null, null);
        CommonTestUtils.waitForAccountToBeRemoved(account, getContext());
    }

	public void testProvisionExchange2007Account() throws Exception {
        String account = "qaex@dashwire.com";
        DashLogger.d(TAG, "Test email account: " + account);
        if (CommonUtils.accountExists(account, getContext())) {
            DashLogger.w(TAG, "Pre-test email account exists: " + account);
            CommonUtils.removeAccount(account, getContext(), null, null);
            CommonTestUtils.waitForAccountToBeRemoved(account, getContext());
        }
        Assert.assertFalse(
                "Account " + account + " exists.  Please delete before running this test",
                CommonUtils.accountExists(account, getContext()));
        new HtcEmailCreator().setupExchangeAccount(getContext(), "mex07a.mlsrvr.com", "mex07a.emailsrvr.com", account,
                "mex07a.mlsrvr.com\\qaex@dashwire.com", "dashwire123!", "qaex at dashwire.com", true, true);
        CommonTestUtils.waitForAccountToBeCreated(account, getContext());
        Assert.assertTrue(
                "Account " + account + " failed to install",
                CommonUtils.accountExists(account, getContext()));
        CommonUtils.removeAccount(account, getContext(), null, null);
        CommonTestUtils.waitForAccountToBeRemoved(account, getContext());
    }

	public void testProvisionHotmailExchange() throws Exception {
        final String account = "henry4sync@hotmail.com";
        DashLogger.d(TAG, "Test email account: " + account);
        if (CommonUtils.accountExists(account, getContext())) {
            DashLogger.w(TAG, "Pre-test email account exists: " + account);
            CommonUtils.removeAccount(account, getContext(), null, null);
            CommonTestUtils.waitForAccountToBeRemoved(account, getContext());
        }
        Assert.assertFalse(
                "Account " + account + " exists.  Please delete before running this test",
                CommonUtils.accountExists(account, getContext()));
		new HtcEmailCreator().setupExchangeAccount(getContext(), "m.hotmail.com", "m.hotmail.com", account,
                "m.hotmail.com\\henry4sync@hotmail.com", "kih@s0ft", "henry4sync at hotmail.com", true, true);
        CommonTestUtils.waitForAccountToBeCreated(account, getContext());
        Assert.assertTrue(
                "Account " + account + " failed to install",
                CommonUtils.accountExists(account, getContext()));
        CommonUtils.removeAccount(account, getContext(), null, null);
        CommonTestUtils.waitForAccountToBeRemoved(account, getContext());
    }

//    public void testProvisionOtherAccount() throws Exception {
//        String emailsJson =
//                "[{\"login\":\"ljhalleran@att.net\"," +
//                "\"password\":\"ready2go\"," +
//                "\"service\":\"Other\"," +
//                "\"incoming_host\":\"imap.mail.yahoo.com\"," +
//                "\"incoming_type\":\"IMAP\"," +
//                "\"outgoing_host\":\"smtp.mail.yahoo.com\"," +
//                "\"sync_email\":true," +
//                "\"display_name\":\"Tom Bostelmann\"}]";
//        Intent mailService = new Intent();
//        mailService.setComponent( new ComponentName( getContext(), "com.dashwire.config.email.NewApiEmailService" ) );
//        mailService.putExtra( "emails", emailsJson );
//        getContext().startService( mailService );
//    }

    public void testProvisionYahooAccount() throws Exception {
        final String account = "ernesto.fresco@yahoo.com";
        DashLogger.d(TAG, "Test email account: " + account);
        final String password = "wordpass";
        if (CommonUtils.accountExists(account, getContext())) {
            DashLogger.w(TAG, "Pre-test email account exists: " + account);
            CommonUtils.removeAccount(account, getContext(), null, null);
            CommonTestUtils.waitForAccountToBeRemoved(account, getContext());
        }
        Assert.assertFalse(
                "Account " + account + " exists.  Please delete before running this test",
                CommonUtils.accountExists(account, getContext()));
//        final String inServer = "imap.mail.yahoo.com";
//        final String outServer = "smtp.mail.yahoo.com";
//        final int inPort = 993;
//        final int outPort = 25;
//        final int emailType = HtcEmailCreator.EXTRA_VALUE_PROTOCOL_TYPE_IMAP;
//        assertTrue(new HtcEmailCreator().saveAccount(mContext, email, password, inServer, outServer, inPort, outPort, emailType));
        assertTrue(new HtcEmailCreator().saveAccount(getContext(), account, password, "auto test yahoo"));
        CommonTestUtils.waitForAccountToBeCreated(account, getContext());
        Assert.assertTrue(
                "Account " + account + " failed to install",
                CommonUtils.accountExists(account, getContext()));
        CommonUtils.removeAccount(account, getContext(), null, null);
        CommonTestUtils.waitForAccountToBeRemoved(account, getContext());
    }
}
