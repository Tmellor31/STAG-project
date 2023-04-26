package edu.uob;

import java.util.ArrayList;

public class BasicCommands {
    private ServerState serverState;

    // Constructor
    public BasicCommands(ServerState serverState) {
        this.serverState = serverState;
    }


    public String performBasicCommand(GameServer.CommandType command) {
        switch (command) {
            case INVENTORY:
                ArrayList <String> result = checkInventory();
                String output = String.join(", ", result) + "\n";
                return output;
            case GET:
                performGetAction();
                break;
            case DROP:
                performDropAction();
                break;
            case LOOK:
                performLookAction();
                break;
            case GOTO:
                performGotoAction();
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
        return inventoryList();
    }

    private ArrayList <String> inventoryList () {
        ArrayList<String> itemNames = new ArrayList<String>();
       for (GameEntity item: serverState.inventory){
           itemNames.add(item.getName());
       }
       return itemNames;
    }

    private void performGetAction() {
        System.out.println("performGetAction() called");
    }

    private void performDropAction() {
        System.out.println("performDropAction() called");
    }

    private void performLookAction() {
        System.out.println("performLookAction() called");
    }

    private void performGotoAction() {
        System.out.println("performGotoAction() called");
    }

}

