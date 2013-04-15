package com.dashwire.config.http;

import com.google.gson.Gson;

/**
 * Author: tbostelmann
 */
public class DashHttpResponse {

    private int statusCode;
    private String responseEntity;

    public DashHttpResponse(int statusCode, String responseEntity) {
        this.statusCode = statusCode;
        this.responseEntity = responseEntity;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getEntity() {
        return responseEntity;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String toString() {
        return toJson();
    }
}
