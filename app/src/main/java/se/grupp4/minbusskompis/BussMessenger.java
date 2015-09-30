package se.grupp4.minbusskompis;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SendCallback;

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
    private String listeningChannel;
    private String sendingChannel;

    private Queue<String> incomingData;

    public static BussMessenger getInstance() {
        return bussMessenger;
    }

    private BussMessenger(){
        incomingData = new ConcurrentLinkedQueue<>();
    }

    /**
     * Used to send data.
     * The data will be sent through the channel specified in the setChannel method
     * @param data data so be sent.
     *
     */
    public void sendData(String data){
        sendData(data,null);
    }

    /**
     * Used to send data.
     * Use this method if you want to provide a callback method to be called when the message
     * has been successfully sent.
     *
     * @param callback callback object to be called when the message has been sent.
     *
     */
    public boolean sendData(String data, SendCallback callback){
        try {
            trySendData(data, callback);
            return true;
        }catch(JSONException e){
            return false;
        }
    }

    private void trySendData(String data, SendCallback callback) throws JSONException {
        ParsePush push = makePushWithCurrentChannel();
        putDataInPush(data, push);
        sendPushWithCallback(push, callback);
    }

    @NonNull
    private ParsePush makePushWithCurrentChannel() {
        ParsePush push = getParsePush();
        push.setChannel(sendingChannel);
        return push;
    }

    @NonNull
    protected ParsePush getParsePush() {
        return new ParsePush();
    }

    private void putDataInPush(String data, ParsePush push) throws JSONException {
            JSONObject jsonObject = getJsonObjectWithData(data);
            push.setData(jsonObject);
    }

    @NonNull
    protected JSONObject getJsonObjectWithData(String data) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("data", data);
        return object;
    }

    private void sendPushWithCallback(ParsePush push, SendCallback callback) {
        push.sendInBackground(callback);
        Log.d("BUSSPARSE", "Data sent");
    }

    /**
     * This method should only be called by the applications ParsePushBroadcastReceiver.
     *
     * Enqueues incoming data and notifies observers that new data is available.
     * @param data The data to be enqueued
     */
    public void dataReceived(String data){
        enqueueData(data);
        notifyListeners(data);
    }

    private void enqueueData(String data) {
        this.incomingData.add(data);
    }

    private void notifyListeners(String data) {
        setChanged();
        notifyObservers(data);
    }

    /**
     * Get the current sending channel
     * @return the current sending channel
     */
    public String getSendingChannel() {
        return sendingChannel;
    }

    /**
     * Must be called before sendData. Sets the channel the data will be sent through
     * @param sendingChannel the channel to send data through
     */
    public void setSendingChannel(String sendingChannel) {
        this.sendingChannel = sendingChannel;
    }

    /**
     * Get the current listening channel.
     *
     * This is the channel the broadcast receiver will be receiving data from.
     * @return the current listening channel
     */
    public String getListeningChannel() {
        return listeningChannel;
    }

    /**
     * Must be set before any data can be received.
     *
     * This is the channel the broadcast receiver will be receiving data from.
     * @param listeningChannel the channel to receive data from.
     */
    public void setListeningChannel(String listeningChannel) {
        this.listeningChannel = listeningChannel;
        clearInstalledChannels();
        subscribeToChannel(listeningChannel);
    }

    private void clearInstalledChannels() {
        List<String> channels = getCurrentInstallation().getList("channels");
        if(channels != null){
            channels.clear();
            getCurrentInstallation().put("channels",channels);
        }
    }

    protected ParseInstallation getCurrentInstallation() {
        return ParseInstallation.getCurrentInstallation();
    }

    private void subscribeToChannel(String listeningChannel) {
        ParsePush.subscribeInBackground(listeningChannel);
    }

    /**
     * Returns a queue containing yet unprocessed messages.
     * @return a Queue containing incoming data.
     */
    public Queue<String> getDataQueue() {
        return incomingData;
    }
}
