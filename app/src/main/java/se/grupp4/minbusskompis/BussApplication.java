package se.grupp4.minbusskompis;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;

import java.util.List;

import se.grupp4.minbusskompis.parsebuss.AsyncTaskCompleteCallback;
import se.grupp4.minbusskompis.parsebuss.BussData;
import se.grupp4.minbusskompis.parsebuss.BussDestination;

/**
 * Created by Marcus on 9/29/2015.
 */
public class BussApplication extends Application {
    private static final String TAG = "APPLICATION";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this);
        BussData.getInstance().fetchData(new AsyncTaskCompleteCallback() {
            @Override
            public void done() {
                List destinations = BussData.getInstance().getDestinations();
                Log.d(TAG, "done: destinations: "+destinations);
            }
        });
    }
}
