package com.cubixos.pocket.models;

public class User {

    private String id;
    private String userName; // Submit User Name
    private String userNameId;//@username
    private String bio;
    private String status;//Email Verified
    private String profilePicUrl;//PicUrl
    private String emailAddress;//Email
    private String phoneNumber;//Email
    private String othersDesc;//-

    public User() {
        //empty constructor needed
    }

    public User(String id, String userName, String userNameId, String bio, String status, String profilePicUrl, String emailAddress, String phoneNumber, String othersDesc) {
        this.id = id;
        this.userName = userName;
        this.userNameId = userNameId;
        this.bio = bio;
        this.status = status;
        this.profilePicUrl = profilePicUrl;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.othersDesc = othersDesc;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserNameId() {
        return userNameId;
    }

    public String getBio() {
        return bio;
    }

    public String getStatus() {
        return status;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getOthersDesc() {
        return othersDesc;
    }
}