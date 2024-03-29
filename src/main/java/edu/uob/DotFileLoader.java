package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.util.ArrayList;

public class DotFileLoader {
    private ServerState serverState;

    public DotFileLoader(ServerState serverState) {
        this.serverState = serverState;
    }

    public void loadDotFile(String filePath) {
        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(filePath);
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();

            // The locations will always be in the first subgraph
            ArrayList<Graph> locations = sections.get(0).getSubgraphs();
            Graph startLocation = locations.get(0); //First location in the list and therefore start location
            Node locationDetails = startLocation.getNodes(false).get(0); //Gets the nodes of the first location in this case cabin
            // Yes, you do need to get the ID twice !
            populateLocationMap(locations); //Loads locations into the serverstate

            // The paths will always be in the second subgraph
            ArrayList<Edge> paths = sections.get(1).getEdges();
            addPaths(paths);

            serverState.setCurrentLocation(serverState.getFirstLocation());

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
            String locationName = locationDetails.getId().getId().trim();
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
                            Furniture newFurniture = new Furniture(entityName, entityDescription,newLocation);
                            newLocation.addFurniture(newFurniture);
                            break;
                        case "artefacts":
                            Artefact newArtefact = new Artefact(entityName, entityDescription,newLocation);
                            newLocation.addArtefact(newArtefact);
                            break;
                        case "characters":
                            Character newCharacter = new Character(entityName, entityDescription,newLocation);
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
                String fromName = fromLocation.getId().getId().trim();
                Node toLocation = path.getTarget().getNode();
                String toName = toLocation.getId().getId().trim();

                // Find the locations in the server state
                Location from = serverState.getLocation(fromName);
                Location to = serverState.getLocation(toName);

                from.addPathTo(to);
            }
        }
    }





