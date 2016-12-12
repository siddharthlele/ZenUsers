package com.zenpets.users.utils.models.user;

public class UserData {

    private String userID;
    private String userName;
    private String userEmail;
    private String userPhonePrefix;
    private String userPhoneNumber;
    private String userState;
    private String userCity;
    private String userGender;
    private String userDOB;

    public UserData() {
    }

    public UserData(String userID, String userName, String userEmail, String userPhonePrefix, String userPhoneNumber, String userState, String userCity, String userGender, String userDOB) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhonePrefix = userPhonePrefix;
        this.userPhoneNumber = userPhoneNumber;
        this.userState = userState;
        this.userCity = userCity;
        this.userGender = userGender;
        this.userDOB = userDOB;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhonePrefix() {
        return userPhonePrefix;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public String getUserState() {
        return userState;
    }

    public String getUserCity() {
        return userCity;
    }

    public String getUserGender() {
        return userGender;
    }

    public String getUserDOB() {
        return userDOB;
    }
}