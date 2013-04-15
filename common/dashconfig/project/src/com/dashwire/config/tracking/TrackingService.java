//package com.dashwire.config.tracking;
//
//import android.app.Service;
//import android.content.*;
//import android.content.pm.PackageManager;
//import android.os.*;
//import com.dashwire.config.http.DashClient;
//import com.dashwire.config.http.DashClientRestClient;
//import com.dashwire.config.integration.*;
//import com.dashwire.config.tasks.CheckinHandler;
//import com.dashwire.config.tasks.CheckinTask;
//
//import java.util.Date;
//
//public class TrackingService extends Service implements CheckinHandler {
////    private static final String TAG = TrackingService.class.getCanonicalName();
//
//    private BroadcastReceiver broadcastReceiver;
//
//    public static Boolean IS_RUNNING = false;
//    public static Boolean CHECKIN_COMPLETE = false;
//
//    public static ComponentName track(Context context, String tag) {
//        Intent trackingIntent = new Intent(context, TrackingService.class);
//
//        trackingIntent.putExtra("event", tag);
//        return context.startService(trackingIntent);
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    private TrackingManager trackingManager;
//
//    @Override
//    public void onCreate() {
//        IS_RUNNING = true;
//        NetworkStatus networkStatus = new NetworkStatusAndroid(getApplicationContext());
//        DashSettings dashSettings = DashSettingsAndroid.create(getApplicationContext());
//        DashClient dashClient = new DashClientRestClient(dashSettings);
//        DateGenerator dateGenerator = new DateGenerator() {
//            @Override
//            public Date generateDate() {
//                return new Date();
//            }
//        };
//        trackingManager = new TrackingManager(networkStatus, dashClient, dateGenerator);
//        registerNetworkStateBroadcastReceiver();
//
//        CHECKIN_COMPLETE = false;
//        CheckinTask checkin = new CheckinTask( getApplicationContext(), this, "TrackingService onCreate()" );
//        checkin.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//        super.onCreate();
//    }
//
//    public void processCheckin(boolean success) {
//        CHECKIN_COMPLETE = true;
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        TrackingAsyncTask task = new TrackingAsyncTask(this, startId, trackingManager);
//        String event = intent.getStringExtra("event");
//        if (event != null)
//            task.execute(event);
//        else
//            task.execute();
//        return Service.START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        IS_RUNNING = false;
//        if (broadcastReceiver != null) {
//            unregisterReceiver(broadcastReceiver);
//        }
//    }
//
//    void registerNetworkStateBroadcastReceiver() {
//        broadcastReceiver = new NetworkStateBroadcastReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        registerReceiver(broadcastReceiver, filter);
//    }
//}
