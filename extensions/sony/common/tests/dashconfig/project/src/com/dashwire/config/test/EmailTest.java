package com.dashwire.config.test;


import com.dashwire.config.email.SonyEmailCreator;

import android.content.Intent;
import android.test.AndroidTestCase;

public class EmailTest extends AndroidTestCase {
	
    public void testAttSetup() throws Exception {
        final String email = "ljhalleran@att.net";
        assertTrue(new SonyEmailCreator().saveAccount(mContext, email, "ready2go", "auto test"));
     }
	
	public void testProvisionExchange2007Account() throws Exception {
		new SonyEmailCreator().setupExchangeAccount(mContext, "mex07a.mlsrvr.com", "mex07a.emailsrvr.com", "qaex@dashwire.com", 
				"mex07a.mlsrvr.com\\qaex@dashwire.com", "dashwire123!", "qaex at dashwire.com", true, true);
	}

	public void testProvisionHotmailExchange() throws Exception {
		new SonyEmailCreator().setupExchangeAccount(mContext, "m.hotmail.com", "m.hotmail.com", "henry4sync@hotmail.com", 
				"m.hotmail.com\\henry4sync@hotmail.com", "kih@s0ft", "henry4sync at hotmail.com", true, true);

	}
}
