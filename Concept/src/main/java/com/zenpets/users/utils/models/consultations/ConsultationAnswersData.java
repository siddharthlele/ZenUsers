package com.zenpets.users.utils.models.consultations;

public class ConsultationAnswersData {
    private String userKey;
    private String answerKey;
    private String doctorID;
    private String doctorName;
    private String doctorProfile;
    private String consultAnswer;
    private String consultNextSteps;
    private String timeStamp;
    private int helpfulYes;
    private int helpfulNo;
    private int totalVotes;

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getAnswerKey() {
        return answerKey;
    }

    public void setAnswerKey(String answerKey) {
        this.answerKey = answerKey;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorProfile() {
        return doctorProfile;
    }

    public void setDoctorProfile(String doctorProfile) {
        this.doctorProfile = doctorProfile;
    }

    public String getConsultAnswer() {
        return consultAnswer;
    }

    public void setConsultAnswer(String consultAnswer) {
        this.consultAnswer = consultAnswer;
    }

    public String getConsultNextSteps() {
        return consultNextSteps;
    }

    public void setConsultNextSteps(String consultNextSteps) {
        this.consultNextSteps = consultNextSteps;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getHelpfulYes() {
        return helpfulYes;
    }

    public void setHelpfulYes(int helpfulYes) {
        this.helpfulYes = helpfulYes;
    }

    public int getHelpfulNo() {
        return helpfulNo;
    }

    public void setHelpfulNo(int helpfulNo) {
        this.helpfulNo = helpfulNo;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }
}