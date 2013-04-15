package com.dashwire.asset.gatherer;

import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import com.dashwire.asset.AssetGatherer;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/*
Samsung only gives us release versions of their ROMs so we need
to use a different method to extract resources than the more
common AndroidWidgets version
 */


public class AndroidWidgetsReleaseVersion extends AssetGatherer {
    private static final String TAG = AndroidWidgetsReleaseVersion.class.getCanonicalName();

    public AndroidWidgetsReleaseVersion(JSONObject config,File outputDir)
    {
        super(config,outputDir);
        this.assetName = "widgets";
        this.sortProp = "title";
    }
    @Override
    protected JSONArray getAssets(Context context) {
        Log.d(TAG, "FIND Android Widgets------------------");

        JSONArray array = new JSONArray();
        Log.d(TAG, "---- FIND package list ----");

        List<PackageInfo> pis = context.getPackageManager().getInstalledPackages(0 );
        Log.d(TAG, "---- DONE FINDING package list ----");

        File outputDirectory = new File( outputDir, "widgets" );
        if ( !outputDirectory.exists() ) {
            outputDirectory.mkdirs();
        }


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( context );
        List<AppWidgetProviderInfo> providers = appWidgetManager.getInstalledProviders();
        for(AppWidgetProviderInfo awpi : providers)
        {
            try {
                JSONObject object = new JSONObject();
                object.put( "category", "Widgets" );

                object.put( "title", awpi.label);

                object.put( "id", awpi.provider.getPackageName() + "/" + awpi.provider.getClassName() );
                object.put( "src", "" );
                object.put( "type", "aw" );

                Rect padding = AppWidgetHostView.getDefaultPaddingForWidget(context, awpi.provider, null);
                object.put("padding_top",padding.top);
                object.put("padding_bottom",padding.bottom);
                object.put("padding_left",padding.left);
                object.put("padding_right",padding.right);
//			        // We want to account for the extra amount of padding that we are adding to the widget
//			        // to ensure that it gets the full amount of space that it has requested
//		        int requiredWidth = awpi.minWidth + padding.left + padding.right;
//		        int requiredHeight = awpi.minHeight + padding.top + padding.bottom;
//		        // Always assume we're working with the smallest span to make sure we
//		        // reserve enough space in both orientations.
//		        int actualWidth = resources.getDimensionPixelSize(R.dimen.workspace_cell_width);
//		        int actualHeight = resources.getDimensionPixelSize(R.dimen.workspace_cell_height);
//		        int smallerSize = Math.min(actualWidth, actualHeight);
//		        
//		        // Always round up to next largest cell
//		        int spanX = (int) Math.ceil(requiredWidth / (float) smallerSize);
//		        int spanY = (int) Math.ceil(requiredHeight / (float) smallerSize);
//
//		        if (result == null) {
//		            return new int[] { spanX, spanY };
//		        }
//		        result[0] = spanX;
//		        result[1] = spanY;


                object.put( "cols", awpi.minWidth +"dip");
                object.put( "rows",awpi.minHeight +"dip");
                int imageId = 0;
                if(awpi.previewImage != 0)
                {
                    imageId = awpi.previewImage;
                }
                else if(awpi.icon != 0)
                {
                    imageId = awpi.icon;
                }

                if(imageId != 0)
                {
                    Resources packageResource = context.getPackageManager().getResourcesForApplication( awpi.provider.getPackageName() );

                    Bitmap bitmap = ( ( BitmapDrawable ) packageResource.getDrawable( imageId ) ).getBitmap();
                    String name = (awpi.provider.getPackageName() + "/" + awpi.provider.getClassName() ).replaceAll("[^\\w]+", "_") + ".png";
                    copyBitmapToFile( bitmap, new File( outputDirectory , "/"+ name ) );
                    object.put( "src", "/widgets/" + name );
                }
                array.put( object );
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "Error", e);
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "Error", e);
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Error", e);
            }
        }

        return array;
    }

}