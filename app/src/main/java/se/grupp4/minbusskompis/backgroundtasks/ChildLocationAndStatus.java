package se.grupp4.minbusskompis.backgroundtasks;


import android.location.Location;

/**
 * Created by Tobias on 2015-10-08.
 */
public class ChildLocationAndStatus extends Location {
    private int mode;
    private String destination = "";

    public ChildLocationAndStatus(Location l, int tripStatus, String destination) {
    	super(l);
        this.mode = tripStatus;
        this.destination = destination;
    }

    public int getTripStatus(){
        return this.mode;
    }
    public String getDestination(){ return this.destination; }

}
