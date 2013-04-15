//package com.dashwire.config.tracking;
//
//import com.dashwire.base.device.DateGenerator;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
///**
// * Author: tbostelmann
// */
//public class Event {
//    static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss.SSS 'UTC'";
//    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);
//
//    private String event;
//    private Date time;
//    private Boolean network;
//
//    public Event(String event, Boolean network, Date time) {
//        this.event = event;
//        this.network = network;
//        this.time = time;
//    }
//
//    Event(String event, Date time, Boolean network) {
//        this.event = event;
//        this.time = time;
//        this.network = network;
//    }
//
//    public static String formatDate(Date date) {
//        return DATE_FORMAT.format(date);
//    }
//
//    public static Event parseJson(String jsonEvent) {
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.setDateFormat(Event.DATE_FORMAT_STRING);
//        Gson gson = gsonBuilder.create();
//        return gson.fromJson(jsonEvent, Event.class);
//    }
//
//    public String getName() {
//        return event;
//    }
//
//    @Override
//    public boolean equals(Object other) {
//        if (this == other) return true;
//        if (!(other instanceof Event)) return false;
//        Event oEvent = (Event) other;
//        return ((this.event.equals(oEvent.event)) &&
//                (this.time.equals(oEvent.time)) &&
//                (this.network.equals(oEvent.network)));
//    }
//
//    @Override
//    public String toString() {
//        return toJson();
//    }
//
//    public String toJson() {
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.setDateFormat(Event.DATE_FORMAT_STRING);
//        Gson gson = gsonBuilder.create();
//        return gson.toJson(this);
//    }
//
//    public static class EventFactory {
//        private DateGenerator dateGenerator;
//
//        EventFactory(DateGenerator dateGenerator1) {
//            dateGenerator = dateGenerator1;
//        }
//
//        Event newInstance(String eventName, Boolean networkUp) {
//            return new Event(eventName, networkUp, dateGenerator.generateDate());
//        }
//    }
//}
