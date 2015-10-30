package se.grupp4.minbusskompis.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.TravelingData;
import se.grupp4.minbusskompis.backgroundtasks.ChildLocationAndStatus;
import se.grupp4.minbusskompis.parsebuss.ParseCloudManager;
import se.grupp4.minbusskompis.parsebuss.BussRelationMessenger;
import se.grupp4.minbusskompis.parsebuss.BussRelationships;
/*
    ParentActiveChild
    See details information about an active child.

    * Fetches tripdata from parse, with position and trip status
    * Observes BussRelationMessanger that will receive the updates on ongoing trips
 */
public class ParentActiveChild extends AppCompatActivity implements Observer {

    private static final String TAG = "PARENT_ACTIVE_CHILD";
    private GoogleMap map;
    private String childId;
    private int status;
    private double latitude;
    private double longitude;
    private String destination;
    private ViewHolder viewHolder;

    private class ViewHolder {
        TextView childName;
        TextView childStatus;
        TextView childGoingTo;
        ImageView childStatusImage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_active_child);
        viewHolder = new ViewHolder();
        childId = getIntent().getStringExtra("child_id");

        //Init views
        initViews();

        //Set initial values
        viewHolder.childName.setText(ParseCloudManager.getInstance().getNameFromId(childId));

        //Listen to child
        BussRelationships relationships = ParseCloudManager.getInstance().getChildren();
        BussRelationMessenger.getInstance().setRelationships(relationships);

        //Get and set initial values
        ChildLocationAndStatus initialStatus = ParseCloudManager.getInstance().getChildLocationAndStatusForId(childId);
        updateInfo(initialStatus);

        //Start listening for updates
        Log.d(TAG, "onCreate: adding self as observer");
        BussRelationMessenger.getInstance().addObserver(this);
    }

    private void initViews() {
        viewHolder.childName = (TextView) findViewById(R.id.parent_active_child_name_textview);
        viewHolder.childStatus = (TextView) findViewById(R.id.parent_active_child_current_status);
        viewHolder.childStatusImage = (ImageView) findViewById(R.id.parent_active_child_status_icon);
        viewHolder.childGoingTo = (TextView) findViewById(R.id.parent_active_child_destination_textview);
        map = ((MapFragment) getFragmentManager().
                findFragmentById(R.id.parent_active_child_map)).getMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parent_active_child, menu);
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
            Intent intent = new Intent(getApplicationContext(), ParentSettings.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about)    {
            Intent intent = new Intent(getApplicationContext(), ParentInfoAbout.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * On an update new data will be populated
     */
    @Override
    public void update(Observable observable, Object data) {
        Log.d(TAG, "update() called with: " + "observable = [" + observable + "], data = [" + (JSONObject)data + "]");
        Log.d(TAG,"Got update!");
        JSONObject object = (JSONObject) data;
        try {
            if (object.getString("from").equals(childId)) {
                Log.d(TAG, "Update was from my child!");
                ChildLocationAndStatus locationAndStatus =
                        ParseCloudManager.getInstance().getChildLocationAndStatusForId(childId);
                updateInfo(locationAndStatus);
            }else{
                Log.d(TAG, "update: Update was NOT from my child");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update views with new data
     * @param locationAndStatus Data about the current location and status
     */
    private void updateInfo(ChildLocationAndStatus locationAndStatus) {
        Log.d(TAG, "updateInfo() called with: " + "locationAndStatus = [" + locationAndStatus + "]");
        status = locationAndStatus.getTripStatus();
        destination = locationAndStatus.getDestination();
        latitude = locationAndStatus.getLatitude();
        longitude = locationAndStatus.getLongitude();
        updateStatusText();
        updateDestinationText();
        updatePinAndCamera();
    }

    /**
     * Update destinationtext
     */
    private void updateDestinationText() {
        viewHolder.childGoingTo.setText("Going to: " +destination);
    }

    /**
     * Update statustext and icon based on trip status
     */
    private void updateStatusText() {
        switch (status){
            case TravelingData.INACTIVE:
                viewHolder.childStatus.setText(R.string.parent_active_child_inactive_text);
                viewHolder.childStatusImage.setImageResource(R.drawable.inactive);
                Intent backIntent = new Intent(this,ParentChildrenList.class);
                startActivity(backIntent);
                finish();
                break;
            case TravelingData.WALKING:
                viewHolder.childStatus.setText(R.string.parent_active_child_walking_text);
                viewHolder.childStatusImage.setImageResource(R.drawable.walking);
                break;
            case TravelingData.AT_BUS_STATION:
                viewHolder.childStatus.setText(R.string.parent_active_child_atbusstation_text);
                viewHolder.childStatusImage.setImageResource(R.drawable.busstop);
                break;
            case TravelingData.ON_BUS:
                viewHolder.childStatus.setText(R.string.parent_active_child_onbus_text);
                viewHolder.childStatusImage.setImageResource(R.drawable.bus);
                break;
            case TravelingData.LEAVING_BUS:
                viewHolder.childStatus.setText(R.string.parent_active_child_leavingbus_text);
                viewHolder.childStatusImage.setImageResource(R.drawable.busstop);
                break;
            case 5:
                viewHolder.childStatus.setText(R.string.parent_active_child_goingtodestination_text);
                viewHolder.childStatusImage.setImageResource(R.drawable.walking);
                break;
            default:
                viewHolder.childStatus.setText(R.string.parent_active_child_default);
                viewHolder.childStatusImage.setImageResource(R.drawable.inactive);
        }
    }

    /**
     * Move map pin, refocus and zoom
     */
    private void updatePinAndCamera() {
        //Update pin and camera
        map.clear();
        LatLng position = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(position).
                title(viewHolder.childName.getText().toString()));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
    }
}
