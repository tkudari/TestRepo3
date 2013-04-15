package com.dashwire.config.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class GoogleAccountUtils {
    public static String SET_BACKUP_ACCOUNT = "com.google.android.backup.SetBackupAccount";
    public static String ENABLE_BACKUP = "com.google.android.backup.BackupEnabler";
    public static final Intent START_RESTORE = new Intent("android.intent.action.START_RESTORE");
    public static final String COM_GOOGLE = "com.google";

    // com.google.android.backup.BackupTransportService.onStartCommand accepts an account as an input.
    public static void setBackupAccount(Context context, String accountName, String accountType) {
        setBackupAccount(context, new Account(accountName, accountType));
    }

    public static void setBackupAccount(Context context, Account account) {
        context.startService(new Intent(SET_BACKUP_ACCOUNT).putExtra("backupAccount", account));
    }

    // com.google.android.backup.BackupEnabler.onStartCommand accepts a backup enable as a boolean.
    public static void enableBackup(Context context, boolean enable) {
        context.startService(new Intent(ENABLE_BACKUP).putExtra("BACKUP_ENABLE", enable));
    }

    // com.google.android.gsf.login.SyncIntroActivity shows an option to perform the account restore
    // if this is the 1st google account, and an existing google account (not a new one), and backup exists.
    // We get notified of restore completion event by ServiceConnection.onServiceDisconnected from the service.
    public static void performRestoreFor1stGoogleAccountIfBackupExists(Context context) {
        if (backupExists(context) && 
                1 == findAccountsOfTypeAndNotMatching(AccountManager.get(context), COM_GOOGLE, "@youtube.com").size()) {
            performRestore(context);
        }
    }

    public static void performRestore(Context context) {
        context.startService(START_RESTORE);
    }

    public static boolean backupExists(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo("com.google.android.backup", 0);
            ResolveInfo resolveInfo = pm.resolveService(new Intent("com.google.android.backup.BackupEnabler"), 0);
            return null != appInfo && null != resolveInfo;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            return false;
        }
    }

    public static List<Account> findAccountsOfTypeAndNotMatching(AccountManager accountManager, String type, String pattern) {
        return findAccountsOfTypeAndNotMatching(accountManager, type, Pattern.compile(pattern));
    }

    public static List<Account> findAccountsOfTypeAndNotMatching(AccountManager accountManager, String type, Pattern pattern) {
        Account[] accounts = accountManager.getAccountsByType(type);
        List<Account> output = new ArrayList<Account>(accounts.length);
        for (Account account : accounts) {
            if (!pattern.matcher(account.name).find()) {
                output.add(account);
            }
        }

        return output;
    }
}
