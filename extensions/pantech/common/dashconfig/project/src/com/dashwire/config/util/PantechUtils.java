package com.dashwire.config.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PantechUtils
{

    protected static final String TAG = PantechUtils.class.getCanonicalName();

    public static void setChangePantechModeFlag( boolean flag, Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean( "CHANGE_MODE", flag );
        editor.commit();
    }
    
    public static boolean isChangePantechModeFlag( Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getBoolean( "CHANGE_MODE", false );
    }
    
    public static void setEasyModeFlag( boolean flag, Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean( "SET_EASY_MODE", flag );
        editor.commit();
    }
    
    public static boolean getEasyModeFlag( Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getBoolean( "SET_EASY_MODE", false );
    }
    
    public static void setStandardModeFlag( boolean flag, Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean( "SET_STANDARD_MODE", flag );
        editor.commit();
    }
    
    public static boolean getStandardModeFlag( Context context )
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        return settings.getBoolean( "SET_STANDARD_MODE", false );
    }

}
