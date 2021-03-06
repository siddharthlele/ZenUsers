package com.zenpets.users.utils.models.reviews;

public class ReviewsData {

    private String recommendStatus;
    private String appointmentStatus;
    private int doctorRating;
    private String visitReason;
    private String doctorExperience;
    private String userKey;
    private String userID;
    private String userName;
    private String timeStamp;

    public ReviewsData() {}

    public ReviewsData(String recommendStatus, String appointmentStatus, int doctorRating, String visitReason, String doctorExperience, String userKey, String userID, String userName, String timeStamp) {
        this.recommendStatus = recommendStatus;
        this.appointmentStatus = appointmentStatus;
        this.doctorRating = doctorRating;
        this.visitReason = visitReason;
        this.doctorExperience = doctorExperience;
        this.userKey = userKey;
        this.userID = userID;
        this.userName = userName;
        this.timeStamp = timeStamp;
    }

    public String getRecommendStatus() {
        return recommendStatus;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public int getDoctorRating() {
        return doctorRating;
    }

    public String getVisitReason() {
        return visitReason;
    }

    public String getDoctorExperience() {
        return doctorExperience;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}