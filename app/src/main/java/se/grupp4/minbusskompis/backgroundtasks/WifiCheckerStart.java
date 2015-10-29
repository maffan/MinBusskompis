package se.grupp4.minbusskompis.backgroundtasks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
    WifiCheckerStart
    Used to either look for valid wifis or check if you are leaving a specific one.
 */
public class WifiCheckerStart {
    private static final String TAG = "WifiCheckerStart";
    ScheduledThreadPoolExecutor poolExecutor;
    WifiChecker wifiChecker;

    public WifiCheckerStart(){
        poolExecutor = new ScheduledThreadPoolExecutor(1);
    }

    /**
     * Look for valid wifis
     * @param currentContext Current activity, will be used when switching activities
     * @param nextIntent What the next activity should be (when finding a persistent match)
     * @param macAdresses List of valid mac addresses
     * @param delay How often in seconds, you should fetch new wifis
     * @return
     */
    public boolean startLookForWifi(Context currentContext, Intent nextIntent, ArrayList<String> macAdresses, int delay){
        wifiChecker = new WifiChecker(currentContext, nextIntent, macAdresses);
        poolExecutor.scheduleWithFixedDelay(wifiChecker,0,delay, TimeUnit.SECONDS);
        return true;
    }

    /**
     * Keep track if you are leaving a wifi
     * @param currentContext Current activity, will be used when switching activities
     * @param nextIntent What the next activity should be (when finding a persistent match)
     * @param macAdress What mac address you should keep track of
     * @param delay How often in seconds, you should fetch new wifis
     * @return
     */
    public boolean startCheckIfLeave(Context currentContext, Intent nextIntent, String macAdress, int delay){
        wifiChecker = new WifiChecker(currentContext, nextIntent, macAdress);
        poolExecutor.scheduleWithFixedDelay(wifiChecker,0,delay, TimeUnit.SECONDS);
        return true;
    }

    /**
     * Shutdown WifiChecker, remove receivers, stop threadpool
     */
    public void shutdown(){
        unregisterReceivers();
        poolExecutor.shutdown();
    }


    /**
     * Unregister receivers if started
     */
    public void unregisterReceivers(){
        Log.d(TAG,"Unregister receivers");
       if(wifiChecker != null){
           Log.d(TAG,"WifiChecker not null, removing receiver");
           wifiChecker.unregReceiver();
           wifiChecker = null;
       }
    }

}
