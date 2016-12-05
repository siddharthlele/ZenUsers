package com.zenpets.users.utils.models.consultations;

public class ConsultationHeaderData {
    private String consultHeader;
    private String consultDescription;
    private String timeStamp;
    private int consultViews;

    public String getConsultHeader() {
        return consultHeader;
    }

    public void setConsultHeader(String consultHeader) {
        this.consultHeader = consultHeader;
    }

    public String getConsultDescription() {
        return consultDescription;
    }

    public void setConsultDescription(String consultDescription) {
        this.consultDescription = consultDescription;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getConsultViews() {
        return consultViews;
    }

    public void setConsultViews(int consultViews) {
        this.consultViews = consultViews;
    }
}