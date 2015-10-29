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

/*
    UpdateLocListener
    Locationlistener that will send data to parse on updated position
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

    /**
     * On location changed, send current location and mode to parse and parent
     * @param loc Current location, callback
     */
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                Log.d(TAG, "Updated location to Parse");
                ChildLocationAndStatus childLocationAndStatus = new ChildLocationAndStatus(loc,tripStatus,destination);
                ParseCloudManager.getInstance().updateLatestLocationAndStatusForSelf(childLocationAndStatus);

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

        public void setTripStatus(int tripStatus){
            this.tripStatus = tripStatus;
        }
}
