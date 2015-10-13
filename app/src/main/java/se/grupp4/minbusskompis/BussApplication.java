package se.grupp4.minbusskompis;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import java.util.List;

import se.grupp4.minbusskompis.backgroundtasks.ChildLocationAndStatus;
import se.grupp4.minbusskompis.parsebuss.AsyncTaskCompleteCallback;
import se.grupp4.minbusskompis.parsebuss.BussData;
import se.grupp4.minbusskompis.parsebuss.BussDestination;
import se.grupp4.minbusskompis.parsebuss.BussRelationships;

/**
 * Created by Marcus on 9/29/2015.
 */
public class BussApplication extends Application {
    private static final String TAG = "APPLICATION";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this);
        BussData.getInstance().fetchData(null);
        ParsePush.subscribeInBackground("i"+ParseInstallation.getCurrentInstallation().getInstallationId());
    }
}
