package com.dashwire.config.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.widget.TextView;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.DashconfigApplication;
import com.dashwire.config.DownloadService;
import com.dashwire.config.R;
import com.dashwire.config.tasks.SharedPreferenceWriteTask;
import com.dashwire.config.ui.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

public class CommonUtils implements DashCommonUtil
{

    protected static final String TAG = CommonUtils.class.getCanonicalName();

    private static ProgressDialog progressDialog;
    private static PowerManager powerManager = null;
    private static WakeLock wakeLock = null;

    public static boolean isDataConnectionAvailable( Context context )
    {
        ConnectivityManager connectivity = ( ConnectivityManager ) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        if ( connectivity != null )
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if ( info != null )
            {
                for ( int i = 0; i < info.length; i++ )
                {
                    if ( info[ i ].getState() == NetworkInfo.State.CONNECTED )
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getAndroidOSProperty(Context context, String key) throws IllegalArgumentException {
        String ret= "";

        try{

            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[1];
            paramTypes[0]= String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            //Parameters
            Object[] params= new Object[1];
            params[0]= new String(key);

            ret= (String) get.invoke(SystemProperties, params);

        }catch( IllegalArgumentException iAE ){
            throw iAE;
        }catch( Exception e ){
            ret= "";
        }

        return ret;
    }

    public static boolean isHTCSense4(String senseLevel) {
        return senseLevel.startsWith("4");
    }

    public static boolean accountExists(String login, Context context) {
        if ( login != null ) {
            Account[] accounts = AccountManager.get(context).getAccountsByType( null );
            for ( int index = 0; index < accounts.length; index++ ) {
                if ( login.equals( accounts[ index ].name ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean removeAccount(String login, Context context, AccountManagerCallback<Boolean> callback, Handler handler) {
        AccountManager accountManager = AccountManager.get(context);
        if (login != null) {
            Account[] accounts = accountManager.getAccountsByType( null );
            for ( int index = 0; index < accounts.length; index++ ) {
                if ( login.equals( accounts[ index ].name ) ) {
                    accountManager.removeAccount(accounts[ index ], callback, handler);
                    return true;
                }
            }
        }
        return false;
    }

    public static void startGenericErrorActivity( String retryUri, String cancelUri, String title, String body, Context context )
    {
        startGenericErrorActivity( retryUri, cancelUri, title, body, null, null, context );
    }

    public static void startGenericErrorActivity( String retryUri, String cancelUri, String title, String body, String retryText, String cancelText,
            Context context )
    {
        Intent intent = new Intent( context, GenericErrorActivity.class );
        intent.putExtra( "retry", retryUri );
        intent.putExtra( "cancel", cancelUri );
        intent.putExtra( "title", title );
        intent.putExtra( "body", body );
        intent.putExtra( "retry_text", retryText );
        intent.putExtra( "cancel_text", cancelText );
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity( intent );
    }

    public static void startConnectivityErrorActivity( String retryUri, String cancelUri, Context context )
    {
        startGenericErrorActivity( retryUri, cancelUri, context.getResources().getString( R.string.connectivity_error_title ), context.getResources()
                .getString( R.string.connectivity_error_body ), context );
    }

    public static void startNotConnectedErrorActivity( String retryUri, String cancelUri, Context context )
    {
        startGenericErrorActivity( retryUri, cancelUri, context.getResources().getString( R.string.not_connected_error_title ), context.getResources()
                .getString( R.string.not_connected_error_body ), context );
    }

    public static void startDownloadingConfigErrorActivity( String retryUri, String cancelUri, Context context )
    {
        startGenericErrorActivity( retryUri, cancelUri, context.getResources().getString( R.string.downloading_config_error_title ), context.getResources()
                .getString( R.string.downloading_config_error_body ), context );
    }

    public static void startTimeoutActivity( String retryUri, String cancelUri, Context context )
    {
        startGenericErrorActivity( retryUri, cancelUri, context.getResources().getString( R.string.timeout_error_title ),
                context.getResources().getString( R.string.timeout_error_body ), context );
    }

    public static void startCodeExpiredActivity( String retryUri, String cancelUri, Context context )
    {
        startGenericErrorActivity( retryUri, cancelUri, context.getResources().getString( R.string.code_expired_title ),
                context.getResources().getString( R.string.code_expired_body ), context );
    }

    public static Intent getConfigurationActivityIntent( Context context )
    {
        Intent intent = new Intent( context, ConfigurationActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
        return intent;
    }

    public static Intent getCompletedActivityIntent( Context context )
    {
        Intent intent = new Intent( context, CompletedActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
        return intent;
    }

    public static Intent getLanguageSelectionActivityIntent( Context context )
    {
        Intent intent = new Intent( context, LanguageSelectionActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
        return intent;
    }

    public static void clickify( TextView view, final String textToSet, final String clickableText, final ClickSpan.OnClickListener listener )
    {

        CharSequence text = textToSet;
        String string = text.toString();
        ClickSpan span = new ClickSpan( listener );

        int start = string.indexOf(  clickableText );
        int end = start + clickableText.length();
        if ( start == -1 ) {
            return;
		}

        if ( text instanceof Spannable ) {
            ( ( Spannable ) text ).setSpan( span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
		} else {
			SpannableString s = SpannableString.valueOf( text );
            s.setSpan( span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
            view.setText( s );
        }

        MovementMethod m = view.getMovementMethod();
        if ( ( m == null ) || !( m instanceof LinkMovementMethod ) ) {
            view.setMovementMethod( LinkMovementMethod.getInstance() );
        }
    }

    public static void setKey( Context context, String key )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        Editor editor = settings.edit();
        editor.putString( "key", key );
        editor.commit();
    }

    public static String getKey( Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getString( "key", null );
    }
    
    public static void setCheckinInProgressFlag( Context context, boolean flag )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        Editor editor = settings.edit();
        editor.putBoolean( CommonConstants.CHECKIN_IN_PROGRESS_FLAG, flag );
        editor.commit();
    }
    
    public static boolean isGoogleAccountSetOnFirstBoot( Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getBoolean( CommonConstants.GOOGLE_ON_FIRST_BOOT, false );
    }
    
    public static void setGoogleAccountOnFirstBootFlag( Context context, boolean flag )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        Editor editor = settings.edit();
        editor.putBoolean( CommonConstants.GOOGLE_ON_FIRST_BOOT, flag );
        editor.commit();
    }
    
    public static boolean getCheckinInProgressFlag( Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getBoolean( CommonConstants.CHECKIN_IN_PROGRESS_FLAG, false );
    }

    public static Intent getAboutActivityIntent( Context context )
    {
        Intent intent = new Intent( context, AboutActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
        return intent;
    }

    public static Intent getNeedHelpActivityIntent( String backToUri, Context context )
    {
        Intent intent = new Intent( context, NeedHelpActivity.class );
        intent.putExtra( "backTo", backToUri );
        intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
        return intent;
    }

    public static Intent getLoginActivityIntent( Context context )
    {
        Intent intent = new Intent( context, LoginActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
        return intent;
    }

    public static Intent getConnectedActivityIntent( Context context )
    {
        Intent intent = new Intent( context, ConnectedActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
        return intent;
    }

    public static Intent getDisplayCodeActivityIntent( Context context )
    {
        Intent intent = new Intent( context, DisplayCodeActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
        return intent;
    }

    public static Intent getCheckConnectionActivityIntent( Context context )
    {
        Intent intent = new Intent( context, CheckConnectionActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
        return intent;
    }

    public static Intent getCheckConnNeedHelpActivityIntent( Context context )
    {
        Intent intent = new Intent( context, CheckConnNeedHelpActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
        return intent;
    }

    public static Intent getForgotPasswordActivityIntent( Context context )
    {
        Intent intent = new Intent( context, ForgotPasswordActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
        return intent;
    }

    public static Intent getDifferentPhoneConfirmActivityIntent( Context context )
    {
        Intent intent = new Intent( context, DifferentPhoneConfirmActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
        return intent;
    }

    public static void startActivityFromUri( String uri, Context context )
    {
        if ( uri != null )
        {
            try
            {
                Intent intent = Intent.parseUri( uri, 0 );
                context.startActivity( intent );
            } catch ( URISyntaxException e )
            {
                DashLogger.e( TAG, "invalid uri: " + uri );
            }
        } else
        {
            DashLogger.e( TAG, "null uri" );
        }
    }

    public static void showProgressDialog( String title, Context context )
    {
        progressDialog = ProgressDialog.show( context, "", title, true );
        progressDialog.setCancelable( false );
        progressDialog.show();
    }

    public static boolean isProgressDialogVisible()
    {
        if ( progressDialog != null )
        {
            return progressDialog.isShowing();
        }
        return false;
    }

    public static void hideProgressDialog()
    {
        if ( progressDialog != null )
        {
            try
            {
                progressDialog.dismiss();
            } catch ( IllegalArgumentException iae )
            {
                // TODO make sure progressDialog is dismissed and nulled onPause
                DashLogger.e( TAG, "Hide progress called after activity closed" );
            }
        }
        progressDialog = null;
    }

    public static boolean packageWaiting( Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getBoolean( "package_waiting", false );
    }

    public static void airplaneMode( Context context, boolean state )
    {
        Settings.System.putInt( context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, state ? 1 : 0 );

        // TODO: SECURITY Make sure permission does not need to be applied.
        Intent intent = new Intent( Intent.ACTION_AIRPLANE_MODE_CHANGED );
        intent.putExtra( "state", state );
        context.sendBroadcast( intent );
    }

    public static void disableSetupWizard( Context context )
    {
        DashLogger.v( TAG, "disableSetupWizard ---------------------------------------------------------" );
        PackageManager localPackageManager = context.getPackageManager();
        ComponentName localComponentName = new ComponentName( "com.dashwire.config", "com.dashwire.config.ui.SetupWizardActivity" );
        localPackageManager.setComponentEnabledSetting( localComponentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP );
        DashLogger.v( TAG, "Setup Wizard disabled" );
    }

    public static boolean isDebug( Context context )
    {
        boolean result = false;
        try
        {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo myAppInfo = packageManager.getApplicationInfo( context.getPackageName(), 0 );
            if ( 0 != ( myAppInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE ) )
            {
                DashLogger.d( TAG, "App is debuggable." );
                result = true;
            } else
            {
                DashLogger.d( TAG, "App is not debuggable." );
                result = false;
            }
        } catch ( NameNotFoundException ex )
        {
            // this won't happen
        }
        return result;
    }

    public static void scheduleDownloader( Context context )
    {
        String uri = getDownloads( context );
        if ( uri != null )
        {
            clearDownloads( context );
            Intent intent = new Intent( context, DownloadService.class );
            intent.setAction( "download" );
            intent.putExtra( "json", uri );

            AlarmManager manager = ( AlarmManager ) context.getSystemService( Context.ALARM_SERVICE );
            PendingIntent pendingIntent = PendingIntent.getService( context, 0, intent, 0 );
            long tenSecondsInTheFuture = System.currentTimeMillis() + 5000;
            manager.set( AlarmManager.RTC_WAKEUP, tenSecondsInTheFuture, pendingIntent );
        }
    }

    public static void acquireDevice( Context context )
    {
        if ( CommonUtils.isFirstLaunch( context ) )
        {
            powerManager = ( PowerManager ) context.getSystemService( Context.POWER_SERVICE );
            if ( wakeLock == null )
            {

                wakeLock = powerManager.newWakeLock( PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "DashConfig" );
            }
            if ( !wakeLock.isHeld() )
            {
                try
                {
                    wakeLock.acquire();
                } catch ( Exception ex )
                {
                    wakeLock = null;
                    DashLogger.e( TAG, "Can't get wait lock" );
                }
            } else
            {
                DashLogger.w( TAG, "Requesting wake lock when wakeLock already held." );
            }
        }
    }

    public static void releaseDevice()
    {
        if ( wakeLock != null && wakeLock.isHeld() )
        {
            wakeLock.release();
        }
        wakeLock = null;
    }

    public static void setConfiguredFlag( boolean flag, Context context )
    {
        new SharedPreferenceWriteTask( context ).execute( CommonConstants.CONFIGURED_FLAG, Boolean.valueOf(flag), CommonConstants.DASHCONFIG_PREF );
    }

    public static boolean isFirstLaunch( Context context )
    {
        return DashconfigApplication.getDeviceContext()
                .getStringConst( context, "FIRST_BOOT_LAUNCHER" )
                .equalsIgnoreCase( CommonUtils.getLaunchFromIndicator( context ) );
    }

    public boolean isThisFirstLaunch(Context context) {
        return isFirstLaunch(context);
    }

    public static void setLauncherRefreshFlag( boolean flag, Context context )
    {
        DashLogger.d( TAG, "Setting REFRESH_LAUNCHER_FLAG to " + flag );
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        Editor editor = settings.edit();
        editor.putBoolean( DashconfigApplication.getDeviceContext().getStringConst( context, "REFRESH_LAUNCHER_FLAG" ), flag );
        editor.commit();
    }

    public static void setConfigCompletedFlag( boolean flag, Context context )
    {
        new SharedPreferenceWriteTask( context ).execute( CommonConstants.CONFIGCOMPLETED_FLAG, Boolean.valueOf(flag), CommonConstants.DEFAULT_PREF );
    }

    public static void setDownloads( Context context, String intentUri )
    {
        new SharedPreferenceWriteTask( context ).execute( CommonConstants.DOWNLOADS, intentUri, CommonConstants.DASHCONFIG_PREF );
    }

    public static void setPostPairFlag( Context context, boolean postpair )
    {
        new SharedPreferenceWriteTask( context ).execute( CommonConstants.POST_PAIR, Boolean.valueOf(postpair), CommonConstants.DEFAULT_PREF );
    }

    public static void setAppInstallationFailedFlag( boolean flag, Context context )
    {
        new SharedPreferenceWriteTask( context ).execute( CommonConstants.APP_INSTALLATION_FAILED_FLAG, Boolean.valueOf(flag), CommonConstants.DEFAULT_PREF );
    }

    public static void setQuitFlag( boolean flag, Context context )
    {
        new SharedPreferenceWriteTask( context ).execute( CommonConstants.QUIT_FLAG, Boolean.valueOf(flag), CommonConstants.DEFAULT_PREF );
    }

    public static void setCanceledFlag( boolean flag, Context context )
    {
        // new SharedPreferenceWriteTask( context ).execute(
        // CommonConstants.CANCELED_FLAG, new Boolean( flag ),
        // CommonConstants.DEFAULT_PREF );
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean( CommonConstants.CANCELED_FLAG, flag );
        editor.clear();
        editor.commit();
    }

    public static void setLaunchFromIndicator( String launchFrom, Context context )
    {
        // new SharedPreferenceWriteTask( context ).execute(
        // CommonConstants.LAUNCH_FROM_IND, launchFrom,
        // CommonConstants.DASHCONFIG_PREF );
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        Editor editor = settings.edit();
        editor.putString( CommonConstants.LAUNCH_FROM_IND, launchFrom );
        editor.commit();
    }

    public static void setGoogleLocationUsageCompletedFlag( boolean flag, Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        Editor editor = settings.edit();
        editor.putBoolean( CommonConstants.GOOGLE_LOC_USE_CMPL, flag );
        editor.commit();
    }

    public static void setSelectLanguageCompletedFlag( boolean flag, Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        Editor editor = settings.edit();
        editor.putBoolean( CommonConstants.LANG_SELECT_CMPL, flag );
        editor.commit();
    }

    public static void setNotificationAlarmFlag( boolean flag, Context context )
    {
        new SharedPreferenceWriteTask( context ).execute( CommonConstants.NOTIFICATIONALARMFLAG, Boolean.valueOf(flag), CommonConstants.DASHCONFIG_PREF );
    }

    public static void setNotificationStartTime( long startTime, Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        Editor editor = settings.edit();
        editor.putLong( CommonConstants.STARTTIME, startTime );
        editor.commit();
    }

    public static void setAlreadyNotifiedFlag( boolean flag, Context context )
    {
        new SharedPreferenceWriteTask( context ).execute( CommonConstants.ALREADYNOTIFIED_FLAG, Boolean.valueOf(flag), CommonConstants.DASHCONFIG_PREF );
    }

    public static void setPushClientStatus( String pushStatus, Context context )
    {
        //new SharedPreferenceWriteTask( context ).execute( CommonConstants.PUSH_STATUS, pushStatus, CommonConstants.DEFAULT_PREF );
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        SharedPreferences.Editor editor = settings.edit();
        editor.putString( CommonConstants.PUSH_STATUS, pushStatus );
        editor.commit();
    }

    public static void setOverrideDeviceFlag( boolean flag, Context context )
    {
        new SharedPreferenceWriteTask( context ).execute( CommonConstants.OVERRIDE_DEVICE_FLAG, Boolean.valueOf(flag), CommonConstants.DEFAULT_PREF );
    }

    public static void resetSession( Context context )
    {
        new SharedPreferenceWriteTask( context ).execute( CommonConstants.RESET_SESSION, null, CommonConstants.DEFAULT_PREF );
    }

    // public static void resetSession( Context context ) {
    // SharedPreferences settings =
    // PreferenceManager.getDefaultSharedPreferences( context );
    // SharedPreferences.Editor editor = settings.edit();
    // editor.clear();
    // editor.commit();
    // }

    // public static void resetSession( Context context ) {
    // SharedPreferences settings =
    // PreferenceManager.getDefaultSharedPreferences( context );
    // SharedPreferences.Editor editor = settings.edit();
    // editor.clear();
    // editor.commit();
    // }

    public static boolean isConfiguredFlagEnabled( Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        return settings.getBoolean( CommonConstants.CONFIGURED_FLAG, false );
    }

    public static boolean isLaucherRefreshFlagEnabled( Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        return settings.getBoolean( DashconfigApplication.getDeviceContext().getStringConst( context, "REFRESH_LAUNCHER_FLAG" ), false );
    }

    public static boolean isNotificationAlarmFlagEnabled( Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        return settings.getBoolean( CommonConstants.NOTIFICATIONALARMFLAG, false );
    }

    public static long getNotificationStartTime( Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        return settings.getLong( CommonConstants.STARTTIME, 0 );
    }

    public static boolean isAlreadyNotified( Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        return settings.getBoolean( CommonConstants.ALREADYNOTIFIED_FLAG, false );
    }

    public static void clearDownloads( Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();
        editor.remove( CommonConstants.DOWNLOADS );
        editor.commit();
    }

    public static void clearAccountsSyncSettings( Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();
        editor.remove( CommonConstants.DOWNLOADS );
        editor.commit();
    }

    public static String getDownloads( Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        return settings.getString( CommonConstants.DOWNLOADS, null );
    }

    public static String getCanceledFromUri( Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        return settings.getString( CommonConstants.CANCELED_FROM_URI, null );
    }

    public static boolean isQuitFlagEnabled( Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getBoolean( CommonConstants.QUIT_FLAG, false );
    }

    public static boolean isCanceledFlagEnabled( Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getBoolean( CommonConstants.CANCELED_FLAG, false );
    }

    public static String getLaunchFromIndicator( Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        return settings.getString( CommonConstants.LAUNCH_FROM_IND, "" );
    }

    public static boolean getGoogleLocationUsageCompletedFlag( Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        return settings.getBoolean( CommonConstants.GOOGLE_LOC_USE_CMPL, false );
    }

    public static boolean getLanguageSelectionCompletedFlag( Context context )
    {
        SharedPreferences settings = context.getSharedPreferences( CommonConstants.DASHCONFIG_PREF, Context.MODE_PRIVATE );
        return settings.getBoolean( CommonConstants.LANG_SELECT_CMPL, false );
    }

    public static boolean isOverrideDeviceFlagEnabled( Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getBoolean( CommonConstants.OVERRIDE_DEVICE_FLAG, false );
    }

    public static String getPushClientStatus( Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getString( CommonConstants.PUSH_STATUS, null );
    }

    public static boolean isConfigCompletedFlagEnabled( Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getBoolean( CommonConstants.CONFIGCOMPLETED_FLAG, false );
    }

    public static boolean isAppInstallationFailed( Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getBoolean( CommonConstants.APP_INSTALLATION_FAILED_FLAG, false );
    }

    public static boolean isPostPairEnabled( Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getBoolean( CommonConstants.POST_PAIR, false );
    }

    public static void zipFile( String path ) throws IOException
    {

        String outFilename = path + ".gz";
        GZIPOutputStream out = new GZIPOutputStream( new FileOutputStream( outFilename ) );
        FileInputStream in = new FileInputStream( path );

        byte[] buf = new byte[ 1024 ];
        int len;
        while ( ( len = in.read( buf ) ) > 0 )
        {
            out.write( buf, 0, len );
        }
        in.close();
        out.finish();
        out.close();
    }

    public static void copyFile( String inputPath, String outPath ) throws IOException
    {
        InputStream in = new FileInputStream( inputPath );
        OutputStream out = new FileOutputStream( outPath );
        byte[] buffer = new byte[ 1024 ];
        int read;
        while ( ( read = in.read( buffer ) ) != -1 )
        {
            out.write( buffer, 0, read );
        }
    }

    public static String extractPackage( String widgetId )
    {
        String packageName = widgetId.substring( 0, widgetId.lastIndexOf( "/" ) );
        return packageName;
    }

    public static String extractProvider( String widgetId )
    {
        String providerClassName = widgetId.substring( widgetId.lastIndexOf( "/" ) + 1 );
        String providerName = "";
        String packageName = extractPackage( widgetId );
        if ( ".".equalsIgnoreCase( providerClassName.substring( 0, 1 ) ) )
        {
            providerName = packageName + providerClassName;
        } else
        {
            providerName = providerClassName;
        }
        return providerName;
    }
    
	public static boolean isHtcDevice() {
		return "HTC".equals(Build.MANUFACTURER);
	}

    public static boolean isTouchWizWidget( JSONObject item )
    {
        return isWidgetType( item, "tw" );
    }

    public static boolean isAndroidWidget( JSONObject item )
    {
        return isWidgetType( item, "aw" );
    }

    public static boolean isWidgetType( JSONObject item, String type )
    {
        try
        {
            if ( item.has( "type" ) )
            {
                if ( type.equals( item.getString( "type" ) ) )
                {
                    return true;
                }
            }
        } catch ( Exception e )
        {
            DashLogger.e(TAG, e.toString());
        }

        return false;
    }

    public static boolean isShortcut( JSONObject item )
    {
        try
        {
            if ( item.has( "category" ) )
            {
                if ( "Shortcuts".equals( item.getString( "category" ) ) )
                {
                    return true;
                }
            }
        } catch ( Exception e )
        {
            DashLogger.e( TAG, e.toString() );
        }

        return false;
    }

    /**
     * Sets the device language.
     *
     * We assume for now that the device is a U.S. device, therefore we set the locale to United States
     * for all language selections (instead of, say, Spain for Spanish or UK for English).
     *
     * @param lang language code
     * @param ctx context
     */
    public static void setLanguage( String lang, Context ctx )
    {
        try
        {
            Locale locale = new Locale( lang, Locale.US.getCountry() );

            Class<?> amnClass = Class.forName( "android.app.ActivityManagerNative" );
            Object amn = null;
            Configuration config = null;

            Method methodGetDefault = amnClass.getMethod( "getDefault" );
            methodGetDefault.setAccessible( true );
            amn = methodGetDefault.invoke( amnClass );

            Method methodGetConfiguration = amnClass.getMethod( "getConfiguration" );
            methodGetConfiguration.setAccessible( true );
            config = ( Configuration ) methodGetConfiguration.invoke( amn );

            Class<? extends Configuration> configClass = config.getClass();
            Field f = configClass.getField( "userSetLocale" );
            f.setBoolean( config, true );

            config.locale = locale;

            // amn.updateConfiguration(config);
            Method methodUpdateConfiguration = amnClass.getMethod( "updateConfiguration", Configuration.class );
            methodUpdateConfiguration.setAccessible( true );
            methodUpdateConfiguration.invoke( amn, config );

            broadcastLocaleChange( ctx );
        } catch ( ClassNotFoundException e )
        {
            DashLogger.v( TAG, "ClassNotFoundException in setLanguage : " + e.getMessage() );
        } catch ( SecurityException e )
        {
            DashLogger.v( TAG, "SecurityException in setLanguage : " + e.getMessage() );
        } catch ( NoSuchMethodException e )
        {
            DashLogger.v( TAG, "NoSuchMethodException in setLanguage : " + e.getMessage() );
        } catch ( IllegalArgumentException e )
        {
            DashLogger.v( TAG, "IllegalArgumentException in setLanguage : " + e.getMessage() );
        } catch ( IllegalAccessException e )
        {
            DashLogger.v( TAG, "IllegalAccessException in setLanguage : " + e.getMessage() );
        } catch ( InvocationTargetException e )
        {
            DashLogger.v( TAG, "InvocationTargetException in setLanguage : " + e.getMessage() );
        } catch ( NoSuchFieldException e )
        {
            DashLogger.v( TAG, "NoSuchFieldException in setLanguage : " + e.getMessage() );
        }
    }

    public static int getUid( Context context )
    {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications( PackageManager.GET_META_DATA );
        int uid = 0;
        for ( ApplicationInfo packageInfo : packages )
        {
            if ( packageInfo.packageName.equals( context.getPackageName() ) )
            {
                uid = packageInfo.uid;
            }
        }
        return uid;
    }
    
    public static void disableStatusBarManager( Context context )
    {
        try
        {
            Class<?> managerClass = Class.forName( "android.app.StatusBarManager" );

            Constructor<?> constr = managerClass.getDeclaredConstructor( Context.class );
            constr.setAccessible( true );

            Object managerObject = constr.newInstance( context );
            Method disableMethod = managerClass.getMethod( "disable", Integer.TYPE );

            disableMethod.invoke( managerObject, 0x00000000 );
        } catch ( Exception e )
        {
            DashLogger.d(TAG,"exception on StatusBarManager disable : " + e.getMessage());
        }
    }

    public static void broadcastDashconfigConfigured( Context context )
    {
        context.sendBroadcast( new Intent( "com.dashwire.config.CONFIGURED" ), "com.dashwire.config.PERM_CONFIG" );
    }

    public static void broadcastDashconfigNotConfigured( Context context )
    {
        context.sendBroadcast( new Intent( "com.dashwire.config.NOT_CONFIGURED" ), "com.dashwire.config.PERM_CONFIG" );
    }

    public static String getGotoLinkText(Context context)
    {
        String gotoLinkText = DashconfigApplication.getDeviceContext().
                getStringConst( context, "DEFAULT_CONFIG_URL" );

        if (gotoLinkText == null) {
            String androidVersion = VERSION.RELEASE;
            HashMap<String, String> gotoLinkVersionMap = new HashMap<String, String>();
            gotoLinkVersionMap.put( "4.2", "http://att.com/configure-jb" );
            gotoLinkVersionMap.put( "4.1", "http://att.com/configure-jb" );
            gotoLinkVersionMap.put( "4.0", "http://att.com/configure-ics" );
            gotoLinkVersionMap.put( "3", "http://att.com/configure" );
            gotoLinkVersionMap.put( "2", "http://att.com/configure" );

            String versionIndex = androidVersion.substring( 0, 1 );
            gotoLinkText = gotoLinkVersionMap.get(versionIndex );
            if (versionIndex.equalsIgnoreCase( "4" ))
            {
                String subVersionIndex = androidVersion.substring( 0, 3 );
                if (subVersionIndex.equalsIgnoreCase( "4.0" ))
                {
                    gotoLinkText = gotoLinkVersionMap.get( "4.0" );
                }else if (subVersionIndex.equalsIgnoreCase( "4.1" ))
                {
                    gotoLinkText = gotoLinkVersionMap.get( "4.1" );
                }else if (subVersionIndex.equalsIgnoreCase( "4.2" ))
                {
                    gotoLinkText = gotoLinkVersionMap.get( "4.2" );
                }
            }else
            {
                gotoLinkText = gotoLinkVersionMap.get( versionIndex );
            }
        }
        return gotoLinkText;
    }

    private static void broadcastLocaleChange( Context ctx )
    {
        ctx.sendBroadcast( new Intent( "android.intent.action.LOCALE_CHANGED" ) );
    }
    
    public static void broadcastDashconfigCanceled( Context context )
    {
        Intent cancelDashconfigIntent = new Intent("com.dashwire.config.cancel");
        cancelDashconfigIntent.setPackage( context.getPackageName());
        context.getApplicationContext().sendBroadcast( cancelDashconfigIntent );
    }
    
    public static void broadcastDashconfigSuccess( Context context )
    {
        Intent successDashconfigIntent = new Intent("com.dashwire.config.success");
        successDashconfigIntent.setPackage( context.getPackageName());
        context.getApplicationContext().sendBroadcast( successDashconfigIntent );
    }

    public static void sendFinishOrderedBroadcast( Context ctx, BroadcastReceiver broadcastReceiver )
    {
        Intent intent = new Intent( "com.dashwire.config.FINISH" );
        intent.setPackage( "com.dashwire.config" );
        DashLogger.i( TAG, "CommonUtils.sendFinishOrderedBroadcast: sending ordered broadcast" );
        ctx.sendOrderedBroadcast( intent, null, broadcastReceiver, null, Activity.RESULT_OK, null, null );
        DashLogger.i( TAG, "CommonUtils.sendFinishOrderedBroadcast: sent ordered broadcast" );
    }

    public static Boolean hasFinishBroadcastReceiver( Context context )
    {
        List<ResolveInfo> resolveInfo = context.getPackageManager().queryBroadcastReceivers( new Intent( "com.dashwire.config.FINISH" ), 0 );
        if ( resolveInfo == null )
            DashLogger.i( TAG, "CommonUtils.hasFinishBroadcastReceiver resolveInfo = null" );
        else
            DashLogger.i( TAG, "CommonUtils.hasFinishBroadcastReceiver resolveInfo.size: " + resolveInfo.size() );
        return ( resolveInfo != null && resolveInfo.size() > 0 );
    }

    public static String buildWidgetsAndShortcutsConfigForOEMAPI( JSONArray configArray )
    {
        JSONArray array = new JSONArray();
        try
        {
            for ( int i = 0; i < configArray.length(); i++ )
            {
                JSONObject item = configArray.getJSONObject( i );

                String packageName = CommonUtils.extractPackage( item.getString( "id" ) );
                String providerName = CommonUtils.extractProvider( item.getString( "id" ) );

                int containerId = -100;
                if ( item.has( "container_id" ) )
                {
                    containerId = item.getInt( "container_id" );
                }

                String category = item.getString( "category" );
                if ( category.equalsIgnoreCase( "widgets" ) )
                {
                    category = "widget";
                } else if ( category.equalsIgnoreCase( "shortcuts" ) )
                {
                    category = "shortcut";
                } else
                {
                    DashLogger.d( TAG, "Invalid class for widget: " + category );
                    category = null;
                }

                JSONObject object = new JSONObject();
                object.put( "type", category );
                object.put( "title", item.getString( "title" ) );
                object.put( "packageName", packageName );
                object.put( "className", providerName );
                object.put( "container", containerId );
                object.put( "screen", item.getInt( "screen" ) );
                object.put( "x", item.getInt( "x" ) );
                object.put( "y", item.getInt( "y" ) );
                object.put( "rows", item.getInt( "rows" ) );
                object.put( "cols", item.getInt( "cols" ) );

                array.put( object );
            }

        } catch ( Exception je )
        {
            je.printStackTrace();
        }
        return array.toString();
    }

}
