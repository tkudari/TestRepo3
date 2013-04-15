package com.dashwire.config.launcher;

import android.content.Context;
import android.os.Environment;
import com.dashwire.base.debug.DashLogger;
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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

class FavoritesInfo {
    private static final String TAG = FavoritesInfo.class.getCanonicalName();
    static final long HOT_SEAT_CONTAINER_ID = -101;
    static final String SHORTCUT_ELEMENT_NAME = "favorite";
    static final String FILENAME = "favorites.xml";

    List<FavoriteInfo> mHomeList;
    List<FavoriteInfo> mHotSeatList;
    Context mContext;

    FavoritesInfo(Context context) {
        mHomeList = new ArrayList<FavoriteInfo>();
        mHotSeatList = new ArrayList<FavoriteInfo>();
        mContext = context;
    }

    void addHomeFavorite(FavoriteInfo favoriteInfo) {
        mHomeList.add(favoriteInfo);
    }

    void addHotSeatFavorite(FavoriteInfo favoriteInfo) {
        mHotSeatList.add(favoriteInfo);
    }

    File writeDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            return null;
        }

        Document document = builder.newDocument();

        Element favorites = document.createElement("favorites");
        favorites.setAttribute("xmlns:launcher", "http://schemas.android.com/apk/res/com.sec.android.app.launcher");
        Element home = document.createElement("home");
        for (FavoriteInfo favoriteInfo : mHomeList) {
            home.appendChild(favoriteInfo.asDomElement(document));
        }
        favorites.appendChild(home);
        document.appendChild(favorites);

        Element hotseat = document.createElement("hotseat");
        for (FavoriteInfo favoriteInfo : mHotSeatList) {
            hotseat.appendChild(favoriteInfo.asDomElement(document));
        }
        favorites.appendChild(hotseat);

        File file = getFile();
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(document);
            FileOutputStream fos = new FileOutputStream(file, false);
            StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);
            file.setReadOnly();
        } catch (Exception e) {
            DashLogger.e(TAG, "Error creating " + FILENAME, e);
            e.printStackTrace();
        }
        return file;
    }

    static File getFile() {
        return new File(Environment.getExternalStorageDirectory(), FILENAME);
    }
}
