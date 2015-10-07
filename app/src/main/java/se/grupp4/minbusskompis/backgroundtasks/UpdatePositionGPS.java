package se.grupp4.minbusskompis.backgroundtasks;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * Created by Tobias on 2015-10-07.
 */
public class UpdatePositionGPS {
    private int tripStatus;
    private int updateRate;
    private Activity activity;
    private LocationManager locationManager;
    private LocationListener locationListener;


    //Tråden skapas med ett mode, dvs vilket läge telefonen befinner sig i, to bs, at bs, at bs etc
    public UpdatePositionGPS(int tripStatus, Activity activity, int updateRate){
        //Set mode & updaterate
        this.tripStatus = tripStatus;
        this.updateRate = updateRate;

        this.activity = activity;

        //Get service
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        //Init location listener with trip status
        locationListener = new ParseLocationUpdater(tripStatus, activity);

        //Enable location updates to locationlistener
        try {
            //GPS, tid mellan uppdateringar, distans per uppdatering, locationlistener att uppdatera till
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateRate, 0, locationListener);
        }catch(SecurityException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public int getUpdateRate() {
        return this.updateRate;
    }

    public void setUpdateRate(int newUpdateRate){
        this.updateRate = newUpdateRate;
    }

    public LocationManager getLocationManager(){
        return this.locationManager;
    }
}
