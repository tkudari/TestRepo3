package com.dashwire.config;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.text.format.Time;
import com.dashwire.base.debug.DashLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DebugTools {
	private static final String TAG = DebugTools.class.getCanonicalName();
	
    public static void dumpShortcutsAndWidgetsValuesFromFavoritesDB( Context context) {
        dumpContentResolver( context, Uri.parse( DashconfigApplication.getDeviceContext().getStringConst(context, "FAVORITES_URI" ) ) );
    }
    
    public static void dumpGoogleSettings( Context context) {
        dumpContentResolver( context, Uri.parse(  DashconfigApplication.getDeviceContext().getStringConst(context, "GOOGLE_SETTINGS_PARTNER_URI" ) ) );
    }
	

	public static void dumpContentResolver(Context context, Uri uri) {
		DashLogger.v(TAG, "dumpContentResolver: " + uri);
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, null, null, null, null);
		if (cursor != null) {
			int row = 0;
			DashLogger.v(TAG,"cursor count = " + cursor.getCount());
			while (cursor.moveToNext()) {
				row++;
				DatabaseUtils.dumpCurrentRow(cursor);
				if (row == 1) {
					dumpRowColumnNameCSVFormat(cursor);
				}
				dumpRowColumnValueCSVFormat(cursor);
			}
		} else {
			DashLogger.v(TAG, uri + " null cursor");
		}
	}
	
    public static void dumpShortcutIcons( Context context) {
        Intent intent = new Intent( Intent.ACTION_MAIN, null );
        intent.addCategory( Intent.CATEGORY_LAUNCHER );

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities( intent, 0 );
        Collections.sort( list, new ResolveInfo.DisplayNameComparator( packageManager ) );
        final int listSize = list.size();
        try {
            JSONArray shortcuts = new JSONArray();
            for ( int i = 0; i < listSize; i++ ) {
                ResolveInfo resolveInfo = list.get( i );
                saveIcon( resolveInfo.activityInfo.packageName, context );
                String label = ( String ) resolveInfo.loadLabel( packageManager );
                JSONObject object = new JSONObject();
                object.put( "id", resolveInfo.activityInfo.packageName + "/" + resolveInfo.activityInfo.name.toString() );
                object.put( "title", label );
                object.put( "src", "http://media.dashwire.com.s3.amazonaws.com/dashconfig/captivate/icons/" + resolveInfo.activityInfo.packageName + ".png" );
                shortcuts.put( object );
                DashLogger.v( TAG, object.toString() );
            }
        } catch ( JSONException je ) {
        }
    }

	public static void createWidgetJSON(Context context, Uri uri) {
		DashLogger.v(TAG, "createWidgetJSON: " + uri);
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, null, null, null, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				DashLogger.v(TAG, "{");
				dumpWidgetRow(cursor);
				DashLogger.v(TAG, "},");
			}
		} else {
			DashLogger.v(TAG, uri + " null cursor");
		}
	}

	@SuppressWarnings("unused")
    private static void dumpRow(Cursor cursor) {
		String names[] = cursor.getColumnNames();
		for (int index = 0; index < names.length; index++) {
			Object column = getColumn(cursor, index);
			StringBuffer buffer = new StringBuffer();
			buffer.append("values.put( \"");
			buffer.append(names[index]);
			buffer.append("\", ");
			if (column instanceof String) {
				buffer.append("\"");
				buffer.append(column);
				buffer.append("\"");
			} else {
				buffer.append(column);
			}
			buffer.append(" );");
			DashLogger.v(TAG, buffer.toString());
		}
	}

	private static void dumpRowColumnNameCSVFormat(Cursor cursor) {
		String names[] = cursor.getColumnNames();
		StringBuffer buffer = new StringBuffer();
		buffer.append("c,");
		for (int index = 0; index < names.length; index++) {
			buffer.append(names[index]);
			buffer.append(",");
		}
		DashLogger.v(TAG, buffer.toString());
	}

	private static void dumpRowColumnValueCSVFormat(Cursor cursor) {
		String names[] = cursor.getColumnNames();
		StringBuffer buffer = new StringBuffer();
		buffer.append("r,");
		for (int index = 0; index < names.length; index++) {
			Object column = getColumn(cursor, index);
			if (column instanceof String) {
				buffer.append("\"");
				buffer.append(column);
				buffer.append("\"");
			} else {
				buffer.append(column);
			}
			buffer.append(",");
		}
		DashLogger.v(TAG, buffer.toString());
	}

	private static void dumpWidgetRow(Cursor cursor) {
		String names[] = cursor.getColumnNames();
		if (getColumn(cursor,1) == null)
		{
			DashLogger.v(TAG,"\"category\": \"Widgets\"");
		}
		for (int index = 0; index < names.length; index++) {
			Object column = getColumn(cursor, index);
			StringBuffer buffer = new StringBuffer();
			buffer.append("\"");
			if (names[index].equalsIgnoreCase("intent"))
			{
				buffer.append("id");
			}
			else
			{
				buffer.append(names[index]);
			}
			buffer.append("\": ");
			buffer.append("\"");
			buffer.append(column);
			buffer.append("\",");
			DashLogger.v(TAG, buffer.toString());
		}
	}

	private static Object getColumn(Cursor cursor, int index) {
		try {
			String stringValue = cursor.getString(index);
			Long longValue = cursor.getLong(index);
			try {
				if (Long.parseLong(stringValue) == longValue) {
					return longValue;
				}
			} catch (NumberFormatException nfe) {
			}
			return stringValue;
		} catch (SQLiteException sqle) {
			byte[] blobValue = cursor.getBlob(index);
			return blobValue.toString();
		}

	}

	@SuppressWarnings("unchecked")
	public static ContentValues jsonToContentValues(String json) {
		ContentValues values = new ContentValues();
		try {
			JSONObject jsonOjbect = new JSONObject(json);
			Iterator<String> iterator = jsonOjbect.keys();
			while (iterator.hasNext()) {
				String key = iterator.next();
				Object value = jsonOjbect.get(key);
				if (value instanceof String) {
					values.put(key, (String) value);
				}
				if (value instanceof Integer) {
					values.put(key, (Integer) value);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return values;
	}

	public static String timestamp() {
		Time now = new Time();
		now.setToNow();
		return now.format3339(false);
	}

    private static void saveIcon( String packageName, Context context ) {
        String path = getIconsPath() + "/" + packageName + ".png";
        try {
            BitmapDrawable icon = ( BitmapDrawable ) context.getPackageManager().getApplicationIcon( packageName );
            Bitmap bitmap = icon.getBitmap();
            FileOutputStream outputStream = new FileOutputStream( path );

            bitmap.compress( CompressFormat.PNG, 100, outputStream );

            outputStream.flush();
            outputStream.close();
        } catch ( NameNotFoundException e1 ) {
            e1.printStackTrace();
        } catch ( Exception e ) {
        }
    }
    
    private static String getIconsPath() {
        String path = getBasePath() + "/icons";
        File dir = new File( path );
        if ( !dir.exists() ) {
            dir.mkdirs();
        }
        return path;
    }
    

    private static String getBasePath() {
        File storage = Environment.getExternalStorageDirectory();
        if ( !storage.exists() ) {
            storage.mkdirs();
        }
        String basePath = storage.getAbsolutePath();
        return basePath;
    }
}