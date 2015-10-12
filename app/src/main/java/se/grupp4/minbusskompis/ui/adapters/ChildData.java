package se.grupp4.minbusskompis.ui.adapters;

/**
 * Created by Tobias on 2015-10-12.
 */
public class ChildData {
    private String name;
    private boolean active;
    private String id;
    private int mode;

    public ChildData(String name, boolean active, String id, int mode){
        this.name = name;
        this.active = active;
        this.id = id;
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public String getId() {
        return id;
    }

    public int getMode(){
        return mode;
    }
}
