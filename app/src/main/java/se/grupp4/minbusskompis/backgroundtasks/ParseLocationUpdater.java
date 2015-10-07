package se.grupp4.minbusskompis.backgroundtasks;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Tobias on 2015-10-07.
 */
public class ParseLocationUpdater implements LocationListener {
        private int tripStatus;
        private Activity activity;

        public ParseLocationUpdater(int tripStatus, Activity activity){
            this.tripStatus = tripStatus;
            this.activity = activity;
        }

        //Skicka data till parse vid ändring av position
        @Override
        public void onLocationChanged(Location loc) {
            double longitude = loc.getLongitude();
            double latitude = loc.getLatitude();

            Toast.makeText(activity.getApplicationContext(), "Sending data to PARSE, mode: " + longitude + " Long: " + longitude + " Lat: " + latitude, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(activity.getApplicationContext(), "Enable GPS to send updates to parent", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            String statusText;

            switch(status) {
                case LocationProvider.AVAILABLE:
                    statusText = "Available";
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    statusText = "Out of service";
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    statusText = "Temporarily unavailable";
                    break;
                default:
                    statusText = "Uknown status";
                    break;
            }

            Toast.makeText(activity.getApplicationContext(), provider.toString() + " status changed: " + statusText, Toast.LENGTH_SHORT).show();

        }
}
