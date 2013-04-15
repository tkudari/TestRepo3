package com.dashwire.config;

import android.test.AndroidTestCase;
import junit.framework.Assert;
import org.apache.http.HttpRequest;
import org.json.JSONObject;

public class RestClientTest extends AndroidTestCase {
    public void testGet() throws Throwable {
        HttpClientTest test = new HttpClientTest("/checkin", getContext()) {
            @Override
            public void validateHttpRequest(HttpRequest httpRequest) {
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
            }

            @Override
            public JSONObject implementTest() {
                return RestClient.get(getServerRootUri() + "/checkin", getContext());
            }
        };
        test.executeTest();
    }
}
