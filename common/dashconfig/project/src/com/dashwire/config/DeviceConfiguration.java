package com.dashwire.config;

import org.json.JSONArray;

public interface DeviceConfiguration {
    public String name();

    public void set( JSONArray data );
}
