package se.grupp4.minbusskompis;


import android.content.Context;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Marcus on 9/21/2015.
 *
 * A singleton for handling all of the Parse interactions.
 *
 * If you want to receive notifications sent to this device, simply implement
 * the Observer interface and add yourself as a listener to this singleton.
 * Set a listening channel and your observer will be called when a message arrives.
 *
 * To send messages just set a sending channel and then use sendData.
 */
public class BussParse extends Observable {
    private static BussParse ourInstance;

    private static Context currentContext;

    private String listeningChannel;
    private String sendingChannel;

    private Queue<String> incomingData;

    /**
     * Initiates Parse and returns the instance.
     * @param context Needed for Parse initiation
     * @return the instance of this class
     */
    public static BussParse getInstance(Context context) {
        currentContext = context;
        if(ourInstance == null){
            ourInstance = new BussParse();
        }
        return ourInstance;
    }

    private BussParse(){
        Parse.initialize(currentContext);
        incomingData = new ConcurrentLinkedQueue<>();
    }

    /**
     * Used to send data.
     * The data will be sent through the channel specified in the setChannel method
     * @param data data so be sent.
     * @return True if successful. False if an exception occurred.
     */
    public boolean sendData(String data){
        try {
            ParsePush push = new ParsePush();
            push.setChannel(sendingChannel);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("incomingData", data);

            push.setData(jsonObject);
            push.sendInBackground();
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Used to send data.
     * Use this method if you want to provide a callback method to be called when the message
     * has been successfully sent.
     * @param data Data to be sent
     * @param callback callback object to be called when the message has been sent.
     * @return True if successful. False if an exception occurred.
     */
    public boolean sendData(String data, SendCallback callback){
        try {
            ParsePush push = new ParsePush();
            push.setChannel(sendingChannel);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("incomingData", data);

            push.setData(jsonObject);
            push.sendInBackground(callback);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method should only be called by the applications ParsePushBroadcastReceiver.
     *
     * Enqueues incoming data and notifies observers that new data is available.
     * @param data The data to be enqueued
     */
    public void dataReceived(String data){
        this.incomingData.add(data);
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
        ParseInstallation.getCurrentInstallation().getList("channels").clear();
        ParsePush.subscribeInBackground(listeningChannel);
    }

    /**
     * Returns a queue containing yet unprocessed messages.
     * @return a Queue containing incoming data.
     */
    public Queue<String> getData() {
        return incomingData;
    }
}
