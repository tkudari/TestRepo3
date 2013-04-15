//package com.dashwire.config.tracking;
//
//import com.dashwire.base.debug.DashLogger;
//import com.dashwire.config.http.DashClient;
//import com.dashwire.config.http.DashHttpResponse;
//import com.dashwire.base.device.DateGenerator;
//import com.dashwire.base.device.NetworkStatus;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.LinkedBlockingQueue;
//
//
///**
// * Author: tbostelmann
// */
//public class TrackingManager {
//    private static final String TAG = TrackingManager.class.getCanonicalName();
//    public static int RETRY_STATUS_CODE = 500;
//
//    EventQueue eventQueue;
//    NetworkStatus networkStatus;
//    DashClient dashClient;
//    Event.EventFactory eventFactory;
//
//    public TrackingManager(NetworkStatus networkStatus, DashClient dashClient, DateGenerator dateGenerator) {
//        eventQueue = new EventQueue();
//        this.networkStatus = networkStatus;
//        this.dashClient = dashClient;
//        this.eventFactory = new Event.EventFactory(dateGenerator);
//    }
//
//    /**
//     * Sends an analytic event to the server.  If the network is not available then
//     * the event is queued until the device comes on line.  Sending queued events are
//     * attempted when a new event is added to the queue or when network state changes.
//     *
//     * @param eventName The event to be sent - can be null (in which case it just tries
//     *                  to send events that are queued).
//     * @return Returns true if the send was successful, false if the even had to be queued
//     */
//    public Boolean send(String eventName) {
//        Boolean isNetAvailable = isNetworkAvailable();
//
//        if (eventName == null && eventQueue.size() > 0)
//            eventQueue.add(createPushEvent());
//        else if (eventName != null)
//            eventQueue.add(createEvent(eventName));
//
//        if (isNetAvailable && eventQueue.size() > 0) {
//            List<Event> eventList = eventQueue.drain();
//            DashHttpResponse httpResponse = null;
//            try {
//                EventContainer eventContainer = new EventContainer(eventList);
//                httpResponse = dashClient.sendEvents(eventContainer);
//            } catch (Throwable e) {
//                DashLogger.e(TAG, "Error sending analytics - requeuing events", e);
//                eventQueue.reQueue(eventList);
//            }
//            if (httpResponse != null && (httpResponse.getStatusCode() < 200 || httpResponse.getStatusCode() > 299)) {
//                eventQueue.reQueue(eventList);
//            }
//        }
//
//        if (eventQueue.size() > 0)
//            return false;
//        else
//            return true;
//    }
//
//    public Event createEvent(String eventName) {
//        return eventFactory.newInstance(eventName, isNetworkAvailable());
//    }
//
//    public Event createPushEvent() {
//        return eventFactory.newInstance("pushEvent", isNetworkAvailable());
//    }
//
//    Boolean isNetworkAvailable() {
//        return networkStatus.isNetworkAvailable();
//    }
//
//    int queueSize() {
//        return eventQueue.size();
//    }
//
//    Event pollQueue() {
//        return eventQueue.poll();
//    }
//
//    /**
//     * Thread-safe wrapper class that handles re-queuing of events if the server fails to send them.
//     */
//    protected class EventQueue {
//        private LinkedBlockingQueue<Event> events = new LinkedBlockingQueue<Event>();
//
//        protected void add(Event event) {
//            events.add(event);
//        }
//
//        protected synchronized void reQueue(List<Event> events) {
//            LinkedBlockingQueue<Event> newQueue = new LinkedBlockingQueue<Event>(events);
//            newQueue.addAll(this.events);
//            this.events = newQueue;
//        }
//
//        protected Event poll() {
//            return events.poll();
//        }
//
//        protected Event peek() {
//            return events.peek();
//        }
//
//        protected synchronized List<Event> drain() {
//            ArrayList<Event> eventList = new ArrayList<Event>();
//            events.drainTo(eventList);
//            return eventList;
//        }
//
//        protected int size() {
//            return events.size();
//        }
//    }
//}
