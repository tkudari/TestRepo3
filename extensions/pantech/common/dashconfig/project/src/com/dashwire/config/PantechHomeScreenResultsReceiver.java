package com.dashwire.config;

import com.dashwire.base.debug.DashLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.dashwire.config.launcher.ResultsReceiver;

/**
 * Author: tbostelmann
 */
public class PantechHomeScreenResultsReceiver extends ResultsReceiver {
    private static final String TAG = PantechHomeScreenResultsReceiver.class.getCanonicalName();

        @Override
        public void onReceive( Context context, Intent resultsIntent ) 
        {

            DashLogger.d( TAG, "Home Screen result receiver called." );

            if(resultsIntent.getBooleanExtra("success", false))  {
                String jsonString = resultsIntent.getStringExtra("homescreen");
                
                if(jsonString != null) {
                    DashLogger.d( TAG, "Widgets response json: " + jsonString);
                    
                    try {
                        JSONArray shortcutArray = new JSONObject(jsonString).getJSONArray("homescreen");
                        for(int i = 0; i < shortcutArray.length(); i++) {
                            JSONObject item = shortcutArray.getJSONObject(i);
                            if(!item.getBoolean("success")) {
                                String packageName = "",className = "",errorMessage = "";
                                int errorCode = 0;
                                if(item.has("packageName")) packageName = item.getString("packageName");
                                if(item.has("className")) className = item.getString("className");
                                if(item.has("errorCode")) errorCode = item.getInt("errorCode");
                                if(item.has("errorMessage")) errorMessage = item.getString("errorMessage");
                                DashLogger.d(TAG, "Failure processing widget: " + packageName + "/" + className + ". errorCode = " + errorCode + ", errorMessage = " + errorMessage);
                                DashLogger.d(TAG, "ErrorCode: " + packageName + "/" + className);   
                            }
                        }
                    } catch(JSONException jse) {
                        DashLogger.d(TAG, "JSON Exception processing widget results: " + jse.getMessage());
                    }
                } else {
                    DashLogger.d(TAG, "Response homescreen object is null.");
                }
            } else {
                DashLogger.d(TAG, "Result from homescreen indicates all failed.");
            }
            
            super.onReceive( context, resultsIntent );
        }     
}
