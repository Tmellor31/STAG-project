package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;


/**
 * This class implements the STAG server.
 */
public final class GameServer {
    ServerState serverState = new ServerState();

    public enum CommandType {
        notBASIC,
        INVENTORY,
        GET,
        DROP,
        LOOK,
        GOTO;
    }

    private static final char END_OF_TRANSMISSION = 4;

    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "custom-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "custom-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
     * KEEP this signature (i.e. {@code edu.uob.GameServer(File, File)}) otherwise we won't be able to mark
     * your submission correctly.
     *
     * <p>You MUST use the supplied {@code entitiesFile} and {@code actionsFile}
     *
     * @param entitiesFile The game configuration file containing all game entities to use in your game
     * @param actionsFile  The game configuration file containing all game actions to use in your game
     */
    public GameServer(File entitiesFile, File actionsFile) {
        DotFileLoader dotFileLoader = new DotFileLoader(this.serverState);
        dotFileLoader.loadDotFile(entitiesFile.getAbsolutePath());
        XMLFileLoader xmlFileLoader = new XMLFileLoader(this.serverState);
        xmlFileLoader.loadXMLFile(actionsFile.getAbsolutePath());
    }

    /**
     * KEEP this signature (i.e. {@code edu.uob.GameServer.handleCommand(String)}) otherwise we won't be
     * able to mark your submission correctly.
     *
     * <p>This method handles all incoming game commands and carries out the corresponding actions.
     */
    public String handleCommand(String command) {
        ArrayList<String> tokenizedCommand = tokenizeInputString(command);
        CommandType commandType = isBasicCommand(tokenizedCommand);
        Set<GameAction> gameActions = getLoadedAction(tokenizedCommand);
        String output = "Not a valid command, may have entered too many built-in commands or not specified subjects for basic ones";
        if (commandType != CommandType.notBASIC && gameActions != null) {
            output = ("Composite commands are not supported, please enter one action at a time");
            return output;
        }
        if (commandType != CommandType.notBASIC)
        //if action is basic
        {
            BasicCommands basicCommands = new BasicCommands(this.serverState);// Pass the GameServer instance to BasicCommands constructor
            output = basicCommands.performBasicCommand(commandType, tokenizedCommand); // Call the performBasicCommand method on the instance
            return output;
        }
        //Start of non-built in commands
        if (gameActions == null) {
            output = ("Not a valid command");
            return output;
        }
        GameAction action;
        if (gameActions.size() > 1) {
            action = getActionFromSubjects(gameActions, tokenizedCommand);
            if (action == null) {
                return "This is an ambigous command";
            }
        }
        else {
            action = gameActions.iterator().next(); //Only one action
        }
        HashSet<String> subjectsInCommand = checkSubjects(action.getSubjects(), tokenizedCommand);
        if (subjectsInCommand == null) { //All commands must contain atleast one subject
            output = ("Commands must contain at least one subject");
            return output;
        }
        if (subjectsInCommand.size() < countEntities(tokenizedCommand)) {
            output = ("Extraneous entities found within this command: ") + command;
            System.out.println(subjectsInCommand.size());
            System.out.println(countEntities(tokenizedCommand));
            return output;
        }

        HashSet<String> produced = action.getProduced();
        HashSet<String> consumed = action.getConsumed();
        String narration = action.getNarration();
        output = performAction(action, subjectsInCommand, produced, consumed, narration);
        System.out.println(output);
        return output;
    }

    private GameAction getActionFromSubjects(Set<GameAction> gameActions, ArrayList<String> tokenizedCommand) {
        ArrayList<ActionsWithMatchingSubjects> actionsWithMatchingSubjectsArrayList = new ArrayList<>();
        for (GameAction action : gameActions) {
            HashSet<String> subjectsInCommand = checkSubjects(action.getSubjects(), tokenizedCommand);
            actionsWithMatchingSubjectsArrayList.add(new ActionsWithMatchingSubjects(action, subjectsInCommand));
        }
        Collections.sort(actionsWithMatchingSubjectsArrayList, (first, second) -> {
            return second.matchingSubjects.size() - first.matchingSubjects.size();
        });
        if (actionsWithMatchingSubjectsArrayList.get(0).matchingSubjects.size() ==
                actionsWithMatchingSubjectsArrayList.get(1).matchingSubjects.size()) {
            return null; //ambigous statement
        } else {
            return actionsWithMatchingSubjectsArrayList.get(0).action;
        }
    }


    private ArrayList<String> tokenizeInputString(String command) {
        String[] tokens = command.split(" ");
        ArrayList<String> tokenList = new ArrayList<>();
        for (String token : tokens) {
            tokenList.add(token.toLowerCase()); //Ensures case insensitivity
        }
        return tokenList;
    }

