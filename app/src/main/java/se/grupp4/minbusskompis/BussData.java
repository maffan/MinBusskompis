package se.grupp4.minbusskompis;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Marcus on 9/29/2015.
 */
public class BussData {
    private static BussData bussData = new BussData();

    private LinkedList people;

    public enum Type{PARENT,CHILD}

    public static BussData getInstance() {
        return bussData;
    }

    public BussData(){
        people = new LinkedList();
    }

    public void fetchPeopleAsync(Type type){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(type.name());
        query.whereEqualTo("installationId", ParseInstallation.getCurrentInstallation().getInstallationId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                people.clear();
                people.addAll(list);
            }
        });
    }

    public void addPersonAsync(Type type){
        ParseObject person = new ParseObject(type.name());
        person.put("installationId",ParseInstallation.getCurrentInstallation().getInstallationId());

    }

}
