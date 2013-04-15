package com.dashwire.asset;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dashwire.asset.event.BusProvider;
import com.dashwire.asset.gatherer.common.R;
import com.dashwire.asset.gatherer.events.ExtractionStateChangedEvent;
import com.dashwire.asset.server.ClientService;
import com.squareup.otto.Subscribe;

public class PackageinfoActivity extends Activity {

    private final static String TAG = PackageinfoActivity.class.getCanonicalName();

    private TextView progressLogView;

    private Handler handler = new Handler();

    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        Log.d(TAG, "onCreate");
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );

        BusProvider.getInstance().register(this);

        progressLogView = (TextView) findViewById( R.id.progressLogView );

        startService(new Intent(this, ClientService.class));
    }

    public void handleToggleExtractionClick(View view) {
        startExtraction();
    }

    public void startExtraction() {
        Intent intent = new Intent( ExtractionService.ACTION_START_EXTRACTION );

        startService(intent);
    }

    @Subscribe
    public void extractionStateChanged(ExtractionStateChangedEvent event) {
        if (ExtractionStateChangedEvent.State.WAITING == event.getState()) {
            logProgress("Waiting to extract ...");
        } else if (ExtractionStateChangedEvent.State.IN_PROGRESS == event.getState()) {
            logProgress("Extracting ...");
        } else if (ExtractionStateChangedEvent.State.COMPLETED == event.getState()) {
            logProgress("Extraction completed!");
        }
    }

    private void logProgress( String log ) {
        Log.d(TAG, "Progress: " + log);
        final String logLineToAppend = log;
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressLogView.append( logLineToAppend + "\n" );
            }
        });
    }

}