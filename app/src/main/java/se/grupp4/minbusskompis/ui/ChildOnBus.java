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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.TravelingData;
import se.grupp4.minbusskompis.api.BusData;
import se.grupp4.minbusskompis.api.Methods;
import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;
import se.grupp4.minbusskompis.parsebuss.BussData;

public class ChildOnBus extends AppCompatActivity implements ServiceConnection,Runnable {
    @Override
    public void run() {
        new UpdateViewsTask().execute();
    }

    private class UpdateViewsTask extends AsyncTask<Void, Void, TravelingData>{

        public UpdateViewsTask() {
        }

        @Override
        protected TravelingData doInBackground(Void... params) {
            //Get data from api
            String dgw = BusData.getDgwByMac("0013951349f7");
            travelingData.nextBusStop = Methods.getNextStop(dgw);
            travelingData.stopButtonPressed = Methods.isAtStop(dgw);
            return travelingData;
        }

        @Override
        protected void onPostExecute(TravelingData travelingData) {
            super.onPostExecute(travelingData);
            viewHolder.nextBusStop.setText(travelingData.nextBusStop);

            if(travelingData.nextBusStop.equals(travelingData.busStopName) && !travelingData.isAtStop){
                //Next stop is same as wwe want to go, ok. to. this case. ok.
                Intent intent = new Intent(context, ChildLeavingBus.class);
                startActivity(intent);
                ((Activity)context).finish();
            }
        }
    }

    private static class ViewHolder {
        TextView busStopName;
        TextView timeToStop;
        TextView nextBusStop;
    }


    protected Button dummyButtonOnBus;
    private Context context = this;
    private TravelingData travelingData;
    private ViewHolder viewHolder;
    private UpdateLocToParseService.UpdateLocBinder updateLocBinder;
    private ScheduledThreadPoolExecutor poolExecutor;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        poolExecutor.shutdown();
        unbindService(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_on_bus);
        viewHolder = new ViewHolder();

        //Set on bus status
        BussData.getInstance().setStatusForSelfAndNotifyParents(TravelingData.ON_BUS);

        //Initiate views
        viewHolder.nextBusStop = (TextView) findViewById(R.id.child_on_bus_next_bus_stop);
        viewHolder.busStopName = (TextView) findViewById(R.id.child_on_bus_destination_bus_stop);
        viewHolder.timeToStop = (TextView) findViewById(R.id.child_on_bus_station_time_to_bus_stop);

        //Get data from intent
        travelingData = getIntent().getParcelableExtra("data");

        //Set data
        viewHolder.busStopName.setText(travelingData.busStopName);
        viewHolder.timeToStop.setText(travelingData.busArrivingAt);

        //Update next busstop
        poolExecutor = new ScheduledThreadPoolExecutor(1);
        poolExecutor.scheduleWithFixedDelay(this,0,20, TimeUnit.SECONDS);

        //Dummy buttons, pass forward
        addButtonListener();
        //Init service
        Intent serviceIntent = new Intent(context, UpdateLocToParseService.class);
        bindService(serviceIntent, this, 0);

        AsyncTask asTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    Log.d("CHILDONBUS", "Im gonna wait to click da button");
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    Log.e("CHILDONBUS", "Something happened while waiting to cklicka da button");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                Log.d("CHILDONBUS", "Gonna klick da button");
                dummyButtonOnBus.callOnClick();
            }};
        asTask.execute();
    }

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

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        updateLocBinder = (UpdateLocToParseService.UpdateLocBinder) service;
        updateLocBinder.getService().getUpdateLocGpsAndSettings().resetLocationListener();
        updateLocBinder.getService().getUpdateLocGpsAndSettings().startLocationListener(TravelingData.ON_BUS, travelingData.destinationName);

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
