package se.grupp4.minbusskompis.backgroundtasks;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
/*
    UpdateLocToParseService
    Service that will send location and modeupdates to parse and parents.
 */
public class UpdateLocToParseService extends Service {
    private static String TAG = "UpdateLocToParseService";
    private static final int SEND_DELAY = 1000*30;
    private final IBinder mBinder = new UpdateLocBinder();
    private UpdateLocGpsAndSettings updateLocGpsAndSettings;

    public UpdateLocGpsAndSettings getUpdateLocGpsAndSettings() {
        return updateLocGpsAndSettings;
    }

    @Override
    public void onCreate() {
        Log.d(TAG,"Service created");
        updateLocGpsAndSettings = new UpdateLocGpsAndSettings(getBaseContext(), SEND_DELAY);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"Locationlistener removed");
        updateLocGpsAndSettings.resetLocationListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Return this service when called
     */
    public class UpdateLocBinder extends Binder {
        public UpdateLocToParseService getService() {
            return UpdateLocToParseService.this;
        }
    }

}
