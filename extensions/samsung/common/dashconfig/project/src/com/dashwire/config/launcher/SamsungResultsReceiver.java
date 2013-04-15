package com.dashwire.config.launcher;

import android.content.Context;
import android.content.Intent;
import com.dashwire.base.debug.DashLogger;

import java.io.File;

/**
 * Author: tbostelmann
 */
public class SamsungResultsReceiver extends ResultsReceiver {
    private static final String TAG = SamsungResultsReceiver.class.getCanonicalName();
    @Override
    public void onReceive( Context context, Intent intent ) {
        DashLogger.d(TAG, "SamsungResultsReceiver.onReceive");
        super.onReceive(context, intent);
        File favoritesFile = FavoritesInfo.getFile();
        favoritesFile.delete();
    }
}
