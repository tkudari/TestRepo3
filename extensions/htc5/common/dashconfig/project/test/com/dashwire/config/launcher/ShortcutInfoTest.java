package com.dashwire.config.launcher;

import android.content.ComponentName;
import com.dashwire.robolectric.DashRobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(DashRobolectricTestRunner.class)
public class ShortcutInfoTest {

    @Test
    public void testWithStandardComponentName() throws Exception {
        ComponentName standardComponentName = new ComponentName(
                "com.dashwire.apps.test_package",
                "com.dashwire.apps.test_package.SomeTestActivity"
        );

        ShortcutInfo info = new ShortcutInfo(standardComponentName);
        info.title = "Shortcut Title";
        info.itemTypeName = "application";
        info.cellX = 2;
        info.cellY = 3;
        info.screen = 1;
        info.container = -100;


        assertHasCorrectValues(
                info.asDomElement(buildNewDocument()),
                "com.dashwire.apps.test_package",
                "com.dashwire.apps.test_package.SomeTestActivity",
                null,
                "application",
                "1",
                "2",
                "3",
                "-100"
        );
    }

    @Test
    public void testWithInconsistentPackageNames() throws Exception {
        ComponentName inconsistentComponentName = new ComponentName(
                "com.dashwire.apps.test_package",
                "com.dashwire.ui.activities.some.package.SomeTestActivity"
        );

        ShortcutInfo info = new ShortcutInfo(inconsistentComponentName);
        info.title = "Shortcut Title";
        info.itemTypeName = "application";
        info.cellX = 2;
        info.cellY = 3;
        info.screen = 1;
        info.container = -100;


        assertHasCorrectValues(
                info.asDomElement(buildNewDocument()),
                "com.dashwire.apps.test_package",
                "com.dashwire.ui.activities.some.package.SomeTestActivity",
                null,
                "application",
                "1",
                "2",
                "3",
                "-100"
        );
    }


    private Document buildNewDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        Document document = factory.newDocumentBuilder().newDocument();

        return document;
    }

    private void assertHasCorrectValues( Element element, String packageName, String className,
                                         String title, String itemTypeName, String screen, String cellX,
                                         String cellY, String container ) {

        NamedNodeMap attributes = element.getAttributes();

        assertEquals("item", element.getNodeName());
        assertEquals( packageName, attributes.getNamedItem("packageName").getNodeValue() );
        assertEquals( className, attributes.getNamedItem("className").getNodeValue() );
        if (title == null) {
            assertNull( attributes.getNamedItem("title") );
        } else {
            assertEquals( title, attributes.getNamedItem("title").getNodeValue() );
        }
        assertEquals( itemTypeName, attributes.getNamedItem("itemType").getNodeValue() );
        assertEquals( screen, attributes.getNamedItem("screen").getNodeValue() );
        assertEquals( cellX, attributes.getNamedItem("cell_x").getNodeValue() );
        assertEquals( cellY, attributes.getNamedItem("cell_y").getNodeValue() );
        assertEquals( container, attributes.getNamedItem("container").getNodeValue() );
        assertEquals( "1", attributes.getNamedItem("span_x").getNodeValue() );
        assertEquals( "1", attributes.getNamedItem("span_y").getNodeValue() );
    }

}
