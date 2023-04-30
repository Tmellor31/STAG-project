package edu.uob;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class ServerState {
    private ArrayList<GameEntity> inventory = new ArrayList<>();

    private LinkedHashMap<String, Location> locationMap = new LinkedHashMap<>();

    private HashMap<String, HashSet<GameAction>> actions = new HashMap<String, HashSet<GameAction>>();


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

    public Location getLocation(String locationName) {
        return locationMap.get(locationName);
    }

    public HashMap<String, HashSet<GameAction>> getActions() {
        return actions;
    }

    public ArrayList<GameAction> getAllGameActions() {//Useful for printing all actions for testing
        ArrayList<GameAction> allGameActions = new ArrayList<>();
        for (HashSet<GameAction> gameActions : actions.values()) {
            allGameActions.addAll(gameActions);
        }
        return allGameActions;
    }

    public ArrayList<String> getAllTriggers() {//Prints out all triggers currently in the game e.g. 'chop' 'cut' etc
        ArrayList<String> triggers = new ArrayList<>();
        for (String trigger : actions.keySet()) {
            triggers.add(trigger);
        }
        return triggers;
    }

}

