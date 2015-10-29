package se.grupp4.minbusskompis.parsebuss;

import com.parse.ParseObject;

import java.util.HashMap;

public class ParseCloudObjectCache {
    private HashMap<String, HashMap<String, ParseObject>> cache;

    public ParseCloudObjectCache() {
        cache = new HashMap<>();
    }

    public void addObject(String type, String id, ParseObject object){
        if(cache.containsKey(type)){
            cache.get(type).put(id,object);
        }else{
            cache.put(type,new HashMap<String, ParseObject>());
            cache.get(type).put(id,object);
        }
    }

    public boolean contains(String type, String id){
        return cache.containsKey(type) && cache.get(type).containsKey(id);
    }

    public ParseObject getObect(String type, String id){
        return cache.get(type).get(id);
    }
}
