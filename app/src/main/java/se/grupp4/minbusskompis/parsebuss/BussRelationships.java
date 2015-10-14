package se.grupp4.minbusskompis.parsebuss;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import se.grupp4.minbusskompis.backgroundtasks.ChildLocationAndStatus;
import se.grupp4.minbusskompis.ui.adapters.ChildData;

/**
 * Created by Marcus on 10/1/2015.
 */
public class BussRelationships  {
    private static final String TAG = "BUSS_RELATIONSHIPS";
    private LinkedList<String> relations;

    public BussRelationships(List relations) {
        this.relations = new LinkedList<>(relations);
    }

    public List<String> getRelationships(){
        return new LinkedList<>(relations);
    }

    public ArrayList<ChildData> getAsChildDataList(){
        Log.d(TAG, "getAsChildDataList: Entered");
        ArrayList<ChildData> list = new ArrayList<>();
        for (String id :
                relations) {
            String name = BussData.getInstance().getNameFromId(id);
            int status = BussData.getInstance().getChildLocationAndStatusForId(id).getTripStatus();
            list.add(new ChildData(name,status != 0,id,status));
            Log.d(TAG, "getAsChildDataList: added child with name: "+name+" and status: "+status);
        }
        return list;
    }

    @Override
    public String toString() {
        return "BussRelationships{" +
                "relations=" + relations +
                '}';
    }

}
