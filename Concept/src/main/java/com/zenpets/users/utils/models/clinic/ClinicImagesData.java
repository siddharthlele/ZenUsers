package com.zenpets.users.utils.models.clinic;

public class ClinicImagesData {

    private String clinicOwner;
    private String clinicID;
    private String clinicImage;

    public ClinicImagesData() {}

    public ClinicImagesData(String clinicOwner, String clinicID, String clinicImage) {
        this.clinicOwner = clinicOwner;
        this.clinicID = clinicID;
        this.clinicImage = clinicImage;
    }

    public String getClinicOwner() {
        return clinicOwner;
    }

    public void setClinicOwner(String clinicOwner) {
        this.clinicOwner = clinicOwner;
    }

    public String getClinicID() {
        return clinicID;
    }

    public void setClinicID(String clinicID) {
        this.clinicID = clinicID;
    }

    public String getClinicImage() {
        return clinicImage;
    }

    public void setClinicImage(String clinicImage) {
        this.clinicImage = clinicImage;
    }
}