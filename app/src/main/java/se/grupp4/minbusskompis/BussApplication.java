package se.grupp4.minbusskompis;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import java.lang.reflect.Method;
import java.util.List;

import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;
import se.grupp4.minbusskompis.parsebuss.AsyncTaskCompleteCallback;
import se.grupp4.minbusskompis.parsebuss.BussData;

/**
 * Created by Marcus on 9/29/2015.
 */
public class BussApplication extends Application {
    private static final String TAG = "APPLICATION";
    private Context context = this;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_APPEND);

        if(!checkIfDataEnabled()){
            Log.d(TAG,"Data disabled");
            sharedPreferences.edit().putBoolean("data_enabled", false).apply();
            //Toast.makeText(context, "Enable data to start application", Toast.LENGTH_SHORT).show();
            //System.exit(0);
        }else{
            Log.d(TAG,"Data enabled");
            sharedPreferences.edit().putBoolean("data_enabled", true).apply();
            initParseAndInitData();
            setDefaultSubscriptions();
        }
    }

    private void initParseAndInitData() {
        Parse.initialize(this);
        BussData.getInstance().fetchData(new AsyncTaskCompleteCallback() {
            @Override
            public void done() {
                setDefaultTravelStatus();
            }
        });
    }

    private void setDefaultTravelStatus() {
        BussData.getInstance().setStatusForSelfAndNotifyParents(0);
    }

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
            Log.d(TAG,"Error checking mobile datat status");
        }
        return mobileDataEnabled;
    }
}