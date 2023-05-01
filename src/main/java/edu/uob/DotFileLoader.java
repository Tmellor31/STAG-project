package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class DotFileLoader {
    private ServerState serverState;

    public DotFileLoader(ServerState serverState) {
        this.serverState = serverState;
    }

    public void loadDotFile(String filePath) {
        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader("config" + File.separator + "basic-entities.dot");
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();

            // The locations will always be in the first subgraph
            ArrayList<Graph> locations = sections.get(0).getSubgraphs();
            Graph startLocation = locations.get(0); //First location in the list and therefore start location
            Node locationDetails = startLocation.getNodes(false).get(0); //Gets the nodes of the first location in this case cabin
            // Yes, you do need to get the ID twice !
            String locationName = locationDetails.getId().getId(); //Name of first location '
            //System.out.println(locationName);
            populateLocationMap(locations); //Loads locations into the serverstate

            // The paths will always be in the second subgraph
            ArrayList<Edge> paths = sections.get(1).getEdges();
            Edge firstPath = paths.get(0); //This path is from cabin
            Node fromLocation = firstPath.getSource().getNode();
            String fromName = fromLocation.getId().getId();//cabin
            Node toLocation = firstPath.getTarget().getNode();//to forest
            String toName = toLocation.getId().getId();//forest
            addPaths(paths);

        } catch (FileNotFoundException fnfe) {
            System.out.println("Couldn't find file");
        } catch (ParseException e) {
            System.out.println("Error parsing file");
        }

    }

    public void populateLocationMap(ArrayList<Graph> locationsToAdd) {
        for (int i = 0; i < (locationsToAdd.size()); i++) {
            Graph location = locationsToAdd.get(i);
            Node locationDetails = location.getNodes(false).get(0);
            String locationName = locationDetails.getId().getId();
            String locationDescription = locationDetails.getAttribute("description");
            Location newLocation = new Location(locationName, locationDescription, i == 0);

            //Adding artefacts/furniture/characters
            ArrayList<Graph> containedEntities = location.getSubgraphs();
            for (Graph entity : containedEntities) {
                String entityType = entity.getId().getId().toLowerCase();

                for (int x = 0; x < entity.getNodes(false).size(); x++) {
                    Node entityDetails = entity.getNodes(false).get(x);
                    String entityName = entityDetails.getId().getId();
                    String entityDescription = entityDetails.getAttribute("description");
                    switch (entityType) {
                        case "furniture":
                            Furniture newFurniture = new Furniture(entityName, entityDescription);
                            newLocation.addFurniture(newFurniture);
                            break;
                        case "artefacts":
                            Artefact newArtefact = new Artefact(entityName, entityDescription);
                            newLocation.addArtefact(newArtefact);
                            break;
                        case "characters":
                            Character newCharacter = new Character(entityName, entityDescription);
                            newLocation.addCharacter(newCharacter);
                            break;
                        default:
                            System.out.println("Entity type unrecognised when trying to add");
                            break;
                    }
                }
                serverState.addLocation(newLocation);
            }
        }
    }


        public void addPaths (ArrayList < Edge > pathsToAdd) {
            for (Edge path : pathsToAdd) {
                Node fromLocation = path.getSource().getNode();
                String fromName = fromLocation.getId().getId();
                Node toLocation = path.getTarget().getNode();
                String toName = toLocation.getId().getId();

                // Find the locations in the server state
                Location from = serverState.getLocation(fromName);
                Location to = serverState.getLocation(toName);

                from.addPath(from, to);
            }
        }
    }





