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

    public XMLFileLoader(ServerState serverState) {
        this.serverState = serverState;
        this.actions = serverState.getActions();
    }

    void loadXMLFile(String filePath) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(filePath);
            Element root = document.getDocumentElement();
            NodeList actionNodes = root.getElementsByTagName("action");

            for (int i = 0; i < actionNodes.getLength(); i++) {
                Element actionElement = (Element) actionNodes.item(i);

                // Get the triggers for this action
                HashSet<String> triggers = loadTriggers(actionElement.getElementsByTagName("keyphrase"));


                // Create the GameAction object for this action
                NodeList subjectNodes = actionElement.getElementsByTagName("entity");
                HashSet<String> subjects = new HashSet<>();
                for (int j = 0; j < subjectNodes.getLength(); j++) {
                    subjects.add(subjectNodes.item(j).getTextContent().trim());
                }
                NodeList consumedNodes = actionElement.getElementsByTagName("consumed");
                HashSet<String> consumed = new HashSet<>();
                for (int j = 0; j < consumedNodes.getLength(); j++) {
                    NodeList childNodes = consumedNodes.item(j).getChildNodes();
                    for (int k = 0; k < childNodes.getLength(); k++) {
                        String child = childNodes.item(k).getTextContent().trim();
                        if (child.isEmpty()){
                            continue;
                        }
                        consumed.add(child);
                    }
                }

                NodeList producedNodes = actionElement.getElementsByTagName("produced");
                HashSet<String> produced = new HashSet<>();
                for (int j = 0; j < producedNodes.getLength(); j++) {
                    NodeList childNodes = producedNodes.item(j).getChildNodes();
                    for (int k = 0; k < childNodes.getLength(); k++) {
                        String child = childNodes.item(k).getTextContent().trim();
                        if (child.isEmpty()){
                            continue;
                        }
                        produced.add(child);
                    }
                }
                String narration = actionElement.getElementsByTagName("narration").item(0).getTextContent();

                GameAction gameAction = new GameAction(subjects, consumed, produced, narration);

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

    private HashSet<String> loadTriggers(NodeList nodeList) {
        HashSet<String> triggers = new HashSet<>();
        for (int j = 0; j < nodeList.getLength(); j++) {
            triggers.add(nodeList.item(j).getTextContent());
        }
        return triggers;
    }


}
