package se.grupp4.minbusskompis.parsebuss;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.SharedPreferencesCompat;
import android.util.Log;

import com.parse.ParseInstallation;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.PriorityQueue;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.ui.ParentActiveChild;

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
    private Context context;
    private Intent intent;




    @Override
    protected void onPushOpen(Context context, Intent intent) {
        Intent nextIntent = new Intent(context, ParentActiveChild.class);
        nextIntent.putExtra("child_id",intent.getStringExtra("child_id"));
        nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(nextIntent);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        this.intent = intent;
        this.context = context;
        try {
            Log.d(TAG,"Data received!");
            tryReceive(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void tryReceive(Intent intent) throws JSONException {
        JSONObject data = getDataFromIntent(intent);
        dispatch(data);
    }

    private JSONObject getDataFromIntent(Intent intent) throws JSONException {
        Bundle extras = intent.getExtras();
        return getDataFromParseJsonObject(extras);
    }

    @NonNull
    protected JSONObject getDataFromParseJsonObject(Bundle extras) throws JSONException {
        return new JSONObject(extras.getString("com.parse.Data"));
    }

    private void dispatch(JSONObject data) throws JSONException {
        String type = getTypeFromJSONData(data);
        dispatchDataByType(data, type);
    }

    private String getTypeFromJSONData(JSONObject data) throws JSONException {
        return data.getString("type");
    }

    private void dispatchDataByType(JSONObject messageData, String type) throws JSONException {
        switch (type) {
            case "Message":
                Log.d(TAG, "dispatchDataByType: GOT MESSAGE WITH DATA: "+messageData);
                if (context.getSharedPreferences("MyPreferences",Context.MODE_APPEND).getBoolean("soundsetting",true)) {
                    String from = messageData.getString("from");
                    if(!from.equals(ParseInstallation.getCurrentInstallation().getInstallationId())) {
                        sendToRelationMessenger(messageData);
                        intent.putExtra("child_id",from);
                        super.onPushReceive(context, intent);
                    }
                }
                break;
            case "SyncRequest":
                sendDataByTypeToSyncMessenger(messageData, BussParseSyncMessenger.REQUEST_TYPE);
                break;
            case "SyncResponse":
                sendDataByTypeToSyncMessenger(messageData, BussParseSyncMessenger.RESPONSE_TYPE);
                break;
            case "PositionUpdate":
                BussRelationMessenger.getInstance().dataReceived(messageData);
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
        if (type == BussParseSyncMessenger.REQUEST_TYPE) {
            provider.getSyncMessenger().setSyncMessage(messageData);
        } else if (type == BussParseSyncMessenger.RESPONSE_TYPE) {
            provider.getSyncMessenger().setSyncMessage(messageData);
        }
    }
}
