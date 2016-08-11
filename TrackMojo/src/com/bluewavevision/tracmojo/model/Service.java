package com.bluewavevision.tracmojo.model;

import java.io.Serializable;

/**
 * Created by Admin on 4/30/2015.
 */
public class Service implements Serializable{
    private int id;
    private String name;
    private double amount;
    private String period;
    private String description;
    private boolean isActive;
    private int personalTrac;
    private int groupTrac;
    private int participantsCount;

    public Service() {
    }

    public Service(int id, String name, int amount, String period, String description, boolean isActive, int personalTrac, int groupTrac, int participantsCount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.period = period;
        this.description = description;
        this.isActive = isActive;
        this.personalTrac = personalTrac;
        this.groupTrac = groupTrac;
        this.participantsCount = participantsCount;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getPersonalTrac() {
        return personalTrac;
    }

    public void setPersonalTrac(int personalTrac) {
        this.personalTrac = personalTrac;
    }

    public int getGroupTrac() {
        return groupTrac;
    }

    public void setGroupTrac(int groupTrac) {
        this.groupTrac = groupTrac;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
