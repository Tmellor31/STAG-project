package edu.uob;

import java.util.ArrayList;

public class BasicCommands {
    private ServerState serverState;
    private ArrayList<String> tokenizedCommand;

    // Constructor
    public BasicCommands(ServerState serverState) {
        this.serverState = serverState;
    }


    public String performBasicCommand(GameServer.CommandType command, ArrayList <String> tokenizedCommand) {
        switch (command) {
            case INVENTORY:
                ArrayList <String> result = checkInventory();
                String output = String.join(", ", result) + "\n";
                return output;
            case GET:
                //performGetAction(tokenizedCommand);
                break;
            case DROP:
                performDropAction();
                break;
            case LOOK:
                output = performLookAction();
                return output;
            case GOTO:
                //performGotoAction();
                break;
            default:
                // Error performing action
                System.out.println("Provided action (" + command + ") did not match command types");
                break;
        }
        return "";
    }

    private ArrayList<String> checkInventory() {
        System.out.println("checkInventory() called");
        return serverState.getInventoryList();
    }

    private void performGetAction() {
        System.out.println("performGetAction() called");

    }

    private void performDropAction() {
        System.out.println("performDropAction() called");
    }

    private String performLookAction() {
        System.out.println("performLookAction() called");
        String currentRoom = serverState.getCurrentLocation().getName();
        String roomDescription = serverState.getCurrentLocation().getDescription();
        String artefactsDescriptions = serverState.getCurrentLocation().getArtefactDescriptions();
        String furnitureDescriptions = serverState.getCurrentLocation().getFurnitureDescriptions();
        String availablePaths = serverState.getCurrentLocation().getAvailablePaths();
        String output = currentRoom + "\n" + roomDescription + "\n" +
                artefactsDescriptions +  "\n" + furnitureDescriptions + "\n" +  availablePaths;
        return output;
    }

    /*private void performGotoAction(ArrayList<String> tokenizedCommand) {
        if (tokenizedCommand.size() < 2) {
            // not enough arguments
            return;
        }
        String destination = tokenizedCommand.get(1);
        Location currentLocation = serverState.
        ArrayList <Path> paths = currentLocation.getPaths();
        if (paths.containsKey(destination)) {
            // update current location to the destination
            serverState.setLocation(paths.get(destination));
            // perform any actions needed for entering the new location
            performLookAction();
        } else {
            // invalid destination
            System.out.println("You can't go there.");
        }
    }*/


}

