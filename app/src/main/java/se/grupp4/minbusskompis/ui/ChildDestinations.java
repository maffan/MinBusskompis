package se.grupp4.minbusskompis.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseInstallation;

import java.util.ArrayList;

import se.grupp4.minbusskompis.R;
import se.grupp4.minbusskompis.TravelingData;
import se.grupp4.minbusskompis.api.datatypes.vt.Coord;
import se.grupp4.minbusskompis.api.datatypes.vt.Leg;
import se.grupp4.minbusskompis.api.datatypes.vt.Trip;
import se.grupp4.minbusskompis.backgroundtasks.GPSTracker;
import se.grupp4.minbusskompis.backgroundtasks.UpdateLocToParseService;
import se.grupp4.minbusskompis.parsebuss.AsyncTaskCompleteCallback;
import se.grupp4.minbusskompis.parsebuss.BussData;
import se.grupp4.minbusskompis.parsebuss.BussDestination;
import se.grupp4.minbusskompis.ui.adapters.DestinationsAdapter;


//Note that dummybuttons are temporary for debugging
public class ChildDestinations extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "ChildDestinations";
    private ViewHolder viewHolder;
    private String installationId;
    Context context = this;
    private GoogleApiClient mGoogleApiClient;

    private static class ViewHolder {
        ListView destinationsListView;
        TextView loadingTextView;
    }

    private DestinationsAdapter destinationsAdapter;
    private ArrayList<BussDestination> destinations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_destinations);

        //Set initial status
        BussData.getInstance().setStatusForSelfAndNotifyParents(TravelingData.INACTIVE);

        viewHolder = new ViewHolder();

        //Initiate views
        viewHolder.destinationsListView = (ListView) findViewById(R.id.child_destinations_list);
        viewHolder.loadingTextView = (TextView) findViewById(R.id.child_destinations_loading_text);

        //Start updateparseservice
        Log.d(TAG,"Starting update to parse service");
        Intent serviceIntent = new Intent(this, UpdateLocToParseService.class);
        startService(serviceIntent);

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
                    Log.d("CHILDDESTINATIONS: ", "BusStop at: " + startCoord.toString());

                    double lat = Double.parseDouble(startCoord.getLatitude());
                    double lng = Double.parseDouble(startCoord.getLongitude());

                    tData.bussStopCoordinates = new LatLng(lat, lng);
                    tData.bussStationName = l.getOrigin().getValue("name");
                    tData.busStopName = l.getDestination().getValue("name");
                    tData.bussStationChar = l.getOrigin().getValue("track");
                    tData.bussName = l.getValue("sname ") + l.getValue("direction");
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
            Toast.makeText(ChildDestinations.this, "Starting journey...", Toast.LENGTH_LONG).show();

            ((Activity)context).finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Send information to next intent
        BussDestination destination = (BussDestination) parent.getAdapter().getItem(position);
        LatLng targetDestination = new LatLng(destination.getDestination().getLatitude(),destination.getDestination().getLongitude());
        LatLng busStopCordinates = new LatLng(destination.getDestination().getLatitude(),destination.getDestination().getLongitude());
        Log.d(this.getLocalClassName(), "Destination: " + busStopCordinates.toString());

        //Pass data forward
        TravelingData travelingData = new TravelingData();
        travelingData.destinationCoordinates = targetDestination;
        travelingData.bussStopCoordinates = busStopCordinates;
        travelingData.destinationName = destination.getName();

        GPSTracker gps = new GPSTracker(this);
        LatLng latLng = new LatLng(gps.getLatitude(),gps.getLongitude());
        Log.d(this.getLocalClassName(), "Currentlocation: " + latLng.toString());

        Coord from = new Coord(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
        Coord to = new Coord(String.valueOf(targetDestination.latitude), String.valueOf(targetDestination.longitude));
        new ApiCallTask(travelingData).execute(from, to);
        Toast.makeText(ChildDestinations.this, R.string.child_destinatins_fetch_data, Toast.LENGTH_SHORT).show();
    }

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

    private void populateDestinations() {
        destinationsAdapter.clear();
        ArrayList<BussDestination> destList =
                BussData.getInstance().getDestinationsForChild(
                        installationId);
        if(destList.isEmpty()){
            viewHolder.loadingTextView.setText("No destinations for child");
            showMessage();
        }else{
            destinationsAdapter.addAll(destList);
            destinationsAdapter.notifyDataSetChanged();
            showContent();
        }
    }

    private void showMessage() {
        viewHolder.loadingTextView.setVisibility(View.VISIBLE);
        viewHolder.destinationsListView.setVisibility(View.GONE);
    }

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

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.all_exit_dialog_title)
                .setMessage(R.string.all_exit_dialog_message)
                .setPositiveButton(R.string.all_exit_dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton(R.string.all_exit_dialog_no, null)
                .show();
    }
}
