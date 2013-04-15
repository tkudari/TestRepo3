package com.dashwire.config.test;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.test.AndroidTestCase;

import com.dashwire.config.resources.GoogleLoginServiceConfigurator;

public class GoogleLoginTest extends AndroidTestCase {

	public void testGoogleLogin() throws Exception {
		final String email = "dashconfig@gmail.com";
		final GoogleLoginServiceConfigurator login = new GoogleLoginServiceConfigurator();
		login.setContext(mContext);
		
		login.provisionGoogleLoginCredential(email, "abababab", 
				true, true);
		
		ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 10000, 
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		FutureTask<Boolean> future = new FutureTask<Boolean>(
				new Callable<Boolean>() {
					public Boolean call() {
						try {
							while(!login.accountExists(email)) { }
							return true;
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
					}
				});
	    executor.execute(future);
	    assertTrue(future.get(10000, TimeUnit.MILLISECONDS));
	    

	}
}
