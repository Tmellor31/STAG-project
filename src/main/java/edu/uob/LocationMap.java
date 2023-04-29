package edu.uob;

import java.util.LinkedHashMap;

public class LocationMap {
    public static void main(String[] args) {
        LinkedHashMap<String, Location> locationMap = new LinkedHashMap<>();

        Location location1 = new Location("Forest", "A dense forest with tall trees.");
        Location location2 = new Location("Cave", "A dark and damp cave.");
        Location location3 = new Location("Beach", "A sunny beach with white sand.");

        /* add paths between locations
        location1.addPath(location2);
        location1.addPath(location3);
        location2.addPath(location1);
        location2.addPath(location3);
        location3.addPath(location1);
        location3.addPath(location2);*/

        // add locations to the map
        locationMap.put(location1.getName(), location1);
        locationMap.put(location2.getName(), location2);
        locationMap.put(location3.getName(), location3);

        // get a location from the map and print its description
        Location forest = locationMap.get("Forest");
        System.out.println(forest.getDescription());
    }
}
