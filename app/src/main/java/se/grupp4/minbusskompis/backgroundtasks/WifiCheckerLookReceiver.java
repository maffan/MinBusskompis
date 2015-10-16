package se.grupp4.minbusskompis.backgroundtasks;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.grupp4.minbusskompis.TravelingData;

/**
 * Created by Tobias on 2015-10-16.
 */
public class WifiCheckerLookReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiCheckIfLeaveReceiver";
    private final Intent nextIntent;
    private HashMap<String,Integer> localWifis;
    private int wifiMatchCounter = 0;
    private int MATCH_LIMIT = 2;
    ArrayList<String> macAdresses;
    private WifiManager wifiManager;

    public WifiCheckerLookReceiver(Intent nextIntent, int MATCH_LIMIT, ArrayList<String> macAdresses, WifiManager wifiManager) {
        this.nextIntent = nextIntent;
        this.MATCH_LIMIT = MATCH_LIMIT;
        this.macAdresses = macAdresses;
        this.wifiManager = wifiManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Scan is complete, get list from scan.
        if(checkIfClose(macAdresses,getWifiList())){
            Log.d(TAG, "Mac matches, bus is near");
            wifiMatchCounter++;

        }
        else{
            Log.d(TAG, "No MAC match waiting for bus");
        }

        //Byt aktivitet, kalla på finish
        if(wifiMatchCounter >= MATCH_LIMIT){
            Log.d(TAG, "Wifi match counter reached, child on bus");
            TravelingData data = ((TravelingData) nextIntent.getParcelableExtra("data"));
            data.currentBusMacAdress = "abcdef123456789";
            nextIntent.putExtra("data", data);
            context.startActivity(nextIntent);
            if (context instanceof Activity) {
                ((Activity)context).finish();
            }else Log.d(TAG,"Context was not an activity");
        }
    }

    //Puts scanresults in hashmap, bssid/level.
    private HashMap<String, Integer> getWifiList() {
        //Replace all non alphanumeric chars with ""
        String regex = "[^A-Za-z0-9]";
        localWifis = new HashMap<>();
        List<ScanResult> wifiScanList = wifiManager.getScanResults();

        for (int i = 0; i < wifiScanList.size(); i++) {
            String currMac = wifiScanList.get(i).BSSID.replaceAll(regex, "");
            localWifis.put(currMac, WifiManager.calculateSignalLevel(wifiScanList.get(i).level, 100));
        }
        return localWifis;
    }

    private boolean checkIfClose(ArrayList<String> macAdresses, Map<String, Integer> scannedAccessPoints) {
        if (scannedAccessPoints == null) {
            Log.v((this).getClass().getSimpleName(), "No wifilist");
            return false;
        }
        for(String scannedMac : scannedAccessPoints.keySet()){
//                Log.d(TAG, "Matching: " + mac + " to: " + scannedAccessPoints);
            if(macAdresses.contains(scannedMac)){
                return true;
            }
        }
        return false;
    }

}
