package se.grupp4.minbusskompis.BussParse;

import android.support.annotation.NonNull;

import com.parse.ParseInstallation;
import com.parse.ParsePush;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marcus on 10/4/2015.
 */
public class BussParseSyncMessenger implements SyncMessenger {
    private static final int TRIES = 10;
    public static final String SYNC_REQUEST = "SyncRequest";
    public static final String SYNC_RESPONSE = "SyncResponse";
    private JSONObject incomingSync;
    private String syncInstallationId;

    private final Object lock;

    public BussParseSyncMessenger(){
        lock = new Object();
    }

    public void sendSyncRequest(String syncCode){
        try {
            SendMessageToChannelWithType(syncCode, SYNC_REQUEST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendSyncResponse() {
        try {
            SendMessageToChannelWithType(syncInstallationId, SYNC_RESPONSE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SendMessageToChannelWithType(String channel, String type) throws JSONException {
        ParsePush push = getParsePushWithChannel(channel);
        JSONObject syncObject = getSyncObject(type);
        sendPushWithObject(push, syncObject);
    }

    @NonNull
    private ParsePush getParsePushWithChannel(String channel) {
        ParsePush push = new ParsePush();
        push.setChannel(channel);
        return push;
    }

    @NonNull
    private JSONObject getSyncObject(String type) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("sender", getInstallationId());
        return jsonObject;
    }

    private String getInstallationId() {
        return ParseInstallation.getCurrentInstallation().getInstallationId();
    }

    private void sendPushWithObject(ParsePush push, JSONObject jsonObject) {
        push.setData(jsonObject);
        push.sendInBackground();
    }

    public boolean waitForSyncMessage() {
        return gotResponse();
    }

    private boolean gotResponse() {
        synchronized (this.lock){
            return responseReceivedAndUnpacked();
        }
    }

    private boolean responseReceivedAndUnpacked() {
        if (noIncommingMessageAfterWait()) return false;
        unpackMessage();
        return true;
    }

    private boolean noIncommingMessageAfterWait() {
        if (this.incomingSync == null) {
            if (responseQueueStillEmptyAfter(TRIES))
                return true;
        }
        return false;
    }

    private void unpackMessage() {
        try {
            retrieveInstallationId();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void retrieveInstallationId() throws JSONException {
        syncInstallationId = incomingSync.getString("sender");
    }

    private boolean responseQueueStillEmptyAfter(int tries) {
        int attempts = 0;
        while(this.incomingSync == null){
            try {
                attempts++;
                if(attempts >= tries)
                    return true;
                this.lock.wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public String getSyncInstallationId() {
        return syncInstallationId;
    }

    @Override
    public void setSyncMessage(JSONObject response) {
        retrieveMessageAndNotify(response);
    }

    private void retrieveMessageAndNotify(JSONObject response) {
        incomingSync = response;
        incomingSync.notify();
    }


}
