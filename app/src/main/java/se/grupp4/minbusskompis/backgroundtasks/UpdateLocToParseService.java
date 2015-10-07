package se.grupp4.minbusskompis.backgroundtasks;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class UpdateLocToParseService extends Service {
    private final IBinder mBinder = new UpdateLocBinder();
    private UpdateLocGpsAndSettings updateLocGpsAndSettings;

    public UpdateLocGpsAndSettings getUpdateLocGpsAndSettings() {
        return updateLocGpsAndSettings;
    }

    @Override
    public void onCreate() {
        //Vid service start skapas location managern.
        updateLocGpsAndSettings = new UpdateLocGpsAndSettings(getBaseContext(), 5000);
    }

    @Override
    public void onDestroy() {
        //Remove listener vid destroy
        updateLocGpsAndSettings.resetLocationListener();
    }

    //Vad som händer efter onCreate vid startService.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    //För att starta något i services, dvs starta locationlistenern som kommer skicka data, använd bind .getService()
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //Skapa en binder till services så det går att hantera den.
    public class UpdateLocBinder extends Binder {
        UpdateLocToParseService getService() {
            return UpdateLocToParseService.this;
        }
    }
}
