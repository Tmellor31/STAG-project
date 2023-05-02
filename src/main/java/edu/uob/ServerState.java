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


    public ArrayList<String> getAllTriggers() {//Prints out all triggers currently in the game e.g. 'chop' 'cut' etc
        ArrayList<String> triggers = new ArrayList<>();
        for (String trigger : actions.keySet()) {
            triggers.add(trigger);
        }
        return triggers;
    }

    public boolean areAvailable(HashSet<String> subjects) {
        // Check if each subject is in the inventory or in the current location
        for (String subject : subjects) {
            boolean found = false;
            for (GameEntity item : inventory) {
                if (item.getName().equalsIgnoreCase(subject)) {
                    found = true;
                    break;
                }
            }
            if (!found && currentLocation != null) {
                for (GameEntity item : currentLocation.getAllEntities()) {
                    if (item.getName().equalsIgnoreCase(subject)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return false; // Subject not found
            }
        }
        return true; // All subjects are available
    }

    public GameEntity getEntityByName(String name) {
        // Search for the entity in the inventory
        for (GameEntity item : inventory) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }

        // Search for the entity in all locations
        for (Location location : locationMap.values()) {
            for (GameEntity item : location.getAllEntities()) {
                if (item.getName().equalsIgnoreCase(name)) {
                    return item;
                }
            }
        }
        // Entity not found
        return null;
    }

    public void moveEntityToCurrentLocation(GameEntity gameEntity) {
        // Remove the entity from its current location
        if (gameEntity.getLocation() != null) {
            gameEntity.getLocation().removeEntity(gameEntity);
        }

        // Add the entity to the current location of the server state
        if (getCurrentLocation() != null) {
            getCurrentLocation().addEntity(gameEntity);
            gameEntity.setLocation(getCurrentLocation());
        }
    }

    public void consumeGameEntity(GameEntity gameEntity) {
        Location entityLocation = gameEntity.getLocation();
        entityLocation.removeEntity(gameEntity);
    }

    public int countMatchingLocations(ArrayList<String> locationNames) {
        int count = 0;
        for (String locationName : locationNames) {
            if (locationMap.containsKey(locationName)) {
                count++;
            }
        }
        return count;
    }

    public int countMatchingEntities(ArrayList<String> names) {
        int count = 0;
        for (GameEntity entity : inventory) {
            if (names.contains(entity.getName())) {
                count++;
            }
        }
        for (Location location : locationMap.values()) {
            for (GameEntity entity : location.getAllEntities()) {
                if (names.contains(entity.getName())) {
                    count++;
                }
            }
        }
        return count;
    }


}

