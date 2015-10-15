package se.grupp4.minbusskompis.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import se.grupp4.minbusskompis.backgroundtasks.ChildLocationAndStatus;
import se.grupp4.minbusskompis.parsebuss.BussData;
import se.grupp4.minbusskompis.parsebuss.BussRelationMessenger;
import se.grupp4.minbusskompis.parsebuss.BussRelationships;

public class ParentActiveChild extends AppCompatActivity implements Observer {

    private static final String TAG = "PARENT_ACTIVE_CHILD";
    private GoogleMap map;
    private String childId;
    private int status;
    private double latitude;
    private double longitude;
    private TextView childName;
    private TextView childStatus;
    private TextView childGoingTo;
    private ImageView childStatusImage;
    private String destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_active_child);
        childId = getIntent().getStringExtra("child_id");

        //Init views
        childName = (TextView) findViewById(R.id.parent_active_child_name_textview);
        childStatus = (TextView) findViewById(R.id.parent_active_child_current_status);
        childStatusImage = (ImageView) findViewById(R.id.parent_active_child_status_icon);
        childGoingTo = (TextView) findViewById(R.id.parent_active_child_destination_textview);
        map = ((MapFragment) getFragmentManager().
                findFragmentById(R.id.parent_active_child_map)).getMap();

        //Set initial values
        childName.setText(BussData.getInstance().getNameFromId(childId));

        //Listen to child
        BussRelationships relationships = BussData.getInstance().getChildren();
        BussRelationMessenger.getInstance().setRelationships(relationships);

        //Get and set initial values
        ChildLocationAndStatus initialStatus = BussData.getInstance().getChildLocationAndStatusForId(childId);
        updateInfo(initialStatus);

        //Start listening for updates
        BussRelationMessenger.getInstance().addObserver(this);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable observable, Object data) {
        Toast.makeText(ParentActiveChild.this, "Got update!", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"Got update!");
        JSONObject object = (JSONObject) data;
        try {
            if (object.getString("from").equals(childId)) {
                Log.d(TAG, "Update was from my child!");
                ChildLocationAndStatus locationAndStatus =
                        BussData.getInstance().getChildLocationAndStatusForId(childId);
                updateInfo(locationAndStatus);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateInfo(ChildLocationAndStatus locationAndStatus) {
        status = locationAndStatus.getTripStatus();
        destination = locationAndStatus.getDestination();
        latitude = locationAndStatus.getLatitude();
        longitude = locationAndStatus.getLongitude();
        updateStatusText();
        updateDestinationText();
        updatePinAndCamera();
    }

    private void updateDestinationText() {
        childGoingTo.setText("Going to: " +destination);
    }

    private void updateStatusText() {
        switch (status){
            case 0:
                childStatus.setText("Child not active");
                childStatusImage.setImageResource(R.drawable.inactive);
                break;
            case 1:
                childStatus.setText("Going to bus stop");
                childStatusImage.setImageResource(R.drawable.walking);
                break;
            case 2:
                childStatus.setText("At bus stop");
                childStatusImage.setImageResource(R.drawable.busstop);
                break;
            case 3:
                childStatus.setText("Traveling by bus");
                childStatusImage.setImageResource(R.drawable.bus);
                break;
            case 4:
                childStatus.setText("Leaving bus");
                childStatusImage.setImageResource(R.drawable.busstop);
                break;
            case 5:
                childStatus.setText("Going to destination");
                childStatusImage.setImageResource(R.drawable.walking);
                break;
            default:
                childStatus.setText("Child is active");
                childStatusImage.setImageResource(R.drawable.inactive);
        }
    }

    private void updatePinAndCamera() {
        //Update pin and camera
        map.clear();
        LatLng position = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(position).
                title(childName.getText().toString()));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
    }
}
