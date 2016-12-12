package com.zenpets.users.utils.helpers;

import android.app.Activity;

import com.zenpets.users.utils.models.location.StatesData;

import java.util.ArrayList;

public class StatesArrayCreator {

    private Activity activity;

    public StatesArrayCreator(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<StatesData> generateStatesArray() {

        ArrayList<StatesData> arrStates = new ArrayList<>();

        /** ADD THE LIST OF STATES **/
        StatesData d1 = new StatesData();
        d1.setStateName("Andhra Pradesh");
        d1.setStateID("1");
        arrStates.add(d1);

        StatesData d2 = new StatesData();
        d2.setStateName("Arunachal Pradesh");
        d2.setStateID("2");
        arrStates.add(d2);

        StatesData d3 = new StatesData();
        d3.setStateName("Assam");
        d3.setStateID("3");
        arrStates.add(d3);

        StatesData d4 = new StatesData();
        d4.setStateName("Bihar");
        d4.setStateID("4");
        arrStates.add(d4);

        StatesData d5 = new StatesData();
        d5.setStateName("Chhattisgarh");
        d5.setStateID("5");
        arrStates.add(d5);

        StatesData d6 = new StatesData();
        d6.setStateName("Goa");
        d6.setStateID("6");
        arrStates.add(d6);

        StatesData d7 = new StatesData();
        d7.setStateName("Gujarat");
        d7.setStateID("7");
        arrStates.add(d7);

        StatesData d8 = new StatesData();
        d8.setStateName("Haryana");
        d8.setStateID("8");
        arrStates.add(d8);

        StatesData d9 = new StatesData();
        d9.setStateName("Himachal Pradesh");
        d9.setStateID("9");
        arrStates.add(d9);

        StatesData d10 = new StatesData();
        d10.setStateName("Jammu and Kashmir");
        d10.setStateID("10");
        arrStates.add(d10);

        StatesData d11 = new StatesData();
        d11.setStateName("Jharkhand");
        d11.setStateID("11");
        arrStates.add(d11);

        StatesData d12 = new StatesData();
        d12.setStateName("Karnataka");
        d12.setStateID("12");
        arrStates.add(d12);

        StatesData d13 = new StatesData();
        d13.setStateName("Kerala");
        d13.setStateID("13");
        arrStates.add(d13);

        StatesData d14 = new StatesData();
        d14.setStateName("Madhya Pradesh");
        d14.setStateID("14");
        arrStates.add(d14);

        StatesData d15 = new StatesData();
        d15.setStateName("Maharashtra");
        d15.setStateID("15");
        arrStates.add(d15);

        StatesData d16 = new StatesData();
        d16.setStateName("Manipur");
        d16.setStateID("16");
        arrStates.add(d16);

        StatesData d17 = new StatesData();
        d17.setStateName("Meghalaya");
        d17.setStateID("17");
        arrStates.add(d17);

        StatesData d18 = new StatesData();
        d18.setStateName("Mizoram");
        d18.setStateID("18");
        arrStates.add(d18);

        StatesData d19 = new StatesData();
        d19.setStateName("Nagaland");
        d19.setStateID("19");
        arrStates.add(d19);

        StatesData d20 = new StatesData();
        d20.setStateName("Odisha");
        d20.setStateID("20");
        arrStates.add(d20);

        StatesData d21 = new StatesData();
        d21.setStateName("Punjab");
        d21.setStateID("21");
        arrStates.add(d21);

        StatesData d22 = new StatesData();
        d22.setStateName("Rajasthan");
        d22.setStateID("22");
        arrStates.add(d22);

        StatesData d23 = new StatesData();
        d23.setStateName("Sikkim");
        d23.setStateID("23");
        arrStates.add(d23);

        StatesData d24 = new StatesData();
        d24.setStateName("Tamil Nadu");
        d24.setStateID("24");
        arrStates.add(d24);

        StatesData d25 = new StatesData();
        d25.setStateName("Telangana");
        d25.setStateID("25");
        arrStates.add(d25);

        StatesData d26 = new StatesData();
        d26.setStateName("Tripura");
        d26.setStateID("26");
        arrStates.add(d26);

        StatesData d27 = new StatesData();
        d27.setStateName("Uttar Pradesh");
        d27.setStateID("27");
        arrStates.add(d27);

        StatesData d28 = new StatesData();
        d28.setStateName("Uttarakhand");
        d28.setStateID("28");
        arrStates.add(d28);

        StatesData d29 = new StatesData();
        d29.setStateName("West Bengal");
        d29.setStateID("29");
        arrStates.add(d29);

        return arrStates;
    }
}