package com.bluewavevision.tracmojo.model;

/**
 * Created by Admin on 3/26/2015.
 */
public class SocialUser {
    private String socialId;
    private String firstName;
    private String lastName;
    private String email;

    public SocialUser() {
    }

    public SocialUser(String socialId, String firstName, String lastName, String email) {
        this.socialId = socialId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }




}
