package com.dashwire.config.email;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ConfigurationItem;
import com.dashwire.config.util.Accounts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;


public class EmailService extends Service {

	private static String TAG = EmailService.class.getCanonicalName();

	public static final String ACTION_CREATE_ACCOUNT 
		= "com.motorola.email.AUTO_CREATE_ACCOUNT";
	public static final String ACTION_EAS_CREATE_ACCOUNT 
		= "com.motorola.email.AUTO_CREATE_ACCOUNT";

	
	  public static final int EXTRA_VALUE_INPROTOCOL_OTHER = 100;
	  public static final int EXTRA_VALUE_PROTOCOL_TYPE_IMAP = 2;
	  public static final int EXTRA_VALUE_PROTOCOL_TYPE_POP3 = 0;
	  public static final String EXTRA_VALUE_PROVIDER_GROUP_GMAIL = "Gmail";
	  public static final String EXTRA_VALUE_PROVIDER_GROUP_OTHER = "Other";
	  public static final String EXTRA_VALUE_PROVIDER_GROUP_YAHOO = "Yahoo";
	  public static final int EXTRA_VALUE_SECURITY_TYPE_NONE_CODE = 0;
	  public static final int EXTRA_VALUE_SECURITY_TYPE_SSL_CODE = 1;
	  public static final int EXTRA_VALUE_SECURITY_TYPE_TLS_CODE = 2;

	private final IBinder binder = new MailServiceBinder();

	private Context mContext;

