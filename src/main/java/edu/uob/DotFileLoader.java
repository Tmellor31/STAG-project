package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class DotFileLoader {
    private ServerState serverState;

    private HashMap<String, GameEntity> gameEntities;

    public DotFileLoader(ServerState serverState) {
        gameEntities = new HashMap<>();
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
        } catch (FileNotFoundException fnfe) {
            System.out.println("Couldn't find file");
        } catch (ParseException e) {
            System.out.println("Error parsing file");
        }

    }

    public void populateLocationMap (ArrayList<Graph> locationsToAdd){
     for (int i = 0; i < (locationsToAdd.size()); i++){
          Graph location = locationsToAdd.get(i);
          Node locationDetails = location.getNodes(false).get(0);
          String locationName = locationDetails.getId().getId();
          String locationDescription = locationDetails.getAttribute("description");
          Location newLocation = new Location(locationName, locationDescription, i == 0);

          //Adding artifacts/furniture
         ArrayList<Graph> containedEntities = location.getSubgraphs();
          for (Graph entity : containedEntities){
              String entityType = entity.getId().getId();

              for (int x = 0; x < entity.getNodes(false).size(); x++){
                  Node entityDetails = entity.getNodes(false).get(x);
                  String entityName = entityDetails.getId().getId();
                  String entityDescription = entityDetails.getAttribute("description");

                  if(entityType.equalsIgnoreCase("furniture")){
                      Furniture newFurniture = new Furniture(entityName,entityDescription);
                      newLocation.addFurniture(newFurniture);
                      continue;
                  }
                  if(entityType.equalsIgnoreCase("artefacts")){
                      Artefact newArtefact = new Artefact(entityName,entityDescription);
                      newLocation.addArtefact(newArtefact);
                      continue;
                  }
                  if(entityType.equalsIgnoreCase("characters")){
                      Character newCharacter = new Character(entityName,entityDescription);
                      newLocation.addCharacter(newCharacter);
                      continue;
                  }
                  else {
                      System.out.println("Entity type unrecognised when trying to add");
                  }
                  //Don't think is needed anymore newLocation.addEntity(entityType, entityName, entityDescription);
              }
          }
          serverState.addLocation(newLocation);
        }
    }
}


