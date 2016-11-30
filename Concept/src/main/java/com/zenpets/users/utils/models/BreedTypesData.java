package com.zenpets.users.utils.models;

import java.util.ArrayList;
import java.util.List;

public class BreedTypesData {

    private String BreedName;
    private String Description;
    private List<String> Origin = new ArrayList<>();
    private String BreedGroup;
    private String BreedType;
    private List<String> Coat = new ArrayList<>();
    private List<String> Colors = new ArrayList<>();
    private List<String> Shedding = new ArrayList<>();
    private String GroomingNeeds;
    private List<String> Temperament = new ArrayList<>();
    private String SensitivityLevel;
    private String Barking;
    private String Mouthiness;
    private String GoodForNewOwners;
    private String GoodWithKids;
    private String CatFriendly;
    private String HuntingDrive;
    private String ImpulseToWander;
    private String HypoallergenicBreed;
    private String DroolingTendency;

    public BreedTypesData() {
    }

    public BreedTypesData(String breedName, String description, List<String> origin, String breedGroup, String breedType, List<String> coat, List<String> colors, List<String> shedding, String groomingNeeds, List<String> temperament, String sensitivityLevel, String barking, String mouthiness, String goodForNewOwners, String goodWithKids, String catFriendly, String huntingDrive, String impulseToWander, String hypoallergenicBreed, String droolingTendency) {
        BreedName = breedName;
        Description = description;
        Origin = origin;
        BreedGroup = breedGroup;
        BreedType = breedType;
        Coat = coat;
        Colors = colors;
        Shedding = shedding;
        GroomingNeeds = groomingNeeds;
        Temperament = temperament;
        SensitivityLevel = sensitivityLevel;
        Barking = barking;
        Mouthiness = mouthiness;
        GoodForNewOwners = goodForNewOwners;
        GoodWithKids = goodWithKids;
        CatFriendly = catFriendly;
        HuntingDrive = huntingDrive;
        ImpulseToWander = impulseToWander;
        HypoallergenicBreed = hypoallergenicBreed;
        DroolingTendency = droolingTendency;
    }

    public String getBreedName() {
        return BreedName;
    }

    public String getDescription() {
        return Description;
    }

    public List<String> getOrigin() {
        return Origin;
    }

    public String getBreedGroup() {
        return BreedGroup;
    }

    public String getBreedType() {
        return BreedType;
    }

    public List<String> getCoat() {
        return Coat;
    }

    public List<String> getColors() {
        return Colors;
    }

    public List<String> getShedding() {
        return Shedding;
    }

    public String getGroomingNeeds() {
        return GroomingNeeds;
    }

    public List<String> getTemperament() {
        return Temperament;
    }

    public String getSensitivityLevel() {
        return SensitivityLevel;
    }

    public String getBarking() {
        return Barking;
    }

    public String getMouthiness() {
        return Mouthiness;
    }

    public String getGoodForNewOwners() {
        return GoodForNewOwners;
    }

    public String getGoodWithKids() {
        return GoodWithKids;
    }

    public String getCatFriendly() {
        return CatFriendly;
    }

    public String getHuntingDrive() {
        return HuntingDrive;
    }

    public String getImpulseToWander() {
        return ImpulseToWander;
    }

    public String getHypoallergenicBreed() {
        return HypoallergenicBreed;
    }

    public String getDroolingTendency() {
        return DroolingTendency;
    }
}