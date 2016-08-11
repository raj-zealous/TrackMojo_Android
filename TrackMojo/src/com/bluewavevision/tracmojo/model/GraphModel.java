package com.bluewavevision.tracmojo.model;

/**
 * Created by Admin on 4/15/2015.
 */
public class GraphModel {
    private int rate;
    private String date;
    private long dateTimeStamp;
    private double groupRate;
    private float percentageOfNoOfParticipants;
    private double participantsRate;

    public GraphModel() {

    }

    public GraphModel(int rate, String date, long dateTimeStamp) {
        this.rate = rate;
        this.date = date;
        this.dateTimeStamp = dateTimeStamp;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDateTimeStamp() {
        return dateTimeStamp;
    }

    public void setDateTimeStamp(long dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
    }

    public double getGroupRate() {
        return groupRate;
    }

    public void setGroupRate(double groupRate) {
        this.groupRate = groupRate;
    }

    public float getPercentageOfNoOfParticipants() {
        return percentageOfNoOfParticipants;
    }

    public void setPercentageOfNoOfParticipants(float percentageOfNoOfParticipants) {
        this.percentageOfNoOfParticipants = percentageOfNoOfParticipants;
    }

    public double getParticipantsRate() {
        return participantsRate;
    }

    public void setParticipantsRate(double participantsRate) {
        this.participantsRate = participantsRate;
    }
}
