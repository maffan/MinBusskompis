package se.grupp4.minbusskompis.backgroundtasks;

import android.content.Context;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tobias on 2015-10-09.
 */


//Create service with macAdress that should be polled against, pass in delay and what activity that should be switched to if matched
public class WifiCheckerStart {
    ScheduledThreadPoolExecutor poolExecutor;
    Context context;
    String macAdress;
    WifiChecker wifiChecker;
    int delay;

    public WifiCheckerStart(Context currentContext, Context newContext, String macAdress, int delay){
        wifiChecker = new WifiChecker(currentContext, newContext, macAdress);
        this.delay = delay;
        poolExecutor = new ScheduledThreadPoolExecutor(1);
    }

    public boolean start(){
        poolExecutor.scheduleWithFixedDelay(wifiChecker,0,delay, TimeUnit.SECONDS);
        return true;
    }

}
