package com.zenpets.users.utils.models.profile;

public class PetData {

    private String petID;
    private String petOwnerID;
    private String petTypeName;
    private String petBreedName;
    private String petName;
    private String petGender;
    private String petDOB;
    private String petPicture;

    public PetData() {}

    public PetData(String petID, String petOwnerID, String petTypeName, String petBreedName, String petName, String petGender, String petDOB, String petPicture) {
        this.petID = petID;
        this.petOwnerID = petOwnerID;
        this.petTypeName = petTypeName;
        this.petBreedName = petBreedName;
        this.petName = petName;
        this.petGender = petGender;
        this.petDOB = petDOB;
        this.petPicture = petPicture;
    }

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public String getPetOwnerID() {
        return petOwnerID;
    }

    public void setPetOwnerID(String petOwnerID) {
        this.petOwnerID = petOwnerID;
    }

    public String getPetTypeName() {
        return petTypeName;
    }

    public void setPetTypeName(String petTypeName) {
        this.petTypeName = petTypeName;
    }

    public String getPetBreedName() {
        return petBreedName;
    }

    public void setPetBreedName(String petBreedName) {
        this.petBreedName = petBreedName;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetGender() {
        return petGender;
    }

    public void setPetGender(String petGender) {
        this.petGender = petGender;
    }

    public String getPetDOB() {
        return petDOB;
    }

    public void setPetDOB(String petDOB) {
        this.petDOB = petDOB;
    }

    public String getPetPicture() {
        return petPicture;
    }

    public void setPetPicture(String petPicture) {
        this.petPicture = petPicture;
    }
}