//package com.dashwire.config.tracking;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import java.util.List;
//
//public class EventContainer {
//    private List<Event> events;
//
//    public EventContainer(List<Event> events) {
//        this.events = events;
//    }
//
//    public static EventContainer parseJson(String jsonEvent) {
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.setDateFormat(Event.DATE_FORMAT_STRING);
//        Gson gson = gsonBuilder.create();
//        return gson.fromJson(jsonEvent, EventContainer.class);
//    }
//
//    public boolean equals(Object other) {
//        if (this == other) return true;
//        if (!(other instanceof EventContainer)) return false;
//        EventContainer oEventContainer = (EventContainer) other;
//        return (this.events.equals(oEventContainer.events));
//    }
//
//    public String toJson() {
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.setDateFormat(Event.DATE_FORMAT_STRING);
//        Gson gson = gsonBuilder.create();
//        return gson.toJson(this);
//    }
//
//    public String toString() {
//        return toJson();
//    }
//}
//
