package se.grupp4.minbusskompis;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import java.lang.reflect.Method;
import java.util.List;

import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;
import se.grupp4.minbusskompis.parsebuss.AsyncTaskCompleteCallback;
import se.grupp4.minbusskompis.parsebuss.ParseCloudManager;

/*
    BussApplication
    Startup for Min Busskompis
 */
public class BussApplication extends Application {
    private static final String TAG = "BussApplication";
    private Context context = this;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_APPEND);

        if(!checkIfDataEnabled()){
            Log.d(TAG,"Data disabled");
            sharedPreferences.edit().putBoolean("data_enabled", false).apply();
        }else{
            Log.d(TAG,"Data enabled");
            sharedPreferences.edit().putBoolean("data_enabled", true).apply();
            initParseAndInitData();
            setDefaultSubscriptions();
        }
    }

    /**
     * Checks if data traffic is enabled, only checks for mobile data and not wifi connections as it is
     */
    public boolean checkIfDataEnabled(){
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm1 = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm1.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm1);
        } catch (Exception e) {
            Log.d(TAG,"Error checking mobile data status");
        }
        return mobileDataEnabled;
    }

    /**
     *  Makes sure that Parse is initialized and that the cloud manager has the latest data.
     */
    private void initParseAndInitData() {
        Parse.initialize(this);
        ParseCloudManager.getInstance().fetchLatestDataFromCloud(new AsyncTaskCompleteCallback() {
            @Override
            public void done() {
                setDefaultTravelStatus();
            }
        });
    }

    /**
     * Makes sure that the initial state of the device is not in a traveling mode.
     */
    private void setDefaultTravelStatus() {
        ParseCloudManager.getInstance().setStatusForSelfAndNotifyParents(0);
    }

    /**
     * Makes sure that this device is listening to the channels it should
     */
    private void setDefaultSubscriptions() {
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        List channels = parseInstallation.getList("channels");
        if (channels != null) {
            channels.clear();
            parseInstallation.put("channels", channels);
            try {
                parseInstallation.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        ParsePush.subscribeInBackground("i" + ParseInstallation.getCurrentInstallation().getInstallationId());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        stopService(new Intent(this, UpdateLocToParseService.class));
    }
}