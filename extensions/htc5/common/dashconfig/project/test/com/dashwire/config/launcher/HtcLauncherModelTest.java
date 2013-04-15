package com.dashwire.config.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import com.dashwire.robolectric.DashRobolectricTestRunner;

import static junit.framework.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

/**
 * @author brasten
 *         Date: 11/28/12 3:23 PM
 */
@RunWith(DashRobolectricTestRunner.class)
public class HtcLauncherModelTest {

    @Test
    public void testStandardShortcut() throws Exception {
        ComponentName standardComponentName = new ComponentName(
                "com.dashwire.apps.test_package",
                "com.dashwire.apps.test_package.SomeTestActivity"
        );

        Context context = new Activity();
        HtcLauncherModel model = new HtcLauncherModel(context);

        model.addShortcut(context, standardComponentName, "Blah", 1, 2, 3);

        Document document = model.buildDocument();
        assertNotNull(document);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile("//HomeScreenSetting/item");
        NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        assertEquals( 1, nodeList.getLength() );

        Node node = nodeList.item(0);
        NamedNodeMap attributes = node.getAttributes();

        assertEquals("item", node.getNodeName());
        assertEquals( "2", attributes.getNamedItem("cell_x").getNodeValue() );
        assertEquals( "3", attributes.getNamedItem("cell_y").getNodeValue() );
        assertEquals( "com.dashwire.apps.test_package.SomeTestActivity", attributes.getNamedItem("className").getNodeValue() );
        assertEquals( "-101", attributes.getNamedItem("container").getNodeValue() );
        assertEquals( "application", attributes.getNamedItem("itemType").getNodeValue() );
        assertEquals( "com.dashwire.apps.test_package", attributes.getNamedItem("packageName").getNodeValue() );
        assertEquals( "1", attributes.getNamedItem("screen").getNodeValue() );
        assertEquals( "1", attributes.getNamedItem("span_x").getNodeValue() );
        assertEquals( "1", attributes.getNamedItem("span_y").getNodeValue() );
    }

    @Test
    public void testShortcutWithInconsistentPackageNamesAndIncrediblyLongClassNames() throws Exception {
        ComponentName standardComponentName = new ComponentName(
                "com.dashwire.apps.test_package",
                "com.dashwire.ui.one.two.three.four.five.six.seven.eight.nine.SomeTestActivity"
        );

        Context context = new Activity();
        HtcLauncherModel model = new HtcLauncherModel(context);

        model.addShortcut(context, standardComponentName, "Blah", 1, 2, 3);

        Document document = model.buildDocument();
        assertNotNull(document);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile("//HomeScreenSetting/item");
        NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        assertEquals( 1, nodeList.getLength() );

        Node node = nodeList.item(0);
        NamedNodeMap attributes = node.getAttributes();

        assertEquals("item", node.getNodeName());
        assertEquals( "2", attributes.getNamedItem("cell_x").getNodeValue() );
        assertEquals( "3", attributes.getNamedItem("cell_y").getNodeValue() );
        assertEquals( "com.dashwire.ui.one.two.three.four.five.six.seven.eight.nine.SomeTestActivity", attributes.getNamedItem("className").getNodeValue() );
        assertEquals( "-101", attributes.getNamedItem("container").getNodeValue() );
        assertEquals( "application", attributes.getNamedItem("itemType").getNodeValue() );
        assertEquals( "com.dashwire.apps.test_package", attributes.getNamedItem("packageName").getNodeValue() );
        assertEquals( "1", attributes.getNamedItem("screen").getNodeValue() );
        assertEquals( "1", attributes.getNamedItem("span_x").getNodeValue() );
        assertEquals( "1", attributes.getNamedItem("span_y").getNodeValue() );
    }

    @Test
    public void testShortcutWithInconsistentPackageNames() throws Exception {
        ComponentName standardComponentName = new ComponentName(
                "com.dashwire.apps.test_package",
                "com.dashwire.ui.SomeTestActivity"
        );

        Context context = new Activity();
        HtcLauncherModel model = new HtcLauncherModel(context);

        model.addShortcut(context, standardComponentName, "Blah", 1, 2, 3);

        Document document = model.buildDocument();
        assertNotNull(document);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile("//HomeScreenSetting/item");
        NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        assertEquals( 1, nodeList.getLength() );

        Node node = nodeList.item(0);
        NamedNodeMap attributes = node.getAttributes();

        assertEquals("item", node.getNodeName());
        assertEquals( "2", attributes.getNamedItem("cell_x").getNodeValue() );
        assertEquals( "3", attributes.getNamedItem("cell_y").getNodeValue() );
        assertEquals( "com.dashwire.ui.SomeTestActivity", attributes.getNamedItem("className").getNodeValue() );
        assertEquals( "-101", attributes.getNamedItem("container").getNodeValue() );
        assertEquals( "application", attributes.getNamedItem("itemType").getNodeValue() );
        assertEquals( "com.dashwire.apps.test_package", attributes.getNamedItem("packageName").getNodeValue() );
        assertEquals( "1", attributes.getNamedItem("screen").getNodeValue() );
        assertEquals( "1", attributes.getNamedItem("span_x").getNodeValue() );
        assertEquals( "1", attributes.getNamedItem("span_y").getNodeValue() );
    }

    @Test
    public void testShortcutWithShortClassName() throws Exception {
        ComponentName standardComponentName = new ComponentName(
                "com.dashwire.apps.test_package",
                ".SomeTestActivity"
        );

        Context context = new Activity();
        HtcLauncherModel model = new HtcLauncherModel(context);

        model.addShortcut(context, standardComponentName, "Blah", 1, 2, 3);

        Document document = model.buildDocument();
        assertNotNull(document);

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile("//HomeScreenSetting/item");
        NodeList nodeList = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        assertEquals( 1, nodeList.getLength() );

        Node node = nodeList.item(0);
        NamedNodeMap attributes = node.getAttributes();

        assertEquals("item", node.getNodeName());
        assertEquals( "2", attributes.getNamedItem("cell_x").getNodeValue() );
        assertEquals( "3", attributes.getNamedItem("cell_y").getNodeValue() );
        assertEquals( "com.dashwire.apps.test_package.SomeTestActivity", attributes.getNamedItem("className").getNodeValue() );
        assertEquals( "-101", attributes.getNamedItem("container").getNodeValue() );
        assertEquals( "application", attributes.getNamedItem("itemType").getNodeValue() );
        assertEquals( "com.dashwire.apps.test_package", attributes.getNamedItem("packageName").getNodeValue() );
        assertEquals( "1", attributes.getNamedItem("screen").getNodeValue() );
        assertEquals( "1", attributes.getNamedItem("span_x").getNodeValue() );
        assertEquals( "1", attributes.getNamedItem("span_y").getNodeValue() );
    }
}
