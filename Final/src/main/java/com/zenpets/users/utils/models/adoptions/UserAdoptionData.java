package com.zenpets.users.utils.models.adoptions;

public class UserAdoptionData {

    private String adoptionID;

    public UserAdoptionData() {}

    public UserAdoptionData(String adoptionID) {
        this.adoptionID = adoptionID;
    }

    public String getAdoptionID() {
        return adoptionID;
    }
}