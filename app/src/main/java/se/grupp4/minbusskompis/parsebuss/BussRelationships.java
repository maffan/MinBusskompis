package se.grupp4.minbusskompis.parsebuss;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import se.grupp4.minbusskompis.ui.adapters.ChildData;

public class BussRelationships  {
    private static final String TAG = "RELATIONSHIPS";
    private LinkedList<String> relations;

    public BussRelationships(List relations) {
        Log.d(TAG, "BussRelationships() called with: " + "relations = [" + relations + "]");
        this.relations = new LinkedList<>(relations);
    }

    public List<String> getRelationships(){
        return new LinkedList<>(relations);
    }

    public ArrayList<ChildData> getAsChildDataList(){
        ArrayList<ChildData> list = new ArrayList<>();
        for (String id :
                relations) {
            String name = ParseCloudManager.getInstance().getNameFromId(id);
            int status = ParseCloudManager.getInstance().getChildLocationAndStatusForId(id).getTripStatus();
            list.add(new ChildData(name, status != 0, id, status));
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
