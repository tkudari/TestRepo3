package com.dashwire.config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ConfigurationReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			Uri uri = intent.getData();
			String name = uri.getQueryParameter("name");
			String code = uri.getQueryParameter("code");
			//Rebroadcast event:
			
			Intent i = new Intent("dashwire.action.config");
			i.putExtra("name", name);
			i.putExtra("code", code);
			context.sendBroadcast(i,"com.dashwire.config.PERM_CONFIG_INTERNAL");
			
			//notifyEvent(name, code);
		}	

}
