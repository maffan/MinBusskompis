package se.grupp4.minbusskompis.parsebuss;

import android.support.annotation.NonNull;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Marcus on 10/4/2015.
 */
public class BussParseSyncMessenger {
    private static final int TRIES_IN_SECONDS = 30;
    private static final String REQUEST_STRING = "SyncRequest";
    private static final String RESPONSE_STRING = "SyncResponse";
    private static final String TAG = "SYNC_MESSENGER";
    public static final String TYPE_FIELD = "type";
    public static final String SENDER_FIELD = "sender";

    private JSONObject incomingSyncMessage;
    private String syncInstallationId;

    private ReentrantLock lock;
    private Condition syncMessageArrived;

    public BussParseSyncMessenger(){
        lock = new ReentrantLock(true);
        syncMessageArrived = lock.newCondition();
    }

    public void sendSyncRequest(String syncCode){
        try {
            SendMessageToChannelWithType(getSyncCodeAsChannel(syncCode), REQUEST_STRING);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * In parse, a channel name may not start with a digit
     */
    public static String getSyncCodeAsChannel(String syncCode) {
        if(Character.isDigit(syncCode.charAt(0)))
            return "c" + syncCode;
        else
            return syncCode;
    }

    public void sendSyncResponse() {
        try { //"i" is because channel may not start with a number
            SendMessageToChannelWithType("i" + syncInstallationId, RESPONSE_STRING);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SendMessageToChannelWithType(String channel, String type) throws JSONException {
        ParsePush push = createPushWithChannel(channel);
        JSONObject syncObject = createSyncObject(type);
        sendPushWithObject(push, syncObject);
    }

    @NonNull
    private ParsePush createPushWithChannel(String channel) {
        ParsePush push = new ParsePush();
        push.setChannel(channel);
        return push;
    }

    @NonNull
    private JSONObject createSyncObject(String type) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TYPE_FIELD, type);
        jsonObject.put(SENDER_FIELD, ParseInstallation.getCurrentInstallation().getInstallationId());
        return jsonObject;
    }

    private void sendPushWithObject(ParsePush push, JSONObject jsonObject) {
        push.setData(jsonObject);
        try {
            push.send();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Object sent!");
    }

    public boolean waitForSyncMessageAndReturnSuccess(){
        lock.lock();
        int tries = 0;
        while (incomingSyncMessage == null){ //no message has arrived
            try {
                syncMessageArrived.await(1, TimeUnit.SECONDS);
                tries++;
                if(tries > TRIES_IN_SECONDS)
                    break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(incomingSyncMessage != null) { //A message was received
            unpackMessage();
            lock.unlock();
            return true;
        }
        else{   //No message was received
            lock.unlock();
            return false;
        }
    }

    private void unpackMessage() {
        try {
            syncInstallationId = incomingSyncMessage.getString(SENDER_FIELD);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getSyncInstallationId() {
        return syncInstallationId;
    }

    public void setSyncMessage(JSONObject response) {
        lock.lock();
        incomingSyncMessage = response;
        syncMessageArrived.signalAll();
        lock.unlock();
    }
}