package com.zenpets.users.utils.models.adoptions;

public class AdoptionsData {
    private String adoptionStatus;
    private String petType;
    private String petBreed;
    private String petName;
    private String petGender;
    private String petVaccination;
    private String petDewormed;
    private String petNeutered;
    private String petDescription;
    private String userKey;
    private String userID;
    private String userName;
    private String timeStamp;

    public AdoptionsData() {}

    public AdoptionsData(String adoptionStatus, String petType, String petBreed, String petName, String petGender, String petVaccination, String petDewormed, String petNeutered, String petDescription, String userKey, String userID, String userName, String timeStamp) {
        this.adoptionStatus = adoptionStatus;
        this.petType = petType;
        this.petBreed = petBreed;
        this.petName = petName;
        this.petGender = petGender;
        this.petVaccination = petVaccination;
        this.petDewormed = petDewormed;
        this.petNeutered = petNeutered;
        this.petDescription = petDescription;
        this.userKey = userKey;
        this.userID = userID;
        this.userName = userName;
        this.timeStamp = timeStamp;
    }

    public String getAdoptionStatus() {
        return adoptionStatus;
    }

    public String getPetType() {
        return petType;
    }

    public String getPetBreed() {
        return petBreed;
    }

    public String getPetName() {
        return petName;
    }

    public String getPetGender() {
        return petGender;
    }

    public String getPetVaccination() {
        return petVaccination;
    }

    public String getPetDewormed() {
        return petDewormed;
    }

    public String getPetNeutered() {
        return petNeutered;
    }

    public String getPetDescription() {
        return petDescription;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}