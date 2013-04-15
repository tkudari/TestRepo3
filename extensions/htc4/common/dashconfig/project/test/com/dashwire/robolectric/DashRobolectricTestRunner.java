package com.dashwire.robolectric;

import android.app.Application;
import com.dashwire.config.DashconfigApplication_HTC;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Method;

/**
 * @author brasten
 *         Date: 11/28/12 1:17 PM
 */
public class DashRobolectricTestRunner extends RobolectricTestRunner {
    public DashRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }


    @Override
    public void beforeTest(Method method) {
        Robolectric.bindShadowClass(ShadowAccountManager.class);
        Robolectric.bindShadowClass(ShadowComponentName.class);
    }

    @Override
    public void afterTest(Method method) {
    }

    @Override protected Application createApplication() {
        Robolectric.bindShadowClass(ShadowAccountManager.class);
        return new DashconfigApplication_HTC();
    }
}
