package se.grupp4.minbusskompis.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.TravelingData;
import se.grupp4.minbusskompis.api.BusData;
import se.grupp4.minbusskompis.api.Methods;
import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;
import se.grupp4.minbusskompis.backgroundtasks.WifiCheckerStart;
import se.grupp4.minbusskompis.parsebuss.ParseCloudManager;
/*
    ChildLeavingBus
    Activity shown when child is about to leave bus

    * Update UpdateLocToParseService with new tripStatus
    * Check every 15s if stop button is pressed
    * Check via WifiChecker if user leaves bus (looses wifi connection)
 */
public class ChildLeavingBus extends Activity implements ServiceConnection, Runnable {

    private UpdateLocToParseService.UpdateLocBinder updateLocBinder;
    protected Button dummyButton1;
    private Context context = this;
    private TravelingData travelingData;
    private ViewHolder viewHolder;
    private ScheduledThreadPoolExecutor poolExecutor;
    private WifiCheckerStart wifiCheckerStart;
    private boolean serviceBound = false;

    private static class ViewHolder {
        TextView leaveBusTitle;
        TextView stopButtonTextView;
        ImageView stopButtonImageView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_leave_bus);
        viewHolder = new ViewHolder();

        //Set leaving bus status
        ParseCloudManager.getInstance().setStatusForSelfAndNotifyParents(TravelingData.LEAVING_BUS);

        //Get traveling data
        travelingData = getIntent().getParcelableExtra("data");

        //Init service
        Intent serviceIntent = new Intent(context, UpdateLocToParseService.class);
        bindService(serviceIntent, this, 0);
        serviceBound = true;

        //Init views
        initViews();

        //Buttons
        addButtonListeners();

        //WifiChecker, leave bus, checks against previously found mac address
        wifiCheckerStart = new WifiCheckerStart();
        Intent nextIntent = new Intent(this,ChildGoingFromBus.class);
        nextIntent.putExtra("data", travelingData);
        String mac = travelingData.currentBusMacAdress;
        wifiCheckerStart.startCheckIfLeave(context, nextIntent, mac, 30);

        //Check stop button task, call this runnable each 15s
        poolExecutor = new ScheduledThreadPoolExecutor(1);
        poolExecutor.scheduleAtFixedRate(this, 0, 15, TimeUnit.SECONDS);
    }

    private void initViews() {
        viewHolder.leaveBusTitle = (TextView) findViewById(R.id.child_leave_bus_exit_at_next_stop);
        viewHolder.stopButtonTextView = (TextView) findViewById(R.id.child_leave_bus_stop_button_text);
        viewHolder.stopButtonImageView = (ImageView) findViewById(R.id.child_leave_bus_stop_button_icon);
    }

    /**
     * Debug button
     */
    public void addButtonListeners(){
        dummyButton1 = (Button)findViewById(R.id.button_dummy_busstop);

        dummyButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChildGoingFromBus.class);
                intent.putExtra("data",travelingData);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Check if stop button is pressed, update view
     */
    private class CheckIfStopIsPressedAsTask extends AsyncTask<Void, Void, TravelingData> {

        public CheckIfStopIsPressedAsTask() {
        }

        @Override
        protected TravelingData doInBackground(Void... params) {
            //Get data from api
            String dgw = BusData.getDgwByMac(travelingData.currentBusMacAdress);
            travelingData.stopButtonPressed = Methods.isStopPressed(dgw);
            return travelingData;
        }

        @Override
        protected void onPostExecute(TravelingData travelingData) {
            if(travelingData.stopButtonPressed){
                viewHolder.stopButtonTextView.setText(R.string.stop_button_pressed);
                viewHolder.stopButtonImageView.setImageResource(R.drawable.button_pressed);
            }else{
                viewHolder.stopButtonImageView.setImageResource(R.drawable.button_not_pressed);
            }
        }
    }

    /**
     * Update service with new tripStatus
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        updateLocBinder = (UpdateLocToParseService.UpdateLocBinder) service;
        updateLocBinder.getService().getUpdateLocGpsAndSettings().setTripStatus(TravelingData.LEAVING_BUS);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    /**
     * Update stop button
     */
    @Override
    public void run() {
        new CheckIfStopIsPressedAsTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        poolExecutor.shutdown();
        wifiCheckerStart.shutdown();
        if(serviceBound){
            unbindService(this);
        }
    }

    /**
     * Cancel trip dialog if back is pressed
     */
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
}
