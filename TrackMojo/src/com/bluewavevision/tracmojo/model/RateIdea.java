package com.bluewavevision.tracmojo.model;

/**
 * Created by Admin on 4/9/2015.
 */
public class RateIdea {
    private int id;
    private String name;


    public RateIdea() {

    }

    public RateIdea(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
