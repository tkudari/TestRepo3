package com.dashwire.config;

import android.content.Context;
import android.content.SharedPreferences;
import com.dashwire.base.debug.OverridePreferences;
import com.dashwire.base.device.DashSettings;
import junit.framework.Assert;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public abstract class HttpClientTest {

    LocalTestServer server;
    Context context;
    String query;
    LocalHttpRequestHandler handler;

    public HttpClientTest(String query, Context context) throws Exception {
        this.query = query;
        this.context = context;
        this.handler = new LocalHttpRequestHandler(this);
        server = new LocalTestServer(null, null);
        server.register(getQuery(), handler);
        server.start();
        SharedPreferences sp = getContext().getSharedPreferences(
                OverridePreferences.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(DashSettings.PROPERTY_BOOLEAN_OVERRIDE_SERVER, true);
        editor.putString(DashSettings.PROPERTY_STRING_OVERRID_SERVER_HOST, getServerRootUri());
        editor.commit();
    }

    public abstract void validateHttpRequest(HttpRequest httpRequest);

    public abstract JSONObject implementTest();

    public String getQuery() {
        return query;
    }

    public Context getContext() {
        return context;
    }

    public LocalTestServer getServer() {
        return server;
    }

    public String getServerRootUri() {
        return "http://" + server.getServiceHostName() + ":" + server.getServicePort();
    }

    public void executeTest() throws Throwable {
        JSONObject response = implementTest();
        if (response.getInt(RestClient.JSON_HTTP_STATUS_CODE) != 200) {
            if (response.has("message")) {
                Assert.fail(response.getString("message") + "\n" + response.getString("stackTrace"));
            } else {
                Assert.fail(response.getString("stackTrace"));
            }
        }
    }

    public String getHeader(HttpRequest httpRequest, String header) {
        if (httpRequest.getFirstHeader(header) != null) {
            return httpRequest.getFirstHeader(header).getValue();
        } else {
            return null;
        }
    }

    private class LocalHttpRequestHandler implements HttpRequestHandler {
        HttpClientTest httpClientTest;

        public LocalHttpRequestHandler(HttpClientTest httpClientTest) {
            this.httpClientTest = httpClientTest;
        }

        @Override
        public void handle(HttpRequest httpRequest,
                           HttpResponse httpResponse,
                           HttpContext httpContext)
                throws HttpException, IOException {
            try {
                Object[] args = new Object[1];
                args[0] = httpRequest;
                httpClientTest.validateHttpRequest(httpRequest);
                httpResponse.setStatusCode(200);
            } catch (Throwable e) {
                httpResponse.setStatusCode(500);
                JSONObject jsonObject = new JSONObject();
                ByteArrayOutputStream stackTrace = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(stackTrace));
                try {
                    jsonObject.put("message", e.getMessage());
                    jsonObject.put("stackTrace", stackTrace.toString());
                } catch (JSONException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                BasicHttpEntity entity = new BasicHttpEntity();
                InputStream is = new ByteArrayInputStream(jsonObject.toString().getBytes("UTF-8"));
                entity.setContent(is);
                httpResponse.setEntity(entity);
            }
        }
    }

    ;
}
