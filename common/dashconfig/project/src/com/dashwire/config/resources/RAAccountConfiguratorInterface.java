package com.dashwire.config.resources;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ResourceConfigurator;
import org.json.JSONArray;

/**
 * User: tbostelmann
 * Date: 2/11/13
 */
public class RAAccountConfiguratorInterface implements ResourceConfigurator
{

    private static final String TAG = RAAccountConfiguratorInterface.class.getCanonicalName();

    protected Context mContext;

    protected ConfigurationEvent mConfigurationEvent;

    protected JSONArray mConfigArray;

    public RAAccountConfiguratorInterface()
    {
    }

    @Override
    public String name()
    {
        return "accounts";
    }

    @Override
    public void setContext(Context context)
    {
        mContext = context;
    }

    @Override
    public void setConfigurationEvent(ConfigurationEvent configurationEvent)
    {
        mConfigurationEvent = configurationEvent;

    }

    @Override
    public void setConfigDetails(JSONArray configArray)
    {
        mConfigArray = configArray;

    }


    // TODO: should break this out into a HTC module and not pollute the general wallpaper module

    @Override
    public void configure()
    {
        if (mConfigArray != null && mConfigArray.length() > 0)
        {
            Log.v(TAG, "mConfigArray = " + mConfigArray.toString());
            try
            {
                Log.v(TAG,"Inside RAAccountConfiguratorInterface configure()");

                Intent intent = new Intent("com.dashwire.feature.intent.CONFIGURE");
                intent.putExtra("featureId", "NotDefinedYet");
                intent.putExtra("featureName", "Account");
                intent.putExtra("featureData", mConfigArray.toString());
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                mContext.sendBroadcast(intent);

                Log.v(TAG,"broadcasted com.dashwire.feature.intent.CONFIGURE : " + mConfigArray.toString());

            } catch (Exception e)
            {
                mConfigurationEvent.notifyEvent(name(), ConfigurationEvent.FAILED);
                return;
            }
        }
    }
}
