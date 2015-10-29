package se.grupp4.minbusskompis.backgroundtasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;

/*
    WifiChecker
    Class used to check if you either are close to a electricity wifi or leaving a specific one.
    To initate looking for wifis, create a WifiChecker with an ArrayList.
    To keep track if you leave initiate with only a mac address
 */
public class WifiChecker implements Runnable {

    private static final String TAG = "WifiChecker";
    private WifiManager wifiManager;
    private BroadcastReceiver wifiReceiver;
    private String macAdress;
    ArrayList<String> macAdresses;
    private Context currentContext;

    //Create WifiChecker that will check if you leave a specific wifi
    public WifiChecker(Context currentContext, Intent nextIntent, String macAdress){
        this.currentContext = currentContext;
        this.macAdress = macAdress;
        this.wifiManager = (WifiManager) currentContext.getSystemService(Context.WIFI_SERVICE);

        wifiReceiver = new WifiCheckerLeaveReceiver(nextIntent, 2, macAdress, wifiManager);
        currentContext.registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    //Create WIfiChecker that will look for wifis included in macAddresses
    public WifiChecker(Context currentContext, Intent nextIntent, ArrayList<String> macAddresses){
        this.currentContext = currentContext;
        this.macAdresses = macAddresses;
        this.wifiManager = (WifiManager) currentContext.getSystemService(Context.WIFI_SERVICE);

        wifiReceiver = new WifiCheckerLookReceiver(nextIntent, macAddresses, wifiManager);
        currentContext.registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    /**
     * Remove broadcastreceiver
     */
    public void unregReceiver(){
        Log.d(TAG,"Unregistered receiver: " + wifiReceiver);
        currentContext.unregisterReceiver(wifiReceiver);
    }


    /**
     * Start a wifi scan, the check will occour on the broadcastreceiver callback
     */
    @Override
    public void run() {
        if (!wifiManager.isWifiEnabled()) {
            Log.v(TAG, "Wifi not enabled");
            return;
        }
        wifiManager.startScan();
    }

}
