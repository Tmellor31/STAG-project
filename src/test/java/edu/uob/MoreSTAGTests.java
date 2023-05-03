package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class MoreSTAGTests {

    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
                    return server.handleCommand(command);
                },
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testExtraneousEntities() {
        sendCommandToServer("simon: get axe");
        String response = sendCommandToServer("simon: chop tree with axe coin");
        response = response.toLowerCase();
        String response2 = sendCommandToServer("simon: chop tree with axe forest");
        response2 = response2.toLowerCase();
        String notExtraneous = sendCommandToServer("simon: chop tree with axe");
        notExtraneous = notExtraneous.toLowerCase();
        assertTrue(response.contains("extraneous"), "Did not see extraneous entities warning");
        assertTrue(response2.contains("extraneous"), "Did not see extraneous entities warning");
        assertFalse(notExtraneous.contains("extraneous"),"Found extraneous warning when there shouldn't be one");
        String lookTest = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
    }

    @Test
    void testProduce(){
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: chop tree with axe");
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("log"), "Did not see the log in response to look");
    }

    // Consuming items in inventory was causing problems so wrote a test for it
    @Test
    void testConsumeItemInInventory() {
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: goto riverbank");
        sendCommandToServer("simon: get horn");
        String response = sendCommandToServer("simon: blow horn");
        response = response.toLowerCase();
        assertTrue(response.contains("lumberjack"), "Did not see the name");
    }
}

