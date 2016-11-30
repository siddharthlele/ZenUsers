package com.zenpets.users.utils.models;

public class DoctorsData {

    private String clinicOwner;
    private String doctorPrefix;
    private String doctorName;
    private String doctorGender;
    private String doctorExperience;
    private String doctorCharges;
    private String doctorSummary;
    private String doctorProfile;

    public DoctorsData() {}

    public DoctorsData(String clinicOwner, String doctorPrefix, String doctorName, String doctorGender, String doctorExperience, String doctorCharges, String doctorSummary, String doctorProfile) {
        this.clinicOwner = clinicOwner;
        this.doctorPrefix = doctorPrefix;
        this.doctorName = doctorName;
        this.doctorGender = doctorGender;
        this.doctorExperience = doctorExperience;
        this.doctorCharges = doctorCharges;
        this.doctorSummary = doctorSummary;
        this.doctorProfile = doctorProfile;
    }

    public String getClinicOwner() {
        return clinicOwner;
    }

    public String getDoctorPrefix() {
        return doctorPrefix;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDoctorGender() {
        return doctorGender;
    }

    public String getDoctorExperience() {
        return doctorExperience;
    }

    public String getDoctorCharges() {
        return doctorCharges;
    }

    public String getDoctorSummary() {
        return doctorSummary;
    }

    public String getDoctorProfile() {
        return doctorProfile;
    }
}