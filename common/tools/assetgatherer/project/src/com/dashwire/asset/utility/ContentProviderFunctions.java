package com.dashwire.asset.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;

public class ContentProviderFunctions {
	private static String TAG = ContentProviderFunctions.class.getCanonicalName();
	public static Object getColumn( Cursor cursor, String name ) {
        int columnIndex = cursor.getColumnIndex(name);
        if(columnIndex >= 0)
            return getColumn(cursor,cursor.getColumnIndex(name));
        else
            Log.v(TAG,"Column not found "+ name +" at index " + columnIndex);
        return "";
    }
	public static  void dumpContentResolver( Context context, Uri uri ) {
        Log.v( TAG, "dumpContentResolver: " + uri );
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query( uri, null, null, null, null );
        if ( cursor != null ) {
            int row = 0;
            Log.v( TAG, "cursor count = " + cursor.getCount() );
            while ( cursor.moveToNext() ) {
                row++;
                // DatabaseUtils.dumpCurrentRow(cursor);
                if ( row == 1 ) {
                    dumpRowColumnNameCSVFormat( cursor );
                }
                // Log.v(TAG,"workspace id = "
                // +getColumn(cursor,17).toString());
                dumpRowColumnValueCSVFormat( cursor );
            }
        } else {
            Log.v( TAG, uri + " null cursor" );
        }
    }

	public static  void dumpRowColumnNameCSVFormat( Cursor cursor ) {
        String names[] = cursor.getColumnNames();
        StringBuffer buffer = new StringBuffer();
        buffer.append( "c," );
        for ( int index = 0; index < names.length; index++ ) {
            buffer.append( names[ index ] );
            buffer.append( "," );
        }
        Log.v( TAG, buffer.toString() );
    }

	public static  void dumpRowColumnValueCSVFormat( Cursor cursor ) {
        String names[] = cursor.getColumnNames();
        StringBuffer buffer = new StringBuffer();
        buffer.append( "r," );
        for ( int index = 0; index < names.length; index++ ) {
            Object column = getColumn( cursor, index );
            if ( column instanceof String ) {
                buffer.append( "\"" );
                buffer.append( column );
                buffer.append( "\"" );
            } else {
                buffer.append( column );
            }
            buffer.append( "," );
        }
        Log.v(TAG, buffer.toString());
    }

	public static  Object getColumn( Cursor cursor, int index ) {
        try {
            String stringValue = cursor.getString( index );
            Long longValue = cursor.getLong( index );
            try {
                if ( Long.parseLong( stringValue ) == longValue ) {
                    return longValue;
                }
            } catch ( NumberFormatException nfe ) {
            }
            return stringValue;
        } catch ( SQLiteException sqle ) {
            byte[] blobValue = cursor.getBlob( index );
            return blobValue.toString();
        }

    }
	
}
