package com.dashwire.asset.gatherer;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Author: tbostelmann
 */
public class AndroidWidgetsTest extends TestCase {
    public void testCreateSortedArray() throws Exception {
        JSONObject[] jsonObjects = new JSONObject[3];
        jsonObjects[0] = new JSONObject();
        jsonObjects[0].put("title", "foo");
        jsonObjects[1] = new JSONObject();
        jsonObjects[1].put("title", "bar");
        jsonObjects[2] = new JSONObject();
        jsonObjects[2].put("title", "zuperduper");
        JSONArray jsonArray = AndroidWidgets.createSortedArray(jsonObjects, "title");
        Assert.assertEquals(((JSONObject)jsonArray.get(0)).get("title"), "bar");
        Assert.assertEquals(((JSONObject)jsonArray.get(1)).get("title"), "foo");
        Assert.assertEquals(((JSONObject)jsonArray.get(2)).get("title"), "zuperduper");
    }
}
