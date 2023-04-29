package edu.uob;

import java.util.ArrayList;

public class Location {
    private String name;
    private String description;
    private ArrayList<Location> paths;
    private ArrayList<Character> characters;
    private ArrayList<Artefact> artefacts;
    private ArrayList<Furniture> furniture;

    public Location(String name, String description) {
        this.name = name;
        this.description = description;
        this.paths = new ArrayList<Location>();
        this.characters = new ArrayList<Character>();
        this.artefacts = new ArrayList<Artefact>();
        this.furniture = new ArrayList<Furniture>();
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public void addPath(Location destination) {
        this.paths.add(destination);
    }

    public ArrayList<Location> getPaths() {
        return this.paths;
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
