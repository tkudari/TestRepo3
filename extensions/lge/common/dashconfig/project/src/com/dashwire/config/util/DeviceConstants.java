package com.dashwire.config.util;

public class DeviceConstants {
    
    protected static final String TAG = DeviceConstants.class.getCanonicalName();
    
    public static final String FIRST_BOOT_LAUNCHER = CommonConstants.LAUNCH_FROM_SETUP_WIZARD_IND;
    
    public static final String REFRESH_LAUNCHER_FLAG = "refreshLaucher";
    
    /**
     *  HTC Congrestional Ice Cream Sandwitch (Andorid 4.0) (HTC Sense 4.0) URIs
     */
    
    public static final String MAIL_BOX_URI = "content://com.android.email.provider/mailbox";
    public static final String HOST_AUTH_URI = "content://com.android.email.provider/hostauth";
    public static final String ACCOUNT_URI = "content://com.android.email.provider/account";
    public static final String ACCOUNT_CB_URI = "content://com.android.email.provider/accountcb";
    public static final String TWLAUNCHER_COMP = "com.lge.launcher2";
    public static final String FAVORITES_URI = "content://com.lge.launcher2.settings/favorites";
    public static final String GOOGLE_SETTINGS_PARTNER_URI = "content://com.google.settings/partner";
    public static final String SETTINGS_COMP = "com.android.settings";
    
    public static final String EMAIL_CONFIG_COMPONENT_URI = "com.dashwire.config.email";
}
