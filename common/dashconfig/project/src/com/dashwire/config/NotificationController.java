package com.dashwire.config;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.ui.AppInstallationActivity;
import com.dashwire.config.ui.IconStartActivity;
import com.dashwire.config.util.CommonConstants;
import com.dashwire.config.util.CommonUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Hashtable;

public class NotificationController {

    protected static final String TAG = NotificationController.class.getCanonicalName();

    class NotificationSetting {
        private String title;
        private String crawl;
        private String text;
        private PendingIntent contentIntent;

        public NotificationSetting( String title, String crawl, String text, PendingIntent contentIntent ) {
            this.title = title;
            this.crawl = crawl;
            this.text = text;
            this.contentIntent = contentIntent;
        }

        public String getTitle() {
            return title;
        }

        public String getText() {
            return text;
        }

        public String getCrawl() {
            return crawl;
        }

        public PendingIntent getContentIntent() {
            return contentIntent;
        }
    }

    // Notification IDs above 1000 are reserved for configuration notifications
    // like AAB
    public static final int CONFIGURE_YOUR_DEVICE = 0;
    public static final int INSTALLING_APPLICATIONS = 1;
    public static final int SETUP_GOOGLE_ACCOUNT = 2;
    public static final int SOCIAL_NETWORK_RESULT = 3;
    public static final int RESTART_DEVICE = 4;

    private static NotificationController notificationController;
    private Context context;
    private static NotificationManager notificationManager;
    private Hashtable<Integer, Notification> notifications;
    private Hashtable<Integer, NotificationSetting> notificationSettings;

    public static final int CONFIG_DEVICE_NOTIFICATION_FIRST_ALARM = 1;
    public static final int CONFIG_DEVICE_NOTIFICATION_SECOND_ALARM = 2;

    public NotificationController( Context context ) {
        this.context = context;
        notifications = new Hashtable<Integer, Notification>();
        notificationManager = ( NotificationManager ) context.getSystemService( Context.NOTIFICATION_SERVICE );
    }

    public Notification getNotification( int notificationId ) {
        Notification notification = null;
        NotificationSetting setting = getNotificationSettings( notificationId );
        if ( setting != null ) {
            if ( notifications.containsKey( notificationId ) ) {
                notification = notifications.get( notificationId );
            } else {
                notification = new Notification( R.drawable.notification_gear, setting.getTitle(), System.currentTimeMillis() );
            }

            if ( notificationId == INSTALLING_APPLICATIONS ) {
                Notification.Builder builder = new Notification.Builder( context );
                builder.setSmallIcon( R.drawable.notification_gear );
                builder.setTicker( context.getResources().getString( R.string.notification_ins_app_title ) );
                builder.setContentTitle( context.getResources().getString( R.string.notification_ins_app_title ) );
                builder.setContentText( context.getResources().getString( R.string.notification_ins_app_body ) );
                builder.setContentIntent( setting.getContentIntent() );
                builder.setOngoing( true );
                notification = builder.getNotification();
            } else if ( notificationId == SOCIAL_NETWORK_RESULT ) {
                notification.setLatestEventInfo( context, setting.getCrawl(), setting.getText(), setting.getContentIntent() );
                notification.flags = Notification.FLAG_AUTO_CANCEL;
            } else if ( notificationId == RESTART_DEVICE ) {
                Notification.Builder builder = new Notification.Builder( context );
                builder.setSmallIcon( R.drawable.notification_gear );
                builder.setTicker( context.getResources().getString( R.string.notification_restart_device_title ) );
                builder.setContentTitle( context.getResources().getString( R.string.notification_restart_device_title ) );
                builder.setContentText( context.getResources().getString( R.string.notification_restart_device_body ) );
                builder.setOngoing( true );
                notification = builder.getNotification();
            } else {
                notification.setLatestEventInfo( context, setting.getCrawl(), setting.getText(), setting.getContentIntent() );
            }
            notifications.put( notificationId, notification );
        }
        
        if ( notificationId == SETUP_GOOGLE_ACCOUNT ) {
            
            Notification.Builder setupGoogleActBuilder = new Notification.Builder( context );
            setupGoogleActBuilder.setSmallIcon( R.drawable.notification_gear );
            setupGoogleActBuilder.setTicker( context.getResources().getString( R.string.notification_setup_google_title ) );
            setupGoogleActBuilder.setContentTitle( context.getResources().getString( R.string.notification_setup_google_title ) );
            setupGoogleActBuilder.setContentText( context.getResources().getString( R.string.notification_setup_google_body ) );
            Intent googleAccountIntent = new Intent();
            googleAccountIntent.setAction( "android.settings.ADD_ACCOUNT_SETTINGS" );
            PendingIntent googleAccountPendingIntent = PendingIntent.getActivity( context, 0, googleAccountIntent, 0 );
            setupGoogleActBuilder.setContentIntent( googleAccountPendingIntent );
            setupGoogleActBuilder.setAutoCancel( true );
            notification = setupGoogleActBuilder.getNotification();
            
        } 

        return notification;
    }

