package com.dashwire.config.email;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
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

public class EmailService extends Service {
    private static String TAG = "EmailService";
    private final IBinder binder = new MailServiceBinder();
    private Context mContext;
    private Set<String> existingAccounts;
    private static final String ACCOUNT_URI = "content://com.lge.providers.lgemail/account";
    private LgeEmailAccountDao emailAccountDAO;

    public EmailService() {
    }
    
    public EmailService( Context context ) {
        mContext = context;
        emailAccountDAO = new LgeEmailAccountDao( this );
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        emailAccountDAO = new LgeEmailAccountDao( mContext );
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {

        ConfigurationEvent configurationEvent = new ConfigurationEvent() {
            @Override
            public void notifyEvent( String name, int code ) {
                notifyEvent( name, code, null );
            }

            @Override
            public void notifyEvent( String name, int code, Throwable e ) {
                if ( equals( e != null ) ) {
                    Log.v( TAG, "Message from notifyEvent throwable = " + e.getMessage() );
                }
                Intent i = new Intent( ACTION_CONFIGURATION );
                i.setData( Uri.parse( "dashconfig://?name=" + name + "&code=" + code ) );
                mContext.sendBroadcast( i, "com.dashwire.config.PERM_CONFIG" );
            }
        };

        if ( intent == null || !intent.hasExtra( "config" ) ) {
            configurationEvent.notifyEvent( "emails", ConfigurationItem.FAILED );
        } else {
            JSONArray mConfigArray;
            boolean hasFailure = false;
            try {
                mConfigArray = new JSONArray( intent.getStringExtra( "config" ) );

                for ( int index = 0; index < mConfigArray.length(); index++ ) {
                    JSONObject account = mConfigArray.getJSONObject( index );
                    String email = account.getString( "login" ).toLowerCase();
                    if ( !accountExists( email ) )
                    {
                        if ( isMailAccount( account.getString( "service" ) ) ) {
                            setupAccount( account, email );
                        }
                    }
                }

            } catch ( JSONException je ) {
                hasFailure = true;
            } catch ( Exception je ) {
               
                hasFailure = true;
            }

            configurationEvent.notifyEvent( "emails", hasFailure ? ConfigurationEvent.FAILED : ConfigurationEvent.CHECKED );
        }
        return START_STICKY;
    }

    private boolean isMailAccount( String service ) {
        return !( "Exchange".equals( service ) );
    }

    private void setupAccount( JSONObject account, String email ) throws Exception {
        String password = account.getString( "password" );
        String service = account.getString( "service" );
        String displayName = account.optString( "display_name", email );
        if ( "Other".equals( service ) ) {
            String incoming_type = account.getString( "incoming_type" );
            String incoming_host = account.getString( "incoming_host" );
            String outgoing_host = account.getString( "outgoing_host" );
            if ( incoming_type.equals( "POP3" ) ) {
                emailAccountDAO.savePop3Account( email, password, incoming_host, outgoing_host, displayName );
            } else if ( incoming_type.equals( "IMAP" ) ) {
                emailAccountDAO.saveImapAccount( email, password, incoming_host, outgoing_host, displayName );
            }
        } else {
            if ( "Aol".equalsIgnoreCase( service ) ) {
                emailAccountDAO.saveImapAccount( email, password, LgeEmailAccountDao.IMAP_AOL_COM, LgeEmailAccountDao.SMTP_AOL_COM, displayName );
            } else if ( "Att".equalsIgnoreCase( service ) || "Yahoo".equalsIgnoreCase( service ) ) {
                emailAccountDAO.saveImapAccount( email, password, LgeEmailAccountDao.IMAP_YAHOO_COM, LgeEmailAccountDao.SMTP_YAHOO_COM, displayName );
            }
        }
    }

    @Override
    public IBinder onBind( Intent intent ) {
        return binder;
    }

    public class MailServiceBinder extends Binder {
        public EmailService getService() {
            return EmailService.this;
        }
    }
    
    private boolean accountExists( String emailAddress ) {
        Set<String> exisingAccounts = getExistingAccounts();
        return exisingAccounts.contains( emailAddress );
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
    

    private Set<String> getExistingAccounts() {
        if ( existingAccounts == null ) {
            existingAccounts = getEmailAddresses();
        }
        return existingAccounts;
    }
}