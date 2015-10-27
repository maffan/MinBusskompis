package se.grupp4.minbusskompis.parsebuss;

import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.grupp4.minbusskompis.backgroundtasks.ChildLocationAndStatus;

public class ParseCloudManager {
    public static final int PARENT = 0;
    public static final int CHILD = 1;
    private static final String PARENTS_FIELD = "parents";
    private static final String CHILDREN_FIELD = "children";
    private static final ParseCloudManager parseCloudData = new ParseCloudManager();
    private static final String DESTINATIONS_TYPE = "Destinations";
    private static final String INSTALLATION_FIELD = "installationId";
    private static final String POSITION_FIELD = "position";
    private static final String STATUS_FIELD = "status";
    private static final String NAME_FIELD = "name";
    private static final String NAME_TYPE = "Name";
    public static final String LOCATION_PROVIDER = "ParseCloud";

    private List<String> parents;
    private List<String> children;
    private ParseObject cloudDestinations;
    private ParseObject cloudRelationships;
    private ParseObject cloudName;
    private ParseObject cloudPosition;

    /**
     * This class handles all interaction to the Parse cloud
     */
    public static ParseCloudManager getInstance() {
        return parseCloudData;
    }

    public void fetchLatestDataFromCloud(AsyncTaskCompleteCallback callback){
        new FetchDataTask(callback).execute();
    }

    private class FetchDataTask extends AsyncTask<Void, Void, Void> {
        private final AsyncTaskCompleteCallback callback;

        public FetchDataTask(AsyncTaskCompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            cloudDestinations = ParseCloudObjectFactory.createDestinationsObjectForID(ParseInstallation.getCurrentInstallation().getInstallationId());

            cloudRelationships = ParseCloudObjectFactory.createRelationshipsObjectForId(ParseInstallation.getCurrentInstallation().getInstallationId());
            parents = cloudRelationships.getList(PARENTS_FIELD);
            children = cloudRelationships.getList(CHILDREN_FIELD);

            cloudName = ParseCloudObjectFactory.createNameObjectForId(ParseInstallation.getCurrentInstallation().getInstallationId());

            cloudPosition = ParseCloudObjectFactory.createPositionObjectForId(ParseInstallation.getCurrentInstallation().getInstallationId());
            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            if(callback != null)
                callback.done();
        }
    }

    public void setStatusForSelfAndNotifyParents(int status){
        cloudPosition.put(STATUS_FIELD, status);
        cloudPosition.saveInBackground();
        BussRelationMessenger.getInstance().sendStatusUpdateNotification(status);
        BussRelationMessenger.getInstance().notifyPositionUpdate();
    }

    public void addDestinationToChild(BussDestination destination, String childId){
        try {
            tryAddDestinationToChild(destination, childId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void tryAddDestinationToChild(BussDestination destination, String childId) throws JSONException {
        ParseObject cloudDestinations = ParseCloudObjectFactory.createDestinationsObjectForID(childId);
        cloudDestinations.getList(DESTINATIONS_TYPE).add(destination.getAsJSONObject());
        cloudDestinations.saveInBackground();
    }

    public BussRelationships getParents(){
        return new BussRelationships(parents);
    }

    public BussRelationships getChildren(){
        return new BussRelationships(children);
    }

    public void removeDestinationFromChild(String destinationName, String childId){
        try {
            tryRemoveDestinationFromChild(destinationName, childId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void tryRemoveDestinationFromChild(String destinationName, String childId) throws JSONException {
        ParseObject destinationsObject = ParseCloudObjectFactory.createDestinationsObjectForID(childId);

        List<BussDestination> destinationList =
                removeFirstOccurrenceOfDestinationNameFromObject(
                        destinationName,
                        destinationsObject);

        saveNewDestinationList(destinationsObject, destinationList);
    }

    @NonNull
    private List<BussDestination> removeFirstOccurrenceOfDestinationNameFromObject(
            String destinationName, ParseObject destinationsObject) {
        List<BussDestination> destinationList =
                BussDestination.getAsDestinationList(
                        destinationsObject.<HashMap>getList(DESTINATIONS_TYPE));

        for (BussDestination destination :
                destinationList) {
            if (destination.getName().equals(destinationName)) {
                destinationList.remove(destination);
                break;
            }
        }

        return destinationList;
    }

    private void saveNewDestinationList(ParseObject destinationsObject, List<BussDestination> destinationList) throws JSONException {
        destinationsObject.put(DESTINATIONS_TYPE, BussDestination.getAsJSONList(destinationList));
        destinationsObject.saveInBackground();
    }

    public ArrayList<BussDestination> getDestinationsForChild(String id){
        ParseObject destinationsObject = ParseCloudObjectFactory.createDestinationsObjectForID(id);
        List<HashMap> parseList = destinationsObject.getList(DESTINATIONS_TYPE);
        return new ArrayList(BussDestination.getAsDestinationList(parseList));
    }

    public void addRelationshipToSelf(String id, int type){
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

    public void removeRelationshipFromSelf(String id, int type){
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

    public void updateLatestPositionAndStatusForSelf(ChildLocationAndStatus location){
        ParseGeoPoint geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        int status = location.getTripStatus();
        cloudPosition.put(POSITION_FIELD,geoPoint);
        cloudPosition.put(STATUS_FIELD,status);
        cloudPosition.put(DESTINATIONS_TYPE,location.getDestination());
        cloudPosition.saveInBackground();
    }

    public ChildLocationAndStatus getChildLocationAndStatusForId(String id){
        ParseObject positionObject = ParseCloudObjectFactory.createPositionObjectForId(id);
        Location location = getLocationFromPositionObject(positionObject);
        int status = positionObject.getInt(ParseCloudManager.STATUS_FIELD);
        String destination = positionObject.getString(DESTINATIONS_TYPE);
        return new ChildLocationAndStatus(location,status,destination);
    }

    @NonNull
    private Location getLocationFromPositionObject(ParseObject positionObject) {
        ParseGeoPoint geoPoint = positionObject.getParseGeoPoint(POSITION_FIELD);
        Location location = new Location(LOCATION_PROVIDER);
        location.setLatitude(geoPoint.getLatitude());
        location.setLongitude(geoPoint.getLongitude());
        return location;
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
        ParseObject nameObject = ParseCloudObjectFactory.createNameObjectForId(id);
        nameObject.put(NAME_FIELD,name);
        nameObject.saveInBackground();
    }

    public String getOwnName(){
        return cloudName.getString(NAME_FIELD);
    }

    public void clearParseData(){
        this.fetchLatestDataFromCloud(new AsyncTaskCompleteCallback() {
            @Override
            public void done() {
                try {
                    cloudPosition.delete();
                    cloudRelationships.delete();
                    cloudDestinations.delete();
                    cloudName.delete();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}