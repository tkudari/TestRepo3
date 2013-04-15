package com.dashwire.asset.gatherer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.dashwire.asset.AssetGatherer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class Shortcuts extends AssetGatherer {
	public Shortcuts(JSONObject config,File outputDir)
	{
		super(config,outputDir);
		this.assetName = "shortcuts";
		this.sortProp = "title";
	}
	@Override
	protected JSONArray getAssets(Context context) {
		Intent intent = new Intent( Intent.ACTION_MAIN, null );
        intent.addCategory( Intent.CATEGORY_LAUNCHER );

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities( intent, 0 );
        Collections.sort( list, new ResolveInfo.DisplayNameComparator( packageManager ) );

        File outputDirectory = new File( outputDir, assetName );
        if ( !outputDirectory.exists() ) {
            outputDirectory.mkdirs();
        }

        final int listSize = list.size();
        try {
            JSONArray array = new JSONArray();
            for ( int i = 0; i < listSize; i++ ) {
                ResolveInfo resolveInfo = list.get( i );
                Resources packageResource = context.getPackageManager().getResourcesForApplication( resolveInfo.activityInfo.packageName );

                String label = ( String ) resolveInfo.loadLabel( packageManager );
                JSONObject object = new JSONObject();
                object.put( "category", "Shortcuts" );
                object.put( "id", resolveInfo.activityInfo.packageName + "/" + resolveInfo.activityInfo.name.toString() );
                object.put( "title", label );

                if ( resolveInfo.activityInfo.getIconResource() != 0 ) {
                    Bitmap bitmap = ( ( BitmapDrawable ) packageResource.getDrawable( resolveInfo.activityInfo.getIconResource() ) ).getBitmap();
                    copyBitmapToFile( bitmap, new File( outputDirectory, "/" + resolveInfo.activityInfo.name + ".png" ) );
                    object.put( "src", "/shortcuts/" + resolveInfo.activityInfo.name + ".png" );
                } else {
                    Bitmap bitmap = ( ( BitmapDrawable ) resolveInfo.loadIcon( packageManager ) ).getBitmap();
                    copyBitmapToFile( bitmap, new File( outputDirectory, "/" + resolveInfo.activityInfo.name + ".png" ) );
                    object.put( "src", "/shortcuts/" + resolveInfo.activityInfo.name + ".png" );
                }

                if (! (object.get("id").toString().equals( "com.dashwire.asset.gatherer.common/com.dashwire.asset.PackageinfoActivity") ) ) {
                    array.put( object );
                }
            }
            return array;
        } catch ( Exception je ) {
        }
        return null;
	}

}
