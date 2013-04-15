package com.dashwire.asset;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Manufacturer {
    private static final String TAG = Manufacturer.class.getCanonicalName();
   
    public static JSONArray loadManufacturerAssets(Context context, String manufacturer) {
        try {
            InputStream inputStream = context.getAssets().open("json/manufacturures.json");
            String jsonString = convertStreamToString(inputStream);
            try {
                JSONObject root = new JSONObject(jsonString);
                return root.getJSONArray(manufacturer.toLowerCase());
            } catch (JSONException e) {
                Log.e(TAG, "Error", e);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
        }
        return null;
    }
    private static String convertStreamToString(java.io.InputStream is) {
        try {
        	return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }
}