    public int countEntities(ArrayList<String> tokens) {
        int nonLocationTotal = serverState.countMatchingEntities(tokens);
        int locationTotal = serverState.countMatchingLocations(tokens);
        return nonLocationTotal + locationTotal;
    }

    private Set<GameAction> getLoadedAction(ArrayList<String> tokenizedCommand) {
        //Function checks if there is more than one action referenced, multiple references to one action are fine.
        //Set<GameAction> validActions = new HashSet<>(); //Set helps to prevent duplication
        HashMap<String, Set<GameAction>> validActions = new HashMap<>();

        for (String token : tokenizedCommand) {
            Set<GameAction> actionSet = serverState.getActions().get(token);
            if (actionSet != null) {
                validActions.put(token, actionSet);
            }
        }
        if (validActions.size() == 0) {
            return null; // No actions present
        }

        if (validActions.size() > 1) {
            return null; // Multiple actions present
        }

        return validActions.values().iterator().next();
    }


    private String performAction(GameAction gameAction, HashSet<String> subjects, HashSet<String> produced, HashSet<String> consumed, String narration) {
        String output;
        System.out.println(subjects);
        if (!serverState.areAvailable(subjects)) {
            output = ("Provided subjects are unavailable - check your inventory and location");
            return output;
        }
        System.out.println("produced here" + produced);
        for (String item : produced) {
            String trimmedItem = item.trim();
            Location location = serverState.getLocation(trimmedItem);
            if (location != null) {
                serverState.produceLocation(serverState.getCurrentLocation(), location);
            } else {
                GameEntity entity = serverState.getEntityByName(trimmedItem);
                System.out.println("ITEM IS " + trimmedItem);
                serverState.moveEntityToCurrentLocation(entity);
            }
        }
        System.out.println("consumed" + consumed.size());//This should be an empty hashset, but instead its a hashset of an empty string
        for (String item : consumed) {
            // Only consume the entity if the item is not empty and contains letters
            Location location = serverState.getLocation(item);
            if (location != null) {
                serverState.consumeLocation(serverState.getCurrentLocation(), location);
            } else {
                GameEntity entity = serverState.getEntityByName(item);
                serverState.consumeGameEntity(entity);
            }
        }
        output = narration;
        return output;
    }


    private HashSet<String> checkSubjects(HashSet<String> subjects, ArrayList<String> tokenizedCommand) {
        if (subjects == null || subjects.isEmpty()) {
            return null; // No subjects to check against
        }

        HashSet<String> matchingSubjects = new HashSet<>();
        for (String token : tokenizedCommand) {
            if (subjects.contains(token)) {
                matchingSubjects.add(token);
            }
        }
        return matchingSubjects;
    }

    private CommandType isBasicCommand(ArrayList<String> commandList) {
        int count = 0; //Used to keep track of how many built-in commands there are - if more than one is found then not a valid command
        CommandType result = CommandType.notBASIC;
        for (String command : commandList) {
            String lowercaseCommand = command.toLowerCase();
            if (lowercaseCommand.equals("inventory") || lowercaseCommand.equals("inv")) {
                result = handleBasicCommand(CommandType.INVENTORY, count, result);
                count++;
            } else if (lowercaseCommand.equals("get")) {
                result = handleBasicCommand(CommandType.GET, count, result);
                count++;
            } else if (lowercaseCommand.equals("drop")) {
                result = handleBasicCommand(CommandType.DROP, count, result);
                count++;
            } else if (lowercaseCommand.equals("goto")) {
                result = handleBasicCommand(CommandType.GOTO, count, result);
                count++;
            } else if (lowercaseCommand.equals("look")) {
                result = handleBasicCommand(CommandType.LOOK, count, result);
                count++;
            } else {
                // Handle unknown command
            }
        }
        return result;
    }

    private CommandType handleBasicCommand(CommandType commandType, int count, CommandType result) {
        if (count == 0) {
            return commandType;
        } else {
            return CommandType.notBASIC;
        }
    }


    //  === Methods below are there to facilitate server related operations. ===

    /**
     * Starts a *blocking* socket server listening for new connections. This method blocks until the
     * current thread is interrupted.
     *
     * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
     * you want to.
     *
     * @param portNumber The port to listen on.
     * @throws IOException If any IO related operation fails.
     */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
     * Handles an incoming connection from the socket server.
     *
     * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
     * * you want to.
     *
     * @param serverSocket The client socket to read/write from.
     * @throws IOException If any IO related operation fails.
     */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
             BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if (incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}