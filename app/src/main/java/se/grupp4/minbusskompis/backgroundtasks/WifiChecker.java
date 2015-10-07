package se.grupp4.minbusskompis.backgroundtasks;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Tobias on 2015-10-07.
 */
public class WifiChecker implements Runnable {

    private WifiManager wifiManager;
    private HashMap<String,Integer> localWifis;
    private WifiReceiver wifiReceiver;


    public WifiChecker (Activity activity){
        this.wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        //Initera receiver som triggas då scanresults finns, dvs då sökningen är klar.
        wifiReceiver = new WifiReceiver();
        activity.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void run() {
        return;
    }

    public HashMap<String,Integer> getWifiList(){
        if(wifiManager.isWifiEnabled()){
            Log.v((this).getClass().getSimpleName(), "Wifi not enabled");
            return null;
        }

        localWifis = new HashMap<>();
        wifiManager.startScan();
        List<ScanResult> wifiScanList = wifiManager.getScanResults();

        for(int i = 0; i < wifiScanList.size(); i++){
            localWifis.put(wifiScanList.get(i).BSSID, wifiScanList.get(i).level);
        }
        return localWifis;
    }

    public boolean checkIfClose(String compareBSSID, Set<String> accessPoints){
        //Return if no list
        if(accessPoints == null){
            return false;
        }

        //Check if bssid exist in accessPoints
        for(String aS : accessPoints){
            //Jämför ifall bsssid finns
            if(aS.toString().trim().equals(compareBSSID.toString().trim())){
                return true;
            }
        }
        return false;
    }
}
