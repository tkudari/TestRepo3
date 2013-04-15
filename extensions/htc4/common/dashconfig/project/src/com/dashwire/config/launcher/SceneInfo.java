package com.dashwire.config.launcher;

import android.content.Context;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.config.util.CommonUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class SceneInfo {
    private static String TAG = SceneInfo.class.getCanonicalName();

    private final List<ItemInfo> mItems;

    private final Context mContext;

    private WallpaperInfo mWallpaper;
    private String mSenseVersion;

    SceneInfo(Context context, String senseVersion) {
        mItems = new ArrayList<ItemInfo>();
        mContext = context;
        mSenseVersion = senseVersion;
    }

    void addItem(ItemInfo info) {
        mItems.add(info);
    }

    void addWallpaper(WallpaperInfo wp) {
        mWallpaper = wp;
    }

    /**
     * Returns a DOM representation of this SceneInfo model
     *
     * @return Document representing SceneInfo model
     */
    Document buildDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            DashLogger.e(TAG, "Exception creating new document in SceneInfo.", e);

            return null;
        }

        Document document = builder.newDocument();

        Element scene = document.createElement("scene");
        if (CommonUtils.isHTCSense4(mSenseVersion)) {
            scene.setAttribute("name", "ATT");
            scene.setAttribute("homeScreen", "1");
            scene.setAttribute("page_count", "3");
        } else {
            scene.setAttribute("name", "ATT");
            scene.setAttribute("homeScreen", "2");
            scene.setAttribute("page_count", "2");
        }

        document.appendChild(scene);

        if (mWallpaper != null) {
            scene.appendChild(mWallpaper.asDomElement(document));
        } else {
            //WallpaperManager.getInstance(mContext).
        }

        if (!mItems.isEmpty()) {
            Element setting = document.createElement("HomeScreenSetting");
            scene.appendChild(setting);

            for (ItemInfo info : mItems) {
                setting.appendChild(info.asDomElement(document));
            }
        }

        return document;
    }

    /**
     * Writes this model out to an XML document on disk and returns a File object referencing the
     * new file.
     *
     * @return File object referencing XML document on disk.
     */
    File writeDocument() {
        Document document = buildDocument();

        FileOutputStream fos = null;
        String filename = "newScene.xml";

        try {
            //fos = new FileOutputStream("/data/local/newScene.xml");
            fos = mContext.openFileOutput(
                    filename, Context.MODE_WORLD_READABLE);//TODO: try private
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return null;
        }

        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }

        return mContext.getFileStreamPath(filename);
    }
}
