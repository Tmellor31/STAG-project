package edu.uob;

        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;

        import java.io.File;
        import java.nio.file.Paths;
        import java.time.Duration;

        import static org.junit.jupiter.api.Assertions.*;

class CustomSTAGTests {

    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "custom-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "custom-actions.xml").toAbsolutePath().toFile();
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
    void testAmbiCommand(){
      sendCommandToServer("simon: get potion");
      sendCommandToServer("simon: goto forest");
      sendCommandToServer("simon: get key");
      sendCommandToServer("simon: goto cabin");
      sendCommandToServer("simon: unlock trapdoor");
      sendCommandToServer("simon: goto cellar");
      String ambiCommand = sendCommandToServer("give potion");
      String ambiCommand2 = sendCommandToServer("give");
      String notAmbi = sendCommandToServer("give potion to elf");
      String twoEntityProduced = sendCommandToServer("look");
      ambiCommand = ambiCommand.toLowerCase();
      ambiCommand2 = ambiCommand2.toLowerCase();
      notAmbi = notAmbi.toLowerCase();
      twoEntityProduced = twoEntityProduced.toLowerCase();
      assertTrue(ambiCommand.contains("ambiguous"), "Did not see ambiguous warning");
      assertTrue(ambiCommand2.contains("ambiguous"), "Did not see ambiguous warning");
      assertTrue(notAmbi.contains("the elf drinks"),"The elf didn't drink the potion");
      assertTrue(twoEntityProduced.contains("elf_corpse"),"The elf corpse was not produced");
      assertTrue(twoEntityProduced.contains("empty_potion_bottle"),"The empty potion bottle was not produced");
    }

    @Test
    void testOtherAmbiChoice(){
        sendCommandToServer("simon: get potion");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: unlock trapdoor");
        sendCommandToServer("simon: goto cellar");
        String ambiCommand = sendCommandToServer("give potion");
        String ambiCommand2 = sendCommandToServer("give");
        String notAmbi = sendCommandToServer("give potion to dwarf");
        ambiCommand = ambiCommand.toLowerCase();
        ambiCommand2 = ambiCommand2.toLowerCase();
        notAmbi = notAmbi.toLowerCase();
        assertTrue(ambiCommand.contains("ambiguous"), "Did not see ambiguous warning");
        assertTrue(ambiCommand2.contains("ambiguous"), "Did not see ambiguous warning");
        assertTrue(notAmbi.contains("the dwarf drinks"),"The dwarf didn't drink the potion"); //Dwarf should drink instead if ambiCommand correctly done
    }

    @Test
    void produceTwoItems(){
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: chop tree");
        String response = sendCommandToServer("look");
        response = response.toLowerCase();
        assertTrue(response.contains("eagle"), "Did not see first produced entity");
        assertTrue(response.contains("log"), "Did not see second produced entity");
    }

    @Test
    void consumeTwoItems(){
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: chop tree");
        String response = sendCommandToServer("look");
        response = response.toLowerCase();
        String response2 = sendCommandToServer("inv");
        response2 = response2.toLowerCase();
        assertFalse(response.contains("tree"), "tree was not consumed");
        assertFalse(response2.contains("axe"), "axe was not consumed");
    }

    @Test
    void testExtraneousEnts() {
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
    }

    @Test
    void testCompositeCmds() {
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        String response = sendCommandToServer("simon: chop tree and get key");
        String response2 = sendCommandToServer("simon: get key");
        response = response.toLowerCase();
        response2 = response2.toLowerCase();
        assertTrue(response.contains("composite"), "Did not see composite warning");
        assertFalse(response2.contains("composite"), "Found composite warning for single command");
    }

    @Test
    void testPartialCommands(){
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: chop tree");
        String response = sendCommandToServer("look");
        response = response.toLowerCase();
        assertTrue(response.contains("log"), "Tree was not chopped despite partial command");
    }

    @Test
    void testGotoFormatting(){
        String response = sendCommandToServer("simon: forest goto");
        String response2 = sendCommandToServer("simon: goto forest");
        response = response.toLowerCase();
        assertFalse(response.contains("you have moved"), "Moved despite incorrect formatting");
        response2 = response2.toLowerCase();
        assertTrue(response2.contains("you have moved"), "Did not move despite correct command");
    }

    @Test
    void testGotoMessages(){
        String response = sendCommandToServer("simon: forest goto");
        String response2 = sendCommandToServer("simon: goto forest");
        String ambCheck = sendCommandToServer("simon: goto cabin riverbank");
        String decorationCheck = sendCommandToServer("simon: goto big cabin");
        response = response.toLowerCase();
        assertFalse(response.contains("you have moved"), "Moved despite incorrect formatting");
        response2 = response2.toLowerCase();
        assertTrue(response2.contains("you have moved"), "Did not move despite correct command");
        ambCheck = ambCheck.toLowerCase();
        assertTrue(ambCheck.contains("ambiguous"), "Didn't find multiple valid locations command");
        decorationCheck = decorationCheck.toLowerCase();
        assertTrue(decorationCheck.contains("you have moved"), "Didn't move when using decorative commands");
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
    void testConsumeInvItem() {
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: goto riverbank");
        sendCommandToServer("simon: get horn");
        String response = sendCommandToServer("simon: blow horn");
        response = response.toLowerCase();
        assertTrue(response.contains("lumberjack"), "Did not see the name");
    }

    @Test
    void testCompleteGame() {
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: get coin");
        sendCommandToServer("goto forest");
        sendCommandToServer("chop tree");
        sendCommandToServer("get log");
        sendCommandToServer("get key");
        sendCommandToServer("goto cabin");
        sendCommandToServer("unlock trapdoor");
        sendCommandToServer("goto cellar");
        sendCommandToServer("pay elf");
        sendCommandToServer("get shovel");
        sendCommandToServer("goto cabin");
        sendCommandToServer("goto forest");
        sendCommandToServer("goto riverbank");
        sendCommandToServer("use log as bridge for river");
        sendCommandToServer("goto clearing");
        sendCommandToServer("dig ground");
        String response = sendCommandToServer("get gold");
        response = response.toLowerCase();
        assertTrue(response.contains("you added the"),"Did not add gold at the end of the game");
    }
}


