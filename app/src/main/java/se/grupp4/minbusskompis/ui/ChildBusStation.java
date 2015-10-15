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
import android.widget.TextView;

import java.util.ArrayList;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.TravelingData;
import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;
import se.grupp4.minbusskompis.backgroundtasks.WifiCheckerStart;

//Note that dummybuttons are temporary for debugging
public class ChildBusStation extends AppCompatActivity implements ServiceConnection {

    private static class ViewHolder {
        TextView busStopName;
        TextView timeToBus;
        TextView nextBusName;
    }

    private ViewHolder viewHolder;

    protected Button dummyButton;
    private Context context = this;
    private UpdateLocToParseService.UpdateLocBinder parseUpdateLocBinder;
    private String destinationName;
    private ArrayList<String> wifiList;
    private TravelingData travelingData;
    WifiCheckerStart wifiCheckerStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_buss_station);
        viewHolder = new ViewHolder();

        //Init views.
        viewHolder.busStopName = (TextView) findViewById(R.id.child_bus_stop_bsname_char);
        viewHolder.timeToBus = (TextView) findViewById(R.id.child_bus_station_time_to_bus);
        viewHolder.nextBusName = (TextView) findViewById(R.id.child_bus_station_next_bus_name);

        //Get extras
        travelingData = (TravelingData) getIntent().getParcelableExtra("data");
        destinationName = travelingData.destinationName;

        //Set texts
        viewHolder.busStopName.setText(travelingData.bussStationName);
        viewHolder.nextBusName.setText(travelingData.bussName);
        viewHolder.timeToBus.setText(String.valueOf(travelingData.time));

        wifiList = new ArrayList<>();
        //eduroam i biblioteket
        wifiList.add("881dfc44578f");
        addButtonListener();

        //Bind and update service
        Intent serviceIntent = new Intent(this,UpdateLocToParseService.class);
        bindService(serviceIntent, this, 0);
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

    public void addButtonListener(){
        dummyButton = (Button) findViewById(R.id.button_dummystation);

        dummyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ChildOnBus.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_child_buss_station, menu);
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
    protected void onDestroy() {
        super.onDestroy();
        wifiCheckerStart.shutdown();
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        parseUpdateLocBinder = (UpdateLocToParseService.UpdateLocBinder) service;
        parseUpdateLocBinder.getService().getUpdateLocGpsAndSettings().resetLocationListener();
        parseUpdateLocBinder.getService().getUpdateLocGpsAndSettings().startLocationListener(2, destinationName);
        Intent nextIntent = new Intent(context,ChildOnBus.class);
        nextIntent.putExtra("data",travelingData);
        wifiCheckerStart = new WifiCheckerStart();
        wifiCheckerStart.startLookForWifi(context,nextIntent,wifiList,30);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
