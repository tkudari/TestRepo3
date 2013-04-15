//package com.dashwire.config.integration;
//
//import android.app.WallpaperManager;
//import android.content.ComponentName;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.content.pm.*;
//import android.content.res.Resources;
//import android.net.Uri;
//import android.nfc.Tag;
//import android.provider.MediaStore;
//import android.test.AndroidTestCase;
//import android.util.Printer;
//import com.dashwire.config.RestClient;
//import com.dashwire.base.debug.DashLogger;
//import com.dashwire.config.resources.RingtoneConfigurator;
//import com.dashwire.config.util.CommonTestUtils;
//import junit.framework.Assert;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.*;
//import java.lang.reflect.Method;
//import java.util.List;
//import java.util.Scanner;
//
///**
// * Author: tbostelmann
// */
//public class Assets extends AndroidTestCase {
//    private static final String TAG = Assets.class.getCanonicalName();
//    private static JSONObject configJson;
//    private boolean failed = false;
//
//    public void setUp() throws Exception {
//        NetworkStatusAndroid networkStatusAndroid = new NetworkStatusAndroid(getContext());
//        Assert.assertTrue(
//                "Need a data connection for these tests to run",
//                networkStatusAndroid.isNetworkAvailable());
//
//        if (configJson == null) {
//            String url = CommonTestUtils.getProp("asset.catalog.url");
//            DashLogger.i(TAG, "asset.catalog.url: " + url);
//            if (url.startsWith("http")) {
//                configJson = RestClient.get(url, getContext());
//            }
//            else {
//                File filesDir = getContext().getFilesDir();
//                Scanner scanner = new Scanner(new File(filesDir, url));
//                StringBuffer fileContents = new StringBuffer();
//                while (scanner.hasNextLine()) {
//                    fileContents.append(scanner.nextLine());
//                }
//                scanner.close();
//                configJson = new JSONObject(fileContents.toString());
//            }
//            DashLogger.d(TAG, "asset catalog: " + configJson);
//            Assert.assertNotNull(configJson);
//        }
//    }
//
//    public void tearDown() throws Exception {
//        Assert.assertFalse("Detected failures", failed);
//    }
//
//    public void testSounds() throws Exception {
//        RingtoneConfigurator ringtoneConfigurator = new RingtoneConfigurator();
//        ringtoneConfigurator.setContext(getContext());
//
//        JSONArray soundsJson = (JSONArray)configJson.get("notifications");
//        JSONObject soundJson;
//        for (int i = 0; i < soundsJson.length(); i++) {
//            soundJson = (JSONObject) soundsJson.get(i);
//            validateSound(soundJson, ringtoneConfigurator);
//        }
//        soundsJson = (JSONArray)configJson.get("ringtones");
//        for (int i = 0; i < soundsJson.length(); i++) {
//            soundJson = (JSONObject) soundsJson.get(i);
//            validateSound(soundJson, ringtoneConfigurator);
//        }
//    }
//
//    public void testWallpapers() throws Exception {
//        JSONArray jsonObjects = (JSONArray)configJson.get("wallpapers");
//        Assert.assertNotNull(jsonObjects);
//        JSONObject jsonObject;
//        for (int i = 0; i < jsonObjects.length(); i++) {
//            jsonObject = (JSONObject) jsonObjects.get(i);
//            validateWallpaper(jsonObject);
//        }
//    }
//
//    public void testShortcuts() throws Exception {
//        JSONArray jsonArray = (JSONArray)configJson.get("shortcuts");
//        Assert.assertNotNull(jsonArray);
//        JSONObject jsonObject;
//        ComponentName componentName;
//        String id;
//        String[] tokens;
//        Boolean[] isSetup = new Boolean[jsonArray.length()];
//        for (int i=0; i<jsonArray.length(); i++) {
//            jsonObject = (JSONObject) jsonArray.get(i);
//            id = (String)jsonObject.get("id");
//            tokens = id.split("/");
//            DashLogger.i(TAG, "validateShortcut(" + tokens[0] + ", " + tokens[1] + ")");
//            componentName = new ComponentName(tokens[0], tokens[1]);
//            isSetup[i] = isPackageExists(componentName);
//            if (!isSetup[i]) {
//                failed = true;
//                DashLogger.e(TAG, "isSetup = FALSE");
//            }
//            else {
//                DashLogger.i(TAG, "isSetup = TRUE");
//            }
//        }
//    }
//
//    public void testWidgets() throws Exception {
//        JSONArray jsonArray = (JSONArray)configJson.get("widgets");
//        Assert.assertNotNull(jsonArray);
//        JSONObject jsonObject;
//        ComponentName componentName;
//        String id;
//        String[] tokens;
//        Boolean[] isSetup = new Boolean[jsonArray.length()];
//        for (int i=0; i<jsonArray.length(); i++) {
//            jsonObject = (JSONObject) jsonArray.get(i);
//            id = (String)jsonObject.get("id");
//            tokens = id.split("/");
//            DashLogger.i(TAG, "validateShortcut(" + tokens[0] + ", " + tokens[1] + ")");
//            componentName = new ComponentName(tokens[0], tokens[1]);
//            isSetup[i] = isPackageExists(componentName);
//            if (!isSetup[i]) {
//                failed = true;
//                DashLogger.e(TAG, "isSetup = FALSE");
//            }
//            else {
//                DashLogger.i(TAG, "isSetup = TRUE");
//            }
//        }
//    }
//
//    public boolean isPackageExists(ComponentName componentName) {
//        PackageManager pm=getContext().getPackageManager();
//        PackageInfo packageInfo;
//        String className = componentName.getClassName();
//        DashLogger.d(TAG, "Looking for: " + className);
//        try {
//            packageInfo = pm.getPackageInfo(componentName.getPackageName(),
//                PackageManager.GET_ACTIVITIES | PackageManager.GET_CONFIGURATIONS |
//                PackageManager.GET_INSTRUMENTATION | PackageManager.GET_PROVIDERS |
//                PackageManager.GET_RECEIVERS | PackageManager.GET_SERVICES);
//            if (packageInfo != null) {
//                if (packageInfo.receivers != null)
//                    for (int i=0; i<packageInfo.receivers.length; i++) {
//                        if (className.equals(packageInfo.receivers[i].name))
//                            return true;
////                        DashLogger.i(TAG, "packageInfo.receivers[" + packageInfo.receivers[i].name + "]: " + packageInfo.receivers[i].targetActivity);
//                    }
//                if (packageInfo.providers != null)
//                    for (int i=0; i<packageInfo.providers.length; i++) {
//                        if (className.equals(packageInfo.providers[i].name))
//                            return true;
////                        DashLogger.i(TAG, "packageInfo.providers[" + packageInfo.providers[i].name + "]: " + packageInfo.providers[i].toString());
//                    }
//                if (packageInfo.activities != null)
//                    for (int i=0; i<packageInfo.activities.length; i++) {
//                        if (className.equals(packageInfo.activities[i].name))
//                            return true;
////                        DashLogger.i(TAG, "packageInfo.activities[" + packageInfo.activities[i].name + "]: " + packageInfo.activities[i].toString());
//                    }
//                if (packageInfo.services != null)
//                    for (int i=0; i<packageInfo.services.length; i++) {
//                        if (className.equals(packageInfo.services[i].name))
//                            return true;
////                        DashLogger.i(TAG, "packageInfo.services[" + packageInfo.services[i].name + "]: " + packageInfo.services[i].toString());
//                    }
//            }
////            PackageInfo info=pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
//        } catch (PackageManager.NameNotFoundException e) {
//            DashLogger.e(TAG, "Package does not exist: " + componentName.flattenToString(), e);
//            return false;
//        }
//        return false;
//    }
//
//    private void validateSound(JSONObject soundJson, RingtoneConfigurator ringtoneConfigurator) {
//        String uriString = null;
//        String title = null;
//        try {
//            uriString = soundJson.get( "uri" ).toString();
//            title = soundJson.get("title").toString();
//        } catch (JSONException e) {
//            failed = true;
//            DashLogger.e(TAG, "Sound does not exist: " + uriString, e);
//        }
//        DashLogger.i(TAG, "validateSound(" + title + ", " + uriString + ")");
//        Uri uri = Uri.parse( uriString );
//
//        if (ContentResolver.SCHEME_CONTENT.equals( uri.getScheme() ))
//            DashLogger.w(TAG, "ContentResolver.SCHEME_CONTENT=" + uri.getScheme());
//        uri = ringtoneConfigurator.getAudioContentUri( uri );
//        if (uri == null) {
//            failed = true;
//            DashLogger.e(TAG, "Sound does not exist: " + uriString);
//        }
//        else {
//            InputStream in = null;
//            try {
//                in = getContext().getContentResolver().openInputStream( uri );
//                if (in.read() == -1) {
//                    failed = true;
//                    DashLogger.e(TAG, "Sound Does not exist: " + uri);
//                }
//                in.close();
//            } catch (IOException e) {
//                failed = true;
//                DashLogger.e(TAG, "Sound Does not exist: " + uri, e);
//            }
//        }
//    }
//
//    private void validateWallpaper(JSONObject jsonObject) throws JSONException {
//        String uriString = jsonObject.get( "uri" ).toString();
//        DashLogger.i(TAG, "validateWallpaper(" + uriString + ")");
//        Uri uri = Uri.parse( uriString );
//        String scheme = uri.getScheme();
//        if ( scheme.equals( "file" ) ) {
//            InputStream in = null;
//            try {
//                in = getContext().getContentResolver().openInputStream( uri );
//                if (in.read() == -1) {
//                    failed = true;
//                    DashLogger.e(TAG, "URI Does not exist: " + uri);
//                }
//                in.close();
//            } catch (IOException e) {
//                DashLogger.e(TAG, "URI Does not exist: " + uri, e);
//            }
//        } else if ( scheme.equals( "android.resource" ) ) {
//            String packageName = uri.getHost();
//            List<String> pathSegments = uri.getPathSegments();
//            String resourceType = pathSegments.get( 0 );
//            String resourceName = pathSegments.get( 1 );
//            PackageManager packageManager = getContext().getPackageManager();
//
//            try {
//                Context appContext = getContext().createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
//                final Resources resources = packageManager.getResourcesForApplication( packageName );
//                int res = resources.getIdentifier( resourceName, resourceType, packageName );
//                if (res <= 0) {
//                    failed = true;
//                }
//            } catch (PackageManager.NameNotFoundException e) {
//                DashLogger.e(TAG, "Wallpaper does not exist: " + uriString, e);
//            }
//        }
//        else {
//            failed = true;
//            DashLogger.e(TAG, "Wallpaper does not exist (unknown scheme: " + scheme + "):" + uriString);
//        }
//    }
//
//    private JSONObject generateConfigJSONObject(JSONObject shortcutJson) throws JSONException {
//        JSONArray shortcutsJson = new JSONArray();
//        shortcutsJson.put(shortcutJson);
//        return generateConfigJSONObject(null, shortcutsJson);
//    }
//
//    private JSONObject generateConfigJSONObject(JSONObject wallpaperJson, JSONArray shortcutsJson) throws JSONException {
//        JSONObject jsonObject = new JSONObject();
//
//        JSONObject configJson = new JSONObject();
//        jsonObject.put("config", configJson);
//
//        JSONArray jsonWallpapersArray = new JSONArray();
//        if (wallpaperJson != null) {
//            JSONObject wallpaperJsonObject = new JSONObject();
//            wallpaperJsonObject.put("uri", wallpaperJson.get("uri"));
//            wallpaperJsonObject.put("page", 0);
//            wallpaperJsonObject.put("src", wallpaperJson.get("src"));
//            jsonWallpapersArray.put(wallpaperJsonObject);
//        }
//        configJson.put("wallpapers", jsonWallpapersArray);
//
//        JSONArray jsonShortcutsArray = new JSONArray();
//        JSONObject shortcutJsonObject, jsonObject1;
//        for (int i=0; i<shortcutsJson.length(); i++) {
//            jsonObject1 = (JSONObject)shortcutsJson.get(i);
//            shortcutJsonObject = new JSONObject();
//            shortcutJsonObject.put("id", jsonObject1.get("id"));
//            shortcutJsonObject.put("screen", i);
//            shortcutJsonObject.put("category", jsonObject1.get("category"));
//            shortcutJsonObject.put("title", jsonObject1.get("title"));
//            shortcutJsonObject.put("cols", jsonObject1.get("cols"));
//            shortcutJsonObject.put("rows", jsonObject1.get("rows"));
//            shortcutJsonObject.put("type", jsonObject1.get("type"));
//            shortcutJsonObject.put("x", 0);
//            shortcutJsonObject.put("y", 0);
//            jsonShortcutsArray.put(jsonObject1);
//        }
//        jsonObject.put("shortcuts", jsonShortcutsArray);
//
//        jsonObject.put("model", "SGH-I577");
//        jsonObject.put("emails", new JSONArray());
//        jsonObject.put("resetHomeScreen", true);
//        jsonObject.put("ringtones", new JSONArray());
//
//        return jsonObject;
//    }
//}
