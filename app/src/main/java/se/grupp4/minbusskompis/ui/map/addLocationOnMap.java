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
import se.grupp4.minbusskompis.parsebuss.ParseCloudData;
import se.grupp4.minbusskompis.parsebuss.BussDestination;
import se.grupp4.minbusskompis.ui.ParentChildDestinations;


public class addLocationOnMap extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "addLocationOnMap";

    private static class ViewHolder{
        AutoCompleteTextView autoCompleteLocationView;
        Button saveLocationButton;
        EditText saveAsName;
        ImageView clearText;
    }

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private ViewHolder viewHolder;
    GoogleMap mMap;
    LatLng destinationLatLng;
    private String childId;
    private static final LatLngBounds BOUNDS_SVERIGE = new LatLngBounds(
            new LatLng(57.958012, 10.916143), new LatLng(59.584815, 23.097162));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_child_destination_add);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.add_destination_map);
        mapFragment.getMapAsync(this);

        childId = getIntent().getStringExtra("child_id");
        mMap = mapFragment.getMap();
        viewHolder = new ViewHolder();

        //Initate views
        viewHolder.autoCompleteLocationView = (AutoCompleteTextView) findViewById(R.id.destination_add_autocomplete);
        viewHolder.saveLocationButton = (Button) findViewById(R.id.destination_add_save_button);
        viewHolder.saveAsName = (EditText) findViewById(R.id.destination_add_name);
        viewHolder.clearText = (ImageView) findViewById(R.id.add_destination_clear_text);

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
                    Toast.makeText(addLocationOnMap.this, "No destination selected", Toast.LENGTH_SHORT).show();
                }else{
                    if(destinationSaveName!=null && !destinationSaveName.equals("")){
                        saveLocation(destinationSaveName);
                    }
                }
            }
        };
        viewHolder.saveLocationButton.setOnClickListener(addLocClickListener);

        //Map onclick listener, saves map click as
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parent_children_add, menu);
        return true;
    }

    //Sätt startpos
    @Override
    public void onMapReady(GoogleMap map) {
        LatLng chalmers = new LatLng(57.70662817011354,11.93630151450634);
        setLocSetPinAndZoom(chalmers);
    }

    private void saveLocation(String name){
        ParseGeoPoint geoPoint = new ParseGeoPoint(destinationLatLng.latitude, destinationLatLng.longitude);
        BussDestination destination = new BussDestination(geoPoint,name);
        ParseCloudData.getInstance().addDestinationToChild(destination, childId);

        //Saveing, going back to childdestinations
        Toast.makeText(addLocationOnMap.this, "Saving: "+name, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ParentChildDestinations.class);
        intent.putExtra("child_id", childId);
        startActivity(intent);
    }

    private void setLocSetPinAndZoom(LatLng newLocation){
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 14));
        mMap.addMarker(new MarkerOptions().position(newLocation).title("Destination"));
    }


    //Hanterar klick i autocomplete listan
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

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    //Callback för vad man skall göra vid val av location
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

            //Spara val, flytta map
            destinationLatLng = place.getLatLng();
            setLocSetPinAndZoom(destinationLatLng);

            Log.i(TAG, "Place details received: " + place.getName());
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

//    // An AsyncTask class for accessing the GeoCoding Web Service
//    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {
//
//        @Override
//        protected List<Address> doInBackground(String... locationName) {
//            // Creating an instance of Geocoder class
//            Geocoder geocoder = new Geocoder(getBaseContext());
//            List<Address> addresses = null;
//
//            try {
//                // Getting a maximum of 3 Address that matches the input text
//                addresses = geocoder.getFromLocationName(locationName[0], 3);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return addresses;
//        }
//
//        @Override
//        protected void onPostExecute(List<Address> addresses) {
//
//            if (addresses == null || addresses.size() == 0) {
//                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
//            }
//
//            // Clears all the existing markers on the map
//            mMap.clear();
//
//            // Adding Markers on Google Map for each matching address
//            for (int i = 0; i < addresses.size(); i++) {
//
//                Address address = (Address) addresses.get(i);
//
//                // Creating an instance of GeoPoint, to display in Google Map
//                latLng = new LatLng(address.getLatitude(), address.getLongitude());
//
//                String addressText = String.format("%s",
//                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "");
//
//                markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//                markerOptions.title(addressText);
//
//                mMap.addMarker(markerOptions);
//
//                // Locate the first location
//                if (i == 0)
//                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//            }
//        }
//    }
}