package com.dashwire.config.email;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.configuration.EmailCreator;
import com.dashwire.config.util.AccountSettingsUtils;
import com.dashwire.config.util.AccountSettingsUtils.Provider;

public class HtcEmailCreator implements EmailCreator {

	public static final String ACTION_CREATE_ACCOUNT = "com.htc.android.mail.mailservice.SetupAccountIntentService.CREATE";
	public static final String ACTION_EAS_CREATE_ACCOUNT = "com.htc.android.mail.eassvc.account.CREATE";

	public static final int EXTRA_VALUE_INPROTOCOL_OTHER = 100;
	public static final int EXTRA_VALUE_PROTOCOL_TYPE_IMAP = 2;
	public static final int EXTRA_VALUE_PROTOCOL_TYPE_POP3 = 0;
	
	public static final int POP_IN_PORT = 110;
	public static final int IMAP_IN_PORT = 143;
	public static final int SMTP_OUT_PORT = 25;
	
	public static final String EXTRA_VALUE_PROVIDER_GROUP_OTHER = "Other";
	
	public static final int EXTRA_VALUE_SECURITY_TYPE_NONE_CODE = 0;
	public static final int EXTRA_VALUE_SECURITY_TYPE_SSL_CODE = 1;
	public static final int EXTRA_VALUE_SECURITY_TYPE_TLS_CODE = 2;

	public boolean saveAccount(Context context, String email, String password,
			String displayName) throws Exception {
		Provider provider = AccountSettingsUtils.findProviderForEmail(context,
				email);
		return saveAccount(context, provider.incomingUsername, password,
				displayName, provider.incomingServer, provider.outgoingServer,
				provider.incomingPort, provider.outgoingPort,
                provider.incomingPortSecurityType, provider.outgoingPortSecurityType,
				provider.accountType);
	}

	public boolean saveAccount(Context context, String email, String password,
			String displayName, String inServer, String outServer, int inPort,
			int outPort, int inPortSecurityType, int outPortSecurityType, String type) {
	    DashLogger.i("EMAIL", "Creating email account");
		Intent localIntent = new Intent(ACTION_CREATE_ACCOUNT);
		localIntent.putExtra("emailaddress", email);
        localIntent.putExtra("displayName", displayName);
        localIntent.putExtra("display_name", displayName);
		localIntent.putExtra("password", password);
		localIntent.putExtra("provider_group", getEmailType(inServer));
		localIntent.putExtra("inserver", inServer);
		localIntent.putExtra("outserver", outServer);
		
        localIntent.putExtra("inport", inPort);
        localIntent.putExtra("outport", outPort);

		localIntent.putExtra("smtpauth", 1);//TODO: not always true
              
        localIntent.putExtra("protocol_type", "imap".equals(type) ?
        		EXTRA_VALUE_PROTOCOL_TYPE_IMAP : EXTRA_VALUE_PROTOCOL_TYPE_POP3);       

        localIntent.putExtra("security_type_in", inPortSecurityType);
        localIntent.putExtra("security_type_out", outPortSecurityType);

		context.startService(localIntent);
		return true;

	}

	public boolean setupExchangeAccount(Context context, String domain,
			String server, String email, String login, String password,
			String displayName, boolean sync_calendar, boolean sync_contacts)
	{
	    DashLogger.i("EMAIL", "Creating exchange account");
		Intent intent = new Intent(ACTION_EAS_CREATE_ACCOUNT);
		intent.setClassName("com.htc.android.mail",
				"com.htc.android.mail.eassvc.EASAppSvc");
		
		Bundle bundle = new Bundle();
		bundle.putString("displayName", displayName);
		bundle.putString("emailAddr", email);
		bundle.putString("serverAddr", server);
		bundle.putString("domain", domain);
		bundle.putString("username", login);
		bundle.putString("password", password);
		bundle.putInt("syncSchedule", 4);
		bundle.putBoolean("useSSL", true);
		bundle.putBoolean("syncMail", true);
		bundle.putBoolean("setDefaultAccount", false);
		bundle.putBoolean("syncCalendar", sync_calendar);
		bundle.putBoolean("syncContacts", sync_contacts);
		intent.putExtras(bundle);
		
		context.startService(intent);
		return true;
	}
	
	/**
	 * returns type of email account based on the incoming server. These are determined by the 
	 * provider.xml (or 'other' server info) not the r2g server. 
	 * 
	 * @param incomingServer
	 * @return
	 */
	private String getEmailType(String incomingServer) {
		if(incomingServer.endsWith("yahoo.com")) {
			return "Yahoo";
		} else if(incomingServer.endsWith("gmail.com")) {
			return "Gmail";
		} else {
			return "Other";
		}
	}

}
