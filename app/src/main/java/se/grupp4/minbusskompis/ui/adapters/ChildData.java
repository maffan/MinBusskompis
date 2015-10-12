package se.grupp4.minbusskompis.ui.adapters;

/**
 * Created by Tobias on 2015-10-12.
 */
public class ChildData {
    private String name;
    private boolean active;
    private String id;

    public ChildData(String name, boolean active, String id){
        this.name = name;
        this.active = active;
        this.id = id;
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
}
