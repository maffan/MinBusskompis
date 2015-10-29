package se.grupp4.minbusskompis.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.parsebuss.ParseCloudManager;
import se.grupp4.minbusskompis.parsebuss.BussDestination;
import se.grupp4.minbusskompis.ui.ParentChildDestinations;

/*
    addLocationOnMap
    Provides an activity that enables parents to add location on his or her child
    When either searching for a place or clicking on the map the location can be saved as a choosen name
    * Uses PlaceAutocompleter from google to find places when searching
    * Uses Google maps to find locations and show a map
 */
public class addLocationOnMap extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "addLocationOnMap";
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private ViewHolder viewHolder;
    GoogleMap mMap;
    LatLng destinationLatLng;
    private String childId;
    private static final LatLngBounds BOUNDS_SVERIGE = new LatLngBounds(
            new LatLng(57.958012, 10.916143), new LatLng(59.584815, 23.097162));

    private static class ViewHolder{
        AutoCompleteTextView autoCompleteLocationView;
        Button saveLocationButton;
        EditText saveAsName;
        ImageView clearText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_child_destination_add);
        viewHolder = new ViewHolder();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.add_destination_map);
        mapFragment.getMapAsync(this);

        childId = getIntent().getStringExtra("child_id");
        mMap = mapFragment.getMap();

        //Initate views
        initateViews();

        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        // fixa adaptern, hämta data från endast sverige
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_SVERIGE,
                null);
        viewHolder.autoCompleteLocationView.setAdapter(mAdapter);
        viewHolder.autoCompleteLocationView.setOnItemClickListener(mAutocompleteClickListener);

        //Clear button
        viewHolder.clearText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.autoCompleteLocationView.setText("");
            }
        });

        //Save button listener
        OnClickListener addLocClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                String destinationSaveName = viewHolder.saveAsName.getText().toString();
                if(destinationLatLng == null){
                    Toast.makeText(addLocationOnMap.this,R.string.addlocationonmap_no_dest, Toast.LENGTH_SHORT).show();
                }
                else if(destinationSaveName.equals("")){
                    Toast.makeText(addLocationOnMap.this, R.string.addlocationonmap_no_name, Toast.LENGTH_SHORT).show();
                }
                else if(destinationSaveName!=null && !destinationSaveName.equals("")){
                        saveLocation(destinationSaveName);
                }
            }
        };
        viewHolder.saveLocationButton.setOnClickListener(addLocClickListener);

        //Map onclick listener, if you move the pin, a new location is saved
        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                destinationLatLng = point;
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                mMap.addMarker(new MarkerOptions().position(point).title("Pin"));
            }
        });
    }

    private void initateViews() {
        viewHolder.autoCompleteLocationView = (AutoCompleteTextView) findViewById(R.id.destination_add_autocomplete);
        viewHolder.saveLocationButton = (Button) findViewById(R.id.destination_add_save_button);
        viewHolder.saveAsName = (EditText) findViewById(R.id.destination_add_name);
        viewHolder.clearText = (ImageView) findViewById(R.id.add_destination_clear_text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parent_children_add, menu);
        return true;
    }

    //Initiate starting position
    @Override
    public void onMapReady(GoogleMap map) {
        LatLng chalmers = new LatLng(57.70662817011354,11.93630151450634);
        setLocSetPinAndZoom(chalmers);
    }

    /**
     * Save current long and lat into a destinationame for child
     * @param name String
     */
    private void saveLocation(String name){
        ParseGeoPoint geoPoint = new ParseGeoPoint(destinationLatLng.latitude, destinationLatLng.longitude);
        BussDestination destination = new BussDestination(geoPoint,name);
        ParseCloudManager.getInstance().addDestinationToChild(destination, childId);

        //Saveing, going back to childdestinations
        Toast.makeText(addLocationOnMap.this, R.string.addlocationonmap_saving + ": " + name, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ParentChildDestinations.class);
        intent.putExtra("child_id", childId);
        startActivity(intent);
    }

    /**
     * Move pin, camera and zoom onto a new location
     * @param newLocation
     */
    private void setLocSetPinAndZoom(LatLng newLocation){
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 14));
        mMap.addMarker(new MarkerOptions().position(newLocation).title("Destination"));
    }


    //Autocomplete adapter
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.d(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    //Callback after selected place
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            //Do something with the selected place, move camera and pin to that location
            destinationLatLng = place.getLatLng();
            setLocSetPinAndZoom(destinationLatLng);

            Log.d(TAG, "Place details received: " + place.getName());
            places.release();
        }
    };

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }
}