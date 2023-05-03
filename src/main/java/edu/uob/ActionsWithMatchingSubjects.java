package edu.uob;

import java.util.Set;

public class ActionsWithMatchingSubjects {
    GameAction action;
    Set<String> matchingSubjects;

    public ActionsWithMatchingSubjects(GameAction action, Set<String> matchingSubjects) {
           this.action = action;
           this.matchingSubjects = matchingSubjects;
    }
}
