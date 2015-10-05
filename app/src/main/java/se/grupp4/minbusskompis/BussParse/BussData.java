package se.grupp4.minbusskompis.BussParse;

import android.support.annotation.NonNull;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Marcus on 9/29/2015.
 */
public class BussData {
    public static final String TAG = "BUSSDATA";
    private static BussData bussData = new BussData();

    private LinkedList parents;
    private LinkedList children;
    private ParseObject relationships;


    public static BussData getInstance() {
        return bussData;
    }

    private BussData(){
        parents = new LinkedList();
        children = new LinkedList();
    }

    public void fetchRelationships(){
        if (relationships == null) {
            fetchRelationshipsObject();
        } else {
            updateFromRelationshipsObject();
        }
    }

    private void updateFromRelationshipsObject() {
        relationships.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(noError(e)){
                    extractRelationsFromRelationships(parseObject);
                }
            }
        });
    }

    private void clearAndSetParents(ParseObject parseObject) {
        parents.clear();
        parents.addAll(parseObject.getList("parents"));
    }

    private void clearAndSetChildren(ParseObject parseObject) {
        children.clear();
        children.addAll(parseObject.getList("children"));
    }

    private void fetchRelationshipsObject() {
        ParseQuery<ParseObject> query = getParseQuery();
        fetchQuery(query);
    }

    private ParseQuery<ParseObject> getParseQuery() {
        return ParseQuery.getQuery("Relationships").whereEqualTo("installationId", getInstallationId()).setLimit(1);
    }

    private String getInstallationId() {
        return ParseInstallation.getCurrentInstallation().getInstallationId();
    }

    private void fetchQuery(ParseQuery<ParseObject> query) {
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> result, ParseException e) {
                if (noError(e))
                    handleResult(result);
            }


        });
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
        ParseObject newRelationships = getEmptyRelationshipsWithId();
        newRelationships.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(noError(e))
                    fetchRelationships();
            }
        });
    }

    @NonNull
    private ParseObject getEmptyRelationshipsWithId() {
        ParseObject newRelationships = new ParseObject("Relationships");
        newRelationships.put("installationId",getInstallationId());
        newRelationships.put("parents", new JSONArray());
        newRelationships.put("children", new JSONArray());
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
        children.addAll(relationships.getList("children"));
    }

    private void extractParentsFromRelationships(ParseObject relationships) {
        parents.addAll(relationships.getList("parents"));
    }

    public BussRelationships getParents(){
        return new BussRelationships(parents);
    }

    public BussRelationships getChildren(){
        return new BussRelationships(children);
    }

}
