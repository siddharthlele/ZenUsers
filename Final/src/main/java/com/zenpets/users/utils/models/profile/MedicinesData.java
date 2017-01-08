package com.zenpets.users.utils.models.profile;

public class MedicinesData {

    private String medicineName;
    private String medicineDate;
    private String medicineDosage;
    private String medicineSymptoms;
    private String medicineSideEffects;
    private String medicineImage;

    public MedicinesData() {
    }

    public MedicinesData(String medicineName, String medicineDate, String medicineDosage, String medicineSymptoms, String medicineSideEffects, String medicineImage) {
        this.medicineName = medicineName;
        this.medicineDate = medicineDate;
        this.medicineDosage = medicineDosage;
        this.medicineSymptoms = medicineSymptoms;
        this.medicineSideEffects = medicineSideEffects;
        this.medicineImage = medicineImage;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getMedicineDate() {
        return medicineDate;
    }

    public String getMedicineDosage() {
        return medicineDosage;
    }

    public String getMedicineSymptoms() {
        return medicineSymptoms;
    }

    public String getMedicineSideEffects() {
        return medicineSideEffects;
    }

    public String getMedicineImage() {
        return medicineImage;
    }
}