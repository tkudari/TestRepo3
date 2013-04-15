package com.dashwire.config.email;

import android.content.Context;
import android.content.Intent;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.configuration.EmailCreator;
import com.dashwire.config.util.AccountSettingsUtils;
import com.dashwire.config.util.AccountSettingsUtils.Provider;
import com.dashwire.config.util.Accounts;

import java.util.concurrent.*;

public class SonyEmailCreator implements EmailCreator {
    private static final String TAG = SonyEmailCreator.class.getCanonicalName();

	public boolean saveAccount(Context context, String email,
			String password, String displayName) throws Exception {
		Provider provider = AccountSettingsUtils.findProviderForEmail(context,
				email);
		return saveAccount(context, provider.incomingUsername, password,
				displayName, provider.incomingServer, provider.outgoingServer, provider.incomingPort,
				provider.outgoingPort, provider.incomingPortSecurityType, provider.outgoingPortSecurityType,
                provider.accountType);
	}

	public boolean saveAccount(Context context, String email,
			String password, String sender, String inServer, String outServer, int inPort, int outPort,
            int inPortSecurityType, int outPortSecurityType, String type) {
		DashLogger.i("EMAIL", "Creating email account");
        final Intent createAccount = new Intent(
				"com.android.email.CREATE_ACCOUNT_SILENT");
		createAccount.putExtra("version", "1.0");
		createAccount.putExtra("email", email);
		createAccount.putExtra("username", email);
		createAccount.putExtra("password", password);
		createAccount.putExtra("displayName",sender);
		createAccount.putExtra("serviceType", type);		
		createAccount.putExtra("inServer", inServer);
		createAccount.putExtra("outServer",outServer);
		createAccount.putExtra("outLogin", email);
		createAccount.putExtra("inPort", inPort);
		createAccount.putExtra("outPort", outPort);
		createAccount.putExtra("outSecurity", "ssl");
		createAccount.putExtra("inSecurity", "ssl");
		
		//TODO: Remove
		DashLogger.i("EMAIL", "Extras: " + createAccount.getExtras().toString());
		
		context.sendBroadcast(createAccount);		
		return true;

	}

	public boolean setupExchangeAccount(Context context, 
			String domain, String server, String email, String login,
			String password, String displayName, boolean sync_calendar,
			boolean sync_contacts)

	{
		DashLogger.i("EMAIL", "Creating exchange account");
        final Intent createAccount = new Intent(
				"com.android.email.CREATE_ACCOUNT_SILENT");
		createAccount.putExtra("version", "1.0");
		createAccount.putExtra("email", email);
		createAccount.putExtra("username", login);
		createAccount.putExtra("password", password);
		createAccount.putExtra("displayName", displayName);
		createAccount.putExtra("domain", domain);	
		createAccount.putExtra("serviceType", "eas");		
		createAccount.putExtra("inServer", server);
		createAccount.putExtra("inPort", 443);
		createAccount.putExtra("inSecurity", "ssl");
		createAccount.putExtra("syncContacts", sync_contacts);
		createAccount.putExtra("syncCalendar", sync_calendar);
		createAccount.putExtra("syncEmail", true);

		DashLogger.i("EMAIL", "Extras: " + createAccount.getExtras().toString());
		
		context.sendBroadcast(createAccount);	
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
							DashLogger.e(TAG, "Error", e);
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
