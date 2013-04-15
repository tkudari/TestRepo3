package com.dashwire.config.widget.services.test;

import com.dashwire.config.widget.services.WidgetConfig;

import android.test.AndroidTestCase;

public class WidgetTest extends AndroidTestCase  {

	
	public void setUp() throws Exception {		
	    WidgetConfig widget = new WidgetConfig( this.mContext  );
        widget.cleanSonyEricssonDesktop();
        assertTrue(true);
	}

    public void testAllocateWidgetID() throws Exception {
        WidgetConfig widget = new WidgetConfig( this.mContext  );
        assertTrue(widget.allocateWidgetId() >= 0);
    }
	public void testAddAndroidWidget() throws Exception {
	    
	    WidgetConfig widget = new WidgetConfig( this.mContext  );
        int widgetID = widget.allocateWidgetId();
        if(widgetID >= 0)
        {
            widget.updateSonyEricssonDesktop( "{\"id\":\"com.telenav.app.android.cingular/com.telenav.searchwidget.android.SearchWidgetProviderMini\",\"screen\":2,\"x\":0,\"y\":0,\"category\":\"Widgets\",\"type\":\"aw\",\"rows\":1,\"cols\":4}");
        }
        assertTrue(widgetID >= 0);
    }

	public void testAddAndroidWidgets() throws Exception {
	    
	    WidgetConfig widget = new WidgetConfig( this.mContext  );
        int widgetID = widget.allocateWidgetId();
        if(widgetID >= 0)
        {
            widget.updateSonyEricssonDesktop( "[{\"id\":\"com.android.calendar/com.android.calendar.widget.CalendarAppWidgetProvider\",\"screen\":1,\"x\":0,\"y\":0,\"category\":\"Widgets\",\"type\":\"aw\",\"rows\":4,\"cols\":4}, {\"id\":\"com.telenav.app.android.cingular/com.telenav.searchwidget.android.SearchWidgetProviderMini\",\"screen\":2,\"x\":0,\"y\":0,\"category\":\"Widgets\",\"type\":\"aw\",\"rows\":1,\"cols\":4}]");
        }
        assertTrue(widgetID >= 0);
    }
}
