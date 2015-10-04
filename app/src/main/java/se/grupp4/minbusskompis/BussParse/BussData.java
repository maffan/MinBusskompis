package se.grupp4.minbusskompis.BussParse;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Marcus on 9/29/2015.
 */
public class BussData {
    private static BussData bussData = new BussData();

    private LinkedList parents;
    private LinkedList children;


    public static BussData getInstance() {
        return bussData;
    }

    public BussData(){
        parents = new LinkedList();
        children = new LinkedList();
    }

    public void fetchRelationships(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Relationships").whereEqualTo("installationId",ParseInstallation.getCurrentInstallation().getInstallationId()).setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null) {
                    if(list.size() == 0){
                        ParseObject newRelationship = ParseObject.create("Relationships");
                        newRelationship.put("installationId",ParseInstallation.getCurrentInstallation().getInstallationId());
                        try {
                            newRelationship.save();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    ParseObject relationships = list.get(0);
                    parents.addAll(relationships.getList("parents"));
                    children.addAll(relationships.getList("children"));
                    Log.d("BUSSDATA","Parents: "+parents.toString() );
                    Log.d("BUSSDATA","Children: "+children.toString() );
                }
                else{

                    }
            }
        });
    }

    public BussRelationships getParents(){
        return new BussRelationships(parents);
    }

    public BussRelationships getChildren(){
        return new BussRelationships(children);
    }

}
