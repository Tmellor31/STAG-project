package edu.uob;

import java.nio.file.Paths;
import java.util.ArrayList;

public class Location {
    private String name;
    private String description;

    private boolean isStart;
    private ArrayList<Path> paths;
    private ArrayList<Character> characters;
    private ArrayList<Artefact> artefacts;
    private ArrayList<Furniture> furniture;

    public Location(String name, String description, boolean isStart) {
        this.name = name;
        this.description = description;
        this.paths = new ArrayList<Path>();
        this.characters = new ArrayList<Character>();
        this.artefacts = new ArrayList<Artefact>();
        this.furniture = new ArrayList<Furniture>();
        this.isStart = isStart;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean getIsStart() {
        return this.isStart;
    }

    public void addPath(Location from, Location to) {
        Path path = new Path(from, to);
        this.paths.add(path);
    }

    public ArrayList<Path> getPaths() {
        return this.paths;
    }

    public boolean hasPathTo(Location location) {
        for (Path path : paths) {
            if (path.getTo() == location) {
                return true;
            }
        }
        return false;
    }

    public void addCharacter(Character character) {
        this.characters.add(character);
    }

    public ArrayList<Character> getCharacters() {
        return this.characters;
    }

    public void addArtefact(Artefact artefact) {
        this.artefacts.add(artefact);
    }

    public ArrayList<Artefact> getArtefacts() {
        return this.artefacts;
    }

    public String getArtefactDescriptions() {
        StringBuilder sb = new StringBuilder();
        sb.append("Artefacts:\n");
        for (Artefact a : artefacts) {
            sb.append("- ").append(a.getName()).append(": ").append(a.getDescription()).append("\n");
        }
        return sb.toString();
    }

    public String getFurnitureDescriptions() {
        StringBuilder sb = new StringBuilder();
        sb.append("Furniture:\n");
        for (Furniture f : furniture) {
            sb.append("- ").append(f.getName()).append(": ").append(f.getDescription()).append("\n");
        }
        return sb.toString();
    }

    public String getAvailablePaths() {
        StringBuilder sb = new StringBuilder();
        sb.append("Available paths:\n");
        for (Path path : paths) {
            if (path.getFrom() == this) {
                sb.append("- ").append(path.getFrom().getName()).append(" -> ").append(path.getTo().getName()).append("\n");
            }
        }
        return sb.toString();
    }

    public void addFurniture(Furniture furniture) {
        this.furniture.add(furniture);
    }

    public ArrayList<Furniture> getFurniture() {
        return this.furniture;
    }

    public void removeCharacter(Character character) {
        this.characters.remove(character);
    }

    public void removeArtefact(Artefact artefact) {
        this.artefacts.remove(artefact);
    }

    public void removeFurniture(Furniture furniture) {
        this.furniture.remove(furniture);
    }

    public Character getCharacterByName(String name) {
        for (Character c : characters) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public Artefact getArtefactByName(String name) {
        for (Artefact a : artefacts) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    public Furniture getFurnitureByName(String name) {
        for (Furniture f : furniture) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }
}
