package se.grupp4.minbusskompis.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.TravelingData;
import se.grupp4.minbusskompis.api.BusData;
import se.grupp4.minbusskompis.api.Methods;
import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;
import se.grupp4.minbusskompis.parsebuss.ParseCloudManager;
/*
    ChildOnBus
    Activity shown when user is traveling on the bus.

    * UpdateLocToParseService, change tripStatus
    * Check if next bus stop and the bus has left the last bus stop each 20s, change activity if that occours.
 */
public class ChildOnBus extends AppCompatActivity implements ServiceConnection,Runnable {
    private Context context = this;
    private TravelingData travelingData;
    private ViewHolder viewHolder;
    private UpdateLocToParseService.UpdateLocBinder updateLocBinder;
    private ScheduledThreadPoolExecutor poolExecutor;
    //Debug
    protected Button dummyButtonOnBus;

    private static class ViewHolder {
        TextView busStopName;
        TextView timeToStop;
        TextView nextBusStop;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_on_bus);
        viewHolder = new ViewHolder();

        //Set on bus status
        ParseCloudManager.getInstance().setStatusForSelfAndNotifyParents(TravelingData.ON_BUS);

        //Initiate views
        initiateViews();

        //Get data from intent
        travelingData = getIntent().getParcelableExtra("data");

        //Set trip information from travelingData
        viewHolder.busStopName.setText(travelingData.busStopName);
        viewHolder.timeToStop.setText(travelingData.busArrivingAt);

        //Check if next bus stop is yours, and if the bus have left the last one each 20s
        poolExecutor = new ScheduledThreadPoolExecutor(1);
        poolExecutor.scheduleWithFixedDelay(this,0,20, TimeUnit.SECONDS);

        //Dummy buttons, pass forward
        addButtonListener();
        //Init service
        Intent serviceIntent = new Intent(context, UpdateLocToParseService.class);
        bindService(serviceIntent, this, 0);
    }

    private void initiateViews() {
        viewHolder.nextBusStop = (TextView) findViewById(R.id.child_on_bus_next_bus_stop);
        viewHolder.busStopName = (TextView) findViewById(R.id.child_on_bus_destination_bus_stop);
        viewHolder.timeToStop = (TextView) findViewById(R.id.child_on_bus_station_time_to_bus_stop);
    }

    /**
     * Debug
     */
    public void addButtonListener(){
        dummyButtonOnBus = (Button)findViewById(R.id.button_dummy_busonbus);
        dummyButtonOnBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChildLeavingBus.class);
                intent.putExtra("data",travelingData);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Shut down the pool executor and unbind updatetoparse serivce on detroy.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        poolExecutor.shutdown();
        unbindService(this);
    }

    @Override
    public void run() {
        new CheckIfNextStopIsYoursAsTask().execute();
    }

    /**
     * Checks if the next bus stop is yours and if the bus has left the bus station, passes user to last activity if so.
     */
    private class CheckIfNextStopIsYoursAsTask extends AsyncTask<Void, Void, TravelingData>{

        public CheckIfNextStopIsYoursAsTask() {
        }

        @Override
        protected TravelingData doInBackground(Void... params) {
            //Get data from api
            String dgw = BusData.getDgwByMac(travelingData.currentBusMacAdress);
            travelingData.nextBusStop = Methods.getNextStop(dgw);
            travelingData.stopButtonPressed = Methods.isAtStop(dgw);
            return travelingData;
        }

        @Override
        protected void onPostExecute(TravelingData travelingData) {
            super.onPostExecute(travelingData);
            viewHolder.nextBusStop.setText(travelingData.nextBusStop);

            if(travelingData.nextBusStop.equals(travelingData.busStopName) && !travelingData.isAtStop){
                Intent intent = new Intent(context, ChildLeavingBus.class);
                startActivity(intent);
                ((Activity)context).finish();
            }
        }
    }

    /**
     * Cancel current trip dialog if back is pressed
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_child_on_bus, menu);
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

    /**
     * Change tripstatus mode
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        updateLocBinder = (UpdateLocToParseService.UpdateLocBinder) service;
        updateLocBinder.getService().getUpdateLocGpsAndSettings().setTripStatus(TravelingData.ON_BUS);

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
