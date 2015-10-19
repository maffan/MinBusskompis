package se.grupp4.minbusskompis;

import android.app.Application;
import android.content.Intent;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import java.util.List;

import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;
import se.grupp4.minbusskompis.parsebuss.AsyncTaskCompleteCallback;
import se.grupp4.minbusskompis.parsebuss.BussData;

/**
 * Created by Marcus on 9/29/2015.
 */
public class BussApplication extends Application {
    private static final String TAG = "APPLICATION";

    @Override
    public void onCreate() {
        super.onCreate();
        initParseAndInitData();
        setDefaultSubscriptions();
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
}