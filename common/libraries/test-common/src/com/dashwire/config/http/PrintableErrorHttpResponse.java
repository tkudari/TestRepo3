package com.dashwire.config.http;

/**
 * Author: tbostelmann
 */
public interface PrintableErrorHttpResponse {
    public static final int HTTP_RESPONSE_EXCEPTION = 599;

    public int getStatusCode();

    public String toString();

    public String getEntity();
}
