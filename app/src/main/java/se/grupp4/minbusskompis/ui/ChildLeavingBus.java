package se.grupp4.minbusskompis.ui;

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
import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;
import se.grupp4.minbusskompis.backgroundtasks.WifiCheckerStart;
import se.grupp4.minbusskompis.parsebuss.BussData;

public class ChildLeavingBus extends AppCompatActivity implements ServiceConnection, Runnable {

    private UpdateLocToParseService.UpdateLocBinder updateLocBinder;
    protected Button dummyButton1;
    private Context context = this;
    private TravelingData travelingData;
    private ViewHolder viewHolder;
    private ScheduledThreadPoolExecutor poolExecutor;
    private WifiCheckerStart wifiCheckerStart;
    private boolean serviceBound = false;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        updateLocBinder = (UpdateLocToParseService.UpdateLocBinder) service;
        updateLocBinder.getService().getUpdateLocGpsAndSettings().resetLocationListener();
        updateLocBinder.getService().getUpdateLocGpsAndSettings().startLocationListener(TravelingData.LEAVING_BUS, travelingData.destinationName);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    public void run() {
        new UpdateViewsTask(travelingData);
    }

    private class UpdateViewsTask extends AsyncTask<Void, Void, TravelingData> {
        private TravelingData parentTravelingData;

        public UpdateViewsTask(TravelingData parentTravelingData) {
            this.parentTravelingData = parentTravelingData;
        }

        @Override
        protected TravelingData doInBackground(Void... params) {
            //Get data from api
            TravelingData travelingData = new TravelingData();
            return travelingData;
        }

        @Override
        protected void onPostExecute(TravelingData travelingData) {
            if(travelingData.stopButtonPressed){
                viewHolder.stopButtonTextView.setText("Stop button is pressed!");
                viewHolder.stopButtonImageView.setImageResource(R.drawable.button_pressed);
            }else{
                //Try to press stop button.
            }
        }
    }

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
        BussData.getInstance().setStatusForSelfAndNotifyParents(TravelingData.LEAVING_BUS);

        //Get traveling data
        travelingData = getIntent().getParcelableExtra("data");

        //Init service
        Intent serviceIntent = new Intent(context, UpdateLocToParseService.class);
        bindService(serviceIntent, this, 0);
        serviceBound = true;

        //Init views
        viewHolder.leaveBusTitle = (TextView) findViewById(R.id.child_leave_bus_exit_at_next_stop);
        viewHolder.stopButtonTextView = (TextView) findViewById(R.id.child_leave_bus_stop_button_text);
        viewHolder.stopButtonImageView = (ImageView) findViewById(R.id.child_leave_bus_stop_button_icon);

        //Starta wifi-kontroll-tjofräset
        wifiCheckerStart = new WifiCheckerStart();
        Intent nextIntent = new Intent(this,ChildGoingFromBus.class);
        nextIntent.putExtra("data", travelingData);
        String mac = travelingData.currentBusMacAdress;

        wifiCheckerStart.startCheckIfLeave(context, nextIntent, mac, 30);

        poolExecutor = new ScheduledThreadPoolExecutor(1);
        poolExecutor.scheduleAtFixedRate(this, 0, 30, TimeUnit.SECONDS);

        addButtonListener();
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

    public void addButtonListener(){
        final Context context = this;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_child_bus_stop, menu);
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
}
