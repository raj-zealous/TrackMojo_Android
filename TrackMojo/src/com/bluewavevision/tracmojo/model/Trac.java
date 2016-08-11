package com.bluewavevision.tracmojo.model;

import java.io.Serializable;

/**
 * Created by Admin on 3/27/2015.
 */
public class Trac implements Serializable {

    public static final int ALL_TRACS = 0;
    public static final int PERSONAL_TRAC = 1;
    public static final int GROUP_TRAC = 2;
    public static final int FOLLOWING_TRAC = 3;


    private int id;
    private int trackType;
    private String goal;
    private boolean isMyTrac;
    private String rate;
    private String lastRate;
    private boolean isNotificationOn;
    private String groupName;
    private String groupType;
    private String lastRated;
    private String rateFrequency;
    private RateOption rateOption;
    private boolean isVisible;
    private String requestStatus;
    private boolean isManageTracVisible;
    private String ownerName;

    public Trac() {

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTrackType(int trackType) {
        this.trackType = trackType;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public void setMyTrac(boolean isMyTrac) {
        this.isMyTrac = isMyTrac;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public void setNotificationOn(boolean isNotificationOn) {
        this.isNotificationOn = isNotificationOn;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public int getId() {
        return id;
    }

    public int getTrackType() {
        return trackType;
    }

    public String getGoal() {
        return goal;
    }

    public boolean isMyTrac() {
        return isMyTrac;
    }

    public String getRate() {
        return rate;
    }

    public boolean isNotificationOn() {
        return isNotificationOn;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public RateOption getRateOption() {
        return rateOption;
    }

    public void setRateOption(RateOption rateOption) {
        this.rateOption = rateOption;
    }

    public String getLastRated() {
        return lastRated;
    }

    public void setLastRated(String lastRated) {
        this.lastRated = lastRated;
    }

    public String getRateFrequency() {
        return rateFrequency;
    }

    public void setRateFrequency(String rateFrequency) {
        this.rateFrequency = rateFrequency;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public boolean isManageTracVisible() {
        return isManageTracVisible;
    }

    public void setManageTracVisible(boolean isManageTracVisible) {
        this.isManageTracVisible = isManageTracVisible;
    }

    public String getLastRate() {
        return lastRate;
    }

    public void setLastRate(String lastRate) {
        this.lastRate = lastRate;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}

