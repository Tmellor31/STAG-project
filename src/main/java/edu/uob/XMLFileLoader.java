package edu.uob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class XMLFileLoader {
    private ServerState serverState;

    private HashMap<String, HashSet<GameAction>> actions;

    public XMLFileLoader(ServerState serverState){
        this.serverState = serverState;
        this.actions = serverState.getActions();
    }
    void loadXMLFile(String filePath) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse("config" + File.separator + "basic-actions.xml");
            Element root = document.getDocumentElement();
            NodeList actionNodes = root.getElementsByTagName("action");

            for (int i = 0; i < actionNodes.getLength(); i++) {
                Element actionElement = (Element) actionNodes.item(i);

                // Get the triggers for this action
                HashSet<String> triggers = new HashSet<>();
                NodeList triggerNodes = actionElement.getElementsByTagName("keyphrase");
                for (int j = 0; j < triggerNodes.getLength(); j++) {
                    triggers.add(triggerNodes.item(j).getTextContent());
                }

                // Create the GameAction object for this action
                NodeList subjectNodes = actionElement.getElementsByTagName("entity");
                HashSet<String> subjects = new HashSet<>();
                for (int j = 0; j < subjectNodes.getLength(); j++) {
                    subjects.add(subjectNodes.item(j).getTextContent());
                }
                NodeList consumedNodes = actionElement.getElementsByTagName("consumed");
                HashSet<String> consumed = new HashSet<>();
                for (int j = 0; j < consumedNodes.getLength(); j++) {
                    consumed.add(consumedNodes.item(j).getTextContent());
                }
                NodeList producedNodes = actionElement.getElementsByTagName("produced");
                HashSet<String> produced = new HashSet<>();
                for (int j = 0; j < producedNodes.getLength(); j++) {
                    produced.add(producedNodes.item(j).getTextContent());
                }
                String narration = actionElement.getElementsByTagName("narration").item(0).getTextContent();

                GameAction gameAction = new GameAction(triggers, subjects, consumed, produced, narration);

                // Add the GameAction object to the HashSet for each trigger phrase
                for (String trigger : triggers) {
                    if (actions.containsKey(trigger)) {
                        actions.get(trigger).add(gameAction);
                    } else {
                        HashSet<GameAction> actionSet = new HashSet<>();
                        actionSet.add(gameAction);
                        actions.put(trigger, actionSet);
                    }
                }
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("ParserConfigurationException was thrown when attempting to read basic actions file");
        } catch (SAXException saxe) {
            System.out.println("SAXException was thrown when attempting to read basic actions file");
        } catch (IOException ioe) {
            System.out.println("IOException was thrown when attempting to read basic actions file");
        }
    }


    public void printAllKeyphrases() {//Prints out all triggers currently in the game e.g. 'chop' 'cut' etc
        for (HashSet<GameAction> gameActions : actions.values()) {
            for (GameAction gameAction : gameActions) {
                HashSet<String> triggers = gameAction.getTriggers();
                for (String trigger : triggers) {
                    System.out.println(trigger);
                }
            }
        }
    }


}
