package com.tracmojo.model;

import java.io.Serializable;

/**
 * Created by Admin on 4/17/2015.
 */
public class SyncTrac implements Serializable{
    private int id;
    private String jsonData;
    private String tracType;

    public SyncTrac() {
    }

    public SyncTrac(int id, String jsonData, String tracType) {
        this.id = id;
        this.jsonData = jsonData;
        this.tracType = tracType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getTracType() {
        return tracType;
    }

    public void setTracType(String tracType) {
        this.tracType = tracType;
    }
}
