package com.dashwire.config.test;


import com.dashwire.config.email.SamsungEmailCreator;

import android.content.Intent;
import android.test.AndroidTestCase;

public class EmailTest extends AndroidTestCase {
	
	public void testProvisionExchange2007Account() throws Exception {
		new SamsungEmailCreator().setupExchangeAccount(mContext, "mex07a.mlsrvr.com", "mex07a.emailsrvr.com", "qaex@dashwire.com", 
				"mex07a.mlsrvr.com\\qaex@dashwire.com", "dashwire123!", "qaex at dashwire.com", true, true);
		}

	public void testProvisionHotmailExchange() throws Exception {
		new SamsungEmailCreator().setupExchangeAccount(mContext, "m.hotmail.com", "m.hotmail.com", "henry4sync@hotmail.com", 
				"m.hotmail.com\\henry4sync@hotmail.com", "kih@s0ft", "henry4sync at hotmail.com", true, true);

	}
	/*
    public void testProvisionExchange2007Account() {
        EmailAccountDAO dao = new EmailAccountDAO(mContext);
        dao.saveExchangeAccount("qaex@dashwire.com", "dashwire123!", "mex07a.emailsrvr.com", "qaex@dashwire.com", "qaex at dashwire.com", false, true, true);

        try {
            long untilMillis = 5000 + System.currentTimeMillis();
            while (System.currentTimeMillis() < untilMillis &&
                    !Accounts.emailAccountExists(mContext, "qaex@dashwire.com")) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Assert.fail();
        }

        Assert.assertTrue(Accounts.emailAccountExists(mContext, "qaex@dashwire.com"));
    }

    public void testProvisionHotmailAccount() {
        EmailAccountDAO dao = new EmailAccountDAO(mContext);
        dao.saveExchangeAccount("henry4sync@hotmail.com", "kih@s0ft", "m.hotmail.com", "henry4sync@hotmail.com", "henry4sync at hotmail.com", false, true, true);

        try {
            long untilMillis = 5000 + System.currentTimeMillis();
            while (System.currentTimeMillis() < untilMillis &&
                    !Accounts.emailAccountExists(mContext, "henry4sync@hotmail.com")) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Assert.fail();
        }

        Assert.assertTrue(Accounts.emailAccountExists(mContext, "henry4sync@hotmail.com"));
    }

    public void testAolSetup() throws Exception {
        EmailAccountDAO emailAccountDAO = new EmailAccountDAO(mContext);
        emailAccountDAO.saveImapAccount("dashconfig@aol.com", "crushit!", "imap.aol.com", "smtp.aol.com",
                "AOL User", false);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 5000,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        FutureTask<Boolean> future = new FutureTask<Boolean>(
                new Callable<Boolean>() {
                    public Boolean call() {
                        try {
                            while (!Accounts.emailAccountExists(mContext, "dashconfig@aol.com")) {
                            }
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                });
        executor.execute(future);
        assertTrue(future.get(5000, TimeUnit.MILLISECONDS));
    }
   */
    public void testExchangeAndAttSetup() throws Exception {
       final String email = "ljhalleran@att.net";
       assertTrue(new SamsungEmailCreator().saveAccount(mContext, email, "ready2go", "auto test"));
       
       assertTrue(new SamsungEmailCreator().setupExchangeAccount(mContext, "mex07a.mlsrvr.com", "mex07a.emailsrvr.com", "qaex@dashwire.com", 
			"mex07a.mlsrvr.com\\qaex@dashwire.com", "dashwire123!", "qaex at dashwire.com", true, true));

    }

    public void testAolSetup() throws Exception {
        final String email = "dashconfig@aol.com";
        assertTrue(new SamsungEmailCreator().saveAccount(mContext, email, "crushit!", "auto test"));
     }
    
    public void testAttSetup() throws Exception {
        final String email = "ljhalleran@att.net";
        assertTrue(new SamsungEmailCreator().saveAccount(mContext, email, "ready2go", "auto test"));
     }
    

}
