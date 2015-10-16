package se.grupp4.minbusskompis.backgroundtasks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tobias on 2015-10-09.
 */


//Create service with macAdress that should be polled against, pass in delay and what activity that should be switched to if matched
public class WifiCheckerStart {
    private static final String TAG = "WifiCheckerStart";
    ScheduledThreadPoolExecutor poolExecutor;
    WifiChecker wifiChecker;

    public WifiCheckerStart(){
        poolExecutor = new ScheduledThreadPoolExecutor(1);
    }

    public boolean startLookForWifi(Context currentContext, Intent nextIntent, ArrayList<String> macAdresses, int delay){
        wifiChecker = new WifiChecker(currentContext, nextIntent, macAdresses);
        poolExecutor.scheduleWithFixedDelay(wifiChecker,0,delay, TimeUnit.SECONDS);
        return true;
    }

    public boolean startCheckIfLeave(Context currentContext, Intent nextIntent, String macAdress, int delay){
        wifiChecker = new WifiChecker(currentContext, nextIntent, macAdress);
        poolExecutor.scheduleWithFixedDelay(wifiChecker,0,delay, TimeUnit.SECONDS);
        return true;
    }

    public void shutdown(){
        unregisterReceivers();
        poolExecutor.shutdown();
    }

    //Call to unregister receivers
    public void unregisterReceivers(){
        Log.d(TAG,"Unregister receivers");
       if(wifiChecker != null){
           Log.d(TAG,"WifiChecker not null, removing receiver");
           wifiChecker.unregReceiver();
           wifiChecker = null;
       }
    }

}
