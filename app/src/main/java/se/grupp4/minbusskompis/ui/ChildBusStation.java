package se.grupp4.minbusskompis.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.TravelingData;
import se.grupp4.minbusskompis.api.BusData;
import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;
import se.grupp4.minbusskompis.backgroundtasks.WifiCheckerStart;
import se.grupp4.minbusskompis.parsebuss.ParseCloudManager;

/*
    ChildBusStation
    Shown when child is at bus station.
    * Updates UpdateLocToParseService to send updates with new mode
    * Start Wifichecker to look for bus mac addresses
    * Uses data from travelingdata to populate information
 */
public class ChildBusStation extends Activity implements ServiceConnection {

    private static final String TAG = "ChildBusStation";
    private ViewHolder viewHolder;
    private Context context = this;
    private UpdateLocToParseService.UpdateLocBinder parseUpdateLocBinder;
    private String destinationName;
    private ArrayList<String> wifiList;
    private TravelingData travelingData;
    private WifiCheckerStart wifiCheckerStart;

    //Debug button
    protected Button dummyButton;

    private static class ViewHolder {
        TextView busStopName;
        TextView timeToBus;
        TextView yourBusName;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_bus_station);
        viewHolder = new ViewHolder();

        //Update status
        ParseCloudManager.getInstance().setStatusForSelfAndNotifyParents(TravelingData.AT_BUS_STATION);

        //Init views.
        initViews();

        //Get extras
        travelingData = (TravelingData) getIntent().getParcelableExtra("data");
        destinationName = travelingData.destinationName;

        //Set texts
        viewHolder.busStopName.setText(travelingData.bussStationName);
        viewHolder.yourBusName.setText(travelingData.bussName);
        viewHolder.timeToBus.setText(travelingData.busLeavingAt);

        wifiList = BusData.getMacAdrList();
        addButtonListener();

        //Bind and update service
        Intent serviceIntent = new Intent(this,UpdateLocToParseService.class);
        bindService(serviceIntent, this, 0);
    }

    private void initViews() {
        viewHolder.busStopName = (TextView) findViewById(R.id.child_bus_stop_bsname_char);
        viewHolder.timeToBus = (TextView) findViewById(R.id.child_bus_station_time_to_bus);
        viewHolder.yourBusName = (TextView) findViewById(R.id.child_bus_station_next_bus_name);
    }

    /**
     * Cancel current trip by pressing back button
     */
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

    /**
     * Debug button, next activity
     */
    public void addButtonListener(){
        dummyButton = (Button) findViewById(R.id.button_dummystation);

        dummyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ChildOnBus.class);
                //Set currentBusMacAdress to test bus if debug button is pressed
                travelingData.currentBusMacAdress = "1337";
                intent.putExtra("data",travelingData);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Shut down wifichecker when destroyed
     * unbind parse service
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG, "Calling onDestroy");
        super.onDestroy();
        wifiCheckerStart.shutdown();
        unbindService(this);
    }

    /**
     * Use onServiceConnected callback to start service with new mode, and initate WifiChecker
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        parseUpdateLocBinder = (UpdateLocToParseService.UpdateLocBinder) service;
        parseUpdateLocBinder.getService().getUpdateLocGpsAndSettings().setTripStatus(TravelingData.AT_BUS_STATION);
        Intent nextIntent = new Intent(context,ChildOnBus.class);
        nextIntent.putExtra("data",travelingData);
        wifiCheckerStart = new WifiCheckerStart();
        wifiCheckerStart.startLookForWifi(context,nextIntent,wifiList,30);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
