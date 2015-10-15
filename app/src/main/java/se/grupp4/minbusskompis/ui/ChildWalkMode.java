package se.grupp4.minbusskompis.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.LocationCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;

public class ChildWalkMode extends AppCompatActivity implements ServiceConnection {

    private static final String TAG = "WALKMODE";
    private static final int TIMEOUT = 10;
    private static final int MODE = 1;
    private Context context = this;
    private LatLng destination;
    private String destinationName;
    private UpdateLocToParseService.UpdateLocBinder parseUpdateLocBinder;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_walk_mode);

        //Get target destination
        destination = (LatLng) getIntent().getParcelableExtra("destination");
        destinationName = getIntent().getStringExtra("destinationName");
        latitude = String.valueOf(destination.latitude);
        longitude = String.valueOf(destination.longitude);
        Log.d(TAG,"Latitude: "+ latitude);
        Log.d(TAG, "Longitude: " + longitude);

        //Start sending updates to parent
        Intent serviceIntent = new Intent(this, UpdateLocToParseService.class);
        bindService(serviceIntent, this, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(context, "Made it back to BussKompis!!!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ChildBusStation.class);
        startActivity(intent);
        finish();
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
        Log.d(TAG,"Received binder, start location listener");
        parseUpdateLocBinder = (UpdateLocToParseService.UpdateLocBinder) service;
        parseUpdateLocBinder.getService().getUpdateLocGpsAndSettings().startLocationListener(MODE, destinationName);

        ParseGeoPoint.getCurrentLocationInBackground(TIMEOUT, new LocationCallback() {
            @Override
            public void done(ParseGeoPoint parseGeoPoint, ParseException e) {

                if (parseGeoPoint != null) {
                    Toast.makeText(context, "Your current position is: " + parseGeoPoint, Toast.LENGTH_SHORT).show();
                    Intent intent =
                            new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=w"));

                    startActivityForResult(intent, 1);
                } else {
                    Toast.makeText(context, "Could not get a clear signal. Please go outside and try again!", Toast.LENGTH_SHORT).show();

                    //Ta bort vid launch
                    parseGeoPoint = new ParseGeoPoint(57.706576, 11.937236);
                    Toast.makeText(context, "Your current position is: " + parseGeoPoint, Toast.LENGTH_SHORT).show();
                    Intent intent =
                            new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=w"));

                    startActivityForResult(intent, 1);
                }
            }
        });

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }
}
