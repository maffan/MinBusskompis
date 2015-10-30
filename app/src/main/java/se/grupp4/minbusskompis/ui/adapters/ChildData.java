package se.grupp4.minbusskompis.ui.adapters;

/*
    ChildData
    Data class used to fetch data from parse to populate into childrenlist, used by ChildAdapter
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

    @Override
    public String toString() {
        return "ChildData{" +
                "name='" + name + '\'' +
                ", active=" + active +
                ", id='" + id + '\'' +
                ", mode=" + mode +
                '}';
    }
}
