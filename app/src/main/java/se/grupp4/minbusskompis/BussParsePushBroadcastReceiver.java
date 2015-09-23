package se.grupp4.minbusskompis;


import android.content.Context;
import android.content.Intent;

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
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            String data = json.getString("data");
            BussParse.getInstance(context).dataReceived(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
