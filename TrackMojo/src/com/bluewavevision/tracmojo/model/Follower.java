package com.bluewavevision.tracmojo.model;

import java.io.Serializable;

/**
 * Created by Admin on 4/21/2015.
 */
public class Follower implements Serializable {

    public static final String NO_RESPONSE = "p";
    public static final String ACCEPTED = "a";
    public static final String DECLINED = "d";

    private int id;
    private String userId;
    private String emailId;
    private String requestStatus;

    public Follower() {
    }

    public Follower(int id, String userId, String emailId, String requestStatus) {
        this.id = id;
        this.userId = userId;
        this.emailId = emailId;
        this.requestStatus = requestStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }
}
