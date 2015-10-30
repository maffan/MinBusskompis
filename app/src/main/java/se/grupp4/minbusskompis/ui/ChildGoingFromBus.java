package se.grupp4.minbusskompis.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.TravelingData;
import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;
import se.grupp4.minbusskompis.parsebuss.ParseCloudManager;
/*
    ChildGoingFromBus
    Gives the user help to find his or her final destination, after leaving bus

    * Updates UpdateToParseService with new mode
    * Can guide user to end destination via google maps, and current position

 */
public class ChildGoingFromBus extends Activity implements ServiceConnection {

    private ViewHolder viewHolder;
    private boolean neededHelp;
    private Intent serviceIntent;
    private static final String TAG = "ChildGoingFromBus";
    private static final int TIMEOUT = 60*1000;
    private static final int MODE = 1;
    private Context context = this;
    private LatLng destination;
    private String destinationName;
    private UpdateLocToParseService.UpdateLocBinder parseUpdateLocBinder;
    private String latitude;
    private String longitude;
    private TravelingData travelingData;

    private static class ViewHolder {
        Button atDestinationButton;
        Button helpToFindDestinationButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_going_from_bus);
        viewHolder = new ViewHolder();

        //Initiate views
        initiateViews();

        //Set walking status
        ParseCloudManager.getInstance().setStatusForSelfAndNotifyParents(TravelingData.WALKING);

        //Get target destination
        travelingData = (TravelingData) getIntent().getParcelableExtra("data");
        destination = travelingData.destinationCoordinates;

        latitude = String.valueOf(destination.latitude);
        longitude = String.valueOf(destination.longitude);
        destinationName = travelingData.destinationName;


        //Start sending updates to parent, bind service set needHelp flag (nav or not)
        serviceIntent = new Intent(this, UpdateLocToParseService.class);
        viewHolder.helpToFindDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neededHelp = true;
                bindService(serviceIntent, (ServiceConnection) context, 0);
            }
        });

        viewHolder.atDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neededHelp = false;
                bindService(serviceIntent, (ServiceConnection) context, 0);
            }
        });
    }

    private void initiateViews() {
        viewHolder.atDestinationButton = (Button) findViewById(R.id.child_going_from_bus_on_location);
        viewHolder.helpToFindDestinationButton = (Button) findViewById(R.id.child_going_from_bus_help_to_loc);
    }

    /**
     * Show dialog when returning from navigation.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.child_going_from_bus_dialog_title)
                .setMessage(R.string.child_going_from_bus_dialog_message)
                .setPositiveButton(R.string.all_dialog_yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),ChildDestinations.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.child_going_from_bus_dialog_no_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ((ServiceConnection) context).onServiceConnected(null,parseUpdateLocBinder);
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.child_onbackwardspressed_dialog_title)
                .setMessage(R.string.child_onbackwardspressed_dialog_message)
                .setPositiveButton(R.string.all_dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), ChildDestinations.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.all_dialog_no, null)
                .show();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG,"Received binder, start location listener");
        parseUpdateLocBinder = (UpdateLocToParseService.UpdateLocBinder) service;
        parseUpdateLocBinder.getService().getUpdateLocGpsAndSettings().setTripStatus(TravelingData.WALKING);

        if (neededHelp) {
            Intent intent =
                    new Intent(Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=w"));

            startActivityForResult(intent, 1);
        }
        else{
            Intent intent = new Intent(this, ChildDestinations.class);
            intent.putExtra("data",travelingData);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
        stopService(serviceIntent);
    }
}
