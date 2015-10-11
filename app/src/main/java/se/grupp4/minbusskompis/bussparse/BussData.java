package se.grupp4.minbusskompis.bussparse;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

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

    private List parents;
    private List children;
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

    private BussData(){

    }

    //Initerar parseobjekten, gör kopplingen mot parse.
    public void fetchData(AsyncTaskCompleteCallback callback){
        new FetchDataTask(callback).execute();
    }

    /**
     * Created by Marcus on 9/29/2015.
     */
    private class FetchDataTask extends AsyncTask<Void, Void, Void> {
        private AsyncTaskCompleteCallback callback;

        public FetchDataTask(@Nullable AsyncTaskCompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //ParseInstallation already initialized, can be used to fetch data
            try {
                ParseInstallation.getCurrentInstallation().fetch();
                parents = ParseInstallation.getCurrentInstallation().getList("parents");
                children = ParseInstallation.getCurrentInstallation().getList("children");
                destinations = ParseInstallation.getCurrentInstallation().getList("destinations");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            if(callback != null)
                callback.done();
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

    public void addRelationship(String id, int type){
        switch (type){
            case PARENT:
                parents.add(id);
                ParseInstallation.getCurrentInstallation().addAllUnique("parents", parents);
                break;
            case CHILD:
                children.add(id);
                ParseInstallation.getCurrentInstallation().addAllUnique(CHILDREN_FIELD, children);
                break;
        }
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public void removeRalationship(String id, int type){
        switch (type){
            case PARENT:
                parents.remove(id);
                ParseInstallation.getCurrentInstallation().put(PARENTS_FIELD,parents);
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
        ParseInstallation.getCurrentInstallation().put("position",geoPoint);
        ParseInstallation.getCurrentInstallation().put("status",status);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

}
