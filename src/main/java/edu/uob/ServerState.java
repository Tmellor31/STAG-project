package edu.uob;


import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ServerState {
    private ArrayList<GameEntity> inventory = new ArrayList<>();

    private LinkedHashMap<String, Location> locationMap = new LinkedHashMap<>();


    public ArrayList <String> getInventoryList () {
        ArrayList<String> itemNames = new ArrayList<String>();
        for (GameEntity item: inventory){
            itemNames.add(item.getName());
        }
        return itemNames;
    }
    public void addLocation(Location location) {
        locationMap.put(location.getName(), location);
    }

    public String getFirstLocationName() throws Exception {
        for (Location location : locationMap.values()) {
            if (location.getIsStart()) {
                return location.getName();
            }
        }
        throw new Exception("No start location found");
    }

}

