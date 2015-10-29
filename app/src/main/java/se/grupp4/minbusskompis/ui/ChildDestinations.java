package se.grupp4.minbusskompis.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.LocationCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;

import java.util.ArrayList;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.TravelingData;
import se.grupp4.minbusskompis.api.datatypes.vt.Coord;
import se.grupp4.minbusskompis.api.datatypes.vt.Leg;
import se.grupp4.minbusskompis.api.datatypes.vt.Trip;
import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;
import se.grupp4.minbusskompis.parsebuss.AsyncTaskCompleteCallback;
import se.grupp4.minbusskompis.parsebuss.BussData;
import se.grupp4.minbusskompis.parsebuss.BussDestination;
import se.grupp4.minbusskompis.ui.adapters.DestinationsAdapter;


/*
    ChildDestinations
    Lists available destinations for current child.
    Fetched from parse

    * Starts UpdateLocToParseService
    * Tries to fetch current position and calculate trip via västtrafik api when a destination is selected
 */
public class ChildDestinations extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "ChildDestinations";
    private static final long GPSTIMEOUT = 30*1000;
    private ViewHolder viewHolder;
    private String installationId;
    private Context context = this;
    private ArrayList<BussDestination> destinations;
    private DestinationsAdapter destinationsAdapter;
    TravelingData travelingData;

    private static class ViewHolder {
        ListView destinationsListView;
        TextView loadingTextView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_destinations);
        viewHolder = new ViewHolder();

        //Set initial status
        BussData.getInstance().setStatusForSelfAndNotifyParents(TravelingData.INACTIVE);

        //Initiate views
        initiateViews();

        //Start updateparseservice
        Log.d(TAG,"Starting update to parse service");
        Intent serviceIntent = new Intent(this, UpdateLocToParseService.class);
        startService(serviceIntent);

        //Initate list adapter, fetch and populate destinations
        destinations = new ArrayList<>();
        installationId = ParseInstallation.getCurrentInstallation().getInstallationId();
        destinationsAdapter =
                new DestinationsAdapter(
                        this,
                        R.layout.fragment_destinations_list_item,
                        destinations,
                        installationId
                );
        viewHolder.destinationsListView.setOnItemClickListener(this);
        viewHolder.destinationsListView.setAdapter(destinationsAdapter);
        new PopulateDestinationListTask().execute();
    }

    private void initiateViews() {
        viewHolder.destinationsListView = (ListView) findViewById(R.id.child_destinations_list);
        viewHolder.loadingTextView = (TextView) findViewById(R.id.child_destinations_loading_text);
    }

    /**
     * Get data from Västtrafik, start trip on callback
     */
    private class ApiCallTask extends AsyncTask<Coord, Void, TravelingData> {
        private TravelingData tData;

        public ApiCallTask(TravelingData tData) {
            this.tData = tData;
        }

        @Override
        protected TravelingData doInBackground(Coord...params)
        {
            Trip trip = se.grupp4.minbusskompis.api.Methods.getClosestTrip(params[0], params[1]);

            if(trip == null)
            {
                Log.d("ChildDestinations: ", "Trip is null.");
                return tData;
            }

            for(int i = 0; i < trip.getLegs().size(); i++)
            {
                if(!trip.getLegs().get(i).getValue("type").equals("WALK"))
                {
                    Leg l = trip.getLegs().get(i);

                    String geometryRef = l.getGeometryRef();
                    Coord startCoord = se.grupp4.minbusskompis.api.Methods.getGeometry(geometryRef).get(0);
                    Log.d(TAG, "BusStop at: " + startCoord.toString());

                    double lat = Double.parseDouble(startCoord.getLatitude());
                    double lng = Double.parseDouble(startCoord.getLongitude());

                    tData.bussStopCoordinates = new LatLng(lat, lng);
                    tData.bussStationName = l.getOrigin().getValue("name");
                    tData.busStopName = l.getDestination().getValue("name");
                    tData.bussStationChar = l.getOrigin().getValue("track");
                    tData.bussName = l.getValue("sname")+" " + l.getValue("direction");
                    tData.busLeavingAt = l.getOrigin().getValue("time");
                    tData.busArrivingAt = l.getOrigin().getValue("time");
                    break;
                }
            }

            return tData;
        }

        @Override
        protected void onPostExecute(TravelingData travelingData) {
            Intent intent = new Intent(context, ChildGoingToBus.class);
            intent.putExtra("data", travelingData);
            startActivity(intent);
            ((Activity)context).finish();
        }
    }

    /**
     * When clicking on a destination, current location is fetched via gps, a call is made to Västtrafik api to get the current trip.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Get destination coordinates
        BussDestination destination = (BussDestination) parent.getAdapter().getItem(position);
        LatLng targetDestination = new LatLng(destination.getDestination().getLatitude(),destination.getDestination().getLongitude());

        //Pass data forward
        travelingData = new TravelingData();
        travelingData.destinationCoordinates = targetDestination;
        travelingData.destinationName = destination.getName();

        //Get current gps position
        Toast.makeText(ChildDestinations.this, R.string.child_destinations_trying_to_get_gps, Toast.LENGTH_SHORT).show();
        ParseGeoPoint.getCurrentLocationInBackground(GPSTIMEOUT, new LocationCallback() {
            @Override
            public void done(ParseGeoPoint parseGeoPoint, ParseException e) {
                if (parseGeoPoint != null) {
                    Toast.makeText(ChildDestinations.this, R.string.child_destinatins_fetch_data, Toast.LENGTH_SHORT).show();
                    //Set current position
                    Coord from = new Coord(String.valueOf(parseGeoPoint.getLatitude()), String.valueOf(parseGeoPoint.getLongitude()));
                    //Set target destination from parse data, passed in travelingdata
                    Coord to = new Coord(String.valueOf(travelingData.destinationCoordinates.latitude), String.valueOf(travelingData.destinationCoordinates.longitude));
                    //Initiate api call in background, pass in travelingData with params
                    new ApiCallTask(travelingData).execute(from, to);
                }
                else{
                    Toast.makeText(ChildDestinations.this, R.string.child_destinations_trying_to_get_gps_not_found, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Fetch data from parse, populate list
     */
    private class PopulateDestinationListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            BussData.getInstance().fetchData(new AsyncTaskCompleteCallback() {
                @Override
                public void done() {
                    populateDestinations();
                }
            });
            return null;
        }
    }

    /**
     * Clear destinationslist and populate with destinations
     */
    private void populateDestinations() {
        destinationsAdapter.clear();
        ArrayList<BussDestination> destList =
                BussData.getInstance().getDestinationsForChild(
                        installationId);
        if(destList.isEmpty()){
            viewHolder.loadingTextView.setText(R.string.child_destinations_not_found);
            showMessage();
        }else{
            destinationsAdapter.addAll(destList);
            destinationsAdapter.notifyDataSetChanged();
            showContent();
        }
    }

    /**
     * Show loading message
     */
    private void showMessage() {
        viewHolder.loadingTextView.setVisibility(View.VISIBLE);
        viewHolder.destinationsListView.setVisibility(View.GONE);
    }

    /**
     * Show destinations list
     */
    private void showContent() {
        viewHolder.loadingTextView.setVisibility(View.GONE);
        viewHolder.destinationsListView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_child_destinations, menu);
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
            Intent intent = new Intent(getApplicationContext(), ChildChildCode.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_about)    {
            Intent intent = new Intent(getApplicationContext(), ChildInfoAbout.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
