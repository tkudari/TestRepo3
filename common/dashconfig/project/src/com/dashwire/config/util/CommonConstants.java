package com.dashwire.config.util;

public class CommonConstants {
    
    protected static final String TAG = CommonConstants.class.getCanonicalName();
    
     public static final long TEN_SECONDS = 10000;
        public static final long THIRTY_SECONDS = 30000;
        public static final long ONE_MINUTE = 60000;
        public static final long NINTY_SECONDS = 90000;
        public static final long TWO_MINUTE = 120000;
        public static final long TEN_MINUTES = 600000;
        public static final long THIRTY_MINUTES = 1800000;
        public static final long SIXTY_MINUTES = 3600000;
        public static final String DASHCONFIG_PREF = "DashConfig";
        public static final String CONFIGURED_FLAG = "configured";
        public static final String QUIT_FLAG = "quit";
        public static final String CANCELED_FLAG = "canceled";
        public static final String CONFIGCOMPLETED_FLAG = "configCompleted";
        public static final String LAUNCH_FROM_IND = "LaunchFrom";
        public static final String LAUNCH_FROM_ICON_IND = "Icon";
        public static final String LAUNCH_FROM_SETUP_WIZARD_IND = "SetUpWizard";
        public static final String LAUNCH_FROM_OTHER_WIZARD_IND = "OtherWizard";
        public static final String GOOGLE_LOC_USE_CMPL = "GoogleLocationUsageCompleted";
        public static final String LANG_SELECT_CMPL = "LanguageSelectionCompleted";
        public static final String NOTIFICATIONALARMFLAG = "NotificationAlarmFlag";
        public static final String STARTTIME = "StartTime";
        public static final String ALREADYNOTIFIED_FLAG = "AlreadyNotified";
        public static final String OVERRIDE_DEVICE_FLAG = "OverrideDevice";
        public static final String PUSH_STATUS = "PushStatus";
        public static final String APP_INSTALLATION_FAILED_FLAG = "AppInstallationFailed";
        public static final String GOOGLE_ACCOUNT_EXISTS_FLAG = "GoogleAccountExists";
        public static final String APP_INSTALLATION_LIST = "AppInstallationList";
        public static final String LAUNCHFROMNOTIFICATION = "LaunchFromNotification";
        public static final String POST_PAIR = "post_pair";
        public static final String SNS_TYPE = "SNS_TYPE";
        public static final String SNS_USERNAME = "SNS_USERNAME";
        public static final String SNS_PASSWORD = "SNS_PASSWORD";
        public static final String DOWNLOADS = "Downloads";
        public static final String RESET_SESSION = "ResetSession";
        public static final String DEFAULT_PREF = "Default";
        public static final String BOOLEAN = "boolean";
        public static final String STRING = "String";
        public static final String CANCELED_FROM_URI = "CanceledFrom";
        public static final String CHECKIN_IN_PROGRESS_FLAG = "CheckinInProgressFlag";
        public static final String GOOGLE_ON_FIRST_BOOT = "GoogleOnFirstBoot";
        public static final int SUCCESS = 1;
        public static final int FAILED = 0;
        
        public static final String PACKAGE_INSTALLER_COMP = "com.android.packageinstaller";
        public static final String DASHCONFIG_WIDGET_COMP = "com.dashwire.config.widget";
        public static final String DASHCONFIG_WIDGET_SERVICE = "com.dashwire.config.widget.services.WidgetService";

        //TODO: Temporarily here to fix build.
        public static final String WALLPAPER_URI = "wallpaperUri";
        public static final String WALLPAPER_FLAG = "wallpaperFlag";

}
