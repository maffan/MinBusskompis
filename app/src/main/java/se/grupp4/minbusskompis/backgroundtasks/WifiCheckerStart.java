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
    WifiChecker wifiChecker;
    int delay;

    public WifiCheckerStart(Context currentContext, Intent nextIntent, ArrayList<String> macAdresses, int delay){
        wifiChecker = new WifiChecker(currentContext, nextIntent, macAdresses);
        this.delay = delay;
        poolExecutor = new ScheduledThreadPoolExecutor(1);
    }

    public boolean start(){
        poolExecutor.scheduleWithFixedDelay(wifiChecker,0,delay, TimeUnit.SECONDS);
        return true;
    }

}
