package com.dashwire.config.email;
import com.dashwire.base.debug.DashLogger;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;


import com.dashwire.config.DashconfigApplication;
import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ConfigurationItem;

public class ExchangeEmailService extends IntentService {
    public ExchangeEmailService() {
        super("service");
    }

    public ExchangeEmailService(String name) {
        super(name);
    }

    private final IBinder binder = new ExchangeServiceBinder();
    private Context mContext;
    private ConfigurationEvent mConfigurationEvent;
    
    public static final String ACTION_CREATE_ACCOUNT 
	= "com.motorola.email.AUTO_CREATE_ACCOUNT";


    @Override
    public void onCreate() {
        super.onCreate();
        DashLogger.d("!!!", "!!! Creating Exchange Service: ");
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
                i.setData(Uri.parse("dashconfig://?name=" + name + "&code=" + code));
                mContext.sendBroadcast(i,"com.dashwire.config.PERM_CONFIG");
            }
        };  

        DashLogger.d("!!!", "!!! Creating Exchange Config Event: ");
    }

    public void onHandleIntent( Intent intent) {
        DashLogger.d("Exchange Service", "Starting Exchange Service: ");
        if(intent == null || !intent.hasExtra("config")) {
            mConfigurationEvent.notifyEvent("exchange_accounts", ConfigurationItem.FAILED);
        }
        
        
        JSONArray configs = null;
        try {
            configs = new JSONArray(intent.getStringExtra("config"));
            Intent mail = new Intent();
            mail.setComponent( new ComponentName( DashconfigApplication.getDeviceContext().getStringConst(mContext, "EMAIL_CONFIG_COMPONENT_URI"), "com.dashwire.config.email.EmailService" ) );
            mail.putExtra( "config", configs.toString() );  
            mContext.startService( mail );
        } catch (JSONException e) {
            mConfigurationEvent.notifyEvent("exchange_accounts", ConfigurationItem.FAILED, e);
        }  
        
    }

    @Override
    public IBinder onBind(Intent intent) {
    	//TODO: SECURITY Add caller check.
		return binder;
    }

    public class ExchangeServiceBinder extends Binder {
        public ExchangeEmailService getService() {
            return ExchangeEmailService.this;
        }
    }

	protected void setContext(Context ctx) {
		mContext = ctx;
	}
	
    
}
