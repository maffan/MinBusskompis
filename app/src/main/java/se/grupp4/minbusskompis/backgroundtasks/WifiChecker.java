package se.grupp4.minbusskompis.backgroundtasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Tobias on 2015-10-07.
 */
public class WifiChecker implements Runnable {

    private WifiManager wifiManager;
    private WifiCheckerReceiver wifiReceiver;
    private String macAdress;


    public WifiChecker (Context currentContext, Context newContext, String macAdress){
        this.wifiManager = (WifiManager) currentContext.getSystemService(Context.WIFI_SERVICE);
        //Initera receiver som triggas då scanresults finns, dvs då sökningen är klar.
        wifiReceiver = new WifiCheckerReceiver(newContext);
        currentContext.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }


    //Initates the scan, the reciver will do the work when scan is complete.
    @Override
    public void run() {
        if (wifiManager.isWifiEnabled()) {
            Log.v((this).getClass().getSimpleName(), "Wifi not enabled");
            return;
        }
        wifiManager.startScan();
    }

    public class WifiCheckerReceiver extends BroadcastReceiver {
        private HashMap<String,Integer> localWifis;
        private int wifiMatchCounter = 0;
        private Context newContext;

        public WifiCheckerReceiver(Context newContext) {
            this.newContext = newContext;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //Scan is complete, get list from scan.
            if(checkIfClose(macAdress,getWifiList())){
                Log.v((this).getClass().getSimpleName(), "Mac matches, wifi is close");
                wifiMatchCounter++;
            }
            else{
                Log.v((this).getClass().getSimpleName(), "No match");
            }

            if(wifiMatchCounter >= 10){
                //Switch activity if 10 matches is correct to newContext
            }
        }

        //Puts scanresults in hashmap, bssid/level.
        private HashMap<String, Integer> getWifiList() {
            localWifis = new HashMap<>();
            List<ScanResult> wifiScanList = wifiManager.getScanResults();

            for (int i = 0; i < wifiScanList.size(); i++) {
                localWifis.put(wifiScanList.get(i).BSSID, wifiScanList.get(i).level);
            }
            return localWifis;
        }

        private boolean checkIfClose(String compareBSSID, Map<String, Integer> accessPoints) {
            //Return if no list
            if (accessPoints == null) {
                Log.v((this).getClass().getSimpleName(), "No wifilist");
                return false;
            }

            //Check if bssid exist in accessPoints
            if(accessPoints.containsKey(compareBSSID)){
                return true;
            }
            else{
                return false;
            }
        }
    }
}
