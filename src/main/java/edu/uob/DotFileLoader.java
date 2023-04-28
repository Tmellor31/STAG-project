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
    private HashMap<String, GameEntity> gameEntities;

    public void DotFileLoader() {
        gameEntities = new HashMap<>();
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
            System.out.println(locationName);
        } catch (FileNotFoundException fnfe) {
            System.out.println("Couldn't find file");
        } catch (ParseException e) {
            System.out.println("Error parsing file");
        }

    }

    public void locationsAdder (ArrayList<Graph> locationsToAdd){

    }
}


