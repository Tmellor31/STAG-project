package edu.uob;

import java.util.ArrayList;

public class Path {
    private Location from;
    private Location to;


    public Path(Location from, Location to) {
        this.from = from;
        this.to = to;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }
}


