package se.grupp4.minbusskompis.parsebuss;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import se.grupp4.minbusskompis.backgroundtasks.ChildLocationAndStatus;

public class BussData {
    public static final String TAG = "BUSSDATA";
    public static final int PARENT = 0;
    public static final int CHILD = 1;
    private static final String PARENTS_FIELD = "parents";
    private static final String CHILDREN_FIELD = "children";
    private static final BussData bussData = new BussData();
    public static final String DESTINATIONS_FIELD = "Destinations";
    public static final String INSTALLATION_CLASS = "Installation";
    public static final String INSTALLATION_FIELD = "installationId";
    public static final String POSITION_FIELD = "position";
    public static final String STATUS_FIELD = "status";

    private List<String> parents;
    private List<String> children;
    private List destinations;


    /*
        1, Vid start av app initeras de alla ParseObject, dvs skapar en koppling mot parse
            * Vid en query begränsas urvalet, normalt en rad i tabellen i fråga, detta blir ett objekt
            * Vid hämting av flera rader skapas en lista med objekt, dvs man hämtar då ett objekt för att nå den radens data
        2, För att hämta data, kallar man på ParseObjektet i fråga, ex position.fetch().
            * Detta hämtar den senaste datan, begränsat av queryn ovan
        3, För att spara används .save() på objektet, detta skall ske efter ändringar är gjorda.
     */

    public static BussData getInstance() {
        return bussData;
    }

    //Initerar parseobjekten, gör kopplingen mot parse.
    public void fetchData(AsyncTaskCompleteCallback callback){
        new FetchDataTask(callback).execute();
    }

    /**
     * Created by Marcus on 9/29/2015.
     */
    private class FetchDataTask extends AsyncTask<Void, Void, Void> {
        private final AsyncTaskCompleteCallback callback;

        public FetchDataTask(@Nullable AsyncTaskCompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //ParseInstallation already initialized, can be used to fetch data
            Log.d(TAG, "Fetching task started");
            Log.d(TAG, "doInBackground: Fetching destinations..");
            ParseObject cloudDestinations = getOrMakeDestinationsObjectForID(getInstallationId());
            destinations = cloudDestinations.getList(DESTINATIONS_FIELD);
            Log.d(TAG, "doInBackground: Got destinations as: " + destinations);
            ParseQuery query = ParseQuery.getQuery("Relationships");
            ParseObject cloudRelationships = getOrMakeRelationshipsObjectForId(query);
            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            if(callback != null)
                callback.done();
        }
    }

    private ParseObject getOrMakeRelationshipsObjectForId(ParseQuery query) {
        query.whereEqualTo(INSTALLATION_FIELD,getInstallationId());
        ParseObject cloudRelationships = null;
        try {
            cloudRelationships = query.getFirst();
        } catch (ParseException e) {
            if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                cloudRelationships = new ParseObject("Relationships");
                cloudRelationships.put(PARENTS_FIELD,new LinkedList<>());
                cloudRelationships.put(CHILDREN_FIELD,new LinkedList());
            } else {
                e.printStackTrace();
            }
        }
        return cloudRelationships;
    }

    public void addDestinationToChild(BussDestination destination, String childId){
        try {
            ParseObject cloudDestinations = getOrMakeDestinationsObjectForID(childId);
            cloudDestinations.getList(DESTINATIONS_FIELD).add(destination.getAsJSONObject());
            cloudDestinations.saveInBackground();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ParseObject getOrMakeDestinationsObjectForID(String installationId) {
        ParseObject cloudDestinations = null;
        ParseQuery query = ParseQuery.getQuery(DESTINATIONS_FIELD);
        query.whereEqualTo(INSTALLATION_FIELD, installationId);
        try {
            Log.d(TAG, "Fetching... ");
            cloudDestinations = query.getFirst();
        } catch (ParseException e) {
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                cloudDestinations = new ParseObject(query.getClassName());
                cloudDestinations.put(INSTALLATION_FIELD, getInstallationId());
                cloudDestinations.put(DESTINATIONS_FIELD, new LinkedList<>());
                try {
                    cloudDestinations.save();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
            else{
                e.printStackTrace();
            }
        }
        return cloudDestinations;
    }

    private String getInstallationId() {
        return ParseInstallation.getCurrentInstallation().getInstallationId();
    }

    public BussRelationships getParents(){
        return new BussRelationships(parents);
    }

    public BussRelationships getChildren(){
        return new BussRelationships(children);
    }

    public List<BussDestination> getDestinations(){
        return BussDestination.getAsDestinationList(destinations);
    }

    public void removeDestinationFromChild(String destinationName, String childId){
        try {
            ParseQuery query = ParseQuery.getQuery(INSTALLATION_CLASS);
            ParseObject installation = query.whereEqualTo(INSTALLATION_FIELD,childId).getFirst();
            List<BussDestination> destinationList = BussDestination.getAsDestinationList(installation.<HashMap>getList(DESTINATIONS_FIELD));
            for (BussDestination destination :
                    destinationList) {
                if (destination.getName().equals(destinationName)) {
                    destinationList.remove(destination);
                    break;
                }
            }
            installation.put(DESTINATIONS_FIELD, BussDestination.getAsJSONList(destinationList));
            installation.saveInBackground();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addRelationship(String id, int type){
        switch (type){
            case PARENT:
                parents.add(id);
                ParseInstallation.getCurrentInstallation().addAllUnique(PARENTS_FIELD, parents);
                break;
            case CHILD:
                children.add(id);
                ParseInstallation.getCurrentInstallation().addAllUnique(CHILDREN_FIELD, children);
                break;
        }
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public void removeRelationship(String id, int type){
        switch (type){
            case PARENT:
                parents.remove(id);
                ParseInstallation.getCurrentInstallation().put(PARENTS_FIELD, parents);
                break;
            case CHILD:
                children.remove(id);
                ParseInstallation.getCurrentInstallation().put(CHILDREN_FIELD, children);
                break;
        }
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public void updateLatestPosition(ChildLocationAndStatus location){
        ParseGeoPoint geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        int status = location.getTripStatus();
        ParseInstallation.getCurrentInstallation().put(POSITION_FIELD,geoPoint);
        ParseInstallation.getCurrentInstallation().put(STATUS_FIELD,status);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public String getNameFromId(String id){
        ParseQuery query = ParseQuery.getQuery(INSTALLATION_CLASS);
        try {
            ParseObject installation = query.whereEqualTo(INSTALLATION_FIELD,id).getFirst();
            String name = installation.getString("name");
            return name;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
