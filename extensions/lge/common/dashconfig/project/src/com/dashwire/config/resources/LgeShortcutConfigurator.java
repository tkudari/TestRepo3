package com.dashwire.config.resources;

import com.dashwire.base.debug.DashLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.dashwire.config.configuration.ConfigurationEvent;
import com.dashwire.config.configuration.ResourceConfigurator;
import com.dashwire.config.util.CommonUtils;

public class LgeShortcutConfigurator implements ResourceConfigurator
{

    public static final String TAG = LgeShortcutConfigurator.class.getCanonicalName();

    protected Context mContext;

    protected ConfigurationEvent mConfigurationEvent;

    protected ContentResolver mContentResolver;

    private BroadcastReceiver mWidgetResultReceiver = null;

    protected JSONArray mConfigArray;

    protected boolean hasFailure = false;

    public LgeShortcutConfigurator() {
    }

    public void setContext( Context context )
    {
        mContext = context;
    }

    public void setConfigurationEvent( ConfigurationEvent configurationEvent )
    {
        mConfigurationEvent = configurationEvent;
    }

    public void setConfigDetails( JSONArray configArray )
    {
        mConfigArray = configArray;
    }

    public String name()
    {
        return "shortcuts";
    }


	public static class HomescreenResponseReceiver extends BroadcastReceiver {

		boolean hasFailure = false;
		ConfigurationEvent mConfigurationEvent = null;
		Context mContext = null;

		String mName = null;

		public HomescreenResponseReceiver(ConfigurationEvent event, Context context, String name) {
			mConfigurationEvent = event;
			mContext = context;
			mName = name;
		}



		@Override
		public void onReceive( Context widgetResultContext, Intent widgetResultIntent )
		{
			DashLogger.v( TAG, "Homescreen result receiver called." );
			DashLogger.v( TAG, "Homescreen result action = " + widgetResultIntent.getAction() );
			DashLogger.v( TAG, "Homescreen result boolean = " + widgetResultIntent.getBooleanExtra( "success", false ) );
			DashLogger.v( TAG, "Homescreen result json blob = " + widgetResultIntent.getStringExtra( "homescreen" ) );
			DashLogger.v(TAG, "Homescreen result json blob length = " + (widgetResultIntent.getStringExtra("homescreen")).length());

			String jsonString = widgetResultIntent.getStringExtra( "homescreen" );

			if ( jsonString != null )
			{
				try
				{
					JSONArray widgetResultJSONArray = new JSONArray( jsonString );
					for ( int i = 0; i < widgetResultJSONArray.length(); i++ )
					{
						JSONObject item = widgetResultJSONArray.getJSONObject( i );
						DashLogger.d( TAG, "Homscreen Result JSON Item = " + item.toString() );
						String packageName = "", className = "", errorMessage = "";
						int errorCode = 0;
						if ( item.has( "packageName" ) )
							packageName = item.getString( "packageName" );
						if ( item.has( "className" ) )
							className = item.getString( "className" );
						if ( item.has( "errorCode" ) )
							errorCode = item.getInt( "errorCode" );
						if ( item.has( "errorMessage" ) )
							errorMessage = item.getString( "errorMessage" );
						if ( item.getInt( "success" ) != 0 )
						{
							hasFailure = true;
							DashLogger.d( TAG, "Widget/Shortcut Item success is not 0" );
							DashLogger.d( TAG, "Package Name: " + packageName );
							DashLogger.d( TAG, "Class Name: " + className );
							DashLogger.d( TAG, "Error Message: " + errorMessage );
							DashLogger.d( TAG, "Error Code: " + errorCode );
							DashLogger.d( TAG, "Item success = " + item.getInt( "success" ) );
						}
					}
				} catch ( JSONException jse )
				{
					hasFailure = true;
					DashLogger.d( TAG, "JSON Exception processing widget results: " + jse.getMessage() );
				}
			} else
			{
				DashLogger.d( TAG, "Response homescreen object is null." );
				hasFailure = true;
			}

			mConfigurationEvent.notifyEvent( mName, hasFailure ? ConfigurationEvent.FAILED : ConfigurationEvent.CHECKED );
			mContext.unregisterReceiver( this );
		}
	}


    public void configure()
    {
        if ( mConfigArray != null && mConfigArray.length() > 0 )
        {
			mWidgetResultReceiver = new HomescreenResponseReceiver(mConfigurationEvent, mContext.getApplicationContext(), name());

            IntentFilter resultIntent = new IntentFilter( "com.dashwire.homescreen.CONFIGURE_HOMESCREEN_RESULT" );

			// register the receiver in the main application context b/c it will not be going away until application closes
			// The IntentService application context will disappear as soon as work is completed.
            mContext.getApplicationContext().registerReceiver( mWidgetResultReceiver, resultIntent );

            String configForAPI = CommonUtils.buildWidgetsAndShortcutsConfigForOEMAPI( mConfigArray );

            Intent homescreenIntent = new Intent();
            homescreenIntent.setAction( "com.dashwire.homescreen.CONFIGURE_HOMESCREEN" );
            homescreenIntent.putExtra( "version", "1.0" );
            homescreenIntent.putExtra( "homescreen", configForAPI );
			DashLogger.v( TAG, "Broadcasting Homescreen Intent" );
            DashLogger.v( TAG, "Homescreen Action = " + homescreenIntent.getAction() );
            DashLogger.v( TAG, "Homescreen version = " + homescreenIntent.getStringExtra( "version" ) );
            DashLogger.v( TAG, "Homescreen json blob = " + homescreenIntent.getStringExtra( "homescreen" ) );
            DashLogger.v( TAG, "Homescreen json blob lenght = " + ( homescreenIntent.getStringExtra( "homescreen" ) ).length() );
            mContext.sendBroadcast( homescreenIntent, "com.dashwire.homescreen.permission.CONFIGURE_HOMESCREEN" );

        } else
        {
            DashLogger.d( TAG, "Empty array in Shortcut configuration" );
            mConfigurationEvent.notifyEvent( name(), ConfigurationEvent.FAILED, null );
        }
    }
}