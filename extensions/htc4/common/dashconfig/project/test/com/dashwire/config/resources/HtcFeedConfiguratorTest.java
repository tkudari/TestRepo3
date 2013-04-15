package com.dashwire.config.resources;

import android.content.Context;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.robolectric.DashRobolectricTestRunner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

/**
 * User: tbostelmann
 * Date: 1/8/13
 */
@RunWith(DashRobolectricTestRunner.class)
public class HtcFeedConfiguratorTest {
    private FeedUtilities mFeedUtilities;
    private Context mContext;
    private ConfigurationEvent mConfigurationEvent;
    private HtcFeedConfigurator mHtcFeedConfigurator;

    @Before
    public void setUp() throws Exception {
        mFeedUtilities = mock(FeedUtilities.class);
        mContext = mock(Context.class);
        mConfigurationEvent = mock(ConfigurationEvent.class);
        DashLogger.setDebugMode(true);
        mHtcFeedConfigurator = new HtcFeedConfigurator();
        mHtcFeedConfigurator.setContext(mContext);
        mHtcFeedConfigurator.setConfigurationEvent(mConfigurationEvent);
        mHtcFeedConfigurator.setFeedUtilities(mFeedUtilities);
    }

    @Test
    public void testConfigureSuccessfulConfigureFeed() throws Exception {
        final String eid = "1";
        final int[] tags = {12345, 22345};
        JSONArray configArray = generateJsonArray(eid, tags);

        when(mFeedUtilities.getFeedCurEditionId(mContext)).thenReturn(eid);

        mHtcFeedConfigurator.setConfigDetails(configArray);
        mHtcFeedConfigurator.configure();

        verify(mConfigurationEvent).notifyEvent(mHtcFeedConfigurator.name(), ConfigurationEvent.CHECKED);
    }

    @Test
    public void testConfigureDifferentEditionIdCausesFailure() throws Exception {
        final String eid = "1";
        final int[] tags = {12345, 22345};
        JSONArray configArray = generateJsonArray(eid, tags);

        when(mFeedUtilities.getFeedCurEditionId(mContext)).thenReturn("2");

        mHtcFeedConfigurator.setConfigDetails(configArray);
        mHtcFeedConfigurator.configure();

        verify(mConfigurationEvent).notifyEvent(mHtcFeedConfigurator.name(), ConfigurationEvent.FAILED);
    }

    @Test
    public void testConfigureEmptyEditionIdFails() throws Exception {
        final String eid = "";
        final int[] tags = {12345, 22345};
        JSONArray configArray = generateJsonArray(eid, tags);

        when(mFeedUtilities.getFeedCurEditionId(mContext)).thenReturn(eid);

        mHtcFeedConfigurator.setConfigDetails(configArray);
        mHtcFeedConfigurator.configure();

        verify(mConfigurationEvent).notifyEvent(mHtcFeedConfigurator.name(), ConfigurationEvent.FAILED);
    }

    @Test
    public void testConfigureNullCurEditionIdSucceeds() throws Exception {
        final String eid = "1";
        final int[] tags = {12345, 22345};
        JSONArray configArray = generateJsonArray(eid, tags);

        when(mFeedUtilities.getFeedCurEditionId(mContext)).thenReturn(null);

        mHtcFeedConfigurator.setConfigDetails(configArray);
        mHtcFeedConfigurator.configure();

        verify(mConfigurationEvent).notifyEvent(mHtcFeedConfigurator.name(), ConfigurationEvent.CHECKED);
    }

    @Test
    public void testConfigureEmptyCurEditionIdSucceeds() throws Exception {
        final String eid = "1";
        final int[] tags = {12345, 22345};
        JSONArray configArray = generateJsonArray(eid, tags);

        when(mFeedUtilities.getFeedCurEditionId(mContext)).thenReturn("");

        mHtcFeedConfigurator.setConfigDetails(configArray);
        mHtcFeedConfigurator.configure();

        verify(mConfigurationEvent).notifyEvent(mHtcFeedConfigurator.name(), ConfigurationEvent.CHECKED);
    }

    @Test
    public void testConfigureNoTagsCausesSucceeds() throws Exception {
        final String eid = "1";
        final int[] tags = null;
        JSONArray configArray = generateJsonArray(eid, tags);

        when(mFeedUtilities.getFeedCurEditionId(mContext)).thenReturn(eid);

        mHtcFeedConfigurator.setConfigDetails(configArray);
        mHtcFeedConfigurator.configure();

        verify(mConfigurationEvent).notifyEvent(mHtcFeedConfigurator.name(), ConfigurationEvent.CHECKED);
    }

    @Test
    public void testConfigureEmptyTagArrayCausesSuccess() throws Exception {
        final String eid = "1";
        final int[] tags = new int[0];
        JSONArray configArray = generateJsonArray(eid, tags);

        when(mFeedUtilities.getFeedCurEditionId(mContext)).thenReturn(eid);

        mHtcFeedConfigurator.setConfigDetails(configArray);
        mHtcFeedConfigurator.configure();

        verify(mConfigurationEvent).notifyEvent(mHtcFeedConfigurator.name(), ConfigurationEvent.CHECKED);
    }

    private JSONArray generateJsonArray(String eid, int[] tags) throws JSONException {
        JSONArray configArray = new JSONArray();
        JSONObject configObject = new JSONObject();
        if (eid != null) {
            configObject.put("tag_edition_id", eid);
        }
        if (tags != null) {
            JSONArray tagsArray = new JSONArray();
            for (int tag: tags) {
                tagsArray.put(tag);
            }
            configObject.put("key_tag_ids", tagsArray);
        }
        configArray.put(configObject);

        return configArray;
    }
}
