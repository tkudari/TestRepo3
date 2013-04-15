package com.dashwire.config;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.configuration.ConfiguratorFactory;

import java.lang.reflect.Field;

public abstract class DeviceContext implements Constants {
	
	private static String TAG = DeviceContext.class.getCanonicalName();
	
	public enum Notification{ DeviceConfigComplete };
	public abstract boolean sendNotification(Context context,Notification notifyMessage);
	
	public abstract ConfiguratorFactory getConfiguratorFactory();
	
	@Override
	public String getStringConst(Context context, String key) {
		String result = null;
		
		//TODO: Migrate all projects to this method. For now, warn on misses
		int resID = context.getResources().getIdentifier(key, "string", context.getPackageName());
		if(resID != 0) {
			try {
				DashLogger.d(TAG,"String constant resource found in constants.xml file!");
				result = context.getResources().getString(resID);
			}
			catch(NotFoundException ex) {
				DashLogger.w(TAG,"Resource not found in constants.xml resource file.");
			}
		}
		
		if(result == null)
		{
			//For now, get it from the evil constant object
			Class<?> specific;
			try {
				specific = Class.forName("com.dashwire.config.util.DeviceConstants");
			} catch (ClassNotFoundException e) {
				DashLogger.d(TAG,"Unable to find class called com.dashwire.config.util.DeviceConstants");
				return null;
			}
			Field field;
			try {
				field = specific.getDeclaredField(key);
			} catch (NoSuchFieldException e) {
				DashLogger.d(TAG, "Unable to find field: " + key);
				return null;
			}
			try {
				result = (String)field.get(null);
			} catch (IllegalArgumentException e) {
				DashLogger.d(TAG,"Field is not a static member: " + key);
				
			} catch (IllegalAccessException e) {
				DashLogger.d(TAG,"Field is not accessible.");
			}
		}
		
		return result;

	}
	
	public abstract boolean isBlackListedDevice();
	
	public abstract long getCustomDefinedTextSize(Context context);
}