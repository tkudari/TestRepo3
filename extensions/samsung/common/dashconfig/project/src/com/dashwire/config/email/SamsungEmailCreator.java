package com.dashwire.config.email;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.dashwire.config.configuration.EmailCreator;
import com.dashwire.config.util.AccountSettingsUtils;
import com.dashwire.config.util.AccountSettingsUtils.Provider;
import com.dashwire.config.util.Accounts;

import android.content.Context;
import android.content.Intent;

public class SamsungEmailCreator implements EmailCreator {

	public boolean saveAccount(Context context, String email, String password,
			String displayName) throws Exception {
		Provider provider = AccountSettingsUtils.findProviderForEmail(context,
				email);
		return saveAccount(context, provider.incomingUsername, password,
				displayName, provider.incomingServer, provider.outgoingServer, -1, -1, -1, -1,
				provider.accountType);
	}

	public boolean saveAccount(Context context, String email, String password,
			String sender, String inServer, String outServer,
            int inPort, int outPort, int inPortSecurityType, int outPortSecurityType,
            String type) {
		Intent i = new Intent();
		i.setClassName("com.android.email",
				"com.android.email.service.AccountCreationService");

		i.putExtra("provider_id", email);
		i.putExtra("account_name", sender);
		i.putExtra("user_id", email);
		i.putExtra("user_passwd", password);
		i.putExtra("receive_host", inServer);
		i.putExtra("receive_port", inPort);
		i.putExtra("receive_security", "ssl+trustallcerts");
		i.putExtra("send_host", outServer);
		i.putExtra("send_port", outPort);
		i.putExtra("send_security", "ssl+trustallcerts");
		i.putExtra("send_from", email);
		i.putExtra("sender_name", sender);
		i.putExtra("notify", false);
		i.putExtra("service", type);
		i.putExtra("vibrate", false);
		i.putExtra("vibrate_when_silent", false);
		i.putExtra("is_default", false);

		context.startService(i);
		return true;
		// return blockForResult(context, email);

	}

	public boolean setupExchangeAccount(Context context, String domain,
			String server, String emailAddress, String login, String password,
			String displayName, boolean sync_calendar, boolean sync_contacts)

	{
		Intent i = new Intent();
		i.setClassName("com.android.email",
				"com.android.email.service.AccountCreationService");

		i.putExtra("service", "eas");
		i.putExtra("provider_id", emailAddress);
		i.putExtra("user_id", emailAddress);
		i.putExtra("user_name", emailAddress);
		i.putExtra("user_passwd", password);
		i.putExtra("server_name", server);
		i.putExtra("domain", domain);
		i.putExtra("account_name", displayName);
		i.putExtra("sync_calendar", sync_calendar ? 1 : 0);
		i.putExtra("sync_contacts", sync_contacts ? 1 : 0);

		i.putExtra("use_ssl", 1);
		i.putExtra("trust_all", 1);

		context.startService(i);
		return true;
	}

	private static boolean blockForResult(final Context context,
			final String email) {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 5000,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		FutureTask<Boolean> future = new FutureTask<Boolean>(
				new Callable<Boolean>() {
					public Boolean call() {
						try {
							while (!Accounts.emailAccountExists(context, email)) {
							}
							return true;
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
					}
				});
		executor.execute(future);
		try {
			return future.get(5000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {// TODO: don't stack trace this
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return false;
	}
}
