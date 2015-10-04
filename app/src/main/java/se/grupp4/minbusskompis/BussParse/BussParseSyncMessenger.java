package se.grupp4.minbusskompis.BussParse;

import android.support.annotation.NonNull;

import com.parse.ParseInstallation;
import com.parse.ParsePush;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Marcus on 10/4/2015.
 */
public class BussParseSyncMessenger implements SyncMessenger {
    private Queue<JSONObject> incomingSync;

    public BussParseSyncMessenger(){
        incomingSync = new ConcurrentLinkedQueue<>();
    }

    public void sendSyncRequest(String syncId){
        try {
            trySendSyncRequest(syncId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void trySendSyncRequest(String syncId) throws JSONException {
        ParsePush push = getParsePushWithChannel(syncId);
        JSONObject syncObject = getSyncRequestObject();
        sendPushWithObject(push, syncObject);
    }

    @NonNull
    private ParsePush getParsePushWithChannel(String installationId) {
        ParsePush push = new ParsePush();
        push.setChannel(installationId);
        return push;
    }

    @NonNull
    private JSONObject getSyncRequestObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "SyncRequest");
        jsonObject.put("sender", getInstallationId());
        return jsonObject;
    }

    private void sendPushWithObject(ParsePush push, JSONObject jsonObject) {
        push.setData(jsonObject);
        push.sendInBackground();
    }

    private String getInstallationId() {
        return ParseInstallation.getCurrentInstallation().getInstallationId();
    }

    public void sendSyncResponse(String syncId) {

    }

    @Override
    public void enqueueResponse(JSONObject response) {
        incomingSync.add(response);
    }

    public boolean waitForSyncResponse(String syncId) {
        synchronized (this.incomingSync){
            int attempts = 0;
            while(this.incomingSync.isEmpty()){
                try {
                    attempts++;
                    if(attempts >= 10)
                        return false;
                    this.incomingSync.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            JSONObject response = this.incomingSync.remove();
            try {
                String type = response.getString("type");
                if(type.equals("SyncResponse")){
                    String responseSyncId = response.getString("SyncId");
                    if(syncId.equals(responseSyncId))
                        return true;
                    else
                        return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
