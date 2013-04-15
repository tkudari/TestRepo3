package com.dashwire.config.http;

/**
 * Author: tbostelmann
 */
public interface OverrideServerSettings {
    public void setServerHostname(String serverRootUri);

    public void setTrackingUri(String trackingUri);
}
