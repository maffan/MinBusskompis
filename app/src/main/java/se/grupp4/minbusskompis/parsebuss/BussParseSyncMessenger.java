package se.grupp4.minbusskompis.parsebuss;

import android.support.annotation.NonNull;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marcus on 10/4/2015.
 */
public class BussParseSyncMessenger {
    private static final int TRIES_IN_SECONDS = 30;
    private static final String REQUEST_STRING = "SyncRequest";
    private static final String RESPONSE_STRING = "SyncResponse";
    public static final int REQUEST_TYPE = 0;
    public static final int RESPONSE_TYPE = 1;
    private static final String TAG = "SYNC_MESSENGER";

    private JSONObject incomingSync;
    private String syncInstallationId;

    private final Object lock;

    public BussParseSyncMessenger(){
        lock = new Object();
    }

    public void sendSyncRequest(String syncCode){
        try {
            SendMessageToChannelWithType(syncCode, REQUEST_STRING);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendSyncResponse() {
        try {
            SendMessageToChannelWithType(getSyncInstallationIdAsChannel(), RESPONSE_STRING);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getSyncInstallationIdAsChannel() {
        return "i"+syncInstallationId;
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
        try {
            push.send();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"Object sent!" );
    }

    public boolean waitForSyncMessage() {
        Log.d(TAG,"Wating for response");
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
        Log.d(TAG, "responseReceivedAndUnpacked: GOT Response!");
        return true;
    }

    private boolean noIncommingMessageAfterWait() {
        if (this.incomingSync == null) {
            if (responseQueueStillEmptyAfter(TRIES_IN_SECONDS))
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

    public String getSyncInstallationId() {
        return syncInstallationId;
    }

    public void setSyncMessage(JSONObject response) {
        retrieveMessageAndNotify(response);
    }

    private void retrieveMessageAndNotify(JSONObject response) {
        synchronized (lock) {
            incomingSync = response;
            lock.notify();
        }
    }


}
