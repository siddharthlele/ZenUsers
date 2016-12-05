package com.zenpets.users.utils.models.consultations;

public class ConsultationAnswersData {
    private String doctorID;
    private String consultAnswer;
    private String consultNextSteps;
    private String timeStamp;
    private int helpfulYes;
    private int helpfulNo;

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
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
}