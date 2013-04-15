package com.dashwire.config.resources;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class LgeEmailAccountDao {
    private static String TAG = "LgeEmailAccountDao";
    public static final String IMAP_YAHOO_COM = "imap.mail.yahoo.com";
    public static final String SMTP_YAHOO_COM = "smtp.mobile.mail.yahoo.com";
    public static final String IMAP_AOL_COM = "imap.aol.com";
    public static final String SMTP_AOL_COM = "smtp.aol.com";
    public static final String POP3_LIVE_COM = "pop3.live.com";
    public static final String SMTP_LIVE_COM = "smtp.live.com";
    
    public static final String TYPE_EXCHANGE = "Exchange";
    public static final String TYPE_HOTMAIL = "Hotmail";
    public static final String TYPE_IMAP = "IMAP";
    public static final String TYPE_POP3 = "POP3";
    
    public static final Uri EMAIL_ACCOUNT_PROVIDER = Uri.parse("content://com.lge.providers.lgemail/account");
   
    private final Context context;

    public LgeEmailAccountDao(Context context) {
        this.context = context;
    }

    public void saveExchangeAccount(String email, String password, String displayName, String exchangeIncomingServer, String exchangeOutgoingServer) {
        addEmailAccount(TYPE_EXCHANGE, email, password, exchangeIncomingServer, 0, exchangeOutgoingServer, 25, displayName);
    }
    
    public void saveHotmailAccount(String email, String password, String displayName) {
    	addEmailAccount(TYPE_HOTMAIL, email, password, POP3_LIVE_COM, 995, SMTP_LIVE_COM, 587, displayName);
    }

    public void saveYahooEmailAccount(String email, String password, String displayName) {
        saveImapAccount(email, password, IMAP_YAHOO_COM, SMTP_YAHOO_COM, displayName);
    }

    public void saveAolEmailAccount(String email, String password, String displayName) {
        saveImapAccount(email, password, IMAP_AOL_COM, SMTP_AOL_COM, displayName);
    }

    public void saveImapAccount(String email, String password, String imapServer, String smtpServer, String displayName) {
        addEmailAccount(TYPE_IMAP, email, password, imapServer, 993, smtpServer, 465, displayName);
    }

    public void savePop3Account(String email, String password, String pop3Server, String smtpServer, String displayName) {
        addEmailAccount(TYPE_POP3, email, password, pop3Server, 995, smtpServer, 465, displayName);
    }

    public void addEmailAccount(String emailType, String email, String password, String incomingServer, int incomingPort, String outgoingServer, int outgoingPort, String displayName) {
        //dropAllEmailAccountsForPlanAToWork();
        ContentValues cv = new ContentValues();
        cv.put("accountName", displayName);
        cv.put("userName", email);
        cv.put("mailAddress", email);
      
        cv.put("incommingLoginID", email);
        cv.put("incommingLoginPassword", password);
        cv.put("incommingServerAddress", incomingServer);
        cv.put("incommingServerPort", Integer.toString(incomingPort));
        cv.put("needSecureConnectionForIncomming", 1);
        cv.put("outgoingProtocolType", 1);
        cv.put("outgoingServerAddress", outgoingServer);
        cv.put("outgoingServerPort", outgoingPort);
        
        if (TYPE_EXCHANGE.equalsIgnoreCase( emailType ))
        {
            cv.put("authenticationType", 0);
            cv.put("incommingProtocolType", 0);
            cv.put("outgoingLoginID","");
            cv.put("outgoingLoginPassword","");
            cv.put("needSecureConnectionForOutgoing", 0);
            cv.put("leaveCopyAtServer", 0);
           
        }else
        {
            cv.put("authenticationType", 1); 
            cv.put("incommingProtocolType", 993 == incomingPort ? "IMAP" : "POP");
            cv.put("outgoingLoginID", email);
            cv.put("outgoingLoginPassword", password);
            cv.put("needSecureConnectionForOutgoing", 
                    outgoingPort == 25 ? 3 : /* tls if available */
                    outgoingPort == 587 ? 2 /* tls */: 1 /* ssl */);
            cv.put("leaveCopyAtServer", 993 == incomingPort ? 0 : 1);
        }
        Uri resultUri = context.getContentResolver().insert(EMAIL_ACCOUNT_PROVIDER, cv);
        if(resultUri == null) {
        	Log.e("LgeEmailAccountDao","Insert of e-mail account failed to create account in db");
        }
    }

}
