package com.dashwire.asset.gatherer.events;

/**
 *
 */
public class ExtractionStateChangedEvent {
    public enum State {
        WAITING,
        IN_PROGRESS,
        COMPLETED
    }

    private State state;

    public ExtractionStateChangedEvent(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }
}
