package se.grupp4.minbusskompis.BussParse;


import android.support.annotation.NonNull;

import com.parse.ParseInstallation;
import com.parse.ParsePush;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Marcus on 9/21/2015.
 *
 * A singleton for handling sending messages between devices
 *
 * If you want to receive notifications sent to this device, simply implement
 * the Observer interface and add yourself as a listener to this singleton.
 *
 */
public class BussMessenger extends Observable {

    private static BussMessenger bussMessenger = new BussMessenger();

    private List<String> recipients;

    private Queue<JSONObject> incomingData;
    private Queue<JSONObject> incomingSync;

    public static BussMessenger getInstance() {
        return bussMessenger;
    }

    private BussMessenger(){
        incomingData = new ConcurrentLinkedQueue<>();
        incomingSync = new ConcurrentLinkedQueue<>();
    }

    public void sendMessage(String data){
        try {
            trySendMessage(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void trySendMessage(String data) throws JSONException {
        ParsePush push = getParsePushWithChannel(getInstallationId());
        JSONObject messageObject = getMessageObjectWithData(data);
        sendPushWithObject(push, messageObject);
    }

    private void sendPushWithObject(ParsePush push, JSONObject jsonObject) {
        push.setData(jsonObject);
        push.sendInBackground();
    }

    @NonNull
    private JSONObject getMessageObjectWithData(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "Message");
        jsonObject.put("data", data);
        return jsonObject;
    }

    private String getInstallationId() {
        return ParseInstallation.getCurrentInstallation().getInstallationId();
    }

    @NonNull
    private ParsePush getParsePushWithChannel(String installationId) {
        ParsePush push = new ParsePush();
        push.setChannel(installationId);
        return push;
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
    private JSONObject getSyncRequestObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "SyncRequest");
        jsonObject.put("sender", getInstallationId());
        return jsonObject;
    }

    public void sendSyncResponse(String syncId) {

    }

    /**
     * This method should only be called by the applications ParsePushBroadcastReceiver.
     *
     * Enqueues incoming data and notifies observers that new data is available.
     * @param data The data to be enqueued
     */
    public void dataReceived(JSONObject data){
        enqueueData(data);
        notifyListeners(data);
    }

    private void enqueueData(JSONObject data) {
        synchronized (this.incomingData){
            this.incomingData.add(data);
            this.incomingData.notify();
        }
    }
    private void enqueueSync(JSONObject data) {
        synchronized (this.incomingSync){
            this.incomingData.add(data);
            this.incomingData.notify();
        }
    }

    private void notifyListeners(JSONObject data) {
        setChanged();
        notifyObservers(data);
    }

    /**
     * Returns a queue containing yet unprocessed messages.
     * @return a Queue containing incoming data.
     */
    public Queue<JSONObject> getDataQueue() {
        return incomingData;
    }

    public Queue<JSONObject> getSyncQueue(){
        return incomingSync;
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
