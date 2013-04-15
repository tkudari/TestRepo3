package com.dashwire.config.widget.services;

interface IWidgetService2 {
    int allocateWidgetId();
    void updateSonyEricssonDesktop(String jsonItem, String type, int widgetId);
    void cleanSonyEricssonDesktop();
}