	ConfigurationEvent mConfigurationEvent;
	
	
	@Override
	public void onCreate() {
		super.onCreate();	
		mContext = getApplicationContext();
		 
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
				Intent i = new Intent(ACTION_CONFIGURATION);
				// TODO:Encode
				i.setData(Uri.parse("dashconfig://?name=" + name + "&code="
						+ code));
				mContext.sendBroadcast(i,"com.dashwire.config.PERM_CONFIG");
			}
		};	
		
		
	}
	private static LinkedList<JSONObject> configs = new LinkedList<JSONObject>();
	private static boolean isRunning = false;
    private static Object syncro = new Object();
    private BroadcastReceiver emailConfigReceiver;
    private static boolean hasFailure = false;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent == null || !intent.hasExtra("config")) {
			mConfigurationEvent.notifyEvent("emails", ConfigurationItem.FAILED);
			return START_STICKY;			
		}
		JSONArray mConfigArray;
		try {
			mConfigArray = new JSONArray(intent.getStringExtra("config"));
		} catch (JSONException e) {
			mConfigurationEvent.notifyEvent("emails", ConfigurationItem.FAILED, e);
			return START_STICKY;
		}
		boolean executestart=false;
		synchronized(syncro)
        {
		    if(configs.isEmpty() && !isRunning)
		    {
		        //before creating a new receiver ensure that the old one is cleaned up.
                //mContext.unregisterReceiver( emailConfigReceiver );
                hasFailure = false;
		        emailConfigReceiver = new BroadcastReceiver(){
                    @Override
                    public void onReceive( Context arg0, Intent arg1 ) {

                        DashLogger.d( TAG, "End individual email config" );
                        boolean configNext = false;
                        synchronized(syncro)
                        {
                            if(arg1.getIntExtra( "result", -100 )<0)
                            {
                                DashLogger.d( TAG, "Individual Email failed to config" );
                                hasFailure=true;
                            }
                            if(configs.isEmpty() && isRunning)
                            {
                                DashLogger.d(TAG, "End email config");
                                
                                mContext.unregisterReceiver( emailConfigReceiver );
                                mConfigurationEvent.notifyEvent("exchange_accounts", hasFailure ? ConfigurationEvent.FAILED : ConfigurationEvent.CHECKED);
                                mConfigurationEvent.notifyEvent("emails",
                                        hasFailure ? ConfigurationEvent.FAILED
                                                : ConfigurationEvent.CHECKED);
                                isRunning=false;
                            }
                            else
                            {
                                DashLogger.d( TAG, "Proceeding to configure next email" );
                                configNext = true;
                            }
                        }
                        if(configNext)
                            configNextEmail();
                    }
                    
                };
                IntentFilter emailConfigIntent = new IntentFilter("com.motorola.email.AutoEmailAccountManager.FINISHED");
                //TODO: SECURITY This is a good example of how to set permission on Intent
                mContext.registerReceiver(emailConfigReceiver,emailConfigIntent, "com.android.email.permission.ACCESS_PROVIDER",null);
                executestart=true;
                isRunning = true;
                
            }
		    for (int i = 0; i < mConfigArray.length(); i++) {
		        try {
                    configs.addLast( mConfigArray.getJSONObject(i) );
                } catch ( JSONException e ) {
                    DashLogger.d(TAG, "Unable to enqueue email due to json error" );
                }
		    }
        }
        if(executestart) configNextEmail();
		
		return START_STICKY;
	}
	private void configNextEmail()
	{
	    DashLogger.d( TAG, "Start individual email config" );
	    JSONObject currentAccount = null;
	    synchronized(syncro)
        {
	        //pull an item from the list if one exists
	        if(!configs.isEmpty())
	            currentAccount = configs.removeFirst();
        }
	    if(currentAccount != null)
	    {
            DashLogger.d( TAG, "Configuring account" );
	        try {
                setupAccount(currentAccount);
            } catch ( Exception e ) {
                DashLogger.e( TAG, "Weird exception in email config : " + e.getMessage() );
            }
	    }
	}

	private void setupAccount(JSONObject account)
			throws Exception {
		String password = account.getString("password");
        String email = account.getString("login");
		String service = account.getString("service");
		String displayName;
		if (account.has("display_name")) {
			displayName = account.getString("display_name");
		} else {
			displayName = email;
		}

		if ("Other".equals(service)) {
            String incoming_type = account.getString( "incoming_type" );
            String incoming_host = account.getString( "incoming_host" );
            String outgoing_host = account.getString( "outgoing_host" );
            
		    if(incoming_type.equals("POP3"))
		    {
                savePopAccount(email, password, displayName, displayName, false, 
                		incoming_host, outgoing_host);
		    }
		    else if(incoming_type.equals( "IMAP"))
		    {
                saveImapAccount(email, password, displayName, incoming_host, outgoing_host);
		    }		    
		} 
		else if("Yahoo".equals(service) || "Att".equals(service)) {
			saveImapAccount(email, password, displayName, "android.imap.mail.yahoo.com", 
					"android.smtp.mail.yahoo.com");
		} else if("Aol".equals(service)) {
			saveImapAccount(email, password, displayName, "imap.aol.com", "smtp.aol.com");
		}
		else if("Exchange".equals( service ) ||"Hotmail".equals(service))
		{
		    
            String domain = account.optString("domain", "");
            String login = account.getString("login").toLowerCase();
            String emailAddress = account.optString("address", login);
            if (!Accounts.exchangeAccountExists(this, login)) {
                if("Exchange".equals(service)) {
                    String server = account.getString("server");
                    saveExchange(service, domain, server, emailAddress, login, password, displayName, account.getBoolean( "sync_calendar" ),account.getBoolean( "sync_contacts" ));
                } else if("Hotmail".equals(service)) {
                    saveExchange(service, domain, "m.hotmail.com", emailAddress, emailAddress, password, displayName, true, true);
                }
            }
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		//TODO: SECURITY Add caller check.
		return binder;
	}

	public class MailServiceBinder extends Binder {
		public EmailService getService() {
			return EmailService.this;
		}
	}
	
	/*
	public void saveYahooOrAttAccount(String email, String password) {
		Methods.invoke(setupClass, "setupAccount", mContext,
				ACTION_CREATE_ACCOUNT, email, password, "Yahoo");	
		
	}
	
	private void saveAolAccount(String email, String password) {
		Methods.invoke(setupClass, "setupAccount", mContext,
				ACTION_CREATE_ACCOUNT, email, password, "Other",
				"imap.aol.com", "smtp.aol.com", 143, 587, 1,
				EXTRA_VALUE_SECURITY_TYPE_NONE_CODE, EXTRA_VALUE_SECURITY_TYPE_NONE_CODE,
				EXTRA_VALUE_PROTOCOL_TYPE_IMAP);
	}
	*/
	
	protected void setContext(Context ctx) {
		mContext = ctx;
	}

    protected void saveImapAccount(String email, String password, String sender, 
        String imapServer, String smtpServer) {
        Intent localIntent = new Intent(ACTION_CREATE_ACCOUNT);
        
        localIntent.putExtra("email",email);
        localIntent.putExtra("user_name",email);
        localIntent.putExtra("pretty_name",sender);
        localIntent.putExtra("pass_encrypted",0);
        localIntent.putExtra("server_type","imap");
        localIntent.putExtra("in_login",email);
        localIntent.putExtra("in_password",password);
        localIntent.putExtra("in_server_address",imapServer);
        localIntent.putExtra("in_server_port", 993);
        localIntent.putExtra("in_security", 1);
        localIntent.putExtra("in_auth_type", 1);
        localIntent.putExtra("out_login",email);
        localIntent.putExtra("out_password",password);
        localIntent.putExtra("out_server_address",smtpServer);
        localIntent.putExtra("out_server_port", 465);
        localIntent.putExtra("out_security", 1);
        localIntent.putExtra("upgrademode", false);
        //localIntent.putExtra("sync_interval", 10);
        localIntent.putExtra("download_wifi", true);
        //paramIntent.getBooleanExtra("sync_wifi_only", false);
        localIntent.putExtra("notify", true);
        localIntent.putExtra("vibrate", false);
        //localIntent.putExtra("signature_b64","I'm sending email via dashconfig");
        //Current state bypasses security policy code
        localIntent.putExtra("validate_result_code",25);
        //these are probably unnecessary unless configuring exchange
        localIntent.putExtra("aes_sync_calendar", true);
        localIntent.putExtra("aes_sync_contacts", true);
        localIntent.putExtra("aes_sync_email", true);
        localIntent.putExtra("aes_sync_tasks", true);
        localIntent.putExtra("aes_sync_amount", 2);
        mContext.startService(localIntent);
    }

    protected void savePopAccount(String email, String password, String sender, String description, boolean isDefault, 
    	String popServer, String smtpServer) {
        Intent localIntent = new Intent(ACTION_CREATE_ACCOUNT);
        
        localIntent.putExtra("email",email);
        localIntent.putExtra("user_name",email);
        localIntent.putExtra("pretty_name",email);
        localIntent.putExtra("pass_encrypted",0);
        localIntent.putExtra("server_type","pop3");
        localIntent.putExtra("in_login",email);
        localIntent.putExtra("in_password",password);
        localIntent.putExtra("in_server_address",popServer);
        localIntent.putExtra("in_server_port", 995);
        localIntent.putExtra("in_security", 1);
        localIntent.putExtra("in_auth_type", 1);
        localIntent.putExtra("out_login",email);
        localIntent.putExtra("out_password",password);
        localIntent.putExtra("out_server_address",smtpServer);
        localIntent.putExtra("out_server_port", 465);
        localIntent.putExtra("out_security", 1);
        localIntent.putExtra("upgrademode", false);
        //localIntent.putExtra("sync_interval", 10);
        localIntent.putExtra("download_wifi", true);
        //paramIntent.getBooleanExtra("sync_wifi_only", false);
        localIntent.putExtra("notify", true);
        localIntent.putExtra("vibrate", false);
        //localIntent.putExtra("signature_b64","I'm sending email via dashconfig");
        //Current state bypasses security policy code
        localIntent.putExtra("validate_result_code",25);
        //these are probably unnecessary unless configuring exchange
        localIntent.putExtra("aes_sync_calendar", true);
        localIntent.putExtra("aes_sync_contacts", true);
        localIntent.putExtra("aes_sync_email", true);
        localIntent.putExtra("aes_sync_tasks", true);
        localIntent.putExtra("aes_sync_amount", 2);
        mContext.startService(localIntent);
    }
    protected void saveExchange(String service, String domain, String server, String emailAddress, String login,
            String password, String displayName, boolean sync_calendar, boolean sync_contacts)
            throws Exception {
 
            Intent localIntent = new Intent(ACTION_CREATE_ACCOUNT);
            
            localIntent.putExtra("email",emailAddress);
            
             	//{DCFGP-712: Hotmail display name is preceded by a backslash]
            	String lg = server.contains("hotmail") ? login : "\\" + login;
             
            if(domain != null && domain.length() > 0) {
                localIntent.putExtra("user_name",domain + "\\" + login);
            }
            else {
                localIntent.putExtra("user_name", lg);
            }
            
            localIntent.putExtra("pretty_name",displayName);
            localIntent.putExtra("pass_encrypted",0);
            localIntent.putExtra("server_type","eas");
            localIntent.putExtra("in_login",login);
            localIntent.putExtra("in_password",password);
            localIntent.putExtra("in_server_address",server);
            localIntent.putExtra("in_server_port", 443);
            localIntent.putExtra("in_security", 1);
            localIntent.putExtra("in_auth_type", 1);
            localIntent.putExtra("out_login",login);
            localIntent.putExtra("out_password",password);
            localIntent.putExtra("out_server_address",server);
            localIntent.putExtra("out_server_port", 443);
            localIntent.putExtra("out_security", 1);
            localIntent.putExtra("upgrademode", false);
            //localIntent.putExtra("sync_interval", 10);
            localIntent.putExtra("download_wifi", true);
            //paramIntent.getBooleanExtra("sync_wifi_only", false);
            localIntent.putExtra("notify", true);
            localIntent.putExtra("vibrate", false);
            //localIntent.putExtra("signature_b64","I'm sending email via dashconfig");
            //Current state bypasses security policy code
            localIntent.putExtra("validate_result_code",25);
            //these are probably unnecessary unless configuring exchange
            localIntent.putExtra("aes_sync_calendar", sync_calendar);
            localIntent.putExtra("aes_sync_contacts", sync_contacts);
            localIntent.putExtra("aes_sync_email", true);
            localIntent.putExtra("aes_sync_tasks", sync_calendar);
            localIntent.putExtra("aes_sync_amount", 2);
            mContext.startService(localIntent);
    }
}