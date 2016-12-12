package com.zenpets.users.utils.models.clinics;

public class ClinicsData {

    private String clinicSubscription;
    private String clinicName;
    private String clinicContactPerson;
    private String clinicPhone;
    private String clinicAddress;
    private String clinicCity;
    private String clinicState;
    private String clinicLocality;
    private String clinicPinCode;
    private String clinicLandmark;
    private String clinicCountry;
    private String clinicCurrency;
    private Double clinicLatitude;
    private Double clinicLongitude;
    private String clinicLogo;

    public ClinicsData() {
    }

    public ClinicsData(String clinicSubscription, String clinicName, String clinicContactPerson, String clinicPhone, String clinicAddress, String clinicCity, String clinicState, String clinicLocality, String clinicPinCode, String clinicLandmark, String clinicCountry, String clinicCurrency, Double clinicLatitude, Double clinicLongitude, String clinicLogo) {
        this.clinicSubscription = clinicSubscription;
        this.clinicName = clinicName;
        this.clinicContactPerson = clinicContactPerson;
        this.clinicPhone = clinicPhone;
        this.clinicAddress = clinicAddress;
        this.clinicCity = clinicCity;
        this.clinicState = clinicState;
        this.clinicLocality = clinicLocality;
        this.clinicPinCode = clinicPinCode;
        this.clinicLandmark = clinicLandmark;
        this.clinicCountry = clinicCountry;
        this.clinicCurrency = clinicCurrency;
        this.clinicLatitude = clinicLatitude;
        this.clinicLongitude = clinicLongitude;
        this.clinicLogo = clinicLogo;
    }

    public String getClinicSubscription() {
        return clinicSubscription;
    }

    public String getClinicName() {
        return clinicName;
    }

    public String getClinicContactPerson() {
        return clinicContactPerson;
    }

    public String getClinicPhone() {
        return clinicPhone;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public String getClinicCity() {
        return clinicCity;
    }

    public String getClinicState() {
        return clinicState;
    }

    public String getClinicLocality() {
        return clinicLocality;
    }

    public String getClinicPinCode() {
        return clinicPinCode;
    }

    public String getClinicLandmark() {
        return clinicLandmark;
    }

    public String getClinicCountry() {
        return clinicCountry;
    }

    public String getClinicCurrency() {
        return clinicCurrency;
    }

    public Double getClinicLatitude() {
        return clinicLatitude;
    }

    public Double getClinicLongitude() {
        return clinicLongitude;
    }

    public String getClinicLogo() {
        return clinicLogo;
    }
}