package se.grupp4.minbusskompis.backgroundtasks;


import android.location.Location;

/**
 * Created by Tobias on 2015-10-08.
 */
public class ChildLocationAndStatus extends Location {
    private int mode;

    public ChildLocationAndStatus(Location l, int tripStatus) {
    	super(l);
        this.mode = tripStatus;
    }

    public int getTripStatus(){
        return this.mode;
    }
}
