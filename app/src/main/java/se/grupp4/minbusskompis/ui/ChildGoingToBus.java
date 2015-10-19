package se.grupp4.minbusskompis.ui;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.parse.LocationCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.TravelingData;
import se.grupp4.minbusskompis.backgroundtasks.ChildLocationAndStatus;
import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;
import se.grupp4.minbusskompis.parsebuss.BussData;

public class ChildGoingToBus extends AppCompatActivity implements ServiceConnection {

    private static final String TAG = "WALKMODE";
    private static final int TIMEOUT = 60*1000;
    private static final int MODE = 1;
    private Context context = this;
    private LatLng destination;
    private String destinationName;
    private UpdateLocToParseService.UpdateLocBinder parseUpdateLocBinder;
    private String latitude;
    private String longitude;
    private TravelingData travelingData;
    private Button needHelpButton;
    private Button noNeedHelpButton;
    private boolean neededHelp;
    private boolean serviceBound = false;
    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_going_to_bus);

        //Check if service running, error when completing travels and trying to start again
        if(!isMyServiceRunning(UpdateLocToParseService.class)){
            Log.d(TAG,"Update to parse service not started, starting service");
            Intent serviceIntent = new Intent(this, UpdateLocToParseService.class);
            startService(serviceIntent);
        }

        //Set walking status
        BussData.getInstance().setStatusForSelfAndNotifyParents(TravelingData.WALKING);

        //Get target destination
        travelingData = getIntent().getParcelableExtra("data");
        Log.d(TAG,"Got travelingData as: "+travelingData);
        destination = travelingData.destinationCoordinates;
        latitude = String.valueOf(destination.latitude);
        longitude = String.valueOf(destination.longitude);
        destinationName = travelingData.destinationName;
        Log.d(TAG,"Latitude: "+ latitude);
        Log.d(TAG, "Longitude: " + longitude);

        needHelpButton = (Button) findViewById(R.id.child_going_to_bus_help_to_bs);
        noNeedHelpButton = (Button) findViewById(R.id.child_going_to_bus_im_on_bs);

        //Send to parse service
        serviceIntent = new Intent(context, UpdateLocToParseService.class);

        needHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neededHelp = true;
                bindService(serviceIntent, (ServiceConnection) context, 0);
            }
        });

        noNeedHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neededHelp = false;
                bindService(serviceIntent, (ServiceConnection) context, 0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent(this, ChildBusStation.class);
        intent.putExtra("data",travelingData);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.child_onbackwardspressed_dialog_title)
                .setMessage(R.string.child_onbackwardspressed_dialog_message)
                .setPositiveButton(R.string.child_onbackwardspressed_dialog_option_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), ChildDestinations.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.child_onbackwardspressed_dialog_option_no, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_child_walk_mode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        serviceBound = true;
        Log.d(TAG,"Received binder, start location listener");

        parseUpdateLocBinder = (UpdateLocToParseService.UpdateLocBinder) service;
        parseUpdateLocBinder.getService().getUpdateLocGpsAndSettings().startLocationListener(TravelingData.WALKING, destinationName);

        if (neededHelp) {
            ParseGeoPoint.getCurrentLocationInBackground(TIMEOUT, new LocationCallback() {
                @Override
                public void done(ParseGeoPoint parseGeoPoint, ParseException e) {

                    if (parseGeoPoint != null) {
                        Location location = new Location("Nanana");
                        location.setLatitude(parseGeoPoint.getLatitude());
                        location.setLongitude(parseGeoPoint.getLongitude());
                        ChildLocationAndStatus locationAndStatus = new ChildLocationAndStatus(location,MODE,destinationName);
                        BussData.getInstance().updateLatestPosition(locationAndStatus);
                        Intent intent =
                                new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=w"));

                        startActivityForResult(intent, 1);
                    } else {
                        //Send back to destinations
                       Intent nintent = new Intent(context, ChildDestinations.class);
                       startActivity(nintent);
                       finish();
                    }
                }
            });
        }
        else{
            Intent intent = new Intent(this, ChildBusStation.class);
            intent.putExtra("data",travelingData);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "Service disconnected");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(this);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
