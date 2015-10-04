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
    private static final int TRIES = 10;
    private Queue<JSONObject> incomingSyncRequest;
    private Queue<JSONObject> incomingSyncResponse;

    public BussParseSyncMessenger(){
        incomingSyncRequest = new ConcurrentLinkedQueue<>();
        incomingSyncResponse = new ConcurrentLinkedQueue<>();
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
    private ParsePush getParsePushWithChannel(String channel) {
        ParsePush push = new ParsePush();
        push.setChannel(channel);
        return push;
    }

    @NonNull
    private JSONObject getSyncRequestObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "SyncRequest");
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

    public boolean waitForSyncResponse(String syncId) {
        synchronized (this.incomingSyncResponse){
            if (noResponse()) return false;
            if (responseHasSameSyncId(syncId)) {
                return true;
            } else {
                return false;
            }

        }
    }

    private boolean noResponse() {
        if (this.incomingSyncResponse.isEmpty()) {
            if (responseQueueStillEmptyAfter(TRIES))
                return true;
        }
        return false;
    }

    private boolean responseQueueStillEmptyAfter(int tries) {
        int attempts = 0;
        while(this.incomingSyncResponse.isEmpty()){
            try {
                attempts++;
                if(attempts >= tries)
                    return true;
                this.incomingSyncResponse.wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }



    @NonNull
    private Boolean responseHasSameSyncId(String syncId) {
        JSONObject response = this.incomingSyncResponse.remove();
        try {
            String responseSyncId = response.getString("SyncId");
            if(syncId.equals(responseSyncId))
                return true;
            else
                return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean waitForSyncRequest(String syncCode) {
        ParsePush.subscribeInBackground(syncCode);
        synchronized (this.incomingSyncRequest){
            if (noRequest()) return false;

        }
        ParsePush.unsubscribeInBackground(syncCode);
        return false;
    }

    private boolean noRequest() {
        if (this.incomingSyncRequest.isEmpty()) {
            if (requestQueueStillEmptyAfter(TRIES))
                return true;
        }
        return false;
    }

    private boolean requestQueueStillEmptyAfter(int tries) {
        int attempts = 0;
        while(this.incomingSyncRequest.isEmpty()){
            try {
                attempts++;
                if(attempts >= tries)
                    return true;
                this.incomingSyncRequest.wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void sendSyncResponse(String syncId) {

    }

    @Override
    public void enqueueResponse(JSONObject response) {
        incomingSyncRequest.add(response);
    }

    @Override
    public void enqueueRequest(JSONObject request) {
        incomingSyncRequest.add(request);
    }




}
