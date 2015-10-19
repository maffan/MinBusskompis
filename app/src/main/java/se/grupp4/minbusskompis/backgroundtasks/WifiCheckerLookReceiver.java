package se.grupp4.minbusskompis.backgroundtasks;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.grupp4.minbusskompis.TravelingData;

/**
 * Created by Tobias on 2015-10-16.
 */
public class WifiCheckerLookReceiver extends BroadcastReceiver {
    private static final int MATCH_LIMIT = 2;
    public static final int WIFI_STRENGTH_THRESHOLD = 70;
    public static final int NUM_WIFI_STRENGTH_LEVELS = 100;
    private static final String TAG = "WifiCheckIfLeaveReceiver";
    public static final String TRAVELING_DATA_FIELD = "data";
    public static final String ALPHA_NUM_ONLY_REGEX = "[^A-Za-z0-9]";
    private final Intent intentForNextActivity;
    private HashMap<String,Integer> wifiHits;
    private ArrayList<String> validMacAddresses;
    private WifiManager wifiManager;
    private String bussMacMatch;

    public WifiCheckerLookReceiver(Intent intentForNextActivity, ArrayList<String> validMacAddresses, WifiManager wifiManager) {
        this.intentForNextActivity = intentForNextActivity;
        this.validMacAddresses = validMacAddresses;
        this.wifiManager = wifiManager;
        wifiHits = new HashMap<>(validMacAddresses.size());
        initValidMacWithZeroHits();
    }

    private void initValidMacWithZeroHits() {
        for (String mac : validMacAddresses)
            wifiHits.put(mac, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        checkIfValidMacIsClose();

        if(validMacIsCloseAndPersistent()){
            Log.d(TAG, "Wifi match counter reached, child on bus");

            TravelingData travelingData = putMatchingMacInTravelingData();
            putTravelingDataIntoIntent(travelingData);
            startNextActivityWithProvidedContextAndFinishCurrentActivity(context);
        }
    }

    private void checkIfValidMacIsClose() {
        Map<String, Integer> scanResult = getScanResultMap();
        if (scanResult.size() > 0) {
            for(String scannedMac : scanResult.keySet()){
                //Log.d(TAG, "Matching: " + scannedMac + " to: " + scanResult);
                if(macIsValidAndClose(scanResult, scannedMac)){
                    Log.d(TAG, "checkIfValidMacIsClose: Match for: "+scannedMac);
                    wifiHits.put(scannedMac, wifiHits.get(scannedMac) + 1);
                    Log.d(TAG, "checkIfValidMacIsClose: wifihits is now: "+wifiHits);
                }
            }
        } else {
            Log.d((this).getClass().getSimpleName(), "No wifilist");
        }
    }

    private HashMap<String, Integer> getScanResultMap() {
        HashMap<String, Integer> macStrengthMap = new HashMap<>();

        for (ScanResult scanResult : wifiManager.getScanResults()) {
            String mac = getCleanMac(scanResult);
            int strength = getCalculatedStrength(scanResult);
            macStrengthMap.put(mac, strength);
        }

        return macStrengthMap;
    }

    private String getCleanMac(ScanResult scanResult) {
        return scanResult.BSSID.replaceAll(ALPHA_NUM_ONLY_REGEX, "");
    }

    private int getCalculatedStrength(ScanResult scanResult) {
        return WifiManager.calculateSignalLevel(scanResult.level, NUM_WIFI_STRENGTH_LEVELS);
    }

    private boolean macIsValidAndClose(Map<String, Integer> scanResult, String scannedMac) {
        return validMacAddresses.contains(scannedMac) && scanResult.get(scannedMac) > WIFI_STRENGTH_THRESHOLD;
    }

    private boolean validMacIsCloseAndPersistent() {
        for (String mac : validMacAddresses) {
            if (macReachedLimit(mac)) {
                bussMacMatch = mac;
                return true;
            }
        }
        return false;
    }

    private boolean macReachedLimit(String mac) {
        return wifiHits.get(mac) > MATCH_LIMIT;
    }

    @NonNull
    private TravelingData putMatchingMacInTravelingData() {
        TravelingData data = intentForNextActivity.getParcelableExtra(TRAVELING_DATA_FIELD);
        data.currentBusMacAdress = bussMacMatch;
        return data;
    }

    private void putTravelingDataIntoIntent(TravelingData data) {
        intentForNextActivity.putExtra(TRAVELING_DATA_FIELD, data);
    }

    private void startNextActivityWithProvidedContextAndFinishCurrentActivity(Context context) {
        context.startActivity(intentForNextActivity);
        if (context instanceof Activity) {
            ((Activity)context).finish();
        }else Log.d(TAG, "Context was not an activity");
    }

}
