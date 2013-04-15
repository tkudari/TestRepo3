package com.dashwire.config.widget.services;


import junit.framework.Test;
import junit.framework.TestSuite;

import android.test.suitebuilder.TestSuiteBuilder;

/**
 * A test suite containing all tests for DashwireADT.
 *
 * To run all suites found in this apk:
 * $ adb shell am instrument -w \
 *   com.dashwire.android.test/android.test.InstrumentationTestRunner
 *
 * To run just this suite from the command line:
 * $ adb shell am instrument -w \
 *   -e class com.dashwire.android.AllTests \
 *   com.dashwire.android.test/android.test.InstrumentationTestRunner
 *
 * To run an individual test case, e.g. {@link com.dashwire.android.app.DashwireApplicationTests}:
 * $ adb shell am instrument -w \
 *   -e class com.dashwire.android.app.DashwireApplication \
 *   com.dashwire.android.test/android.test.InstrumentationTestRunner
 *
 * To run an individual test, e.g. {@link com.dashwire.android.app.DashwireApplicationTests#testSimpleCreate()}:
 * $ adb shell am instrument -w \
 *   -e class com.dashwire.android.app.DashwireApplicationTests#testSimpleCreate \
 *   com.dashwire.android.test/android.test.InstrumentationTestRunner
 */
public class AllTests extends TestSuite {

    public static Test suite() {
        return new TestSuiteBuilder(AllTests.class)
                .includeAllPackagesUnderHere()
                .build();
    }
}
