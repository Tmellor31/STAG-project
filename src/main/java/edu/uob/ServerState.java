package edu.uob;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class ServerState {
    private ArrayList<GameEntity> inventory = new ArrayList<>();

    private LinkedHashMap<String, Location> locationMap = new LinkedHashMap<>();

    private HashMap<String, HashSet<GameAction>> actions = new HashMap<String, HashSet<GameAction>>();

    private Location currentLocation;


    public ArrayList <String> getInventoryList () {
        ArrayList<String> itemNames = new ArrayList<String>();
        for (GameEntity item: inventory){
            itemNames.add(item.getName());
            //itemNames.add(item.getDescription()); Dont think is needed - displays descriptions of items as well
        }
        return itemNames;
    }

    public void addToInventory(GameEntity gameEntity) {
        inventory.add(gameEntity);
    }

    public void removeFromInventory(String artefactName) {
        for (GameEntity item : inventory) {
            if (item instanceof Artefact && item.getName().equalsIgnoreCase(artefactName)) {
                inventory.remove(item);
                return;
            }
        }
        // Artefact not found in inventory
    }


    public String getArtefactDescription(String artefactName) {
        for (GameEntity item : inventory) {
            if (item instanceof Artefact && item.getName().equalsIgnoreCase(artefactName)) {
                return item.getDescription();
            }
        }
        return null; // Artefact not found in inventory
    }




    public void addLocation(Location location) {
        locationMap.put(location.getName(), location);
    }

    public Location getFirstLocation(){
        for (Location location : locationMap.values()) {
            if (location.getIsStart()) {
                return location;
            }
        }
        System.out.println("No locations found");
        return null;
    }


    public Location getLocation(String locationName) {
        return locationMap.get(locationName);
    }

    public Location getCurrentLocation(){
       return this.currentLocation;
    }

    public void setCurrentLocation(Location location){
        this.currentLocation = location;
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

