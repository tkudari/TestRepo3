package com.dashwire.asset.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dashwire.asset.ExtractionService;
import com.dashwire.asset.gatherer.common.R;

import java.io.IOException;
import java.util.Properties;

/**
 */
public class ClientService extends Service {
    private static final String TAG = ClientService.class.getCanonicalName();
    private InterfaceServer interfaceServer;
    private String lastFileName = "";

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate called");

        try {
            interfaceServer =
                new InterfaceServer(8899) {
                    public Response serve( String uri, String method, Properties header, Properties parms, Properties files ) {
                        Log.d(TAG, "InterfaceServer URI: " + uri);

                        Response httpResponse = new Response("404", "text/html", "BAD REQUEST" );

                        if ("POST".equals(method)) {
                            if ("/action/startExtraction".equals(uri)) {
                                Intent intent = new Intent( ExtractionService.ACTION_START_EXTRACTION );
                                startService(intent);

                                String toUri = "/";
                                httpResponse = new Response( HTTP_REDIRECT, MIME_HTML,
                                                        "<html><body>Redirected: <a href=\"" + toUri + "\">" +
                                                                toUri + "</a></body></html>");
                                httpResponse.addHeader( "Location", toUri );
                            }
                        } else {
                            if ("/javascripts/client.js".equals(uri)) {
                                httpResponse = new Response("200", "application/js", "window.alert(\"Blah!\");" );
                            } else if ("/".equals(uri)) {
                                httpResponse = new Response("200", "text/html", getResources().openRawResource(R.raw.console_index) );
                            } else if ("/style.css".equals(uri)) {
                                httpResponse = new Response("200", "text/css", getResources().openRawResource(R.raw.console_style) );
                            }
                        }

                        return httpResponse;
                    }
                };
        } catch (IOException ioe) {
            Log.e(TAG, "IO Exception: " + ioe.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
