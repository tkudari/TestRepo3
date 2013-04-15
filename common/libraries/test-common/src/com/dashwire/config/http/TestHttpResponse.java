package com.dashwire.config.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Author: tbostelmann
 */
public class TestHttpResponse {
    private String message;
    private String stackTrace;

    public TestHttpResponse(Throwable e) {
        this.message = e.getMessage();
        ByteArrayOutputStream stackTrace = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(stackTrace));
        this.stackTrace = stackTrace.toString();
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static TestHttpResponse parseJson(String jsonEvent) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.fromJson(jsonEvent, TestHttpResponse.class);
    }

    public String toString() {
        return message + "\n" + stackTrace;
    }
}
