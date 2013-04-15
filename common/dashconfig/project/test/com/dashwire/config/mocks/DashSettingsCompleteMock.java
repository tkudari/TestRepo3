package com.dashwire.config.mocks;

import com.dashwire.config.http.OverrideServerSettings;
import com.dashwire.base.device.DashSettings;

/**
 * Author: tbostelmann
 */
public class DashSettingsCompleteMock implements DashSettings, OverrideServerSettings {
    private String serverHostname;
    private String trackingUri;

    public void setServerHostname(String hostname) {
        serverHostname = hostname;
    }

    public void setTrackingUri(String uri) {
        trackingUri = uri;
    }

    @Override
    public String getServerHostname() {
        return serverHostname;
    }

    @Override
    public String getTrackingUri() {
        return trackingUri;
    }

    @Override
    public String getTrackingId() {
        return "03dc5f8493baba5dfccd8ad942887308";
    }

    @Override
    public String getPhoneNumber() {
        return "12065551212";
    }

    @Override
    public String getIMEI() {
        return "357288040171191";
    }

    @Override
    public String getAndroidId() {
        return "a65c4b14c75d45dc";
    }

    @Override
    public String getClientVersion() {
        return "0.9.3";
    }

    @Override
    public String getBuildRelease() {
        return "567";
    }

    @Override
    public String getBuildIncremental() {
        return "213";
    }

    @Override
    public String getBuildSDK() {
        return "15";
    }

    @Override
    public String getBuildDevice() {
        return "SGH-I727";
    }

    @Override
    public String getBuildManufacturer() {
        return "samsung";
    }
}
