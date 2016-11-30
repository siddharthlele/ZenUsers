package com.zenpets.users.utils.models.doctors;

public class ServicesData {

    private String doctorID;
    private String serviceName;

    public ServicesData() {
    }

    public ServicesData(String doctorID, String serviceName) {
        this.doctorID = doctorID;
        this.serviceName = serviceName;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getServiceName() {
        return serviceName;
    }
}