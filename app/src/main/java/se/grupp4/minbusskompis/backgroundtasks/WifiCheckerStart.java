package se.grupp4.minbusskompis.backgroundtasks;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tobias on 2015-10-09.
 */


//Create service with macAdress that should be polled against, pass in delay and what activity that should be switched to if matched
public class WifiCheckerStart {
    ScheduledThreadPoolExecutor poolExecutor;
    WifiCheckerLookForWifi wifiCheckerLookForWifi;
    WifiCheckerCheckIfLeave wifiCheckerCheckIfLeave;

    public WifiCheckerStart(){
        poolExecutor = new ScheduledThreadPoolExecutor(1);
    }

    public boolean startLookForWifi(Context currentContext, Intent nextIntent, ArrayList<String> macAdresses, int delay){
        wifiCheckerLookForWifi = new WifiCheckerLookForWifi(currentContext, nextIntent, macAdresses);
        poolExecutor.scheduleWithFixedDelay(wifiCheckerLookForWifi,0,delay, TimeUnit.SECONDS);
        return true;
    }

    public boolean startCheckIfLeave(Context currentContext, Intent nextIntent, ArrayList<String> macAdresses, int delay){
        wifiCheckerCheckIfLeave = new WifiCheckerCheckIfLeave(currentContext, nextIntent, macAdresses);
        poolExecutor.scheduleWithFixedDelay(wifiCheckerCheckIfLeave,0,delay, TimeUnit.SECONDS);
        return true;
    }

    public void shutdown(){
        poolExecutor.shutdown();
    }

}
