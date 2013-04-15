package com.dashwire.config.integration;

import android.content.Context;
import android.content.SharedPreferences;
import com.dashwire.base.device.DashSettings;
import com.dashwire.base.debug.OverridePreferences;
import com.dashwire.base.device.DashSettingsAndroid;
import com.dashwire.config.http.OverrideServerSettings;

/**
 * Author: tbostelmann
 */
public class DashSettingsAndroidOverrideSettingsMock extends DashSettingsAndroid implements OverrideServerSettings {
    public DashSettingsAndroidOverrideSettingsMock(Context context) {
        super(context);
    }

    @Override
    public void setServerHostname(String serverRootUri) {
        SharedPreferences sp = context.getSharedPreferences(
                OverridePreferences.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(DashSettings.PROPERTY_BOOLEAN_OVERRIDE_SERVER, true);
        editor.putString(DashSettings.PROPERTY_STRING_OVERRID_SERVER_HOST, serverRootUri);
        editor.commit();
    }

    @Override
    public void setTrackingUri(String trackingUri) {
        SharedPreferences sp = context.getSharedPreferences(
                OverridePreferences.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(DashSettings.PROPERTY_BOOLEAN_OVERRIDE_SERVER, true);
        editor.putString(DashSettings.PROPERTY_STRING_TRACKING_URI, trackingUri);
        editor.commit();
    }
}
