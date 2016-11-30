package com.zenpets.users.utils.models.doctors;

public class EducationData {
    private String doctorID;
    private String qualificationName;
    private String collegeName;
    private String qualificationYear;

    public EducationData() {}

    public EducationData(String doctorID, String qualificationName, String collegeName, String qualificationYear) {
        this.doctorID = doctorID;
        this.qualificationName = qualificationName;
        this.collegeName = collegeName;
        this.qualificationYear = qualificationYear;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getQualificationName() {
        return qualificationName;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public String getQualificationYear() {
        return qualificationYear;
    }
}