    public void showNotification( int notificationId ) {
        Notification notification = getNotification( notificationId );
        notificationManager.notify( notificationId, notification );
    }

    public void showNotification( Context context, JSONObject item ) {
        try {
            int notificationId = item.getInt( "id" );
            String title = item.getString( "title" );
            String ticker = item.getString( "ticker" );
            String text = item.getString( "text" );
            String uri = item.getString( "uri" );
            Intent uriIntent = Intent.parseUri( uri, 0 );
            PendingIntent pendingIntent = PendingIntent.getActivity( context, 0, uriIntent, 0 );
            Notification notification = new Notification( R.drawable.notification_gear, ticker, System.currentTimeMillis() );
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notification.setLatestEventInfo( context, title, text, pendingIntent );
            notificationManager.notify( notificationId, notification );
        } catch ( JSONException je ) {
            DashLogger.e( TAG, "malformed notification object: " + item.toString() );
        } catch ( URISyntaxException e ) {
            DashLogger.e(TAG, "invalid notification uri: " + item.toString());
        }
    }

    public static void clearNotification( int notificationId ) {
        if ( notificationManager != null ) {
            notificationManager.cancel( notificationId );
        }
    }

    public NotificationSetting getNotificationSettings( int notificationId ) {
        if ( notificationSettings == null ) {
            notificationSettings = new Hashtable<Integer, NotificationSetting>();

            Intent startIntent = new Intent( context, IconStartActivity.class );
            startIntent.putExtra( CommonConstants.LAUNCHFROMNOTIFICATION, true );
            startIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
            notificationSettings.put(
                    CONFIGURE_YOUR_DEVICE,
                    new NotificationSetting( context.getResources().getString( R.string.notification_config_phone_title ), context.getResources().getString(
                            R.string.notification_config_phone_message ), context.getResources().getString( R.string.notification_config_phone_body ),
                            PendingIntent.getActivity( context, 0, startIntent, 0 ) ) );

            Intent appInstallationIntent = new Intent( context, AppInstallationActivity.class );
            notificationSettings.put(
                    INSTALLING_APPLICATIONS,
                    new NotificationSetting( context.getResources().getString( R.string.notification_ins_app_title ), context.getResources().getString(
                            R.string.notification_ins_app_body ), "Downloading", PendingIntent.getActivity( context, 0, appInstallationIntent, 0 ) ) );

            notificationSettings.put( SOCIAL_NETWORK_RESULT,
                    new NotificationSetting( context.getResources().getString( R.string.notification_social_hub_status_title ), context.getResources()
                            .getString( R.string.notification_social_hub_status_title ), "", PendingIntent.getActivity( context, 0, new Intent(), 0 ) ) );

            notificationSettings.put(
                    RESTART_DEVICE,
                    new NotificationSetting( context.getResources().getString( R.string.notification_restart_device_title ), context.getResources().getString(
                            R.string.notification_restart_device_title ), context.getResources().getString( R.string.notification_restart_device_body ),
                            PendingIntent.getActivity( context, 0, new Intent(), 0 ) ) );
        }

        return notificationSettings.get( notificationId );
    }

    public static NotificationController getInstance( Context context ) {
        if ( notificationController == null ) {
            notificationController = new NotificationController( context );
        }
        return notificationController;
    }

    public void updateNotification( int notificationId, String text ) {
        Notification notification = null;
        NotificationSetting setting = getNotificationSettings( notificationId );
        if ( setting != null ) {
            if ( notifications.containsKey( notificationId ) ) {
                notification = notifications.get( notificationId );
                notification.setLatestEventInfo( context, setting.getCrawl(), text, setting.getContentIntent() );
                notificationManager.notify( notificationId, notification );
            }
        }
    }

    public void updateNotification( int notificationId, String notificationTitle, int progress ) {
        Notification notification = null;
        NotificationSetting setting = getNotificationSettings( notificationId );
        if ( setting != null ) {
            if ( notifications.containsKey( notificationId ) ) {
                notification = notifications.get( notificationId );
                notification.setLatestEventInfo( context, notificationTitle, " " + progress + "%", setting.getContentIntent() );
                notificationManager.notify( notificationId, notification );
            }
        }
    }

