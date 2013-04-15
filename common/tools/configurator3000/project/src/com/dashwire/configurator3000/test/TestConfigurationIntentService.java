package com.dashwire.configurator3000.test;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class TestConfigurationIntentService extends IntentService {

    public final String TAG = TestConfigurationIntentService.class.getCanonicalName();
    public final Context context = this;

    public TestConfigurationIntentService() {
        super( "TestConfigurationIntentService" );
        
    }

    public TestConfigurationIntentService( String name ) {
        super( name );
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void onHandleIntent( Intent intent ) {
    	
	     
        String jsonInput = "";
        String action = intent.getAction();
        
        if (intent.hasExtra("json"))
        {
        	jsonInput = intent.getExtras().getString("json", "json");
        } else
        {
        	//jsonInput = "{\"config\":{\"emails\":[{\"login\":\"ernesto.fresco@hotmail.com\",\"password\":\"wordpass\",\"service\":\"Hotmail\",\"sync_email\":true},{\"login\":\"ernesto.frescho@yahoo.com\",\"password\":\"wordpass\",\"service\":\"Yahoo\",\"sync_email\":true}]}}";
        	//jsonInput = "{\"emails\":[{\"login\":\"ernesto.fresco@hotmail.com\",\"password\":\"wordpass\",\"service\":\"Hotmail\",\"sync_email\":true},{\"login\":\"ernesto.frescho@yahoo.com\",\"password\":\"wordpass\",\"service\":\"Yahoo\",\"sync_email\":true}]}";
        	//jsonInput = "[{\"login\":\"ernesto.fresco@hotmail.com\",\"password\":\"wordpass\",\"service\":\"Hotmail\",\"sync_email\":true},{\"login\":\"ernesto.frescho@yahoo.com\",\"password\":\"wordpass\",\"service\":\"Yahoo\",\"sync_email\":true}]";
        	//jsonInput = "[{\"login\":\"qaex@dashwire.com\",\"password\":\"dashwire123!\",\"service\":\"Exchange\",\"sync_email\":true,\"domain\":\"mex07a.mlsrvr.com\",\"server\":\"mex07a.emailsrvr.com\"},{\"login\":\"ernesto.fresco@hotmail.com\",\"password\":\"wordpass\",\"service\":\"Hotmail\",\"sync_email\":true}]";
        	jsonInput = "[{\"login\":\"dashconfig@aol.com\",\"password\":\"crushit!\",\"service\":\"Aol\",\"sync_email\":true},{\"login\":\"ljhalleran@att.net\",\"password\":\"ready2go\",\"service\":\"Att\",\"sync_email\":true}]";
        }
        	
        Log.v(TAG,"json input = " + jsonInput);
        
        if (action.equalsIgnoreCase("com.dashwire.test.config"))
        {
            Intent configurationIntent = new Intent();
            configurationIntent.setClassName("com.dashwire.config", "com.dashwire.config.configuration.ConfigurationService");
            configurationIntent.putExtra( "config", jsonInput );
            context.startService(configurationIntent);
        } else if (action.equalsIgnoreCase("com.dashwire.test.email"))
        {
        	Intent configurationIntent = new Intent();
            configurationIntent.setClassName("com.dashwire.config.email", "com.dashwire.config.email.EmailService");
            configurationIntent.putExtra( "config", jsonInput );
            context.startService(configurationIntent);
        } else if (action.equalsIgnoreCase("com.dashwire.test.exchange"))
        {
        	Intent configurationIntent = new Intent();
            configurationIntent.setClassName("com.dashwire.config.email", "com.dashwire.config.email.ExchangeEmailService");
            configurationIntent.putExtra( "config", jsonInput );
            Log.v(TAG,"starting ExchangeEmailService");
            context.startService(configurationIntent);
        }

    }
}
