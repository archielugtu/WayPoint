package com.springvuegradle.team200.model;

/**
 * Defines action to be stored in ActivityHistory,
 * more actions (CREATED, DELETED) could be added in the future if needed
 */
public enum Action {
    CREATED("created"),
    FOLLOWED("followed"),
    UNFOLLOWED("unfollowed"),
    PARTICIPATED("joined"),
    NOTPARTICIPATE("left"),
    UPDATED("updated"),
    ADDEDRESULTS("added results to");


    private final String name;

    Action(String value) {
        this.name = value;
    }

    public String value() {
        return this.name;
    }

    @Override
    public String toString() {
        return name;
    }
}
