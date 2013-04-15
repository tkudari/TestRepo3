package com.dashwire.config;

//Should not be in prod code yet
//import org.acra.annotation.ReportsCrashes;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;

import com.dashwire.base.debug.DashLogger;

//@ReportsCrashes(formKey = "", logcatArguments = { "-t", Tracing.LOGCAT_TAIL_LENGTH, Tracing.LOGCAT_FILTERSPECS })
public class DashconfigApplication extends Application 
	implements OnAccountsUpdateListener  {
	
	static private DeviceContext deviceContext = null;
	
    @Override
    public void onCreate() {
        super.onCreate();
        // Don't add any 'DashLogger' statements before this, you could use the
        // 'Log' class though
        DashLogger.setContext(this);
       // DashLogger.setDebugMode(true);
        if (PackageManager.PERMISSION_DENIED == checkCallingOrSelfPermission("com.dashwire.config.PERM_CONFIG_INTERNAL")) {
            throw new SecurityException("Permission denied");
        }
       
        //TODO: Add back?
		//StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		//		.detectDiskReads().detectDiskWrites().detectNetwork()
		//		.penaltyLog().build());
		//StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		//		.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
		//		.penaltyLog().penaltyDeath().build());

		AccountManager am = AccountManager.get(this);

		am.addOnAccountsUpdatedListener(this, handler,
				true);
		
        //Tracing.setupCrashReports(this);
    }
    
    private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
		}
    	
    };

	@Override
	public void onAccountsUpdated(Account[] accounts) {
		for(Account account : accounts) {
			DashLogger.d("Accounts", "Account changed: " + account.name);
		}
		
	}
	
	static void setDeviceContext(DeviceContext deviceContext) {DashconfigApplication.deviceContext = deviceContext;}; 
	public static DeviceContext getDeviceContext() {return deviceContext;};
	
 
}
