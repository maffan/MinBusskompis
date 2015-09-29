package se.grupp4.minbusskompis;


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
 *
 * A broadcast receiver that intercepts all incoming pushes from Parse and
 * sends them to the BussParse singleton.
 *
 * This class must be set as receiver in the manifest file.
 */
public class BussParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        try {
            Bundle extras = intent.getExtras();
            JSONObject json = getJsonObject(extras);
            String data = json.getString("data");
            BussParse.getInstance(context).dataReceived(data);
            Log.d("RECEIVER","Data: '"+data+"' received");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    protected JSONObject getJsonObject(Bundle extras) throws JSONException {
        return new JSONObject(extras.getString("com.parse.Data"));
    }
}
