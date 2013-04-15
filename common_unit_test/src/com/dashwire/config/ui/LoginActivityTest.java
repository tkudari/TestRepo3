package com.dashwire.config.ui;

import java.lang.Exception;
import java.util.ArrayList;


import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import com.dashwire.config.configuration.ConfigurationEvent;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.*;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;



@RunWith(RobolectricTestRunner.class)
public class LoginActivityTest {
	@Test
	public void testLoginTaskOnPostExecute() throws Exception {
		LoginActivity activity = new LoginActivity();
		LoginActivity.LoginTask loginTask = new LoginActivity.LoginTask(activity, "abcdefg");

		String json_str = "{\"http_status_code\":200, \"config_id\":\"my_config_id\"}";

		JSONObject object = new JSONObject(json_str);

		loginTask.onPostExecute(object);

		SharedPreferences sharedPreferences = ShadowPreferenceManager.getDefaultSharedPreferences(Robolectric.application.getApplicationContext());
		String keyVal = sharedPreferences.getString("key", "dummyvalue");
		String configId = sharedPreferences.getString("config_id", "dummyvalue");

		Assert.assertEquals("abcdefg", keyVal);
		Assert.assertEquals("my_config_id", configId);
		Assert.assertTrue(activity.isFinishing());
		Intent nextStartedActivity = Robolectric.getShadowApplication().getNextStartedActivity();

		Assert.assertEquals("com.dashwire.config.ui.ConfigurationActivity", nextStartedActivity.getComponent().getClassName());
	}
}
