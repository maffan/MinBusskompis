package se.grupp4.minbusskompis.backgroundtasks;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import se.grupp4.minbusskompis.parsebuss.ParseCloudManager;
import se.grupp4.minbusskompis.parsebuss.BussRelationMessenger;

/**
 * Created by Tobias on 2015-10-07.
 */
public class UpdateLocListener implements LocationListener {
        private static final String TAG = "UpdateLocListener";
        private int tripStatus;
        private Context context;
        private String destination;

        public UpdateLocListener(int tripStatus, Context context, String destination){
            this.tripStatus = tripStatus;
            this.context = context;
            this.destination = destination;
        }

        //Skicka data till parse vid ändring av position
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                Log.d(TAG, "Updated location to Parse");
                //Spara location
                ChildLocationAndStatus childLocationAndStatus = new ChildLocationAndStatus(loc,tripStatus,destination);
                ParseCloudManager.getInstance().updateLatestLocationAndStatusForSelf(childLocationAndStatus);

                //Skicka push till alla föräldrar att nu finns ny position.
                BussRelationMessenger.getInstance().notifyPositionUpdate();
            }else {
                Log.d(TAG, "Got null location");
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(context.getApplicationContext(), "Enable GPS to send updates to parent", Toast.LENGTH_SHORT).show();
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
                    statusText = "Unknown status";
                    break;
            }

            //Toast.makeText(context.getApplicationContext(), provider.toString() + " status changed: " + statusText, Toast.LENGTH_SHORT).show();

        }
}
