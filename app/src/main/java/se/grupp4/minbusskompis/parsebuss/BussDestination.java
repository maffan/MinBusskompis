package se.grupp4.minbusskompis.parsebuss;

import com.parse.ParseGeoPoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Marcus on 10/11/2015.
 */
public class BussDestination {
    private ParseGeoPoint destination;
    private String name;

    public BussDestination(ParseGeoPoint destination, String name) {
        this.destination = destination;
        this.name = name;
    }

    public BussDestination(){

    }

    public BussDestination(JSONObject destination){
        try{
            this.destination = (ParseGeoPoint) destination.get("destination");
            this.name = destination.getString("name");
        } catch (Exception e) {
            throw new IllegalArgumentException("Not a valid BussDestination object");
        }
    }

    public BussDestination(HashMap destination){
        this.destination = (ParseGeoPoint) destination.get("destination");
        this.name = (String) destination.get("name");
    }

    public static List<BussDestination> getAsDestinationList(List<HashMap> list){
        List<BussDestination> destinationList = new LinkedList<>();
        for (HashMap object :
                list) {
            destinationList.add(new BussDestination(object));
        }
        return destinationList;
    }

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
        object.put("destination",destination);
        object.put("name",name);
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
