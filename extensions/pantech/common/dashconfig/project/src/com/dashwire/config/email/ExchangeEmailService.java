package com.dashwire.config.email;

import com.dashwire.base.debug.DashLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.dashwire.config.configuration.ConfigurationEvent;

public class ExchangeEmailService extends IntentService
{
    private static String TAG = ExchangeEmailService.class.getCanonicalName();

    public ExchangeEmailService() {
        super( "service" );
    }

    public ExchangeEmailService( String name ) {
        super( name );
    }

    private final IBinder binder = new ExchangeServiceBinder();
    private Context mContext;
    private ConfigurationEvent mConfigurationEvent;

    public static final String ACTION_CREATE_ACCOUNT = "com.dashwire.email.Accounts.CREATE_ACCOUNT";

    @Override
    public void onCreate()
    {
        super.onCreate();
        DashLogger.d( "ExchangeEmailService", "!!! Creating Exchange Service: " );
        mContext = getApplicationContext();
        mConfigurationEvent = new ConfigurationEvent() {
            @Override
            public void notifyEvent( String name, int code )
            {
                notifyEvent( name, code, null );
            }

            @Override
            public void notifyEvent( String name, int code, Throwable e )
            {
                if ( equals( e != null ) )
                {
                    e.printStackTrace();
                }
                Intent i = new Intent( ACTION_CONFIGURATION );
                // TODO:Encode
                i.setData( Uri.parse( "dashconfig://?name=" + name + "&code=" + code ) );
                mContext.sendBroadcast( i, "com.dashwire.config.PERM_CONFIG" );
            }
        };

        DashLogger.d( "ExchangeEmailService", "!!! Creating Exchange Config Event: " );
    }

    public void onHandleIntent( Intent intent )
    {
        boolean hasFailure = false;

        if ( intent == null || !intent.hasExtra( "config" ) )
        {
            hasFailure = true;
        } else
        {
            JSONArray mConfigArray;
            hasFailure = false;
            try
            {
                mConfigArray = new JSONArray( intent.getStringExtra( "config" ) );

                for ( int index = 0; index < mConfigArray.length(); index++ )
                {
                    JSONObject account = mConfigArray.getJSONObject( index );
                    setupAccount( account );
                }

            } catch ( JSONException je )
            {
                hasFailure = true;
            } catch ( Exception je )
            {

                hasFailure = true;
            }
        }

        mConfigurationEvent.notifyEvent( "exchange_accounts", hasFailure ? ConfigurationEvent.FAILED : ConfigurationEvent.CHECKED );

    }

    @Override
    public IBinder onBind( Intent intent )
    {
        // TODO: SECURITY Add caller check.
        return binder;
    }

    public class ExchangeServiceBinder extends Binder
    {
        public ExchangeEmailService getService()
        {
            return ExchangeEmailService.this;
        }
    }

    protected void setContext( Context ctx )
    {
        mContext = ctx;
    }

    private void setupAccount( JSONObject account ) throws Exception
    {
        DashLogger.d( TAG, "In setupAccount with" );

        String password = account.getString( "password" );
        String email = account.getString( "login" );
        String service = account.getString( "service" );
        String displayName;
        if ( account.has( "display_name" ) )
        {
            displayName = account.getString( "display_name" );
        } else
        {
            displayName = email;
        }
        
        if ( "Exchange".equals( service ) || "Hotmail".equals( service ) )
        {

            String domain = account.optString( "domain", "" );
            String login = account.getString( "login" ).toLowerCase();
            String emailAddress = account.optString( "address", login );
                if ( "Exchange".equals( service ) )
                {
                    String server = account.getString( "server" );
                    saveExchange( service, domain, server, emailAddress, login, password, displayName, account.getBoolean( "sync_calendar" ),
                            account.getBoolean( "sync_contacts" ) );
                } else if ( "Hotmail".equals( service ) )
                {
                    saveExchange( service, domain, "m.hotmail.com", emailAddress, emailAddress, password, displayName, true, true );
                }
        }
    }

    protected void saveExchange( String service, String domain, String server, String emailAddress, String login, String password, String displayName,
            boolean sync_calendar, boolean sync_contacts ) throws Exception
    {

        Intent localIntent = new Intent( ACTION_CREATE_ACCOUNT );

        localIntent.putExtra( "version", "1.0" );

        localIntent.putExtra( "displayName", displayName );

        localIntent.putExtra( "email", emailAddress );
        localIntent.putExtra( "password", password );
        localIntent.putExtra( "domain", domain );

        localIntent.putExtra( "serviceType", "eas" );

        localIntent.putExtra( "inServer", server );
        localIntent.putExtra( "inPort", "443" );
        localIntent.putExtra( "inSecurity", "ssl" );

        localIntent.putExtra( "syncEmail", true );
        localIntent.putExtra( "syncContacts", sync_calendar );
        localIntent.putExtra( "syncCalendar", sync_contacts );

        DashLogger.d(TAG, "Sending email config Intent.");
        
        mContext.sendBroadcast( localIntent, "com.dashwire.email.Accounts.permission.CREATE_ACCOUNT" );
    }

}
