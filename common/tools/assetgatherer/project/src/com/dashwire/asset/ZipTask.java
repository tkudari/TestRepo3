package com.dashwire.asset;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class ZipTask extends AsyncTask<Object, Void, Void> {
    private static final String TAG = ZipTask.class.getCanonicalName();

    @Override
    protected Void doInBackground( Object... params ) {
        File directory = (File) params[ 0 ];
        File file = (File) params[ 1 ];
        try {
          ZipUtility.zipDirectory (directory, file);
          file.setReadable(true,false);
      } catch ( IOException e ) {
          Log.v(TAG, "IO Exception : " + e.getMessage());
      }
        return null;
    }
}
