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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;


public class EmailService extends Service {

	private static String TAG = EmailService.class.getCanonicalName();

	public static final String ACTION_CREATE_ACCOUNT 
	= "com.dashwire.email.Accounts.CREATE_ACCOUNT";
	public static final String ACTION_EAS_CREATE_ACCOUNT 
	= "com.dashwire.email.Accounts.CREATE_ACCOUNT";

	
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
                        	
                            if(!arg1.getBooleanExtra("success", false))
                            {
                            	String email = arg1.getStringExtra("email");
                                DashLogger.d( TAG, "Individual Email failed to config. email = " + email);
                                hasFailure=true;
                            }
                            if(configs.isEmpty() && isRunning)
                            {
                                DashLogger.d( TAG, "End email config" );
                                
                                mContext.unregisterReceiver( emailConfigReceiver );
                                mConfigurationEvent.notifyEvent("exchange_accounts", hasFailure ? ConfigurationEvent.FAILED : ConfigurationEvent.CHECKED);
                                mConfigurationEvent.notifyEvent("emails", hasFailure ? ConfigurationEvent.FAILED
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
                DashLogger.d( TAG, "Registering receiver for com.dashwire.email.Accounts.CREATE_ACCOUNT_RESULT" );
                IntentFilter emailConfigIntent = new IntentFilter("com.dashwire.email.Accounts.CREATE_ACCOUNT_RESULT");
                //TODO: SECURITY This is a good example of how to set permission on Intent
                mContext.registerReceiver(emailConfigReceiver,emailConfigIntent);
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
		DashLogger.d(TAG, "In setupAccount with");
        
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
			saveImapAccount(email, password, displayName, "imap.mail.yahoo.com", "smtp.mail.yahoo.com");
		} else if("Aol".equals(service)) {
			saveImapAccount(email, password, displayName, "imap.aol.com", "smtp.aol.com");
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
	
	protected void setContext(Context ctx) {
		mContext = ctx;
	}

    protected void saveImapAccount(String email, String password, String displayName, 
        String imapServer, String smtpServer) {
        Intent localIntent = new Intent(ACTION_CREATE_ACCOUNT);
        
        localIntent.putExtra("version", "1.0");
        
        localIntent.putExtra("displayName",displayName);
        
        localIntent.putExtra("email", email);
        localIntent.putExtra("password",password);
        
        localIntent.putExtra("serviceType","imap");
        
        localIntent.putExtra("inServer",imapServer);
        localIntent.putExtra("inPort", "993");
        localIntent.putExtra("inSecurity", "ssl");
        
        localIntent.putExtra("outLogin",email);
        localIntent.putExtra("outPassword",password);
        
        localIntent.putExtra("outServer",smtpServer);
        localIntent.putExtra("outPort", "465");
        localIntent.putExtra("outSecurity", "ssl");
        
        localIntent.putExtra("syncEmail", true);
        localIntent.putExtra("syncContacts", true);
        localIntent.putExtra("syncCalendar", true);
        
        DashLogger.d(TAG, "Sending email config Intent.");
        
        
        mContext.sendBroadcast(localIntent, "com.dashwire.email.Accounts.permission.CREATE_ACCOUNT");
    }

    protected void savePopAccount(String email, String password, String displayName, String description, boolean isDefault, 
    	String popServer, String smtpServer) {
        Intent localIntent = new Intent(ACTION_CREATE_ACCOUNT);
        
        localIntent.putExtra("version", "1.0");
        
        localIntent.putExtra("displayName",displayName);
        
        localIntent.putExtra("email", email);
        localIntent.putExtra("password",password);
        
        localIntent.putExtra("serviceType","pop");
        
        localIntent.putExtra("inServer",popServer);
        localIntent.putExtra("inPort", "995");
        localIntent.putExtra("inSecurity", "ssl");
        
        localIntent.putExtra("outLogin",email);
        localIntent.putExtra("outPassword",password);
        
        localIntent.putExtra("outServer",smtpServer);
        localIntent.putExtra("outPort", "465");
        localIntent.putExtra("outSecurity", "ssl");
        
        localIntent.putExtra("syncEmail", true);
        localIntent.putExtra("syncContacts", true);
        localIntent.putExtra("syncCalendar", true);
        
        DashLogger.d(TAG, "Sending email config Intent.");
        
        mContext.sendBroadcast(localIntent, "com.dashwire.email.Accounts.permission.CREATE_ACCOUNT");
    }
}