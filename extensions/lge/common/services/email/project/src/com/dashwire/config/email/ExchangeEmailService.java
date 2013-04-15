package com.dashwire.config.email;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ConfigurationItem;
import com.dashwire.config.resources.LgeEmailAccountDao;

public class ExchangeEmailService extends IntentService {
    private static String TAG = "ExchangeEmailService";
    
    public ExchangeEmailService() {
        super( "service" );
    }

    public ExchangeEmailService( String name ) {
        super( name );
    }

    private static final String ACCOUNT_URI = "content://com.lge.providers.lgemail/account";

    private final IBinder binder = new ExchangeServiceBinder();

    private Context mContext;

    public static final String OPTIONS_USERNAME = "username";
    public static final String OPTIONS_PASSWORD = "password";
    public static final String OPTIONS_CONTACTS_SYNC_ENABLED = "contacts";
    public static final String OPTIONS_CALENDAR_SYNC_ENABLED = "calendar";
    public static final String OPTIONS_EMAIL_SYNC_ENABLED = "email";
    private ConfigurationEvent mConfigurationEvent;
    private Set<String> existingAccounts;
    private LgeEmailAccountDao emailAccountDAO;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        emailAccountDAO = new LgeEmailAccountDao( this );
        mConfigurationEvent = new ConfigurationEvent() {
            @Override
            public void notifyEvent( String name, int code ) {
                notifyEvent( name, code, null );
            }

            @Override
            public void notifyEvent( String name, int code, Throwable e ) {
                if ( equals( e != null ) ) {
                    e.printStackTrace();
                }
                Intent i = new Intent( ACTION_CONFIGURATION );
                // TODO:Encode
                i.setData( Uri.parse( "dashconfig://?name=" + name + "&code=" + code ) );
                mContext.sendBroadcast( i, "com.dashwire.config.PERM_CONFIG" );
            }
        };
    }

    public void onHandleIntent( Intent intent ) {
        boolean hasFailure = false;

        if ( intent == null || !intent.hasExtra( "config" ) ) {
            hasFailure = true;
        } else {
            JSONArray mConfigArray = null;

            try {
                mConfigArray = new JSONArray( intent.getStringExtra( "config" ) );

                for ( int index = 0; index < mConfigArray.length(); index++ ) {

                    JSONObject account = mConfigArray.getJSONObject( index );

                    String login = account.getString( "login" ).toLowerCase();
                    String password = account.getString( "password" );
                    String service = account.getString( "service" );
                    String displayName;
                    if ( account.has( "display_name" ) ) {
                        displayName = account.getString( "display_name" );
                    } else {
                        displayName = login;
                    }

                    //if ( !accountExists( login ) ) {
                        if ( "Exchange".equalsIgnoreCase( service ) ) {
                            String domain = account.optString( "domain" );
                            String server = account.getString( "server" );
                            saveExchangeAccount( domain, server, login, password, displayName, 0, false,false);
                        } else if ( "Hotmail".equals( service ) ) {
                            saveHotmailAccount( login, password, displayName );
                        }
                   // }
                }

            } catch ( JSONException e ) {
                mConfigurationEvent.notifyEvent( "exchange_accounts", ConfigurationItem.FAILED, e );
                hasFailure = true;
            } catch ( Exception je ) {
                Log.v(TAG,"Exception = " + je.getMessage());
                hasFailure = true;
            }
        }

        mConfigurationEvent.notifyEvent( "exchange_accounts", hasFailure ? ConfigurationEvent.FAILED : ConfigurationEvent.CHECKED );

    }

    @Override
    public IBinder onBind( Intent intent ) {
        // TODO: SECURITY Add caller check.
        return binder;
    }

    public class ExchangeServiceBinder extends Binder {
        public ExchangeEmailService getService() {
            return ExchangeEmailService.this;
        }
    }

    private boolean accountExists( String emailAddress ) {
        Set<String> exisingAccounts = getExistingAccounts();
        return exisingAccounts.contains( emailAddress );
    }

    private Set<String> getExistingAccounts() {
        if ( existingAccounts == null ) {
            existingAccounts = getEmailAddresses();
        }
        return existingAccounts;
    }

    private Set<String> getEmailAddresses() {
        Set<String> emailAddresses = new HashSet<String>();
        Uri uri = Uri.parse( ACCOUNT_URI );
        Cursor cursor = mContext.getContentResolver().query( uri, null, null, null, null );
        if ( cursor != null ) {
            int columnIndex = cursor.getColumnIndex( "mailAddress" );
            while ( cursor.moveToNext() ) {
                emailAddresses.add( cursor.getString( columnIndex ) );
            }

            cursor.close();
        }

        return emailAddresses;
    }

    /**
     * AccountSetupAccountType AccountSetupBasics EasSyncServce
     * */
    private void saveExchangeAccount( String domain, String server, String email, String password, String displayName, int port,
            boolean sync_calendar, boolean sync_contacts ) {
        emailAccountDAO.saveExchangeAccount(email, password, displayName, server, domain );

    }

    public void saveHotmailAccount( String email, String password, String displayName ) {
        emailAccountDAO.saveHotmailAccount( email, password, displayName );
    }
}
