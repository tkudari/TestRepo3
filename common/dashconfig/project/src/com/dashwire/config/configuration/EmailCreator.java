package com.dashwire.config.configuration;

import android.content.Context;

public interface EmailCreator {
	boolean saveAccount(Context context, String email,
			String password, String displayName) throws Exception;

	boolean saveAccount(Context context, String email,
			String password, String sender, String inServer, String outServer,
            int inPort, int outPort, int inPortSecurityType, int outPortSecurityType,
			String type);
	
	boolean setupExchangeAccount(Context context, String domain,
			String server, String emailAddress, String login, String password,
			String displayName, boolean sync_calendar, boolean sync_contacts);
}
