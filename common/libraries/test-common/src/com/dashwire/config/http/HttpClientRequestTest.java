package com.dashwire.config.http;

import junit.framework.Assert;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class HttpClientRequestTest {

    LocalTestServer server;
    LocalHttpRequestHandler handler;

    public HttpClientRequestTest(String query, OverrideServerSettings overrideServerSettings) throws Exception {
        this.handler = new LocalHttpRequestHandler(this);
        server = new LocalTestServer(null, null);
        server.register(query, handler);
        server.start();
        overrideServerSettings.setServerHostname(getServerRootUri());
        overrideServerSettings.setTrackingUri(getServerRootUri() + "/t");
    }

    public abstract void validateHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;

    public abstract PrintableErrorHttpResponse implementTest() throws Exception;

    public void validateHeaders(HttpRequest httpRequest) {
        String phoneNumber = getHeader(httpRequest, "phone-number");
        if (phoneNumber != null && phoneNumber.length() > 0) {
            Assert.assertTrue(phoneNumber.length() == "12065551212".length());
        }
        Assert.assertTrue(getHeader(httpRequest, "User-Agent").startsWith("Dashconfig"));
        Assert.assertNotNull(getHeader(httpRequest, "device-id"));
        Assert.assertNotNull(getHeader(httpRequest, "build-release"));
        Assert.assertNotNull(getHeader(httpRequest, "build-incremental"));
        Assert.assertNotNull(getHeader(httpRequest, "build-sdk"));
        Assert.assertNotNull(getHeader(httpRequest, "build-device"));
        Assert.assertNotNull(getHeader(httpRequest, "build-manufacturer"));
        Assert.assertNotNull(getHeader(httpRequest, "android-id"));
        Assert.assertNotNull(getHeader(httpRequest, "tracking-id"));
    }

    public String getServerRootUri() {
        return "http://" + server.getServiceHostName() + ":" + server.getServicePort();
    }

    public void executeTest() throws Exception {
        executeTest(0, false);
    }

    public void executeTest(int waitMilliseconds) throws Exception {
        executeTest(waitMilliseconds, false);
    }

    public void executeTest(Boolean skipHandlerAssertion) throws Exception {
        executeTest(0, skipHandlerAssertion);
    }

    public void executeTest(int waitMilliseconds, Boolean skipHandlerAssertion) throws Exception {
        PrintableErrorHttpResponse response = implementTest();
        if (waitMilliseconds > 0)
            Thread.sleep(waitMilliseconds);
        Assert.assertTrue(skipHandlerAssertion || handler.getHandlerWasExecuted());
        if (response != null && response.getStatusCode() == PrintableErrorHttpResponse.HTTP_RESPONSE_EXCEPTION) {
            Assert.fail(TestHttpResponse.parseJson(response.getEntity()).toString());
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
        HttpClientRequestTest httpClientRequestTest;
        Boolean handlerWasExecuted = false;

        public LocalHttpRequestHandler(HttpClientRequestTest httpClientRequestTest) {
            this.httpClientRequestTest = httpClientRequestTest;
        }

        public Boolean getHandlerWasExecuted() {
            return handlerWasExecuted;
        }

        @Override
        public void handle(HttpRequest httpRequest,
                           HttpResponse httpResponse,
                           HttpContext httpContext)
                throws HttpException, IOException {
            handlerWasExecuted = true;
            try {
                Object[] args = new Object[1];
                args[0] = httpRequest;
                httpClientRequestTest.validateHeaders(httpRequest);
                httpClientRequestTest.validateHttpRequest(httpRequest, httpResponse);
            } catch (Throwable e) {
                httpResponse.setStatusCode(PrintableErrorHttpResponse.HTTP_RESPONSE_EXCEPTION);
                TestHttpResponse exceptionResponse = new TestHttpResponse(e);
                BasicHttpEntity entity = new BasicHttpEntity();
                InputStream is = new ByteArrayInputStream(exceptionResponse.toJson().getBytes("UTF-8"));
                entity.setContent(is);
                httpResponse.setEntity(entity);
            }
        }
    }
}
