package com.dashwire.config.resources;

import android.content.ContentProviderClient;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import com.dashwire.base.debug.DashLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * User: tbostelmann
 * Date: 1/11/13
 */
public class FeedUtilitiesImpl implements FeedUtilities {
    private static final String TAG = FeedUtilitiesImpl.class.getCanonicalName();

    /**
     * Looks up the current edition id.
     * @param context
     * @return Returns the existing edition id or null if it isn't set
     */
    public String getFeedCurEditionId( Context context ) {
        DashLogger.d(TAG, "getFeedCurEditionId() ");

        String CONTENT_AUTHORITY = "com.htc.plugin.news";
        Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        String PATH_EDITION = "edition";
        Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EDITION).build();
        String COLUMN_EID_INT = BaseColumns._ID;
        String COLUMN_NAME_STR = "edition_name";
        String COLUMN_ORDER_INT = "edition_order";
        String COLUMN_CURRENT_INT = "edition_current";
        String COLUMN_VERSION_INT = "edition_version";


        Uri.Builder builder = CONTENT_URI.buildUpon();
        builder.appendQueryParameter("current_edition", "true");
        Uri uri = builder.build();

        ContentProviderClient providerClient = context.getContentResolver().acquireUnstableContentProviderClient( uri );
        Cursor cursor = null;
        try {
            cursor = providerClient.query( uri, new String[] {
                    COLUMN_EID_INT,
                    COLUMN_CURRENT_INT }, null, null, null );
            if ( cursor != null && cursor.moveToNext() ) {
                if (cursor.getInt(1) == 1) {
                    String eid = cursor.getString(0);
                    DashLogger.d(TAG, "Found eid: " + eid);
                    return eid;
                }
            }
        } catch ( RemoteException e ) {
            DashLogger.e(TAG, "get content provider client failed", e);
        } finally {
            providerClient.release();
            if( cursor != null )
                cursor.close();
        }

        return null;
    }

    public List<String> getFeedCurTagIds( Context context ) throws IllegalArgumentException {
        DashLogger.d(TAG, "getFeedCurTagIds() ");

        String CONTENT_AUTHORITY = "com.htc.plugin.news";
        Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        String PATH_TAG = "tag";
        Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TAG).build();
        String COLUMN_EID_INT = BaseColumns._ID;

        Uri.Builder builder = CONTENT_URI.buildUpon();
        builder.appendQueryParameter("current_edition", "true");
        Uri uri = builder.build();

        ContentProviderClient providerClient = context.getContentResolver().acquireUnstableContentProviderClient( uri );
        Cursor cursor = null;
        try {
            cursor = providerClient.query( uri, new String[] {
                    COLUMN_EID_INT }, null, null, null );
            List<String> tags = new ArrayList<String>();
            while (cursor.moveToNext()) {
                tags.add(cursor.getString(0));
            }
            return tags;
        } catch ( RemoteException e ) {
            DashLogger.d(TAG, "get content provider client failed");
            e.printStackTrace();
        } finally {
            providerClient.release();
            if( cursor != null )
                cursor.close();
        }

        throw new IllegalArgumentException( uri.toString() );
    }
}
