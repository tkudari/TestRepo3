package com.pantech.modetestapp.homeswitch;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

final public class HomeSwitch {
	final public static void switchPantechHome(Context context,boolean simpleMode) {
		int iPantechHome;
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		PackageMgr packageMgr = new PackageMgr(context);
		packageMgr.getPackageList(intent, null);
		
		iPantechHome = packageMgr.findPantechHome(simpleMode);
		if (iPantechHome > -1) {
		    final String curHome = packageMgr.getCurrentHome();
			packageMgr.clearDefault();
			packageMgr.setDefault(iPantechHome, context);			

			ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		    am.forceStopPackage(curHome);
		} // End of if
		else{
			packageMgr.clearDefault();	
		}
			
			
		packageMgr = null;
	} // End of switchPantechHome
}
