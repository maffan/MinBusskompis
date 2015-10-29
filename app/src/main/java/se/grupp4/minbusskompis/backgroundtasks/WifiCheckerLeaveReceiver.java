package se.grupp4.minbusskompis.backgroundtasks;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.grupp4.minbusskompis.TravelingData;

/*
     WifiCheckerLeaveReceiver
     BroadcastReceiver used to check if your leave a specific wifi.
 */
public class WifiCheckerLeaveReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiCheckIfLeaveReceiver";
    private final Intent nextIntent;
    private HashMap<String,Integer> localWifis;
    private int wifiMatchCounter = 0;
    private int MATCH_LIMIT = 2;
    private String matchMac;
    private WifiManager wifiManager;

    public WifiCheckerLeaveReceiver(Intent nextIntent, int MATCH_LIMIT, String matchMac, WifiManager wifiManager) {
        this.nextIntent = nextIntent;
        this.MATCH_LIMIT = MATCH_LIMIT;
        this.matchMac = matchMac;
        this.wifiManager = wifiManager;
    }

    /**
     * On each new receive, the new wifis will be matched against the wifi you are polling against
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //Scan is complete, get list from scan.
        if(checkIfClose(matchMac,getWifiList())){
            Log.d(TAG, "Mac matches, wifi is close, still on bus");
        }
        else{
            Log.d(TAG, "No MAC match, leaving bus, inc counter");
            wifiMatchCounter++;
        }

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


    /**
     * Put mac addresses from scan results into a HashMap
     * @return HashMap
     */
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

    /**
     * Determine if a mac exist in a list of mac addresses
     * @param currMac Mac address to poll against
     * @param scannedAccessPoints Results from latest scan
     * @return If currMac is close to scannedAccessPoints
     */
    private boolean checkIfClose(String currMac, Map<String, Integer> scannedAccessPoints) {
        if (scannedAccessPoints == null) {
            Log.v((this).getClass().getSimpleName(), "No wifilist");
            return false;
        }
        for(String scannedMac : scannedAccessPoints.keySet()){
//                Log.d(TAG, "Matching: " + mac + " to: " + scannedAccessPoints);
            if(scannedMac.equals(currMac)){
                return true;
            }
        }
        return false;
    }

}
