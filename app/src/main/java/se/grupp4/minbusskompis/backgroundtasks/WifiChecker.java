package se.grupp4.minbusskompis.backgroundtasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import se.grupp4.minbusskompis.ui.StartActivity;

/**
 * Created by Tobias on 2015-10-07.
 */
public class WifiChecker implements Runnable {

    private static final String TAG = "WifiChecker";
    private WifiManager wifiManager;
    private WifiCheckerReceiver wifiReceiver;
    private ArrayList<String> macAdresses;
    private static int wifiMatchCounter = 0;
    private static final int MATCH_LIMIT = 2;


    public WifiChecker (Context currentContext, Intent nextIntent, ArrayList<String> macAdresses){
        this.wifiManager = (WifiManager) currentContext.getSystemService(Context.WIFI_SERVICE);
        //Initera receiver som triggas då scanresults finns, dvs då sökningen är klar.
        wifiReceiver = new WifiCheckerReceiver(nextIntent);
        currentContext.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        this.macAdresses = macAdresses;
    }


    //Initates the scan, the reciver will do the work when scan is complete.
    @Override
    public void run() {
        if (wifiManager.isWifiEnabled()) {
            Log.v(TAG, "Wifi not enabled");
            return;
        }
        wifiManager.startScan();
    }

    public class WifiCheckerReceiver extends BroadcastReceiver {
        private final Intent nextIntent;
        private HashMap<String,Integer> localWifis;



        public WifiCheckerReceiver(Intent nextIntent) {
            this.nextIntent = nextIntent;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //Scan is complete, get list from scan.
            if(checkIfClose(macAdresses,getWifiList())){
                Log.d(TAG, "Mac matches, wifi is close, increasing counter");
                wifiMatchCounter++;
            }
            else{
                Log.d(TAG, "No MAC match");
            }

            if(wifiMatchCounter >= MATCH_LIMIT){
                Log.d(TAG, "Wifi match counter reached, child on bus");
                context.startActivity(nextIntent);
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
                localWifis.put(currMac, wifiScanList.get(i).level);
            }
            return localWifis;
        }

        private boolean checkIfClose(ArrayList<String> bussMacAdresses, Map<String, Integer> accessPoints) {
            //Return if no list
            if (accessPoints == null) {
                Log.v((this).getClass().getSimpleName(), "No wifilist");
                return false;
            }

            //Check if bssid exist in accessPoints
            for(String key : accessPoints.keySet()){
                if(accessPoints.containsKey(key)){
                    return true;
                }
            }
            return false;
        }
    }
}
