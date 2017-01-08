package com.zenpets.users.utils.models.pet;

public class VaccinationsData {

    private String userID;
    private String petID;
    private String vaccineName;
    private String vaccineDate;
    private String vaccineNotes;

    public VaccinationsData() {}

    public VaccinationsData(String userID, String petID, String vaccineName, String vaccineDate, String vaccineNotes) {
        this.userID = userID;
        this.petID = petID;
        this.vaccineName = vaccineName;
        this.vaccineDate = vaccineDate;
        this.vaccineNotes = vaccineNotes;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public String getVaccineDate() {
        return vaccineDate;
    }

    public void setVaccineDate(String vaccineDate) {
        this.vaccineDate = vaccineDate;
    }

    public String getVaccineNotes() {
        return vaccineNotes;
    }

    public void setVaccineNotes(String vaccineNotes) {
        this.vaccineNotes = vaccineNotes;
    }
}