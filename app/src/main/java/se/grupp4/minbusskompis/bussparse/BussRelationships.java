package se.grupp4.minbusskompis.bussparse;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Marcus on 10/1/2015.
 */
public class BussRelationships  {
    private LinkedList<String> relations;

    public BussRelationships(List relations) {
        this.relations = new LinkedList<>(relations);
    }

    public List<String> getRelationships(){
        return new LinkedList<>(relations);
    }
}
