package com.dashwire.config.resources;

import android.accounts.*;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.DropBoxManager;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ConfigurationItem;
import com.dashwire.config.configuration.ResourceConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class GoogleLoginServiceConfigurator implements ResourceConfigurator {

    private static String TAG = GoogleLoginServiceConfigurator.class.getCanonicalName();

    private ConfigurationEvent mConfigurationEvent;

    protected Context mContext;

    private AccountManager mAccountManager;

    private DropBoxManager mDropBoxManager;

    private static final String MESSAGE = "GooglLogin JSON does not contain {0} entry";

    private Stack<JSONObject> mAccounts = new Stack<JSONObject>();

    @SuppressWarnings("unused")
    private JSONArray mConfigArray;

    private boolean hasFailure;

    public GoogleLoginServiceConfigurator() {

    }

    public String name() {
        return "accounts";
    }

    public boolean provisionGoogleLoginCredential( String email, String password, boolean syncCalendar, boolean syncContacts ) {
        DashLogger.d( "", "Provisioning login service: " + email );

        Bundle addAccountOptions = new Bundle();
        addAccountOptions.putString( "username", email );
        addAccountOptions.putString( "password", password );

        AccountManagerFuture<Bundle> future = mAccountManager.addAccount( "com.google", null, null, addAccountOptions, null, null, null );

        Bundle resultBundle = null;
        try {
        	resultBundle = future.getResult( 10, TimeUnit.SECONDS );
        } catch ( IOException ex ) {
        	DashLogger.d( TAG, "IOException provisioning account " + email + ", ex = " + ex.getMessage());
            hasFailure = true;
        } catch ( OperationCanceledException ex ) {
        	DashLogger.d( TAG, "OperationCanceledException provisioning account " + email + ", ex = " + ex.getMessage());
            hasFailure = true;
        } catch ( AuthenticatorException ex ) {
        	DashLogger.d( TAG, "AuthenticatorException provisioning account " + email + ", ex = " + ex.getMessage());
            hasFailure = true;
        }

        if(!hasFailure && resultBundle != null) {
        	if(resultBundle.containsKey(AccountManager.KEY_ERROR_CODE)) {
        		hasFailure = true;
        		DashLogger.d( TAG, "Error Provsioning Account " + email + ". error =" + resultBundle.getInt(AccountManager.KEY_ERROR_CODE) + ",");
        	} else if(resultBundle.containsKey(AccountManager.KEY_ACCOUNT_NAME) &&
        			resultBundle.containsKey(AccountManager.KEY_ACCOUNT_TYPE)) {
        		DashLogger.d( TAG, "Succeeded provisioning account " + resultBundle.getString(AccountManager.KEY_ACCOUNT_NAME));
        	} else {
        		hasFailure = true;
        		DashLogger.d( TAG, "Error Provsioning Account " + email + ", bundle does not contain expected values");
        	}
        }

        if(!hasFailure)
        {
        	syncAccount( email, syncCalendar, syncContacts );
        }

        return hasFailure;
    }

    private void syncAccount( String email, boolean syncCalendar, boolean syncContacts ) {
        Account acc = new Account( email, "com.google" );

        ContentResolver.setIsSyncable( acc, "com.android.calendar", 1 );
        ContentResolver.setSyncAutomatically( acc, "com.android.calendar", syncCalendar );
        ContentResolver.requestSync( acc, "com.android.calendar", new Bundle() );

        ContentResolver.setIsSyncable( acc, "com.android.contacts", 1 );
        ContentResolver.setSyncAutomatically( acc, "com.android.contacts", syncContacts );
        ContentResolver.requestSync( acc, "com.android.contacts", new Bundle() );

        ContentResolver.setIsSyncable( acc, "gmail-ls", 1 );
        ContentResolver.setSyncAutomatically( acc, "gmail-ls", true );
        ContentResolver.requestSync( acc, "gmail-ls", new Bundle() );

        //verify the settings
        if(ContentResolver.getIsSyncable( acc, "com.android.calendar") != 1) {
        	DashLogger.d( TAG, "Gmail calendar syncable option not set");
        }
        if(ContentResolver.getSyncAutomatically( acc, "com.android.calendar") != syncCalendar) {
        	DashLogger.d( TAG, "Gmail calendar auto sync option not set");
        }
        if(ContentResolver.getIsSyncable( acc, "com.android.contacts") != 1) {
        	DashLogger.d( TAG, "Gmail contacts syncable option not set");
        }
        if(ContentResolver.getSyncAutomatically( acc, "com.android.contacts") != syncContacts) {
        	DashLogger.d( TAG, "Gmail contacts auto sync option not set");
        }
        if(ContentResolver.getIsSyncable( acc, "gmail-ls") != 1) {
        	DashLogger.d( TAG, "Gmail syncable option not set");
        }
        if(!ContentResolver.getSyncAutomatically( acc, "gmail-ls")) {
        	DashLogger.d(TAG, "Gmail auto sync option not set");
        }

    }

    public boolean accountExists(String login) {
        if ( login != null ) {
            Account[] accounts = mAccountManager.getAccountsByType( null );
            for ( int index = 0; index < accounts.length; index++ ) {
                if ( login.equals( accounts[ index ].name ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setContext( Context context ) {
        mContext = context;
        mAccountManager = AccountManager.get( context );
        mDropBoxManager = (DropBoxManager) mContext.getSystemService(Context.DROPBOX_SERVICE);
    }

    @Override
    public void setConfigurationEvent( ConfigurationEvent configurationEvent ) {
        mConfigurationEvent = configurationEvent;
    }

    @Override
    public void setConfigDetails( JSONArray configArray ) {
        mConfigArray = configArray;
        for ( int index = 0; index < configArray.length(); index++ ) {
            try {
                mAccounts.add( configArray.getJSONObject( index ) );
            } catch ( JSONException e ) {
                DashLogger.v( TAG, "JSONException : " + e.getMessage() );
                hasFailure = true;
            }
        }
    }

    @Override
    public void configure() {
        setupAccount();
    }

    private boolean validateJSONEntries(JSONObject account, String... names) {
    	boolean isOK = true;
    	for(String name : names) {
        	if(!account.has(name)) {
        		mDropBoxManager.addText("dashwire", MessageFormat.format(MESSAGE, name));
        		isOK = false;
        	}
    	}
    	return isOK;
    }

    private boolean validateJSON(JSONObject account) {

    	if(!validateJSONEntries(account, "login", "password", "sync_calendar", "sync_contacts")) {
        	try {
    			JSONObject copy = new JSONObject(account.toString());
    			copy.put("password", "*****");
    			copy.put("login", account.getString("login").subSequence(0, 3) + "*****");
    			mDropBoxManager.addText("dashwire", "GoogleLogin invalid JSON:\r\n " + copy.toString());
    		} catch (JSONException e) {

    		}
        	return false;
    	}
    	return true;
    }

    private void setupAccount() {

        while ( !mAccounts.isEmpty() ) {
            JSONObject account = mAccounts.pop();
            if(validateJSON(account)) {
                try {
                    if ( !accountExists(account.getString("login")) ) {
                        String login = account.getString( "login" ).toLowerCase();
                        String password = account.getString( "password" );
                        provisionGoogleLoginCredential( login, password, account.getBoolean( "sync_calendar" ), account.getBoolean( "sync_contacts" ) );
                    } else {
                    	DashLogger.d( TAG, "Google account " + account.getString( "login" ) + " already exists, will not provision or set options.");
                    }
                } catch ( JSONException e ) {
                	DashLogger.d( TAG, "JSon exception in account entry in GoogleLoginService.");
                    e.printStackTrace();
                    hasFailure = true;
                }
            } else {
            	hasFailure = true;
            }
        }

        if ( mAccounts.isEmpty() ) {
            mConfigurationEvent.notifyEvent( name(), hasFailure ? ConfigurationItem.FAILED : ConfigurationItem.CHECKED );
        }
    }
}
