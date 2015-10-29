package se.grupp4.minbusskompis.backgroundtasks;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

/*
    UpdateLocGpsAndSettings
    Class that manages gps and location listeners.
    When calling startLocationListener a locationlistener is bound to gps updates and will provide updates to parse.
    This is used to send the current location and mode for the child
 */
public class UpdateLocGpsAndSettings {
    private static final String TAG = "UpdateLocGpsAndSettings";
    private static final float METER_UPDATE = 30;
    private int updateRate;
    private Context context;
    private LocationManager locationManager;
    private UpdateLocListener locationListener;

    public UpdateLocGpsAndSettings(Context context, int updateRate){
        this.updateRate = updateRate;
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Start a locationlistener that will send updates to parse when location has been changed.
     * @param tripStatus Current tripStatus, Walking, On bus etc
     * @param destination Destination name
     * @return boolean added for future development
     */
    public boolean startLocationListener(int tripStatus, String destination){
        locationListener = new UpdateLocListener(tripStatus, context, destination);
        Log.d(TAG, "Starting Locationlistener");

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateRate, METER_UPDATE, locationListener);
            Log.d(TAG, "Locationlistener successfully ADDED");
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

    /**
     * Removes updates to the locationlistener added.
     * @return boolean added for future development
     */
    public boolean resetLocationListener(){
        try {
            Log.d(TAG, "Locationlistener successfully REMOVED");
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

    /**
     * Change current triptatus, when a child switches mode/activity
     * @param tripStatus Current trip status
     */
    public void setTripStatus(int tripStatus){
        locationListener.setTripStatus(tripStatus);
    }
}
