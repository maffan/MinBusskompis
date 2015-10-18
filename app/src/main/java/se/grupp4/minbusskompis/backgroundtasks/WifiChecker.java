package se.grupp4.minbusskompis.backgroundtasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Tobias on 2015-10-07.
 */
public class WifiChecker implements Runnable {

    private static final String TAG = "WifiChecker";
    private WifiManager wifiManager;
    private BroadcastReceiver wifiReceiver;
    private String macAdress;
    ArrayList<String> macAdresses;
    private Context currentContext;

    //Skapa kontroll mot ifall man lämnar wifi
    public WifiChecker(Context currentContext, Intent nextIntent, String macAdress){
        this.currentContext = currentContext;
        this.macAdress = macAdress;
        this.wifiManager = (WifiManager) currentContext.getSystemService(Context.WIFI_SERVICE);

        wifiReceiver = new WifiCheckerLeaveReceiver(nextIntent, 2, macAdress, wifiManager);
        currentContext.registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    //Skapar koll mot vanligt wifi
    public WifiChecker(Context currentContext, Intent nextIntent, ArrayList<String> macAdresses){
        this.currentContext = currentContext;
        this.macAdresses = macAdresses;
        this.wifiManager = (WifiManager) currentContext.getSystemService(Context.WIFI_SERVICE);

        wifiReceiver = new WifiCheckerLookReceiver(nextIntent, macAdresses, wifiManager);
        currentContext.registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void unregReceiver(){
        Log.d(TAG,"Unregistered receiver: " + wifiReceiver);
        currentContext.unregisterReceiver(wifiReceiver);
    }

    //Initates the scan, the reciver will do the work when scan is complete.
    @Override
    public void run() {
        if (!wifiManager.isWifiEnabled()) {
            Log.v(TAG, "Wifi not enabled");
            return;
        }
        wifiManager.startScan();
    }

}
