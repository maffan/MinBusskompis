package se.grupp4.minbusskompis.BussParse;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marcus on 9/21/2015.
 * <p/>
 * A broadcast receiver that intercepts all incoming pushes from Parse and
 * sends them to the BussRelationMessenger singleton.
 * <p/>
 * This class must be set as receiver in the manifest file.
 */
public class BussParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {

    private static final String TAG = "RECEIVER";
    private BussSyncMessengerProvider provider;

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        try {
            tryReceive(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void tryReceive(Intent intent) throws JSONException {
        JSONObject data = getDataFromIntent(intent);
        dispatch(data);
        Log.d(TAG, "Data: '" + data + "' received");
    }

    private JSONObject getDataFromIntent(Intent intent) throws JSONException {
        Bundle extras = intent.getExtras();
        JSONObject json = getDataFromParseJsonObject(extras);
        return getDataFromJSONData(json);
    }

    @NonNull
    protected JSONObject getDataFromParseJsonObject(Bundle extras) throws JSONException {
        return new JSONObject(extras.getString("com.parse.Data"));
    }

    private void dispatch(JSONObject data) throws JSONException {
        String type = getTypeFromJSONData(data);
        JSONObject messageData = getDataFromJSONData(data);
        dispatchDataByType(messageData, type);
    }

    private String getTypeFromJSONData(JSONObject data) throws JSONException {
        return data.getString("type");
    }

    private JSONObject getDataFromJSONData(JSONObject data) throws JSONException {
        return data.getJSONObject("data");
    }

    private void dispatchDataByType(JSONObject messageData, String type) {
        switch (type) {
            case "Message":
                sendToRelationMessenger(messageData);
                break;
            case "SyncRequest":
                sendDataByTypeToSyncMessenger(messageData, SyncMessenger.REQUEST);
                break;
            case "SyncResponse":
                sendDataByTypeToSyncMessenger(messageData, SyncMessenger.RESPONSE);
                break;
            default:
                Log.d(TAG,type+" is not a valid message type");
        }
    }

    private void sendToRelationMessenger(JSONObject messageData) {
        BussRelationMessenger.getInstance().dataReceived(messageData);
    }

    private void sendDataByTypeToSyncMessenger(JSONObject messageData, int type) {
        try {
            trySendToSyncMessenger(messageData, type);
        } catch (NoMessengerPresentException e) {
            e.printStackTrace();
            Log.d(TAG,"Received sync message, but no SyncMessenger was instantiated");
        }
    }

    private void trySendToSyncMessenger(JSONObject messageData, int type) throws NoMessengerPresentException {
        getProvider();
        enqueueDataIfMessengerExists(messageData, type);
    }

    private void getProvider() {
        provider = BussSyncMessengerProvider.getInstance();
    }

    private void enqueueDataIfMessengerExists(JSONObject messageData, int type) throws NoMessengerPresentException {
        if (provider.hasMessenger())
            enqueueSyncDataByType(messageData, type);
        else
            throw new NoMessengerPresentException("No Messenger present");
    }

    private void enqueueSyncDataByType(JSONObject messageData, int type) throws NoMessengerPresentException {
        if (type == SyncMessenger.REQUEST) {
            provider.getSyncMessenger().setSyncMessage(messageData);
        } else if (type == SyncMessenger.RESPONSE) {
            provider.getSyncMessenger().setSyncMessage(messageData);
        }
    }
}