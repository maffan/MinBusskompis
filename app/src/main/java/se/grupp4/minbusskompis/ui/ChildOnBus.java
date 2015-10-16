package se.grupp4.minbusskompis.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.TravelingData;
import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;

public class ChildOnBus extends AppCompatActivity implements ServiceConnection {
    private static class ViewHolder {
        TextView busStopName;
        TextView timeToStop;
        TextView nextBusStop;
    }


    protected Button dummyButtonOnBus;
    private Context context = this;
    private TravelingData travelingData;
    private ViewHolder viewHolder;
    UpdateLocToParseService.UpdateLocBinder updateLocBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_on_bus);
        viewHolder = new ViewHolder();

        //Initiate views
        viewHolder.nextBusStop = (TextView) findViewById(R.id.child_on_bus_next_bus_stop);
        viewHolder.busStopName = (TextView) findViewById(R.id.child_on_bus_destination_bus_stop);
        viewHolder.timeToStop = (TextView) findViewById(R.id.child_on_bus_station_time_to_bus_stop);

        //Get data from intent
        travelingData = (TravelingData) getIntent().getParcelableExtra("data");

        //Set data
        viewHolder.busStopName.setText(travelingData.destinationName);
        viewHolder.timeToStop.setText(String.valueOf(travelingData.time));
        viewHolder.nextBusStop.setText(travelingData.bussStationName);

        //Init service
        Intent serviceIntent = new Intent(context, UpdateLocToParseService.class);
        bindService(serviceIntent,this,0);

        //Dummy buttons, pass forward
        addButtonListener();
    }

    public void addButtonListener(){

        dummyButtonOnBus = (Button)findViewById(R.id.button_dummy_busonbus);
        dummyButtonOnBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChildLeavingBus.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Stop Trip")
                .setMessage("Do you wish to go back to Destinations?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), ChildDestinations.class);
                        startActivity(intent);
                        finish();
                    }})
                .setNegativeButton("No", null)
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
        updateLocBinder.getService().getUpdateLocGpsAndSettings().startLocationListener(3, travelingData.destinationName);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
