package se.grupp4.minbusskompis.parsebuss;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParseInstallation;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

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
    public static final String PARSE_DATA_KEY = "com.parse.Data";
    public static final String TYPE_FIELD = "type";
    public static final String MESSAGE_TYPE = "Message";
    public static final String SOUNDSETTING = "soundsetting";
    public static final String PREFERENCES = "MyPreferences";
    public static final String FROM_FIELD = "from";
    public static final String CHILD_ID_FIELD = "child_id";
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
        JSONObject data = getJSONDataFromIntent(intent);
        dispatch(data);
    }

    private JSONObject getJSONDataFromIntent(Intent intent) throws JSONException {
        Bundle extras = intent.getExtras();
        return getJSONDataFromBundle(extras);
    }

    private JSONObject getJSONDataFromBundle(Bundle extras) throws JSONException {
        return new JSONObject(extras.getString(PARSE_DATA_KEY));
    }

    private void dispatch(JSONObject data) throws JSONException {
        String type = getTypeFromJSONData(data);
        dispatchDataByType(data, type);
    }

    private String getTypeFromJSONData(JSONObject data) throws JSONException {
        return data.getString(TYPE_FIELD);
    }

    private void dispatchDataByType(JSONObject messageData, String type) throws JSONException {
        switch (type) {
            case MESSAGE_TYPE:
                Log.d(TAG, "dispatchDataByType: GOT MESSAGE WITH DATA: "+messageData);
                if (soundSettingsEnabled()) {
                    showPushIfNotFromSelf(messageData);
                }
                break;
            case "SyncRequest":
                sendDataToSyncMessenger(messageData);
                break;
            case "SyncResponse":
                sendDataToSyncMessenger(messageData);
                break;
            case "PositionUpdate":
                BussRelationMessenger.getInstance().dataReceived(messageData);
                break;
            default:
                Log.d(TAG,type+" is not a valid message type");
        }
    }

    private boolean soundSettingsEnabled() {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_APPEND).getBoolean(SOUNDSETTING,true);
    }

    private void showPushIfNotFromSelf(JSONObject messageData) throws JSONException {
        String from = messageData.getString(FROM_FIELD);
        if(!from.equals(ParseInstallation.getCurrentInstallation().getInstallationId())) {
            sendToRelationMessenger(messageData);
            intent.putExtra(CHILD_ID_FIELD,from);
            super.onPushReceive(context, intent);
        }
    }

    private void sendDataToSyncMessenger(JSONObject messageData) {
        try {
            trySendToSyncMessenger(messageData);
        } catch (NoMessengerPresentException e) {
            e.printStackTrace();
            Log.d(TAG,"Received sync message, but no SyncMessenger was instantiated");
        }
    }

    private void trySendToSyncMessenger(JSONObject messageData) throws NoMessengerPresentException {
        getMessengerProvider();
        enqueueDataIfMessengerExists(messageData);
    }

    private void getMessengerProvider() {
        provider = BussSyncMessengerProvider.getInstance();
    }

    private void enqueueDataIfMessengerExists(JSONObject messageData) throws NoMessengerPresentException {
        if (provider.hasMessenger())
            provider.getSyncMessenger().setSyncMessage(messageData);
    }

    private void sendToRelationMessenger(JSONObject messageData) {
        BussRelationMessenger.getInstance().dataReceived(messageData);
    }
}
