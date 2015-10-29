package se.grupp4.minbusskompis.ui;

import android.app.ActivityManager;
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

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.TravelingData;
import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;
import se.grupp4.minbusskompis.parsebuss.BussData;

/*
    ChildGoingToBus
    First activity for child, gives the user the choice of either saying that you are at the bus stop
    or if you need help to find it.

    * Starts sending updates on current position when clicking a button
    * Changes UpdateLocToParseService mode
    * Starts UpdateLocToParseService if not started
 */
public class ChildGoingToBus extends AppCompatActivity implements ServiceConnection {

    private static final String TAG = "ChildGoingToBus";
    private Context context = this;
    private UpdateLocToParseService.UpdateLocBinder parseUpdateLocBinder;
    private TravelingData travelingData;
    private boolean neededHelp;
    private boolean serviceBound = false;
    private Intent serviceIntent;
    private ViewHolder viewHolder;

    private static class ViewHolder {
        Button needHelpButton;
        Button noNeedHelpButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_going_to_bus);
        viewHolder = new ViewHolder();


        //Check if service running, error when completing travels and trying to start again
        if(!isMyServiceRunning(UpdateLocToParseService.class)){
            Log.d(TAG,"Update to parse service not started, starting service");
            Intent serviceIntent = new Intent(this, UpdateLocToParseService.class);
            startService(serviceIntent);
        }

        //Set walking status
        BussData.getInstance().setStatusForSelfAndNotifyParents(TravelingData.WALKING);

        //Init travelingData
        travelingData = getIntent().getParcelableExtra("data");

        //Init views
        initiateViews();

        //Send to parse service
        serviceIntent = new Intent(context, UpdateLocToParseService.class);

        viewHolder.needHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neededHelp = true;
                bindService(serviceIntent, (ServiceConnection) context, 0);
            }
        });

        viewHolder.noNeedHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neededHelp = false;
                bindService(serviceIntent, (ServiceConnection) context, 0);
            }
        });

    }

    private void initiateViews() {
        viewHolder.needHelpButton = (Button) findViewById(R.id.child_going_to_bus_help_to_bs);
        viewHolder.noNeedHelpButton = (Button) findViewById(R.id.child_going_to_bus_im_on_bs);
    }

    /**
     * Send user to next activity after returning from google maps
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent(this, ChildBusStation.class);
        intent.putExtra("data",travelingData);
        startActivity(intent);
        finish();
    }

    /**
     * Cancel current trip and go back to destinations
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

    /**
     * On buttonclicks and when the service is connected, either start google maps or send user to next activity
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        serviceBound = true;
        Log.d(TAG,"Received binder, start location listener");

        parseUpdateLocBinder = (UpdateLocToParseService.UpdateLocBinder) service;
        parseUpdateLocBinder.getService().getUpdateLocGpsAndSettings().resetLocationListener();
        parseUpdateLocBinder.getService().getUpdateLocGpsAndSettings().startLocationListener(TravelingData.WALKING, travelingData.destinationName);

        if (neededHelp) {
            Intent intent =
                    new Intent(Intent.ACTION_VIEW,
                            Uri.parse("google.navigation:q=" + travelingData.bussStopCoordinates.latitude + "," + travelingData.bussStopCoordinates.longitude + "&mode=w"));
            startActivityForResult(intent, 1);
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

    /**
     * Check if a service is running
     * @param serviceClass Your current service
     * @return boolean
     */
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
