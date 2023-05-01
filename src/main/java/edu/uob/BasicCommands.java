package edu.uob;

import java.util.ArrayList;

public class BasicCommands {
    private ServerState serverState;
    private ArrayList<String> tokenizedCommand;

    // Constructor
    public BasicCommands(ServerState serverState) {
        this.serverState = serverState;
    }


    public String performBasicCommand(GameServer.CommandType command, ArrayList<String> tokenizedCommand) {
        switch (command) {
            case INVENTORY:
                ArrayList<String> result = checkInventory();
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
                output = performGotoAction(tokenizedCommand);
                return output;
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
                artefactsDescriptions + "\n" + furnitureDescriptions + "\n" + availablePaths;
        return output;
    }

    private String performGotoAction(ArrayList<String> tokenizedCommand) {
        int gotoIndex = tokenizedCommand.indexOf("goto");
        String output = ("No location found after goto command");
        if (gotoIndex != -1 && gotoIndex + 1 < tokenizedCommand.size()) {
            //If statement checks for goto command and a location following it
            String location = tokenizedCommand.get(gotoIndex + 1);
            Location newLocation = serverState.getLocation(location);
            if (!serverState.getCurrentLocation().hasPathTo(newLocation)) {
                output = ("The location you are in doesn't have a path to the targeted location (" + location + ")");
                return output;
            }
            serverState.setCurrentLocation(newLocation);
            output = ("You have moved to " + location + ".");
            return output;
        }
        return output;
    }
}


