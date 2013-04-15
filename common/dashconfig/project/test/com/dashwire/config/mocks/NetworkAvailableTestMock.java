package com.dashwire.config.mocks;

import com.dashwire.base.device.NetworkStatus;

/**
 * Author: tbostelmann
 */
public class NetworkAvailableTestMock implements NetworkStatus {
    private Boolean isNetworkAvailable = true;

    public NetworkAvailableTestMock(Boolean networkAvailable) {
        isNetworkAvailable = networkAvailable;
    }

    public void setNetworkAvailable(Boolean val) {
        isNetworkAvailable = val;
    }

    @Override
    public Boolean isNetworkAvailable() {
        return isNetworkAvailable;
    }
}
