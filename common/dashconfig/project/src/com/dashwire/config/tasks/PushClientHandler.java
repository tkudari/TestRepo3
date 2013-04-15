package com.dashwire.config.tasks;

import org.json.JSONObject;

public interface PushClientHandler {
    
    public void handlePushMessage( JSONObject message );

}
