package se.grupp4.minbusskompis.parsebuss;

import com.parse.ParseGeoPoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This class contains all information regarding a specific destination
 */
public class BussDestination {
    private static final String DESTINATION_FIELD = "destination";
    private static final String NAME_FIELD = "name";
    private ParseGeoPoint destination;
    private String name;

    public BussDestination(ParseGeoPoint destination, String name) {
        this.destination = destination;
        this.name = name;
    }

    public BussDestination(JSONObject destination){
        try{
            this.destination = (ParseGeoPoint) destination.get(DESTINATION_FIELD);
            this.name = destination.getString(NAME_FIELD);
        } catch (Exception e) {
            throw new IllegalArgumentException("Not a valid BussDestination object");
        }
    }

    public BussDestination(HashMap destination){
        this.destination = (ParseGeoPoint) destination.get(DESTINATION_FIELD);
        this.name = (String) destination.get(NAME_FIELD);
    }

    /**
     * Takes a list of Destinations in the form of hashmaps (as they are returned from Parse) and
     * returns it as a list of Destination objects
     * @param list
     * @return
     */
    public static List<BussDestination> getAsDestinationList(List<HashMap> list){
        List<BussDestination> destinationList = new LinkedList<>();
        for (HashMap object :
                list) {
            destinationList.add(new BussDestination(object));
        }
        return destinationList;
    }

    /**
     * Takes a list of BussDestination objects and turns it into a list of JSON representations (for
     * saving to Parse) of the same objects.
     * @param list
     * @return
     * @throws JSONException
     */
    public static List<JSONObject> getAsJSONList(List<BussDestination> list) throws JSONException {
        List<JSONObject> jsonObjects = new LinkedList<>();
        for (BussDestination destination :
                list) {
            jsonObjects.add(destination.getAsJSONObject());
        }
        return jsonObjects;
    }

    public JSONObject getAsJSONObject() throws JSONException {
        JSONObject object = new JSONObject();
        object.put(DESTINATION_FIELD,destination);
        object.put(NAME_FIELD,name);
        return object;
    }

    public ParseGeoPoint getDestination() {
        return destination;
    }

    public void setDestination(ParseGeoPoint destination) {
        this.destination = destination;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BussDestination{" +
                "destination=" + destination +
                ", name='" + name + '\'' +
                '}';
    }
}
