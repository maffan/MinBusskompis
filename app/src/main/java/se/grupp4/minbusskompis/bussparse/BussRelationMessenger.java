package se.grupp4.minbusskompis.bussparse;


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
public class BussRelationMessenger extends Observable {

    private static BussRelationMessenger bussRelationMessenger = new BussRelationMessenger();

    private Queue<JSONObject> incomingData;

    public static BussRelationMessenger getInstance() {
        return bussRelationMessenger;
    }

    private BussRelationMessenger(){
        incomingData = new ConcurrentLinkedQueue<>();
    }

    public void setRelationships(BussRelationships relationships){
        clearOldAndSetNewChannels(relationships);
    }

    private void clearOldAndSetNewChannels(BussRelationships relationships) {
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        List channels = parseInstallation.getList("channels");
        channels.clear();
        channels.addAll(relationships.getRelationships());
        parseInstallation.put("channels", channels);
        parseInstallation.saveInBackground();
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

    @NonNull
    private ParsePush getParsePushWithChannel(String installationId) {
        ParsePush push = new ParsePush();
        push.setChannel(installationId);
        return push;
    }

    @NonNull
    private JSONObject getMessageObjectWithData(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "Message");
        jsonObject.put("data", data);
        return jsonObject;
    }

    private void sendPushWithObject(ParsePush push, JSONObject jsonObject) {
        push.setData(jsonObject);
        push.sendInBackground();
    }

    public void notifyPositionUpdate(){
        try {
            tryNotifyPositionUpdate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void tryNotifyPositionUpdate() throws JSONException {
        ParsePush push = getParsePushWithChannel(getInstallationId());
        JSONObject object = getPositionObjectWithData("");
        sendPushWithObject(push,object);
    }

    @NonNull
    private JSONObject getPositionObjectWithData(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "PositionUpdate");
        jsonObject.put("data", data);
        return jsonObject;
    }

    private String getInstallationId() {
        return ParseInstallation.getCurrentInstallation().getInstallationId();
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




}
