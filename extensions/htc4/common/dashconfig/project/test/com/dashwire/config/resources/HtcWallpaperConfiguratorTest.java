package com.dashwire.config.resources;

import android.content.Context;
import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.debug.DashLogger;
import com.dashwire.config.integration.DashCommonUtil;
import com.dashwire.robolectric.DashRobolectricTestRunner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * User: tbostelmann
 * Date: 1/16/13
 */
@RunWith(DashRobolectricTestRunner.class)
public class HtcWallpaperConfiguratorTest {
    private Context mContext;
    private ConfigurationEvent mConfigurationEvent;
    private HtcWallpaperConfigurator mHtcWallpaperConfigurator;
    private DashCommonUtil mDashCommonUtils;

    @Before
    public void setUp() throws Exception {
        mContext = mock(Context.class);
        mConfigurationEvent = mock(ConfigurationEvent.class);
        mDashCommonUtils = mock(DashCommonUtil.class);
        DashLogger.setDebugMode(true);
        mHtcWallpaperConfigurator = new HtcWallpaperConfigurator();
        mHtcWallpaperConfigurator.setContext(mContext);
        mHtcWallpaperConfigurator.setConfigurationEvent(mConfigurationEvent);
    }

    @Test
    public void testValidWallpaper() throws Exception {
        String uri = "file:///system/customize/resource/wallpapers_c_02.jpg";
        JSONArray jaWallpaper = generateJsonArray(uri);
        when(mDashCommonUtils.isThisFirstLaunch(mContext)).thenReturn(true);

        mHtcWallpaperConfigurator.setDashCommonUtil(mDashCommonUtils);
        mHtcWallpaperConfigurator.setConfigDetails(jaWallpaper);
        mHtcWallpaperConfigurator.configure();

        verify(mConfigurationEvent).notifyEvent(mHtcWallpaperConfigurator.name(), ConfigurationEvent.CHECKED);
    }

    @Test
    public void testJsonDoesNotContainUri() throws Exception {
        String uri = "file:///system/customize/resource/wallpapers_c_02.jpg";
        JSONArray jaWallpaper = generateJsonArray(null);
        when(mDashCommonUtils.isThisFirstLaunch(mContext)).thenReturn(true);

        mHtcWallpaperConfigurator.setDashCommonUtil(mDashCommonUtils);
        mHtcWallpaperConfigurator.setConfigDetails(jaWallpaper);
        mHtcWallpaperConfigurator.configure();

        verify(mConfigurationEvent).notifyEvent(mHtcWallpaperConfigurator.name(), ConfigurationEvent.FAILED);
    }

    private JSONArray generateJsonArray(String uri) throws JSONException {
        //[{"uri":"file:///system/customize/resource/wallpapers_c_02.jpg","page":0,"src":"/branded/htc/44524564/devices/dna/jb/wallpaper/wallpapers_c_02.jpg"}]
        JSONObject joWallpaper = new JSONObject();
        if (uri != null)
            joWallpaper.put("uri", uri);
        joWallpaper.put("page", 0);
        joWallpaper.put("src", "/branded/htc/44524564/devices/dna/jb/wallpaper/wallpapers_c_02.jpg");
        JSONArray returnArray = new JSONArray();
        returnArray.put(joWallpaper);
        return returnArray;
    }
}
