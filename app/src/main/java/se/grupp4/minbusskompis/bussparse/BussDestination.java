package se.grupp4.minbusskompis.bussparse;

import com.parse.ParseGeoPoint;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static List<BussDestination> getAsDestinationList(List<JSONObject> list){
        List<BussDestination> destinationList = new LinkedList<>();
        for (JSONObject object :
                list) {
            destinationList.add(new BussDestination(object));
        }
        return destinationList;
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
}
