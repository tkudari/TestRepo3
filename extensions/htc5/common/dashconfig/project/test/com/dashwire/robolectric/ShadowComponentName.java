package com.dashwire.robolectric;

import android.content.ComponentName;
import com.xtremelabs.robolectric.util.Implementation;
import com.xtremelabs.robolectric.util.Implements;

/**
 *
 */
@SuppressWarnings({"UnusedDeclaration"})
@Implements(ComponentName.class)
public class ShadowComponentName extends com.xtremelabs.robolectric.shadows.ShadowComponentName {

    @Implementation
    public String getShortClassName() {
        String shortName = getClassName();

        if (getClassName().startsWith(getPackageName())) {
            shortName = getClassName().substring(getPackageName().length());
        }

        return shortName;
    }

}
