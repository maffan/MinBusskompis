package se.grupp4.minbusskompis.BussParse;

import org.json.JSONObject;

/**
 * Created by Marcus on 10/4/2015.
 */
public interface SyncMessenger {
    void sendSyncRequest(String to);
    boolean waitForSyncResponse(String from);
    void sendSyncResponse(String syncId);
    void enqueueResponse(JSONObject response);
}
