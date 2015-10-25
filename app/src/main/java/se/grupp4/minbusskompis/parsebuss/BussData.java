package se.grupp4.minbusskompis.parsebuss;

import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;

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
    public static final String DESTINATIONS_TYPE = "Destinations";
    public static final String INSTALLATION_CLASS = "Installation";
    public static final String INSTALLATION_FIELD = "installationId";
    public static final String POSITION_FIELD = "position";
    public static final String STATUS_FIELD = "status";
    public static final String NAME_FIELD = "name";
    public static final String POSITION_TYPE = "Position";
    public static final String NAME_TYPE = "Name";
    public static final String RELATIONSHIPS_TYPE = "Relationships";

    private List<String> parents;
    private List<String> children;
    private List destinations;
    private ParseObject cloudDestinations;
    private ParseObject cloudRelationships;
    private ParseObject cloudName;
    private String name;
    private ParseObject cloudPosition;

    public static BussData getInstance() {
        return bussData;
    }

    //Initerar parseobjekten, gör kopplingen mot parse.
    public void fetchData(AsyncTaskCompleteCallback callback){
        new FetchDataTask(callback).execute();
    }

    private class FetchDataTask extends AsyncTask<Void, Void, Void> {
        private final AsyncTaskCompleteCallback callback;

        public FetchDataTask(AsyncTaskCompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            cloudDestinations = getOrMakeDestinationsObjectForID(getInstallationId());
            destinations = cloudDestinations.getList(DESTINATIONS_TYPE);

            cloudRelationships = getOrMakeRelationshipsObjectForId(getInstallationId());
            parents = cloudRelationships.getList(PARENTS_FIELD);
            children = cloudRelationships.getList(CHILDREN_FIELD);

            cloudName = getOrMakeNameObjectForId(getInstallationId());
            name = cloudName.getString(NAME_FIELD);

            cloudPosition = getOrMakePositionObjectForId(getInstallationId());
            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            if(callback != null)
                callback.done();
        }
    }

    private ParseObject getOrMakePositionObjectForId(String id) {
        ParseQuery query = getTypeQueryForId(POSITION_TYPE, id);
        ParseObject positionObject;
        try {
            positionObject = query.getFirst();
        } catch (ParseException e) {
            //Could not get object from parse
            positionObject = createDefaultPositionObjectForId(id);
        }
        return positionObject;
    }

    @NonNull
    private ParseObject createDefaultPositionObjectForId(String id) {
        ParseObject positionObject;
        positionObject = new ParseObject(POSITION_TYPE);
        positionObject.put(INSTALLATION_FIELD,id);
        positionObject.put(POSITION_FIELD,new ParseGeoPoint(0,0));
        positionObject.put(BussData.STATUS_FIELD,0);
        positionObject.put(DESTINATIONS_TYPE,"");
        try {
            positionObject.save();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return positionObject;
    }

    @NonNull
    private ParseQuery getTypeQueryForId(String type, String id) {
        ParseQuery query = ParseQuery.getQuery(type);
        query.whereEqualTo(INSTALLATION_FIELD, id);
        return query;
    }

    private ParseObject getOrMakeNameObjectForId(String id){
        ParseQuery query = getTypeQueryForId(NAME_TYPE,id);
        ParseObject cloudName;
        try {
            cloudName = query.getFirst();
        } catch (ParseException e) {
            cloudName = createDefaultNameObjectForId(id);
        }
        return cloudName;
    }

    @NonNull
    private ParseObject createDefaultNameObjectForId(String id) {
        ParseObject cloudName;
        cloudName = new ParseObject(NAME_TYPE);
        cloudName.put(INSTALLATION_FIELD,id);
        cloudName.put(NAME_FIELD,"default");
        try {
            cloudName.save();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return cloudName;
    }

    private ParseObject getOrMakeRelationshipsObjectForId(String id) {
        ParseQuery query = getTypeQueryForId(RELATIONSHIPS_TYPE,id);
        ParseObject cloudRelationships;
        try {
            cloudRelationships = query.getFirst();
        } catch (ParseException e) {
            cloudRelationships = getDefaultRelationshipsObject(id);
        }
        return cloudRelationships;
    }

    @NonNull
    private ParseObject getDefaultRelationshipsObject(String id) {
        ParseObject cloudRelationships;
        cloudRelationships = new ParseObject(RELATIONSHIPS_TYPE);
        cloudRelationships.put(INSTALLATION_FIELD,id);
        cloudRelationships.put(PARENTS_FIELD,new LinkedList<>());
        cloudRelationships.put(CHILDREN_FIELD,new LinkedList<>());
        try {
            cloudRelationships.save();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return cloudRelationships;
    }

    private ParseObject getOrMakeDestinationsObjectForID(String installationId) {
        ParseQuery query = getTypeQueryForId(DESTINATIONS_TYPE,installationId);
        ParseObject cloudDestinations;
        try {
            cloudDestinations = query.getFirst();
        } catch (ParseException e) {
            cloudDestinations = getDefaultDestinationsObject(installationId);
        }
        return cloudDestinations;
    }

    @NonNull
    private ParseObject getDefaultDestinationsObject(String installationId) {
        ParseObject cloudDestinations;
        cloudDestinations = new ParseObject(DESTINATIONS_TYPE);
        cloudDestinations.put(INSTALLATION_FIELD, installationId);
        cloudDestinations.put(DESTINATIONS_TYPE, new LinkedList<>());
        try {
            cloudDestinations.save();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return cloudDestinations;
    }

    public void setStatusForSelfAndNotifyParents(int status){
        cloudPosition.put(STATUS_FIELD, status);
        cloudPosition.saveInBackground();
        if(status != TravelingData.INACTIVE)
            BussRelationMessenger.getInstance().sendStatusUpdateNotification(status);
        BussRelationMessenger.getInstance().notifyPositionUpdate();
    }

    public void addDestinationToChild(BussDestination destination, String childId){
        try {
            ParseObject cloudDestinations = getOrMakeDestinationsObjectForID(childId);
            cloudDestinations.getList(DESTINATIONS_TYPE).add(destination.getAsJSONObject());
            cloudDestinations.saveInBackground();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

            List<BussDestination> destinationList = BussDestination.getAsDestinationList(destinationsObject.<HashMap>getList(DESTINATIONS_TYPE));
            for (BussDestination destination :
                    destinationList) {
                if (destination.getName().equals(destinationName)) {
                    destinationList.remove(destination);
                    break;
                }
            }
            destinationsObject.put(DESTINATIONS_TYPE, BussDestination.getAsJSONList(destinationList));
            destinationsObject.saveInBackground();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<BussDestination> getDestinationsForChild(String id){
        ArrayList<BussDestination> list = new ArrayList<>();
        ParseObject destinationsObject = getOrMakeDestinationsObjectForID(id);
        Log.d(TAG, "getDestinationsForChild: Got destination object");
        List<HashMap> parseList = destinationsObject.getList(DESTINATIONS_TYPE);
        Log.d(TAG, "addDestinationToChild: got destinations: "+parseList);
        list.addAll(BussDestination.getAsDestinationList(parseList));
        return list;
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
        cloudPosition.put(BussData.STATUS_FIELD,status);
        cloudPosition.put("destination",location.getDestination());
        cloudPosition.saveInBackground();
    }

    public ChildLocationAndStatus getChildLocationAndStatusForId(String id){
        ParseObject positionObject = getOrMakePositionObjectForId(id);
        ParseGeoPoint geoPoint = positionObject.getParseGeoPoint("position");
        Location location = new Location("ParseCloud");
        location.setLatitude(geoPoint.getLatitude());
        location.setLongitude(geoPoint.getLongitude());
        int status = positionObject.getInt(BussData.STATUS_FIELD);
        String destination = positionObject.getString("destination");
        return new ChildLocationAndStatus(location,status,destination);
    }

    public String getNameFromId(String id){
        ParseQuery query = ParseQuery.getQuery(NAME_TYPE);
        try {
            ParseObject cloudName = query.whereEqualTo(INSTALLATION_FIELD,id).getFirst();
            return cloudName.getString(NAME_FIELD);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setNameForId(String name, String id){
        ParseObject nameObject = getOrMakeNameObjectForId(id);
        nameObject.put(NAME_FIELD,name);
        nameObject.saveInBackground();
    }

    public String getOwnName(){
        return cloudName.getString(NAME_FIELD);
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
