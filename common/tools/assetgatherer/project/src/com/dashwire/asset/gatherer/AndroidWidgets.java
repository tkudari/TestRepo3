package com.dashwire.asset.gatherer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.dashwire.asset.AssetGatherer;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class AndroidWidgets extends AssetGatherer {
    private static final String TAG = AndroidWidgets.class.getCanonicalName();

	public AndroidWidgets(JSONObject config,File outputDir)
	{
		super(config,outputDir);
		this.assetName = "widgets";
	}
	@Override
	protected JSONArray getAssets(Context context) {
		Log.d(TAG, "FIND Android Widgets------------------");
        
        //JSONArray array = new JSONArray();
        ArrayList<JSONObject> array = new ArrayList<JSONObject>();
        Log.d(TAG, "---- FIND package list ----");
        
        List<PackageInfo> pis = context.getPackageManager().getInstalledPackages(0 );
        Log.d(TAG, "---- DONE FINDING package list ----");
        
        File outputDirectory = new File( outputDir, "widgets" );
        if ( !outputDirectory.exists() ) {
            outputDirectory.mkdirs();
        }
        
        
        
        for ( PackageInfo pi : pis ) {
            try {
                pi = context.getPackageManager().getPackageInfo( pi.packageName, PackageManager.GET_RECEIVERS | PackageManager.GET_META_DATA | PackageManager.GET_ACTIVITIES );
            } catch ( NameNotFoundException e ) {
                Log.e(TAG, "Error", e);
            }
            if ( pi.receivers != null ) {
                for ( ActivityInfo rai : pi.receivers ) {
                    if ( !hasMetadata( rai ) ) {
                        continue;
                    }

                    for ( String k : rai.metaData.keySet() ) {
                        try {
                            if ( "android.appwidget.provider".equals( k ) ) {
                                XmlResourceParser parser = rai.loadXmlMetaData( context.getPackageManager(), "android.appwidget.provider" );
                                Resources packageResource = context.getPackageManager().getResourcesForApplication( rai.packageName );
                                while ( parser.next() != XmlPullParser.END_DOCUMENT ) {

                                    if ( "appwidget-provider".equals( parser.getName() ) ) {
                                        JSONObject object = new JSONObject();
                                        object.put( "category", "Widgets" );
                                        object.put( "title", rai.loadLabel( context.getPackageManager() ) );
                                        object.put( "id", rai.packageName + "/" + rai.name );
                                        object.put( "src", "" );
                                        object.put( "type", "aw" );
                                        
                                        
                                        if ( parser.getAttributeCount() != -1 ) {
                                            array.add(object);
                                            for ( int i = 0; i < parser.getAttributeCount(); i++ ) {
                                                if ( "minWidth".equals( parser.getAttributeName( i ) ) ) {
                                                    String s = parser.getAttributeValue( i );
                                                    if ( s.contains( "@" ) ) {
                                                        String widthIdentifier = s.replace( "@", "" );
                                                        String widthString = Float.valueOf( packageResource.getDimension( Integer.parseInt( widthIdentifier ) ) )
                                                                .toString();
                                                        object.put( "cols", widthString);
                                                    } else {
                                                        object.put( "cols", s);
                                                    }
                                                }
                                                if ( "minHeight".equals( parser.getAttributeName( i ) ) ) {
                                                    String s = parser.getAttributeValue( i );
                                                    if ( s.contains( "@" ) ) {
                                                        String heightIdentifier = s.replace( "@", "" );
                                                        String heightString = Float.valueOf(
                                                                packageResource.getDimension( Integer.parseInt( heightIdentifier ) ) ).toString();
                                                        object.put( "rows", heightString);
                                                    } else {
                                                        object.put( "rows",s );
                                                    }
                                                }
                                                if ( "previewImage".equals( parser.getAttributeName( i ) ) ) {
                                                    String s = parser.getAttributeValue( i );
                                                    if ( s.contains( "@" ) ) {
                                                        String id = s.replace( "@", "" );
                                                        Bitmap bitmap = ( ( BitmapDrawable ) packageResource.getDrawable( Integer.parseInt( id )) ).getBitmap();
                                                        String name = (rai.packageName + "/" + rai.name ).replaceAll("[^\\w]+", "_") + ".png";
                                                        copyBitmapToFile( bitmap, new File( outputDirectory , "/"+ name ) );
                                                        object.put( "src", "/widgets/" + name );
                                                        
                                                    } 
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch ( Exception e ) {
                            Log.e(TAG, "Error", e);
                        }

                    }// end for metadata

                } // condition
            }// end for receiver
        }// end for package
        return createSortedArray((JSONObject[])array.toArray(), "title");
	}

    static JSONArray createSortedArray(JSONObject[] jsonObjects, final String attribute) {
        Arrays.sort(jsonObjects, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                try {
                    return ((String) lhs.get(attribute)).compareTo((String) rhs.get(attribute));
                } catch (JSONException e) {
                    throw new RuntimeException("Error sorting Widgets", e);
                }
            }
        });
        JSONArray jsonArray = new JSONArray();
        for (JSONObject jsonObject : jsonObjects)
            jsonArray.put(jsonObject);
        return jsonArray;
    }
}