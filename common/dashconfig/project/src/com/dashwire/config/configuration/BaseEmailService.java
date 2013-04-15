package com.dashwire.config.configuration;

import com.dashwire.config.util.AccountSettingsUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.util.Accounts;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public abstract class BaseEmailService extends IntentService {
	private Context context;
	private ConfigurationEvent configurationEvent;
	private static final String TAG = BaseEmailService.class.getCanonicalName();

	public BaseEmailService() { 
		this("default");
	}
	
	public BaseEmailService(String name) {
		super(name);
	}
	
	public abstract EmailCreator getEmailCreator();

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		configurationEvent = new ConfigurationEvent() {
			@Override
			public void notifyEvent(String name, int code) {
				notifyEvent(name, code, null);
			}

			@Override
			public void notifyEvent(String name, int code, Throwable e) {
				if (equals(e != null)) {
					e.printStackTrace();
				}
				Intent i = new Intent(ACTION_CONFIGURATION);
				i.setData(Uri.parse("dashconfig://?name=" + name + "&code="
						+ code));
				context.sendBroadcast(i);
			}
		};
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		DashLogger.d(TAG, "Starting Mail Service: ");
		if (intent == null) {
			configurationEvent.notifyEvent("emails", ConfigurationItem.FAILED);
			configurationEvent.notifyEvent("exchange_accounts", ConfigurationItem.FAILED);
			return;
		}

		if(intent.hasExtra("emails")) {
			try {
				provisionEmailAccounts(new JSONArray(intent.getStringExtra("emails")));
			} catch (JSONException e) {
				configurationEvent.notifyEvent("emails", ConfigurationItem.FAILED,
						e);
			}
		}		
		
		if(intent.hasExtra("exchange")) {
			try {
				provisionExchangeAccounts(new JSONArray(intent.getStringExtra("exchange")));
			} catch (JSONException e) {
				configurationEvent.notifyEvent("exchange_accounts", ConfigurationItem.FAILED, e);
			}
		} 
	}

	private void provisionExchangeAccounts(JSONArray configs) {
		boolean hasFailure = false;
		for (int index = 0; index < configs.length(); index++) {
			try {
				JSONObject account = configs.getJSONObject(index);
				String service = account.getString("service");
				String domain = account.optString("domain", "");
				String login = account.getString("login").toLowerCase();
				String emailAddress = account.optString("address", login);
				String password = account.getString("password");
				String displayName = account.optString("display_name", login);
				if (!Accounts.exchangeAccountExists(this, login)) {
					if ("Exchange".equals(service)) {
						String server = account.getString("server");
						getEmailCreator().setupExchangeAccount(this,
								domain, server, emailAddress, login, password,
								displayName,
								account.getBoolean("sync_calendar"),
								account.getBoolean("sync_contacts"));
					} else if ("Hotmail".equals(service)) {
						getEmailCreator().setupExchangeAccount(this, "m.live.com", "m.hotmail.com",
						 emailAddress, login, password, displayName,
						 true, true);
					}
				}
			} catch (Exception je) {
				DashLogger.d("!!!",
						"!!! UNHANDLED EXCEPTION !!! : " + je.getMessage());
				hasFailure = true;
			}
		}

		DashLogger.d("!!!", "Notify completeness of exchange setup");
		configurationEvent.notifyEvent("exchange_accounts",
				hasFailure ? ConfigurationEvent.FAILED
						: ConfigurationEvent.CHECKED);

	}
	
	private void provisionEmailAccounts(JSONArray configs) {
		DashLogger.d(TAG, "Setting emails: " + configs.length());
		boolean hasFailure = false;
		for (int index = 0; index < configs.length(); index++) {
			try {
				JSONObject account = configs.getJSONObject(index);
				String email = account.getString("login");

				if (TextUtils.isEmpty(email)) {
					hasFailure = true;
					continue;
				} else {
					email = email.toLowerCase();
				}

			//	if (!Accounts.emailAccountExists(this, email)
			//			&& !"Exchange".equals(account.getString("service"))) {
					DashLogger.d(TAG, "Setting up account: " + email);
					setupAccount(account, email);
				//}
			} catch (Exception je) {
				DashLogger.v(TAG, "JSONException : " + je.getMessage());
				hasFailure = true;
			}
		}

		configurationEvent.notifyEvent("emails",
				hasFailure ? ConfigurationEvent.FAILED
						: ConfigurationEvent.CHECKED);
	}

    private static final String JSON_KEY_INCOMING_PORT = "incoming_port";
    private static final String JSON_KEY_OUTGOING_PORT = "outgoing_port";
    private static final String JSON_KEY_OUTGOING_PORT_SECURITY_TYPE = "outgoing_port_security_type";
    private static final String JSON_KEY_INCOMING_PORT_SECURITY_TYPE = "incoming_port_security_type";

	private void setupAccount(JSONObject account, String email)
			throws Exception {

		String password = account.getString("password");
		String service = account.getString("service");
		String displayName = account.optString("display_name", email);
			
		if ("Other".equals(service)) {		
			String incoming_type = account.getString("incoming_type").toLowerCase();
			String incoming_host = account.getString("incoming_host");
			String outgoing_host = account.getString("outgoing_host");
            int incoming_port = -1;
            int outgoing_port = -1;
            int incoming_port_security_type = AccountSettingsUtils.Provider.SECURITY_TYPE_SSL;
            int outgoing_port_security_type = AccountSettingsUtils.Provider.SECURITY_TYPE_SSL;
            if (account.has(JSON_KEY_INCOMING_PORT_SECURITY_TYPE))
                incoming_port_security_type = account.getInt(JSON_KEY_INCOMING_PORT_SECURITY_TYPE);
            if (account.has(JSON_KEY_OUTGOING_PORT_SECURITY_TYPE))
                outgoing_port_security_type = account.getInt(JSON_KEY_OUTGOING_PORT_SECURITY_TYPE);
            if (account.has(JSON_KEY_INCOMING_PORT))
                incoming_port = account.getInt(JSON_KEY_INCOMING_PORT);
            else if (incoming_type.contains("imap") && incoming_port_security_type == AccountSettingsUtils.Provider.SECURITY_TYPE_SSL)
                incoming_port = 993;
            else if (incoming_type.contains("imap")) // IMAP TLS and non-secure ports are the same
                incoming_port = 143;
            else if (incoming_type.contains("pop") && incoming_port_security_type == AccountSettingsUtils.Provider.SECURITY_TYPE_SSL)
                incoming_port = 995;
            else
                incoming_port = 110; // POP TLS and non-secure ports are the same.
            if (account.has(JSON_KEY_OUTGOING_PORT))
                outgoing_port = account.getInt(JSON_KEY_OUTGOING_PORT);
            else if (outgoing_port_security_type == AccountSettingsUtils.Provider.SECURITY_TYPE_SSL)
                outgoing_port = 465;
            else
                outgoing_port = 587;
            getEmailCreator().saveAccount(this, email, password, displayName, incoming_host,
					outgoing_host, incoming_port, outgoing_port, incoming_port_security_type,
                    outgoing_port_security_type, incoming_type);
		} else {
			getEmailCreator().saveAccount(this, email, password, displayName);
		}
	}
}
