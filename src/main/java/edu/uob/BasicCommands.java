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
                output = performGetAction(tokenizedCommand);
                return output;
            case DROP:
                output = performDropAction(tokenizedCommand);
                return output;
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
        return serverState.getInventoryList();
    }

    private String performGetAction(ArrayList<String> tokenizedCommand) {
            int getIndex = tokenizedCommand.indexOf("get");
            String output = ("No artefact found after get command");
            if (getIndex != -1 && getIndex + 1 < tokenizedCommand.size()) {
                //If statement checks for get command and an artefact following it
                String artefactName = tokenizedCommand.get(getIndex + 1);
                Artefact artefact = serverState.getCurrentLocation().getArtefactByName(artefactName);
                if (artefact == null) {//if artefact is not found
                    output = ("The location you are in doesn't contain the artefact '" + artefactName + "'");
                    return output;
                }
                serverState.addToInventory(artefact);
                serverState.getCurrentLocation().removeArtefact(artefact);
                output = ("You added the artefact '" + artefactName + "' to your inventory!");
                return output;
            }
            return output;
        }

    private String performDropAction(ArrayList<String> tokenizedCommand) {
        int dropIndex = tokenizedCommand.indexOf("drop");
        String output = ("No artefact found after drop command");
        if (dropIndex != -1 && dropIndex + 1 < tokenizedCommand.size()) {
            //If statement checks for drop command and an artefact following it
            String artefactName = tokenizedCommand.get(dropIndex + 1);
            String artefactDescription = serverState.getArtefactDescription(artefactName);

            if (artefactDescription == null) {//if artefact is not found in inventory
                output = ("Your inventory doesn't contain the artefact '" + artefactName + "'");
                return output;
            }
            Artefact artefact = new Artefact(artefactName, artefactDescription,serverState.getCurrentLocation());
            serverState.removeFromInventory(artefactName);
            serverState.getCurrentLocation().addArtefact(artefact);
            output = ("You placed the artefact '" + artefactName + "' in the current location!");
            return output;
        }
        return output;
    }

    private String performLookAction() {
        String currentRoom = serverState.getCurrentLocation().getName();
        String roomDescription = serverState.getCurrentLocation().getDescription();
        String artefactsDescriptions = serverState.getCurrentLocation().getArtefactDescriptions();
        String furnitureDescriptions = serverState.getCurrentLocation().getFurnitureDescriptions();
        String characterDescriptions = serverState.getCurrentLocation().getCharacterDescriptions();
        String availablePaths = serverState.getCurrentLocation().getAvailablePaths();
        String output = currentRoom + "\n" + roomDescription + "\n" +
                artefactsDescriptions + "\n" + furnitureDescriptions + "\n" + characterDescriptions +
                "\n" + availablePaths;
        return output;
    }

    private String performGotoAction(ArrayList<String> tokenizedCommand) {
        int gotoIndex = tokenizedCommand.indexOf("goto");
        String output = "No location found after 'goto' in this command: " + tokenizedCommand;
        int numValidLocations = 0;
        String validLocation = null;
        if (gotoIndex != -1 && gotoIndex + 1 < tokenizedCommand.size()) {
            // Check if any subsequent tokens represent a valid location
            for (int i = gotoIndex + 1; i < tokenizedCommand.size(); i++) {
                String location = tokenizedCommand.get(i);
                if (serverState.getLocation(location) != null) {
                    // Found a valid location
                    numValidLocations++;
                    validLocation = location;
                }
            }
            if (numValidLocations == 1) {
                // Only one valid location found, move there and return success message
                Location newLocation = serverState.getLocation(validLocation);
                if (!serverState.getCurrentLocation().hasPathTo(newLocation)) {
                    output = "The location you are in doesn't have a path to the targeted location (" + validLocation + ")";
                } else {
                    serverState.setCurrentLocation(newLocation);
                    output = "You have moved to " + validLocation + ".";
                }
            } else if (numValidLocations > 1) {
                // Multiple valid locations found, return error message
                output = "Ambiguous command, please only enter one location";
            }
        }
        return output;
    }

}


