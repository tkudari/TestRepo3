package com.dashwire.config.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

public class BaseActivity extends Activity {

    @SuppressWarnings("unused")
    private static final String TAG = BaseActivity.class.getCanonicalName();
    protected Context context = this;
    @SuppressWarnings("unused")
    private Button quitButton;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        
        //enableStrictMode();
        
    }
    
    @SuppressWarnings( "unused" )
    private void enableStrictMode() {
        try {
            Class<?> strictMode = Class.forName( "android.os.StrictMode" );
            Method method = strictMode.getMethod( "enableDefaults" );
            method.invoke( ( Object[] ) null, ( Object[] ) null );
        } catch ( SecurityException e ) {
            e.printStackTrace();
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
        } catch ( IllegalArgumentException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        }
    }
    
}
