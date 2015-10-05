package se.grupp4.minbusskompis.BussParse;

import org.json.JSONObject;

/**
 * Created by Marcus on 10/4/2015.
 */
public interface SyncMessenger {
    int REQUEST = 0;
    int RESPONSE = 1;

    void sendSyncRequest(String syncCode);
    void sendSyncResponse();
    boolean waitForSyncMessage();
    void setSyncMessage(JSONObject response);
    String getSyncInstallationId();
}
