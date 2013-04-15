//package com.dashwire.config.tracking;
//
//import android.test.ServiceTestCase;
//import com.dashwire.config.http.DashClient;
//import com.dashwire.config.http.HttpClientRequestTest;
//import com.dashwire.config.http.OverrideServerSettings;
//import com.dashwire.config.http.PrintableErrorHttpResponse;
//import com.dashwire.config.integration.DashSettingsAndroidOverrideSettingsMock;
//import org.apache.http.HttpRequest;
//import org.apache.http.HttpResponse;
//
//public class TrackingServiceTest extends ServiceTestCase {
//    public TrackingServiceTest() {
//        super(TrackingService.class);
//    }
//
//    public void testCheckinComplete() throws Exception {
//        OverrideServerSettings overrideServerSettings =
//                new DashSettingsAndroidOverrideSettingsMock(getContext().getApplicationContext());
//        HttpClientRequestTest test = new HttpClientRequestTest(DashClient.CLIENT_TRACKING_QUERY, overrideServerSettings) {
//
//            @Override
//            public void validateHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
//                httpResponse.setStatusCode(200);
//            }
//
//            @Override
//            public PrintableErrorHttpResponse implementTest() throws Exception {
//                TrackingService.track(getContext(), "Foo");
//                Thread.sleep(100);
//                assertTrue(TrackingService.IS_RUNNING);
//                return null;
//            }
//        };
//    }
//
//    public void testServiceIsProperlyShutdown() throws Exception {
//        OverrideServerSettings overrideServerSettings =
//                new DashSettingsAndroidOverrideSettingsMock(getContext().getApplicationContext());
//        HttpClientRequestTest test = new HttpClientRequestTest(DashClient.CLIENT_TRACKING_QUERY, overrideServerSettings) {
//
//            @Override
//            public void validateHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
//                httpResponse.setStatusCode(200);
//            }
//
//            @Override
//            public PrintableErrorHttpResponse implementTest() throws Exception {
//                TrackingService.track(getContext(), "Foo");
//                Thread.sleep(100);
//                assertTrue(TrackingService.IS_RUNNING);
//                return null;
//            }
//        };
//        assertFalse(TrackingService.IS_RUNNING);
//        test.executeTest(2000);
//        assertFalse(TrackingService.IS_RUNNING);
//
//        test = new HttpClientRequestTest(DashClient.CLIENT_TRACKING_QUERY, overrideServerSettings) {
//
//            @Override
//            public void validateHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
//                Thread.sleep(1000);
//                httpResponse.setStatusCode(200);
//            }
//
//            @Override
//            public PrintableErrorHttpResponse implementTest() throws Exception {
//                TrackingService.track(getContext(), "Foo");
//                Thread.sleep(1000);
//                assertTrue(TrackingService.IS_RUNNING);
//                TrackingService.track(getContext(), "Bar");
//                return null;
//            }
//        };
//        assertFalse(TrackingService.IS_RUNNING);
//        test.executeTest(5000);
//        assertTrue(TrackingService.IS_RUNNING);
//        TrackingService.track(getContext(), null);
//        Thread.sleep(5000);
//        assertFalse(TrackingService.IS_RUNNING);
//    }
//}
