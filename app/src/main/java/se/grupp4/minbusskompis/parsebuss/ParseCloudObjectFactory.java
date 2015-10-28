package se.grupp4.minbusskompis.parsebuss;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.LinkedList;

public class ParseCloudObjectFactory {
    private static final String PARENTS_FIELD = "parents";
    private static final String CHILDREN_FIELD = "children";
    private static final String DESTINATIONS_TYPE = "Destinations";
    private static final String INSTALLATION_FIELD = "installationId";
    private static final String POSITION_FIELD = "position";
    private static final String STATUS_FIELD = "status";
    private static final String NAME_FIELD = "name";
    private static final String POSITION_TYPE = "Position";
    private static final String NAME_TYPE = "Name";
    private static final String RELATIONSHIPS_TYPE = "Relationships";
    private static ParseCloudObjectCache cache = new ParseCloudObjectCache();

    private static ParseQuery getTypeQueryForId(String type, String id) {
        ParseQuery query = ParseQuery.getQuery(type);
        query.whereEqualTo(INSTALLATION_FIELD, id);
        return query;
    }

    public static ParseObject createPositionObjectForId(String id) {
        if (!cache.contains(POSITION_TYPE,id)) {
            ParseQuery query = getTypeQueryForId(POSITION_TYPE, id);
            ParseObject positionObject;
            try {
                positionObject = query.getFirst();
            } catch (ParseException e) {
                //Could not get object from parse
                positionObject = createDefaultPositionObjectForId(id);
            }
            cache.addObject(POSITION_TYPE,id,positionObject);
            return positionObject;
        } else {
            return cache.getObect(POSITION_TYPE,id);
        }
    }

    public static ParseObject createPositionObjectForSelf() {
        String id = ParseInstallation.getCurrentInstallation().getInstallationId();
        ParseQuery query = getTypeQueryForId(POSITION_TYPE, id);
        ParseObject positionObject;
        try {
            positionObject = query.getFirst();
        } catch (ParseException e) {
            //Could not get object from parse
            positionObject = createDefaultPositionObjectForId(id);
        }
        return positionObject;
    }

    private static ParseObject createDefaultPositionObjectForId(String id) {
        ParseObject positionObject;
        positionObject = new ParseObject(POSITION_TYPE);
        positionObject.put(INSTALLATION_FIELD,id);
        positionObject.put(POSITION_FIELD,new ParseGeoPoint(0,0));
        positionObject.put(STATUS_FIELD,0);
        positionObject.put(DESTINATIONS_TYPE, "");
        try {
            positionObject.save();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return positionObject;
    }

    public static ParseObject createNameObjectForId(String id){
        if (!cache.contains(NAME_TYPE,id)) {
            ParseQuery query = getTypeQueryForId(NAME_TYPE,id);
            ParseObject cloudName;
            try {
                cloudName = query.getFirst();
            } catch (ParseException e) {
                cloudName = createDefaultNameObjectForId(id);
            }
            cache.addObject(NAME_TYPE,id,cloudName);
            return cloudName;
        } else {
            return cache.getObect(NAME_TYPE,id);
        }
    }

    public static ParseObject createNameObjectForSelf(){
        String id = ParseInstallation.getCurrentInstallation().getInstallationId();
        ParseQuery query = getTypeQueryForId(NAME_TYPE,id);
        ParseObject cloudName;
        try {
            cloudName = query.getFirst();
        } catch (ParseException e) {
            cloudName = createDefaultNameObjectForId(id);
        }
        return cloudName;
    }

    private static ParseObject createDefaultNameObjectForId(String id) {
        ParseObject cloudName;
        cloudName = new ParseObject(NAME_TYPE);
        cloudName.put(INSTALLATION_FIELD,id);
        cloudName.put(NAME_FIELD,"default");
        try {
            cloudName.save();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return cloudName;
    }

    public static ParseObject createRelationshipsObjectForId(String id) {
        if (!cache.contains(RELATIONSHIPS_TYPE,id)) {
            ParseQuery query = getTypeQueryForId(RELATIONSHIPS_TYPE,id);
            ParseObject cloudRelationships;
            try {
                cloudRelationships = query.getFirst();
            } catch (ParseException e) {
                cloudRelationships = createDefaultRelationshipsObject(id);
            }
            cache.addObject(RELATIONSHIPS_TYPE,id,cloudRelationships);
            return cloudRelationships;
        } else {
            return cache.getObect(RELATIONSHIPS_TYPE,id);
        }
    }

    public static ParseObject createRelationshipsObjectForSelf() {
        String id = ParseInstallation.getCurrentInstallation().getInstallationId();
        ParseQuery query = getTypeQueryForId(RELATIONSHIPS_TYPE,id);
        ParseObject cloudRelationships;
        try {
            cloudRelationships = query.getFirst();
        } catch (ParseException e) {
            cloudRelationships = createDefaultRelationshipsObject(id);
        }
        return cloudRelationships;
    }

    private static ParseObject createDefaultRelationshipsObject(String id) {
        ParseObject cloudRelationships;
        cloudRelationships = new ParseObject(RELATIONSHIPS_TYPE);
        cloudRelationships.put(INSTALLATION_FIELD,id);
        cloudRelationships.put(PARENTS_FIELD,new LinkedList<>());
        cloudRelationships.put(CHILDREN_FIELD,new LinkedList<>());
        try {
            cloudRelationships.save();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return cloudRelationships;
    }

    public static ParseObject createDestinationsObjectForID(String id) {
        if (!cache.contains(DESTINATIONS_TYPE,id)) {
            ParseQuery query = getTypeQueryForId(DESTINATIONS_TYPE,id);
            ParseObject cloudDestinations;
            try {
                cloudDestinations = query.getFirst();
            } catch (ParseException e) {
                cloudDestinations = createDefaultDestinationsObject(id);
            }
            cache.addObject(DESTINATIONS_TYPE,id,cloudDestinations);
            return cloudDestinations;
        } else {
            return cache.getObect(DESTINATIONS_TYPE,id);
        }
    }
    public static ParseObject createDestinationsObjectForSelf() {
        String id = ParseInstallation.getCurrentInstallation().getInstallationId();
        ParseQuery query = getTypeQueryForId(DESTINATIONS_TYPE,id);
        ParseObject cloudDestinations;
        try {
            cloudDestinations = query.getFirst();
        } catch (ParseException e) {
            cloudDestinations = createDefaultDestinationsObject(id);
        }
        return cloudDestinations;
    }

    private static ParseObject createDefaultDestinationsObject(String installationId) {
        ParseObject cloudDestinations;
        cloudDestinations = new ParseObject(DESTINATIONS_TYPE);
        cloudDestinations.put(INSTALLATION_FIELD, installationId);
        cloudDestinations.put(DESTINATIONS_TYPE, new LinkedList<>());
        try {
            cloudDestinations.save();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return cloudDestinations;
    }
}
