package com.tracmojo.model;

import java.io.Serializable;

/**
 * Created by Admin on 4/28/2015.
 */
public class Comment implements Serializable{
    private int id;
    private String comment;
    private String date;
    private String time;
    private String commentBy;
    private boolean isAnonymous;

    public Comment() {
    }

    public Comment(int id, String comment, String date, String time, String commentBy) {
        this.id = id;
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.commentBy = commentBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCommentBy() {
        return commentBy;
    }

    public void setCommentBy(String commentBy) {
        this.commentBy = commentBy;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
}
