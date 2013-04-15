//package com.dashwire.config.tracking;
//
//import android.os.AsyncTask;
//
///**
// * Author: tbostelmann
// */
//public class TrackingAsyncTask extends AsyncTask<String, Void, Boolean> {
//    private TrackingService trackingService;
//    private TrackingManager trackingManager;
//    private int startId;
////    private TrackingAsyncTaskCallbackHandler handler;
//
//    public TrackingAsyncTask(TrackingService trackingService, int startId, TrackingManager trackingManager) {
//        this.trackingService = trackingService;
//        this.startId = startId;
//        this.trackingManager = trackingManager;
//    }
//
//    @Override
//    protected Boolean doInBackground(String... params) {
//        if (params != null && params.length > 0)
//            return trackingManager.send(params[0]);
//        else
//            return trackingManager.send(null);
//    }
//
//    @Override
//    protected void onPostExecute(Boolean result) {
//        if (result) {
//            // edge case, what happens if a second async task executes and completes first?
//            // Monitor the number of async tasks
//            trackingService.stopSelf(startId);
//        }
//    }
//}
