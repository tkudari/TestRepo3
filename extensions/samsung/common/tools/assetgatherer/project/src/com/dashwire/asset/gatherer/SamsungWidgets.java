package com.dashwire.asset.gatherer;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.dashwire.asset.AssetGatherer;

public class SamsungWidgets extends AssetGatherer {
    private static final String TAG = SamsungWidgets.class.getCanonicalName();

	public SamsungWidgets(JSONObject config,File outputDir)
	{
		super(config,outputDir);
		this.assetName = "samsung_widgets";
	}
	@Override
	protected JSONArray getAssets(Context context) {
		Log.d(TAG, "FIND Samsung Widgets------------------");
        JSONArray array = new JSONArray();
        Intent intent = new Intent( "com.samsung.sec.android.SAMSUNG_APP_WIDGET_ACTION", null );
        intent.addCategory( "com.samsung.sec.android.SAMSUNG_APP_WIDGET" );

        PackageManager packageManager = context.getPackageManager();

        List<ResolveInfo> ris = packageManager.queryIntentActivities( intent, PackageManager.GET_META_DATA );
        Collections.sort( ris, new ResolveInfo.DisplayNameComparator( packageManager ) );

        try {

            for ( ResolveInfo ri : ris ) {
                Resources packageResource;

                packageResource = context.getPackageManager().getResourcesForApplication( ri.activityInfo.packageName );
                Log.v( TAG, "package name = " + ri.activityInfo.packageName );
                ActivityInfo ai = ri.activityInfo;

                if ( hasMetadata( ai ) ) {
                    Log.v( TAG, "metaData keyset = " + ri.activityInfo.metaData.keySet().size() );
                    Log.v( TAG, "metaData keyset = " + ri.activityInfo.metaData.keySet().toString() );

                    for ( String metaDataKeySet : ri.activityInfo.metaData.keySet() ) {
                        JSONObject object = new JSONObject();
                        object.put( "category", "Widgets" );
                        object.put( "type", "tw" );
                        object.put( "src", "/widgets/" );
                        String theme = "";
                        Log.v( TAG, "id = " + ri.activityInfo.packageName + "/" + ri.activityInfo.name );
                        XmlResourceParser metaDataParser = ri.activityInfo.loadXmlMetaData( context.getPackageManager(), metaDataKeySet );
                        Log.v( TAG, "metaDataParser = " + metaDataParser.getDepth() );
                        while ( metaDataParser.next() != XmlPullParser.END_DOCUMENT ) {
                            if ( "metadata".equals( metaDataParser.getName() ) ) {
                                if ( metaDataParser.getAttributeCount() != -1 ) {
                                    for ( int i = 0; i < metaDataParser.getAttributeCount(); i++ ) {
                                        if ( "description".equals( metaDataParser.getAttributeName( i ) ) ) {
                                            String descriptionReference = metaDataParser.getAttributeValue( i ).replace( "@", "" );
                                            int description_iden = packageResource.getIdentifier( ri.activityInfo.packageName + ":" + descriptionReference,
                                                    "string", ri.activityInfo.packageName );
                                            String descriptionString = packageResource.getString( description_iden );
                                            object.put( "title", descriptionString );
                                        }
                                        if ( "width".equals( metaDataParser.getAttributeName( i ) ) ) {
                                            String widthReference = metaDataParser.getAttributeValue( i ).replace( "@", "" );
                                            int width_iden = packageResource.getIdentifier( ri.activityInfo.packageName + ":" + widthReference, "dimen",
                                                    ri.activityInfo.packageName );
                                            String widthString = Float.valueOf( packageResource.getDimension( width_iden ) ).toString();
                                            object.put( "cols", widthString);
                                        }
                                        if ( "height".equals( metaDataParser.getAttributeName( i ) ) ) {
                                            String heightReference = metaDataParser.getAttributeValue( i ).replace( "@", "" );
                                            int height_iden = packageResource.getIdentifier( ri.activityInfo.packageName + ":" + heightReference, "dimen",
                                                    ri.activityInfo.packageName );
                                            String heightString = Float.valueOf( packageResource.getDimension( height_iden ) ).toString();
                                            object.put( "rows", heightString);
                                        }
                                        if ("themeName".equals(metaDataParser.getAttributeName(i)))
                                        {
                                            theme = ";S.com.samsung.sec.android.SAMSUNG_WIDGET.themename="+metaDataParser.getAttributeValue( i );
                                        }
                                    }
                                }
                            }
                        }
                        
                        object.put( "id", ri.activityInfo.packageName + "/" + ri.activityInfo.name + theme);
                        array.put( object );
                    }

                } else {
                     JSONObject object = new JSONObject();
                     object.put( "category", "Widgets" );
                     object.put( "type", "tw" );

                     object.put( "id", ri.activityInfo.packageName + "/" +
                     ri.activityInfo.name );
                     object.put("title", ai.loadLabel(packageManager));
                     object.put( "src", "/widgets/" );
                     if(packageResource.getIdentifier(ri.activityInfo.packageName+":"+"string/max_width",null,null)>0)
                     {
                         object.put( "cols", Float.valueOf( packageResource.getDimension( packageResource.getIdentifier(ri.activityInfo.packageName+":"+"string/max_width",null,null) ) ).toString());
                         object.put( "rows",  Float.valueOf( packageResource.getDimension( packageResource.getIdentifier(ri.activityInfo.packageName+":"+"string/max_height",null,null) ) ).toString());
                     }
                     array.put(object);
                }
            }
        } catch ( NameNotFoundException e ) {
            Log.e(TAG, "Error", e);
        } catch ( JSONException e ) {
            Log.e(TAG, "Error", e);
        } catch ( NumberFormatException e ) {
            Log.e(TAG, "Error", e);
        } catch ( NotFoundException e ) {
            Log.e(TAG, "Error", e);
        } catch ( XmlPullParserException e ) {
            Log.e(TAG, "Error", e);
        } catch ( IOException e ) {
            Log.e(TAG, "Error", e);
        }

        List<PackageInfo> pis = context.getPackageManager().getInstalledPackages(
                PackageManager.GET_RECEIVERS | PackageManager.GET_META_DATA | PackageManager.GET_ACTIVITIES );

        for ( PackageInfo pi : pis ) {
            if ( pi.activities != null ) {
                for ( ActivityInfo aai : pi.activities ) {
                    if ( !hasMetadata( aai ) ) {
                        continue;
                    }

                    for ( String metaDataKeySet : aai.metaData.keySet() ) {
                        try {
                            if ( metaDataKeySet.contains( ".SAMSUNG_WIDGET" ) ) {
                                Resources packageResource = context.getPackageManager().getResourcesForApplication( aai.packageName );
                                JSONObject object = new JSONObject();
                                object.put( "category", "Widgets" );
                                object.put( "type", "tw" );
                                object.put( "id", aai.packageName + "/" + aai.name );
                                object.put( "src", "/widgets/" );
                                XmlResourceParser metaDataParser = aai.loadXmlMetaData( context.getPackageManager(), metaDataKeySet );
                                while ( metaDataParser.next() != XmlPullParser.END_DOCUMENT ) {
                                    if ( "metadata".equals( metaDataParser.getName() ) ) {
                                        if ( metaDataParser.getAttributeCount() != -1 ) {
                                            for ( int i = 0; i < metaDataParser.getAttributeCount(); i++ ) {
                                                if ( "description".equals( metaDataParser.getAttributeName( i ) ) ) {
                                                    String descriptionReference = metaDataParser.getAttributeValue( i ).replace( "@", "" );
                                                    int description_iden = packageResource.getIdentifier( aai.packageName + ":" + descriptionReference,
                                                            "string", aai.packageName );
                                                    String descriptionString = packageResource.getString( description_iden );
                                                    object.put( "title", descriptionString );
                                                }
                                                if ( "width".equals( metaDataParser.getAttributeName( i ) ) ) {
                                                    String widthReference = metaDataParser.getAttributeValue( i ).replace( "@", "" );
                                                    int width_iden = packageResource.getIdentifier( aai.packageName + ":" + widthReference, "dimen",
                                                            aai.packageName );
                                                    String widthString = Float.valueOf( packageResource.getDimension( width_iden ) ).toString();
                                                    object.put( "cols", widthString);//getCellFromDipColumn( Float.parseFloat( widthString ) ) );
                                                }
                                                if ( "height".equals( metaDataParser.getAttributeName( i ) ) ) {
                                                    String heightReference = metaDataParser.getAttributeValue( i ).replace( "@", "" );
                                                    int height_iden = packageResource.getIdentifier( aai.packageName + ":" + heightReference, "dimen",
                                                            aai.packageName );
                                                    String heightString = Float.valueOf( packageResource.getDimension( height_iden ) ).toString();
                                                    object.put( "rows", heightString );// getCellFromDipRow( Float.parseFloat( heightString ) ) );
                                                }
                                            }
                                        }
                                    }
                                }
                                array.put( object );
                            }
                        } catch ( Exception e ) {
                            Log.e(TAG, "Error", e);
                        }

                    }// end for metadata

                }
            }

        }// end for package
        return array;
	}
}
