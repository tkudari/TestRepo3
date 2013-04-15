package com.dashwire.asset.gatherer;

import android.content.Context;
import android.util.Log;

import com.dashwire.asset.AssetGatherer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;

public class WallpaperByXMLQuery extends AssetGatherer {
    private static final String TAG = WallpaperByXMLQuery.class.getCanonicalName();
	private String directory;
	private String xml_location;
	private String xml_query;
	public WallpaperByXMLQuery(JSONObject config,File outputDir)
	{
		super(config,outputDir);
		this.assetName = "wallpapers";
		this.directory = this.config.optString("directory","");
		this.xml_location = this.config.optString("xml_location","");
		this.xml_query = this.config.optString("xml_query","");
	}
	@Override
	protected JSONArray getAssets(Context context) {
		Log.d(TAG, "FIND WALLPAPERS By XML Query------------------");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        File b = new File( outputDir, assetName );
        if ( !b.exists() ) {
            b.mkdirs();
        }
        JSONArray array = new JSONArray();
        try {
            builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(xml_location);
            XPathFactory xpathfactory = XPathFactory.newInstance();
            XPath xpath = xpathfactory.newXPath();
            NodeList wallpapers = (NodeList) xpath.evaluate( xml_query , doc,XPathConstants.NODESET );

            Log.d("list_wallpapers","wallpapers found: " + wallpapers.getLength());
            for (int i = 0; i < wallpapers.getLength(); i++) {
                File f = new File(directory + wallpapers.item(i).getNodeValue());
                String fileName = f.getName();
               
                JSONObject object = new JSONObject();
                try {
                    object.put( "uri", "file://" + f.getAbsolutePath() );
                    object.put( "src", "/"+this.assetName+"/" + fileName );
                    array.put( object );
                    copyFile( f, new File( outputDir, assetName + "/" + f.getName() ) );
                } catch ( JSONException e ) {
                    Log.e(TAG, "Error", e);
                }
            }
        } catch ( ParserConfigurationException e ) {
            Log.e(TAG, "Error", e);
        } catch ( SAXException e ) {
            Log.e(TAG, "Error", e);
        } catch ( IOException e ) {
            Log.e(TAG, "Error", e);
        } catch ( XPathExpressionException e ) {
            Log.e(TAG, "Error", e);
        }
        
        return array;
	}

}