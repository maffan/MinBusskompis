package se.grupp4.minbusskompis;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;

import se.grupp4.minbusskompis.parsebuss.AsyncTaskCompleteCallback;
import se.grupp4.minbusskompis.parsebuss.BussData;
import se.grupp4.minbusskompis.parsebuss.BussDestination;

/**
 * Created by Marcus on 9/29/2015.
 */
public class BussApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this);
        BussData.getInstance().fetchData(new AsyncTaskCompleteCallback() {
            @Override
            public void done() {
                BussData.getInstance().addDestinationToChild(new BussDestination(new ParseGeoPoint(0, 0), "Saxofonlektion"), ParseInstallation.getCurrentInstallation().getInstallationId());
            }
        });
    }
}
