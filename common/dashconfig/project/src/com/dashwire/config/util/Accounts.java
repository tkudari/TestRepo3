package com.dashwire.config.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.database.Cursor;
import android.net.Uri;
import com.dashwire.base.debug.DashLogger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Accounts {
    public static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    public static final String COM_HTC_ANDROID_MAIL = "com.htc.android.mail";
    public static final String COM_ANDROID_EXCHANGE = "com.android.exchange";
    public static final String COM_ANDROID_CONTACTS = "com.android.contacts";
    public static final String COM_ANDROID_CALENDAR = "com.android.calendar";
    public static final String COM_GOOGLE = "com.google";

    public static final String IMAP_YAHOO_COM = "imap.mail.yahoo.com";
    public static final String SMTP_YAHOO_COM = "smtp.mail.yahoo.com";
    public static final String IMAP_AOL_COM = "imap.aol.com";
    public static final String SMTP_AOL_COM = "smtp.aol.com";
    public static final String POP_GMAIL_COM = "pop.gmail.com";
    public static final String IMAP_GMAIL_COM = "imap.gmail.com";
    public static final String SMTP_GMAIL_COM = "smtp.gmail.com";

    private static AtomicReference<Runnable> delayedAccountsChangedBroadcast = new AtomicReference<Runnable>();
    private static final Intent ACCOUNTS_CHANGED_INTENT = new Intent(AccountManager.LOGIN_ACCOUNTS_CHANGED_ACTION).setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);

    private static final String TAG = Accounts.class.getCanonicalName();
    

    public static void beginSync(Context context, String accountName, String accountType, Map<String, Boolean> syncSettings) {
        Account account = new Account(accountName, accountType);
        Account[] accounts = AccountManager.get(context).getAccountsByType(accountType);
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].name.equals(accountName)) {
                account = accounts[i];
                break;
            }
        }

        ContentResolver.getMasterSyncAutomatically();
        for (SyncAdapterType type : ContentResolver.getSyncAdapterTypes()) {
            if (accountType.equals(type.accountType)) {
                boolean sync = Accounts.shouldBeginSync(syncSettings, type.authority);
                ContentResolver.setIsSyncable(account, type.authority, 1);
                ContentResolver.setSyncAutomatically(account, type.authority, sync);
                if (sync) {
                    DashLogger.d(TAG, String.format("beginSync sets sync automatically on account '%s' for %s.", accountName, type.authority));
                }
            }
        }
    }

    public static boolean shouldBeginSync(Map<String, Boolean> syncSettings, String provider) {
        return Boolean.TRUE.equals(syncSettings.get(provider));
    }

    public static void broadcastAccountsChangedWithDelay(Context context, long delay, TimeUnit unit) {
        EXECUTOR.schedule(delayedAccountsChangedBroadcastOf(context), delay, unit);
    }

    private static Runnable delayedAccountsChangedBroadcastOf(final Context context) {
        delayedAccountsChangedBroadcast.set(new Runnable() {
            public void run() {
                if (delayedAccountsChangedBroadcast.get() == this) {
                    broadcastAccountsChanged(context); // alternative to accountManager.setUserData(account, "broadcast", "true");
                }
            }
        });

        return delayedAccountsChangedBroadcast.get();
    }

    private static void broadcastAccountsChanged(Context context) {
        context.sendBroadcast(ACCOUNTS_CHANGED_INTENT,"com.dashwire.config.PERM_CONFIG_INTERNAL");
        DashLogger.d(TAG, "broadcastAccountsChanged().");
    }
    
    public static boolean emailAccountExists(Context context, String emailAddress) {
        return getEmailAddresses(context).contains(emailAddress);
    }


    private static Set<String> getEmailAddresses(Context context) {
        Set<String> emailAddresses = new HashSet<String>();
        Uri uri = Uri.parse("content://com.android.email.provider/account");
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        try {
            int columnIndex = cursor.getColumnIndex("emailAddress");
            while (cursor.moveToNext()) {
                emailAddresses.add(cursor.getString(columnIndex));
            }
        } finally {
            cursor.close();
        }

        return emailAddresses;
    }  
    
    public static boolean exchangeAccountExists(Context context, String emailAddress) {
        for (Account account : getExchangeAccounts(context)) {
            if (emailAddress.equals(account.name)) {
                return true;
            }
        }

        return false;
    }

    private static Account[] getExchangeAccounts(Context context) {
        AccountManager accountManager = AccountManager.get(context.getApplicationContext());
        return accountManager.getAccountsByType(COM_ANDROID_EXCHANGE);
    }
   
}
