package com.zenpets.users.utils.models;

public class VaccinesData {

    private String VaccineName;
    private String VaccineDescription;

    public VaccinesData() {}

    public VaccinesData(String vaccineName, String vaccineDescription) {
        VaccineName = vaccineName;
        VaccineDescription = vaccineDescription;
    }

    public String getVaccineName() {
        return VaccineName;
    }

    public String getVaccineDescription() {
        return VaccineDescription;
    }
}