    public void createNotification( int notificationId, String notificationKey, String notificationTitle, String notificationMessage, Intent notificationIntent ) {
        if ( notificationId == SOCIAL_NETWORK_RESULT ) {
            Notification notification = getNotification( notificationId );
            notification.setLatestEventInfo( context, notificationTitle, notificationMessage, PendingIntent.getActivity( context, 0, notificationIntent, 0 ) );
            notificationManager.notify( notificationId, notification );
        }

        if ( notificationId == RESTART_DEVICE ) {
            Notification notification = getNotification( notificationId );
            notificationManager.notify( notificationId, notification );
        }
    }

    public static void setNotificationAlarm( Context context, long timeInterval, int notificationAlarmCount ) {
        Intent notificaitonIntent = new Intent( context, NotificationReceiver.class );
        notificaitonIntent.putExtra( "NotificationAlarmCount", notificationAlarmCount );

        PendingIntent pendingIntent = PendingIntent.getBroadcast( context.getApplicationContext(), notificationAlarmCount, notificaitonIntent, 0 );

        AlarmManager alarmManager = ( AlarmManager ) context.getSystemService( Context.ALARM_SERVICE );
        alarmManager.set( AlarmManager.RTC_WAKEUP, timeInterval, pendingIntent );
    }
    
    public static void setGoogleLoginAlarm( Context context ) {
        Intent notificaitonIntent = new Intent( context, NotificationReceiver.class );
        notificaitonIntent.setAction("SETUP_GOOGLE_ACCOUNT");
        PendingIntent pendingIntent = PendingIntent.getBroadcast( context.getApplicationContext(), 0, notificaitonIntent, 0 );
        
        long startTime = System.currentTimeMillis() +
        		((Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ? 20000 : 0);
        AlarmManager alarmManager = ( AlarmManager ) context.getSystemService( Context.ALARM_SERVICE );
        alarmManager.set( AlarmManager.RTC_WAKEUP, startTime, pendingIntent );
    }    
    
    public static void setRebootNotificationAlarm( Context context ) {
        Intent rebootIntent = new Intent( context, NotificationReceiver.class );
        rebootIntent.putExtra( "action", "notify_to_reboot" );
        PendingIntent rebootAlarm = PendingIntent.getBroadcast( context.getApplicationContext(), 0, rebootIntent, 0 );
        long currentTime = System.currentTimeMillis();
        AlarmManager alarmManager = ( AlarmManager ) context.getSystemService( Context.ALARM_SERVICE );
        alarmManager.set( AlarmManager.RTC_WAKEUP, currentTime + CommonConstants.TEN_SECONDS, rebootAlarm );
    }
    
    public static void setSceneChangeAlarm( Context context ) {   
        Intent appySceneIntent = new Intent();
        appySceneIntent.setAction("com.htc.launcher.ThemeChooser.action.theme_change");
        appySceneIntent.putExtra("workspace_id", 1);
        
        PendingIntent sceneChangetAlarm = PendingIntent.getBroadcast( context.getApplicationContext(), 0, appySceneIntent, 0 );
        long currentTime = System.currentTimeMillis();
        AlarmManager alarmManager = ( AlarmManager ) context.getSystemService( Context.ALARM_SERVICE );
        alarmManager.set( AlarmManager.RTC_WAKEUP, currentTime + CommonConstants.THIRTY_SECONDS, sceneChangetAlarm );
    }

    public static void cancelNotificationAlarm( Context context, int notificationAlarmCount ) {
        Intent intent = new Intent( context, NotificationReceiver.class );

        PendingIntent pendingIntent = PendingIntent.getBroadcast( context.getApplicationContext(), notificationAlarmCount, intent, 0 );

        AlarmManager alarmManager = ( AlarmManager ) context.getSystemService( Context.ALARM_SERVICE );
        alarmManager.cancel( pendingIntent );
    }

    public static void startNotificationAlarms( long startTime, Context context ) {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - startTime;
        long firstAlarmTime = 0;
        long secondAlarmTime = 0;
        long firstAlarmInterval = CommonConstants.SIXTY_MINUTES;
        long secondAlarmInterval = CommonConstants.SIXTY_MINUTES * 24;

        CommonUtils.setNotificationStartTime( startTime, context );

        if ( timeDiff < firstAlarmInterval ) {
            firstAlarmTime = startTime + firstAlarmInterval;
            setNotificationAlarm( context, firstAlarmTime, CONFIG_DEVICE_NOTIFICATION_FIRST_ALARM );
        }

        if ( timeDiff < secondAlarmInterval ) {
            secondAlarmTime = startTime + secondAlarmInterval;
            setNotificationAlarm( context, secondAlarmTime, CONFIG_DEVICE_NOTIFICATION_SECOND_ALARM );
        }
    }

    public static void stopNotificationAlarms( Context context ) {
        cancelNotificationAlarm( context, CONFIG_DEVICE_NOTIFICATION_FIRST_ALARM );
        cancelNotificationAlarm( context, CONFIG_DEVICE_NOTIFICATION_SECOND_ALARM );
    }
}
