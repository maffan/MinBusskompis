package se.grupp4.minbusskompis.backgroundtasks;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * Created by Tobias on 2015-10-07.
 */
public class UpdateLocGpsAndSettings {
    private int tripStatus = 0;
    private int updateRate;
    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public UpdateLocGpsAndSettings(Context context, int updateRate){
        //Set mode & updaterate
        this.updateRate = updateRate;
        this.context = context;
        //Get service
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    //Lyssnaren skapas vid denna metod, detta för att kunna ändra "mode"
    public boolean startLocationListener(int tripStatus){
        //Init location listener with trip status
        locationListener = new UpdateLocListener(tripStatus, context);

        //Enable location updates to locationlistener
        try {
            //GPS, tid mellan uppdateringar, distans per uppdatering, locationlistener att uppdatera till
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateRate, 0, locationListener);
            return true;
        }catch(SecurityException e){
            e.printStackTrace();
            return false;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //Ta bort aktuell lyssnare
    public boolean resetLocationListener(){
        try {
            //GPS, tid mellan uppdateringar, distans per uppdatering, locationlistener att uppdatera till
            locationManager.removeUpdates(locationListener);
            return true;
        }catch(SecurityException e){
            e.printStackTrace();
            return false;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
