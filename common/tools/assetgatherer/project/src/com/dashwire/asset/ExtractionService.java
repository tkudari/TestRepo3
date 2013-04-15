package com.dashwire.asset;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * @author brasten
 *         Date: 11/13/12 10:33 PM
 */
public class ExtractionService extends IntentService {
    private static final String TAG = ExtractionService.class.getCanonicalName();

    public static String ACTION_START_EXTRACTION = "com.dashwire.asset.gatherer.intent.action.START_EXTRACTION";

    public ExtractionService() {
        super(TAG);
    }

    @Override
    public void onHandleIntent(Intent intent) {
    	Log.v(TAG,"onHandleIntent : " + intent.getAction());
        if (intent.getAction().equals(ACTION_START_EXTRACTION)) {
            AssetExtractionTask extractionTask = new AssetExtractionTask( getApplicationContext() );
            extractionTask.execute();
        }
    }
}
