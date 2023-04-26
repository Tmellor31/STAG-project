package edu.uob;

public class BasicCommands {

    // Constructor
    public BasicCommands() {
    }


    public void performBasicCommand(GameServer.CommandType command) {
        switch (command) {
            case INVENTORY:
                checkInventory();
                break;
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
    }

    private void checkInventory() {
        System.out.println("checkInventory() called");
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

