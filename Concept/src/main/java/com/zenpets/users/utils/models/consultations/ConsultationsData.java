package com.zenpets.users.utils.models.consultations;

public class ConsultationsData {
    private String consultStatus;
    private String consultHeader;
    private String consultDescription;
    private String petID;
    private String userKey;
    private String timeStamp;
    private int consultViews;

    public ConsultationsData() {}

    public ConsultationsData(String consultStatus, String consultHeader, String consultDescription, String petID, String userKey, String timeStamp, int consultViews) {
        this.consultStatus = consultStatus;
        this.consultHeader = consultHeader;
        this.consultDescription = consultDescription;
        this.petID = petID;
        this.userKey = userKey;
        this.timeStamp = timeStamp;
        this.consultViews = consultViews;
    }

    public String getConsultStatus() {
        return consultStatus;
    }

    public String getConsultHeader() {
        return consultHeader;
    }

    public String getConsultDescription() {
        return consultDescription;
    }

    public String getPetID() {
        return petID;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public int getConsultViews() {
        return consultViews;
    }
}