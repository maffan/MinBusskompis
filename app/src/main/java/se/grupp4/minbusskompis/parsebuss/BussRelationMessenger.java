package se.grupp4.minbusskompis.parsebuss;


import android.util.Log;

import com.parse.ParseInstallation;
import com.parse.ParsePush;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import se.grupp4.minbusskompis.TravelingData;

/**
 * This class handles all messages that is either received via broadcast or to be sent via broadcast.
 */
public class BussRelationMessenger extends Observable {

    private static final String TAG = "RELATION_MESSENGER";
    public static final String CHANNELS_FIELD = "channels";
    public static final String TYPE_FIELD = "type";
    public static final String DATA_FIELD = "data";
    public static final String FROM_FIELD = "from";
    private static BussRelationMessenger bussRelationMessenger = new BussRelationMessenger();

    private Queue<JSONObject> incomingData;

    public static BussRelationMessenger getInstance() {
        return bussRelationMessenger;
    }

    private BussRelationMessenger(){
        incomingData = new ConcurrentLinkedQueue<>();
    }

    /**
     * Adds the provided relationships to the Parse subscription list i.e. this device will receive
     * messages sent from the devices contained in the BussRelationships object
     * @param relationships
     */
    public void setRelationships(BussRelationships relationships){
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        List channelsList = getChannelsListBasedOnRelationships(relationships, parseInstallation);
        parseInstallation.put(CHANNELS_FIELD, channelsList);
        parseInstallation.saveInBackground();
    }

    private List getChannelsListBasedOnRelationships(BussRelationships relationships, ParseInstallation parseInstallation) {
        List oldChannelsList = parseInstallation.getList(CHANNELS_FIELD);
        List<String> newChannelsList = getDifferenceOfRelationshipsAndChannelsList(relationships, oldChannelsList);
        oldChannelsList.addAll(newChannelsList);
        return oldChannelsList;
    }

    private List<String> getDifferenceOfRelationshipsAndChannelsList(BussRelationships relationships, List oldChannelsList) {
        List<String> newChannels = new ArrayList<>();
        for(String channel: relationships.getRelationships()){
            if(oldChannelsList.contains("i"+channel))
                continue;
            newChannels.add("i"+channel);
        }
        return newChannels;
    }

    /**
     * Broadcasts a status change
     * @param status
     */
    public void broadcastStatusUpdateNotification(int status){
        String name = ParseCloudManager.getInstance().getOwnName();
        String activity;
        switch (status){
            case TravelingData.WALKING:
                activity = "walking";
                break;
            case TravelingData.AT_BUS_STATION:
                activity = "waiting for the bus";
                break;
            case TravelingData.ON_BUS:
                activity = "riding the bus";
                break;
            case TravelingData.LEAVING_BUS:
                activity = "getting of the bus";
                break;
            default:
                return;
        }
        String message = name+" is now "+activity+".";
        broadcastMessage(message);
    }

    private void broadcastMessage(String data){
        try {
            trySendMessage(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void trySendMessage(String data) throws JSONException {
        ParsePush push = getParsePushWithChannel("i"+ ParseInstallation.getCurrentInstallation().getInstallationId());
        JSONObject messageObject = getMessageObjectWithData(data);
        sendPushWithObject(push, messageObject);
    }

    private ParsePush getParsePushWithChannel(String installationId) {
        ParsePush push = new ParsePush();
        push.setChannel(installationId);
        return push;
    }

    private JSONObject getMessageObjectWithData(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "Message");
        jsonObject.put("alert", data);
        jsonObject.put(FROM_FIELD, ParseInstallation.getCurrentInstallation().getInstallationId());
        return jsonObject;
    }

    private void sendPushWithObject(ParsePush push, JSONObject jsonObject) {
        push.setData(jsonObject);
        push.sendInBackground();
    }

    /**
     * Broadcast that there now is new position data for this device in the Parse cloud
     */
    public void notifyPositionUpdate(){
        try {
            tryNotifyPositionUpdate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void tryNotifyPositionUpdate() throws JSONException {
        ParsePush push = getParsePushWithChannel("i"+ ParseInstallation.getCurrentInstallation().getInstallationId());
        JSONObject object = getPositionObjectWithData("");
        sendPushWithObject(push,object);
    }

    private JSONObject getPositionObjectWithData(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TYPE_FIELD, "PositionUpdate");
        jsonObject.put(DATA_FIELD, data);
        jsonObject.put(FROM_FIELD, ParseInstallation.getCurrentInstallation().getInstallationId());
        return jsonObject;
    }

    /**
     * Enqueues messages sent to this device. Should only be called from a BroadcastReceivers
     * onPushReceived method.
     * @param data
     */
    public void dataReceived(JSONObject data){
        Log.d(TAG, "dataReceived() called with: " + "data = [" + data + "]");
        enqueueData(data);
        notifyListeners(data);
    }

    private void enqueueData(JSONObject data) {
        Log.d(TAG, "enqueueData() called with: " + "data = [" + data + "]");
        this.incomingData.add(data);
    }

    private void notifyListeners(JSONObject data) {
        Log.d(TAG, "notifyListeners() called with: " + "data = [" + data + "]");
        setChanged();
        notifyObservers(data);
    }

    /**
     * Returns the queue containing all messages
     * @return
     */
    public Queue<JSONObject> getDataQueue() {
        return incomingData;
    }
}
