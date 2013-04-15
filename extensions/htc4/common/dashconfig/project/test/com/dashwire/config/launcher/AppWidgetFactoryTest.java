package com.dashwire.config.launcher;

import android.content.ComponentName;
import com.dashwire.base.debug.DashLogger;
import com.dashwire.robolectric.DashRobolectricTestRunner;
import com.xtremelabs.robolectric.Robolectric;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

import static junit.framework.Assert.assertEquals;

@RunWith(DashRobolectricTestRunner.class)
public class AppWidgetFactoryTest {
    private static final String TAG = AppWidgetFactory.class.getCanonicalName();

    private AppWidgetFactory factory;
    private Document document;

    @Before
    public void setUp() throws ParserConfigurationException {
        factory = AppWidgetFactory.getInstance(Robolectric.application);
        DashLogger.setDebugMode(true);

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();

        document = builder.newDocument();
    }

    @Test
    public void testAppWidgetWithStandardComponentName() {
        ItemInfo info = factory.createItemInfo("aw",
                new ComponentName("com.dashwire.widget_one", "com.dashwire.widget_one.MainActivity"),
                null
        );

        Element element = info.asDomElement(document);
        assertHasCorrectValuesForAppWidget(element, "com.dashwire.widget_one", "com.dashwire.widget_one.MainActivity");
    }

    @Test
    public void testAppWidgetWithShortComponentName() {
        ItemInfo info = factory.createItemInfo("aw",
                new ComponentName("com.dashwire.widget_one", ".ui.MainActivity"),
                null
        );

        Element element = info.asDomElement(document);
        assertHasCorrectValuesForAppWidget(element, "com.dashwire.widget_one", "com.dashwire.widget_one.ui.MainActivity");
    }

    @Test
    public void testAppWidgetWithInconsistentComponentName() {
        ItemInfo info = factory.createItemInfo("aw",
                new ComponentName("com.dashwire.widget_one", "com.dashwire.widgets.one.MainActivity"),
                null
        );

        Element element = info.asDomElement(document);
        assertHasCorrectValuesForAppWidget(element, "com.dashwire.widget_one", "com.dashwire.widgets.one.MainActivity");
    }

    @Test
    public void testHtcWidgetWithStandardComponentName() {
        ItemInfo info = factory.createItemInfo("htcw",
                new ComponentName("com.dashwire.widget_one", "com.dashwire.widget_one.MainActivity"),
                "com.dashwire.widget_one/com.dashwire.widget_one.MainActivity"
        );

        Element element = info.asDomElement(document);
        assertHasCorrectValuesForHtcWidget(element, "com.dashwire.widget_one", "com.dashwire.widget_one.MainActivity");
    }

    @Test
    public void testHtcWidgetWithShortComponentName() {
        ItemInfo info = factory.createItemInfo("htcw",
                new ComponentName("com.dashwire.widget_one", ".ui.MainActivity"),
                "com.dashwire.widget_one/.ui.MainActivity"
        );

        Element element = info.asDomElement(document);
        assertHasCorrectValuesForHtcWidget(element, "com.dashwire.widget_one", "com.dashwire.widget_one.ui.MainActivity");
    }

    @Test
    public void testHtcWidgetWithInconsistentComponentName() {
        ItemInfo info = factory.createItemInfo("htcw",
                new ComponentName("com.dashwire.widget_one", "com.dashwire.widgets.one.MainActivity"),
                "com.dashwire.widget_one/com.dashwire.widgets.one.MainActivity"
        );

        Element element = info.asDomElement(document);
        assertHasCorrectValuesForHtcWidget(element, "com.dashwire.widget_one", "com.dashwire.widgets.one.MainActivity");
    }

    private void assertHasCorrectValuesForAppWidget( Element element, String packageName, String className ) {
        NamedNodeMap attributes = element.getAttributes();

        assertEquals( "item", element.getNodeName() );
        assertEquals( "app_widget", attributes.getNamedItem("itemType").getNodeValue() );
        assertEquals( packageName, attributes.getNamedItem("packageName").getNodeValue() );
        assertEquals( className, attributes.getNamedItem("className").getNodeValue() );

    }

    private void assertHasCorrectValuesForHtcWidget( Element element, String packageName, String widgetName ) {
        NamedNodeMap attributes = element.getAttributes();

        assertEquals( "item", element.getNodeName() );
        assertEquals( "htc_fx_widget", attributes.getNamedItem("itemType").getNodeValue() );
        assertEquals( packageName, attributes.getNamedItem("packageName").getNodeValue() );
        assertEquals( widgetName, attributes.getNamedItem("widgetName").getNodeValue() );

    }

    private void writeDocument( Document document ) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));

            DashLogger.d(TAG, "XML: " + writer.toString());
        } catch (Exception ex) {
            // bury
        }
    }

}

