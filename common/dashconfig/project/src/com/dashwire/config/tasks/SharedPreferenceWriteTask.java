package com.dashwire.config.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.dashwire.config.util.CommonConstants;

public class SharedPreferenceWriteTask extends AsyncTask<Object, Void, Void> {

    @SuppressWarnings("unused")
    private static final String TAG = SharedPreferenceWriteTask.class.getCanonicalName();
    private Context context;

    public SharedPreferenceWriteTask( Context context ) {
        this.context = context;
    }

    @Override
    protected Void doInBackground( Object... params ) {

        SharedPreferences settings = null;

        String keyString = params[ 0 ].toString();
        Object value = params[ 1 ];
        Object preferenceLevel = params[ 2 ].toString();
        
        if ( preferenceLevel.toString().equalsIgnoreCase( CommonConstants.DEFAULT_PREF ) ) {
            settings = PreferenceManager.getDefaultSharedPreferences( context );
        } else {
            settings = context.getSharedPreferences( preferenceLevel.toString(), Context.MODE_PRIVATE );
        }

        Editor editor = settings.edit();
        
        if ( keyString.equalsIgnoreCase( CommonConstants.RESET_SESSION ) ) {
            editor.clear();
        } else {
            if ( value.getClass().equals( String.class ) ) {
                editor.putString( keyString, value.toString() );
            } else {
                editor.putBoolean( keyString, Boolean.valueOf(value.toString()) );
            }
        }
        
        editor.commit();
        return null;
    }
}
