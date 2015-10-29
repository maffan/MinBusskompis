package se.grupp4.minbusskompis.backgroundtasks;


import android.location.Location;

/*
    ChildLocationAndStatus
    Data object used to pass along trip mode and location
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
