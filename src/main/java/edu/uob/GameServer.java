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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;


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
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
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
        CommandType action = isBasicCommand(tokenizedCommand);
        String output = "default";
        if (action != CommandType.notBASIC)
        //if action is basic
        {
            BasicCommands basicCommands = new BasicCommands(this.serverState);// Pass the GameServer instance to BasicCommands constructor
            output = basicCommands.performBasicCommand(action,tokenizedCommand); // Call the performBasicCommand method on the instance
        }
        System.out.println(serverState.getAllTriggers());//Prints out all triggers that have been loaded for testing purposes
        return output;
    }

    private ArrayList<String> tokenizeInputString(String command) {
        String[] tokens = command.split(" ");
        ArrayList<String> tokenList = new ArrayList<>();
        for (String token : tokens) {
            tokenList.add(token);
        }
        return tokenList;
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