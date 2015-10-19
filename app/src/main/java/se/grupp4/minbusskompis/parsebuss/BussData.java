package se.grupp4.minbusskompis.parsebuss;

import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import se.grupp4.minbusskompis.TravelingData;
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
    private ParseObject cloudDestinations;
    private ParseObject cloudRelationships;
    private ParseObject cloudName;
    private String name;
    private ParseObject cloudPosition;


    /*
        1, Vid startLookForWifi av app initeras de alla ParseObject, dvs skapar en koppling mot parse
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
            cloudDestinations = getOrMakeDestinationsObjectForID(getInstallationId());
            destinations = cloudDestinations.getList(DESTINATIONS_FIELD);
            Log.d(TAG, "doInBackground: Got destinations as: " + destinations);
            cloudRelationships = getOrMakeRelationshipsObjectForId(getInstallationId());
            parents = cloudRelationships.getList(PARENTS_FIELD);
            children = cloudRelationships.getList(CHILDREN_FIELD);
            cloudName = getOrMakeNameObjectForId(getInstallationId());
            name = cloudName.getString("name");

            cloudPosition = getOrMakePositionObjectForId(getInstallationId());
            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            if(callback != null)
                callback.done();
        }
    }

    public void setStatusForChild(int status, String childId){
        ParseObject cloudPosition = getOrMakePositionObjectForId(childId);
        cloudPosition.put("status",status);
        cloudPosition.saveInBackground();
    }

    public void setStatusForSelfAndNotifyParents(int status){
        cloudPosition.put("status",status);
        cloudPosition.saveInBackground();
        if(status != TravelingData.INACTIVE)
            BussRelationMessenger.getInstance().sendStatusUpdateNotification(status);
    }

    private ParseObject getOrMakePositionObjectForId(String id) {
        ParseQuery query = ParseQuery.getQuery("Position");
        query.whereEqualTo(INSTALLATION_FIELD, id);
        ParseObject positionObject = null;
        try {
            positionObject = query.getFirst();
        } catch (ParseException e) {
            positionObject = new ParseObject("Position");
            positionObject.put(INSTALLATION_FIELD,id);
            positionObject.put("position",new ParseGeoPoint(0,0));
            positionObject.put("status",0);
            positionObject.put("destination","");
            try {
                positionObject.save();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        return positionObject;
    }

    private ParseObject getOrMakeNameObjectForId(String id){
        ParseQuery query = ParseQuery.getQuery("Name");
        query.whereEqualTo(INSTALLATION_FIELD, id);
        ParseObject cloudName = null;
        try {
            cloudName = query.getFirst();
        } catch (ParseException e) {
            if (e.getCode() == ParseException.OBJECT_NOT_FOUND){
                cloudName = new ParseObject("Name");
                cloudName.put(INSTALLATION_FIELD,id);
                cloudName.put("name","default");
                try {
                    cloudName.save();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
            else{
                e.printStackTrace();
            }
        }
        return cloudName;
    }

    private ParseObject getOrMakeRelationshipsObjectForId(String id) {
        ParseQuery query = ParseQuery.getQuery("Relationships");
        query.whereEqualTo(INSTALLATION_FIELD,id);
        ParseObject cloudRelationships = null;
        try {
            cloudRelationships = query.getFirst();
        } catch (ParseException e) {
            if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                cloudRelationships = new ParseObject("Relationships");
                cloudRelationships.put(INSTALLATION_FIELD,id);
                cloudRelationships.put(PARENTS_FIELD,new LinkedList<>());
                cloudRelationships.put(CHILDREN_FIELD,new LinkedList<>());
                try {
                    cloudRelationships.save();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
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
        Log.d(TAG, "getOrMakePositionObjectForId: Getting destination object for id: "+installationId);
        ParseObject cloudDestinations = null;
        ParseQuery query = ParseQuery.getQuery(DESTINATIONS_FIELD);
        query.whereEqualTo(INSTALLATION_FIELD, installationId);
        try {
            Log.d(TAG, "Fetching... ");
            cloudDestinations = query.getFirst();
        } catch (ParseException e) {
            if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                Log.d(TAG, "getOrMakeDestinationsObjectForID: No object exists. Creating new object for ID");
                cloudDestinations = new ParseObject(query.getClassName());
                cloudDestinations.put(INSTALLATION_FIELD, installationId);
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
        Log.d(TAG, "getOrMakeDestinationsObjectForID: Found destinations object for ID");
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

    public void removeDestinationFromChild(String destinationName, String childId){
        try {
            ParseObject destinationsObject = getOrMakeDestinationsObjectForID(childId);

            List<BussDestination> destinationList = BussDestination.getAsDestinationList(destinationsObject.<HashMap>getList(DESTINATIONS_FIELD));
            for (BussDestination destination :
                    destinationList) {
                if (destination.getName().equals(destinationName)) {
                    destinationList.remove(destination);
                    break;
                }
            }
            destinationsObject.put(DESTINATIONS_FIELD, BussDestination.getAsJSONList(destinationList));
            destinationsObject.saveInBackground();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<BussDestination> getDestinationsForChild(String id){
        ArrayList<BussDestination> list = new ArrayList<>();
        ParseObject destinationsObject = getOrMakeDestinationsObjectForID(id);
        Log.d(TAG, "getDestinationsForChild: Got destination object");
        List<HashMap> parseList = destinationsObject.getList(DESTINATIONS_FIELD);
        Log.d(TAG, "addDestinationToChild: got destinations: "+parseList);
        list.addAll(BussDestination.getAsDestinationList(parseList));
        return list;
    }

    public List<BussDestination> getDestinations(){
        return BussDestination.getAsDestinationList(destinations);
    }

    public void addRelationship(String id, int type){
        switch (type){
            case PARENT:
                parents.add(id);
                cloudRelationships.addAllUnique(PARENTS_FIELD, parents);
                break;
            case CHILD:
                children.add(id);
                cloudRelationships.addAllUnique(CHILDREN_FIELD, children);
                break;
        }
        cloudRelationships.saveInBackground();
    }

    public void removeRelationship(String id, int type){
        switch (type){
            case PARENT:
                parents.remove(id);
                cloudRelationships.put(PARENTS_FIELD, parents);
                break;
            case CHILD:
                children.remove(id);
                cloudRelationships.put(CHILDREN_FIELD, children);
                break;
        }
        cloudRelationships.saveInBackground();
    }

    public void updateLatestPosition(ChildLocationAndStatus location){
        ParseGeoPoint geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        int status = location.getTripStatus();
        cloudPosition.put("position",geoPoint);
        cloudPosition.put("status",status);
        cloudPosition.put("destination",location.getDestination());
        cloudPosition.saveInBackground();
    }

    public ChildLocationAndStatus getChildLocationAndStatusForId(String id){
        ParseObject positionObject = getOrMakePositionObjectForId(id);
        ParseGeoPoint geoPoint = positionObject.getParseGeoPoint("position");
        Location location = new Location("ParseCloud");
        location.setLatitude(geoPoint.getLatitude());
        location.setLongitude(geoPoint.getLongitude());
        int status = positionObject.getInt("status");
        String destination = positionObject.getString("destination");
        return new ChildLocationAndStatus(location,status,destination);
    }

    public String getNameFromId(String id){
        ParseQuery query = ParseQuery.getQuery("Name");
        try {
            ParseObject cloudName = query.whereEqualTo(INSTALLATION_FIELD,id).getFirst();
            return cloudName.getString("name");
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setNameForId(String name, String id){
        ParseObject nameObject = getOrMakeNameObjectForId(id);
        nameObject.put("name",name);
        nameObject.saveInBackground();
    }

    public String getOwnName(){
        return cloudName.getString("name");
    }

    public void clearParseData(){
        this.fetchData(new AsyncTaskCompleteCallback() {
            @Override
            public void done() {
                try {
                    cloudPosition.delete();
                    cloudRelationships.delete();
                    cloudDestinations.delete();
                    cloudName.delete();
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e(TAG,"Could not clear data");
                }
            }
        });
    }
}
