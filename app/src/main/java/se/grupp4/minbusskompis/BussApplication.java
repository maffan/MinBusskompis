package se.grupp4.minbusskompis;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Marcus on 9/29/2015.
 */
public class BussApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this);
    }
}
