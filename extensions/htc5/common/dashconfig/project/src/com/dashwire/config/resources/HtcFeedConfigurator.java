package com.dashwire.config.resources;

import android.content.Context;
import android.content.Intent;
import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ResourceConfigurator;
import com.dashwire.base.debug.DashLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HtcFeedConfigurator implements ResourceConfigurator {
    private static final String TAG = HtcFeedConfigurator.class.getCanonicalName();

    protected Context mContext;

    protected ConfigurationEvent mConfigurationEvent;

    protected JSONArray mConfigArray;

    protected FeedUtilities mFeedUtilities;

    public void setFeedUtilities(FeedUtilities feedUtilities) {
        mFeedUtilities = feedUtilities;
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public void setConfigurationEvent(ConfigurationEvent configurationEvent) {
        mConfigurationEvent = configurationEvent;
    }

    @Override
    public void setConfigDetails(JSONArray jsonArray) {
        mConfigArray = jsonArray;
    }

    @Override
    public void configure() {
        DashLogger.d(TAG, "configObj = " + mConfigArray.toString());
        try {
            JSONObject data = (JSONObject) mConfigArray.get(0);

            String editionId = null;
            if ( data.has( "tag_edition_id" ) ) {
                editionId = "" + data.getInt( "tag_edition_id" );
            }

            if ( editionId == null || editionId.isEmpty() ) {
                mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.FAILED);
                return;
            }
            else {
                String curEditionId = mFeedUtilities.getFeedCurEditionId(mContext);
                DashLogger.d(TAG, "Current EditionId: " + curEditionId);
                if ( curEditionId != null && !curEditionId.isEmpty() && !editionId.equals(curEditionId) ) {
                    mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.FAILED);
                    return;
                }
            }

            String idArray[] = null;
            if ( data.has( "key_tag_ids" ) ) {
                JSONArray jsonArray = data.getJSONArray( "key_tag_ids" );
                idArray = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++ ) {
                    idArray[i] = String.valueOf( jsonArray.getInt( i ) );
                    DashLogger.d( TAG, "idArray = " + idArray[i] );
                }
            }

            if ( idArray == null || idArray.length == 0 ) {
                DashLogger.d(TAG, "feed tags: " + idArray);
                mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.CHECKED);
            }
            else {
                DashLogger.d(TAG, "broadcasting editionId: " + editionId + " tag array length: " + idArray.length);
                configureFeeds(editionId, idArray);
                mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.CHECKED);
            }
        }
        catch (JSONException e) {
            DashLogger.e(TAG, "Exception reading htc_feed configuration file", e);
            mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.FAILED);
        }
    }

    void configureFeeds(String editionId, String[] tagIds) {
        Intent feedIntent = new Intent();
        feedIntent.setAction( "com.htc.plugin.news.ACTION_CONFIG_FROM_AURORA" );
        feedIntent.putExtra( "tag_edition_id", editionId );
        feedIntent.putExtra( "key_tag_ids", tagIds );
        mContext.sendBroadcast( feedIntent );
    }

    @Override
    public String name() {
        return "htc_feeds";
    }
}
