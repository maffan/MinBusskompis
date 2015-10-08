package se.grupp4.minbusskompis.bussparse;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.LinkedList;
import java.util.List;

import se.grupp4.minbusskompis.backgroundtasks.ChildLocationAndStatus;

public class BussData {
    public static final String TAG = "BUSSDATA";
    public static final int PARENT = 0;
    public static final int CHILD = 1;
    private static final String PARENTS_FIELD = "parents";
    private static final String CHILDREN_FIELD = "children";
    private static final String RELATIONSHIPS_CLASS = "Relationships";
    private static final String INSTALLATION_ID_FIELD = "installationId";
    private static BussData bussData = new BussData();

    private LinkedList parents;
    private LinkedList children;
    private List<ParseObject> destinations;


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

    private BussData(){
        parents = new LinkedList();
        children = new LinkedList();
    }

   /* //Initerar parseobjekten, gör kopplingen mot parse.
    public void fetchData(AsyncTaskCompleteCallback callback){
        new FetchDataTask(callback).execute();
    }

    *//**
     * Created by Marcus on 9/29/2015.
     *//*
    private class FetchDataTask extends AsyncTask<Void, Void, Void> {
        private AsyncTaskCompleteCallback callback;

        public FetchDataTask(@Nullable AsyncTaskCompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //ParseInstallation already initialized, can be used to fetch data

            ParseQuery destinationQuery = ParseQuery.getQuery("Destinations");
            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            if(callback != null)
                callback.done();
        }
    }

    private void fetchObjectsAndUpdate() {
        ParseQuery<ParseObject> relationshipQuery = getRelationshipParseQuery();
        fetchQuery(relationshipQuery);
    }

    private ParseQuery<ParseObject> getRelationshipParseQuery() {
        return ParseQuery.getQuery(RELATIONSHIPS_CLASS).whereEqualTo(INSTALLATION_ID_FIELD, getInstallationId()).setLimit(1);
    }

    private void fetchQuery(ParseQuery<ParseObject> query) {

        try {
            List result = query.find();
            handleResult(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateFromObjects() {
        try {
            ParseObject parseObject = relationships.fetch();
            extractRelationsFromRelationships(parseObject);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void clearAndSetParents(ParseObject parseObject) {
        parents.clear();
        parents.addAll(parseObject.getList(PARENTS_FIELD));
    }

    private void clearAndSetChildren(ParseObject parseObject) {
        children.clear();
        children.addAll(parseObject.getList(CHILDREN_FIELD));
    }

    private String getInstallationId() {
        return ParseInstallation.getCurrentInstallation().getInstallationId();
    }

    private boolean noError(ParseException e) {
        return e == null;
    }

    private void handleResult(List<ParseObject> result) {
        if (result.isEmpty())
            handleEmptyResult();
        else
            extractDataFromResult(result);
    }


    private void handleEmptyResult() {
        try {
            ParseObject newRelationships = getEmptyRelationshipsWithId();
            newRelationships.save();
            fetchData(null);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private ParseObject getEmptyRelationshipsWithId() {
        ParseObject newRelationships = new ParseObject(RELATIONSHIPS_CLASS);
        newRelationships.put(INSTALLATION_ID_FIELD,getInstallationId());
        newRelationships.put(PARENTS_FIELD, new JSONArray());
        newRelationships.put(CHILDREN_FIELD, new JSONArray());
        return newRelationships;
    }

    private void extractDataFromResult(List<ParseObject> result) {
        getAndSetRelationshipsObject(result);
        extractRelationsFromRelationships(relationships);
        Log.d(TAG, "Parents: " + parents.toString());
        Log.d(TAG, "Children: " + children.toString());
    }

    private void getAndSetRelationshipsObject(List<ParseObject> result) {
        relationships = getRelationshipsFromResult(result);
    }

    private ParseObject getRelationshipsFromResult(List<ParseObject> result) {
        return result.get(0);
    }

    private void extractRelationsFromRelationships(ParseObject relationships) {
        extractParentsFromRelationships(relationships);
        extractChildrenFromRelationships(relationships);
    }

    private void extractChildrenFromRelationships(ParseObject relationships) {
        children.addAll(relationships.getList(CHILDREN_FIELD));
    }

    private void extractParentsFromRelationships(ParseObject relationships) {
        parents.addAll(relationships.getList(PARENTS_FIELD));
    }

    public BussRelationships getParents(){
        return new BussRelationships(parents);
    }

    public BussRelationships getChildren(){
        return new BussRelationships(children);
    }

    public void addRelationship(String id, int type){
        switch (type){
            case PARENT:
                parents.add(id);
                relationships.addAllUnique(PARENTS_FIELD, parents);
                break;
            case CHILD:
                children.add(id);
                relationships.addAllUnique(CHILDREN_FIELD,children);
                break;
        }
        relationships.saveInBackground();
    }

    public void removeRalationship(String id, int type){
        switch (type){
            case PARENT:
                parents.remove(id);
                relationships.put(PARENTS_FIELD, parents);
                break;
            case CHILD:
                children.remove(id);
                relationships.put(CHILDREN_FIELD, children);
                break;
        }
        relationships.saveInBackground();
    }

    public void updateLatestPosition(ChildLocationAndStatus location){
        ParseGeoPoint geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        int status = location.getTripStatus();
        ParseObject object = new ParseObject("Position");
        object.put("installationId", getInstallationId());
        object.put("latestPosition",geoPoint);
        object.put("tripStatus",status);
        object.saveInBackground();
    }

    private void fetchPosition(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
        query.whereEqualTo("playerName", "Dan Stemkoski");
    }*/
}
