package com.dashwire.config.http;

/**
 * Author: tbostelmann
 */
public class DashPrintableErrorHttpResponse implements PrintableErrorHttpResponse {

    private DashHttpResponse dashHttpResponse;

    public DashPrintableErrorHttpResponse(DashHttpResponse dashHttpResponse) {
        this.dashHttpResponse = dashHttpResponse;
    }

    @Override
    public int getStatusCode() {
        return dashHttpResponse.getStatusCode();
    }

    @Override
    public String getEntity() {
        return dashHttpResponse.getEntity();
    }
}
