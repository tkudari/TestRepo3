package com.dashwire.config;

import android.content.Context;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.configuration.ConfiguratorFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SonyDeviceContext extends DefaultDeviceContext {
	
    @Override
	public ConfiguratorFactory getConfiguratorFactory() {
		return SonyConfiguratorFactory.getInstance();
	}
    
    @Override
    public boolean sendNotification(Context context, Notification notification) {
	    switch(notification)
	    {
	        case DeviceConfigComplete:
	            //CommonUtils.killEmailApp(context);
	            return true;
	        default:
	            return super.sendNotification(context, notification );
	    }
	   
    }
    
    @Override
	public boolean isBlackListedDevice() {
		return !isSonyJulepOrLunaForAtt();
	}
    
    private static boolean isSonyJulepOrLunaForAtt() {
		DashLogger.d("SonyDeviceContext", "Performing device model check..");
		String SYSTEM_PROP_MANUFACTURER = "ro.product.manufacturer";
		String SYSTEM_PROP_MODEL = "ro.semc.product.model";
		String SYSTEM_PROP_FS = "ro.semc.version.fs";
		String PRODUCT_MANUFACTURER = "Sony";
		String PRODUCT_MODEL_JULEP = "LT30at"; // Products it should be working on
		String PRODUCT_MODEL_LUNA = "LT28at";
		String FS_VERSION = "ATT-LTE"; // Operator customized products
		String propManufacturer = "";
		String propModel = "";
		String fsVersion = "";

		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class, String.class);
			propManufacturer = (String) (get.invoke(c,
					SYSTEM_PROP_MANUFACTURER, "unknown"));
			propModel = (String) (get.invoke(c, SYSTEM_PROP_MODEL, "unknown"));
			fsVersion = (String) (get.invoke(c, SYSTEM_PROP_FS, "unknown"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		// verify the System Properties against Sony
		if (!(PRODUCT_MANUFACTURER.equals(propManufacturer)
				&& (PRODUCT_MODEL_JULEP.equals(propModel) || PRODUCT_MODEL_LUNA.equals(propModel)) && FS_VERSION
					.equals(fsVersion))) {
			return false;
		} else {
			return true;
		}
	}
}
