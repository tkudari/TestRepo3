package com.dashwire.config.email;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.test.AndroidTestCase;

import com.dashwire.config.util.Accounts;

public class EmailTest extends AndroidTestCase {
	/*
    public void testProvisionExchange2007Account() throws Exception {
       ExchangeEmailService service = new ExchangeEmailService();
       service.setContext(mContext);
       service.setupAccount("", "mex07a.mlsrvr.com", "mex07a.emailsrvr.com", 
    		   "qaex@dashwire.com", "mex07a.mlsrvr.com\\qaex@dashwire.com", "dashwire123!", 
    		   "qaex at dashwire.com", true, true);
  
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

    public void testProvisionHotmailAccount() throws Exception {
        ExchangeEmailService service = new ExchangeEmailService();
        service.setContext(mContext);
        service.setupAccount("", "", "m.hotmail.com", 
        		"henry4sync@hotmail.com", "henry4sync@hotmail.com", "kih@s0ft", 
        		"henry4sync at hotmail.com", true, true);
        

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
    */

//    public void testAolSetup() throws Exception {
//        EmailService service = new EmailService();
//        service.setContext(mContext);
//        service.saveImapAccount("dashconfig@aol.com", "crushit!", "AOL User", "imap.aol.com", "smtp.aol.com");
//
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 5000,
//                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
//        FutureTask<Boolean> future = new FutureTask<Boolean>(
//                new Callable<Boolean>() {
//                    public Boolean call() {
//                        try {
//                            while (!Accounts.emailAccountExists(mContext, "dashconfig@aol.com")) {
//                            }
//                            return true;
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            return false;
//                        }
//                    }
//                });
//        executor.execute(future);
//        assertTrue(future.get(5000, TimeUnit.MILLISECONDS));
//    }
//
//    public void testAttSetup() throws Exception {
//        final String email = "ljhalleran@att.net";
//        EmailService service = new EmailService();
//        service.setContext(mContext);
//        service.saveImapAccount(email, "ready2go", "ATT User", "imap.mail.yahoo.com", "smtp.mail.yahoo.com");
//
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 5000,
//                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
//        FutureTask<Boolean> future = new FutureTask<Boolean>(
//                new Callable<Boolean>() {
//                    public Boolean call() {
//                        try {
//                            while (!Accounts.emailAccountExists(mContext, email)) {
//                            }
//                            return true;
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            return false;
//                        }
//                    }
//                });
//        executor.execute(future);
//        assertTrue(future.get(5000, TimeUnit.MILLISECONDS));
//    }
}
