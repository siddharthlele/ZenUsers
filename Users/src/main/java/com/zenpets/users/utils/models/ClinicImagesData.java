package com.zenpets.users.utils.models;

public class ClinicImagesData {

    private String clinicOwner;
    private String clinicID;
    private String clinicImage;

    public ClinicImagesData() {
    }

    public ClinicImagesData(String clinicOwner, String clinicID, String clinicImage) {
        this.clinicOwner = clinicOwner;
        this.clinicID = clinicID;
        this.clinicImage = clinicImage;
    }

    public String getClinicOwner() {
        return clinicOwner;
    }

    public String getClinicID() {
        return clinicID;
    }

    public String getClinicImage() {
        return clinicImage;
    }
}