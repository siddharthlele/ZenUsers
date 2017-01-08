package com.zenpets.users.utils.models.profile;

public class PetData {

    private String petOwnerID;
    private String petTypeName;
    private String petBreedName;
    private String petName;
    private String petGender;
    private String petDOB;
    private String petPicture;

    public PetData() {
    }

    public PetData(String petOwnerID, String petTypeName, String petBreedName, String petName, String petGender, String petDOB, String petPicture) {
        this.petOwnerID = petOwnerID;
        this.petTypeName = petTypeName;
        this.petBreedName = petBreedName;
        this.petName = petName;
        this.petGender = petGender;
        this.petDOB = petDOB;
        this.petPicture = petPicture;
    }

    public String getPetOwnerID() {
        return petOwnerID;
    }

    public String getPetTypeName() {
        return petTypeName;
    }

    public String getPetBreedName() {
        return petBreedName;
    }

    public String getPetName() {
        return petName;
    }

    public String getPetGender() {
        return petGender;
    }

    public String getPetDOB() {
        return petDOB;
    }

    public String getPetPicture() {
        return petPicture;
    }
}