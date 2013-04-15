package com.dashwire.config.test;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;

import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.resources.WifiConfigurator;

import android.test.AndroidTestCase;

public class WifiTest extends AndroidTestCase {

	int mCode = -1;
	
	ConfigurationEvent mConfigurationEvent;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mConfigurationEvent = new ConfigurationEvent() {
			@Override
			public void notifyEvent(String name, int code) {
				notifyEvent(name, code, null);
			}

			@Override
			public void notifyEvent(String name, int code, Throwable e) {
				if (equals(e != null)) {
					e.printStackTrace();
				}
				mCode = code;
			}
		};
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		mConfigurationEvent = null;
		mCode = -1;
	}
	
	public void testDCFGP97_UnableToConfigureNetworkThatIsNotInRange() throws Exception{
		String network = "testABCDE-" + Math.random();
		
		JSONArray json = new JSONArray("[{\"ssid\": \""+ network + "\",\"security\": \"wep\",\"key\": \"1A648C9FE2\"}]");
		configureWifi(json);
		
		ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 10000, 
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		FutureTask<Boolean> future = new FutureTask<Boolean>(
				new Callable<Boolean>() {
					public Boolean call() {
						try {
							while(mCode == -1) { }
							return true;
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
					}
				});
	    executor.execute(future);
	    assertTrue(future.get(10000, TimeUnit.MILLISECONDS));
	    assertEquals(ConfigurationEvent.CHECKED, mCode);		
	}

	/*
	 * This test just tests that the account is added. The WifiConfigurator does not properly flag if password is incorrect.
	 */
	public void testNetworkThatCanBeFound() throws Exception {
			
		JSONArray json = new JSONArray("[{\"ssid\": \"Dashwire Private\",\"security\": \"wpa\",\"key\": \"d@sm0b1l3\"}]");
		configureWifi(json);
		
		ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 10000, 
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		FutureTask<Boolean> future = new FutureTask<Boolean>(
				new Callable<Boolean>() {
					public Boolean call() {
						try {
							while(mCode == -1) { }
							return true;
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
					}
				});
	    executor.execute(future);
	    
	    assertTrue(future.get(10000, TimeUnit.MILLISECONDS));
	    assertEquals(ConfigurationEvent.CHECKED, mCode);
	}
	
	private WifiConfigurator configureWifi(JSONArray json) {
		WifiConfigurator wifi = new WifiConfigurator();	
		wifi.setContext(mContext);
		wifi.setConfigDetails(json);
		wifi.setConfigurationEvent(mConfigurationEvent);
		wifi.configure();
		
		return wifi;		
	}
}
