package com.zenpets.users.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.zenpets.users.R;
import com.zenpets.users.utils.models.PetData;

import java.util.ArrayList;

public class UserPetsSpinnerAdapter extends ArrayAdapter<PetData> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** LAYOUT INFLATER TO USE A CUSTOM LAYOUT *****/
    private LayoutInflater inflater = null;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<PetData> arrPets;

    public UserPetsSpinnerAdapter(Activity activity, ArrayList<PetData> arrPets) {
        super(activity, R.layout.vaccine_types_row);

        /** CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrPets = arrPets;

        /** INSTANTIATE THE LAYOUT INFLATER **/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrPets.size();
    }

    @Override
    public PetData getItem(int position) {
        return arrPets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {

        /** A VIEW HOLDER INSTANCE **/
        ViewHolder holder;

        /** CAST THE CONVERT VIEW IN A VIEW INSTANCE **/
        View vi = convertView;

        /** CHECK CONVERT VIEW STATUS **/
        if (convertView == null)	{
            /** CAST THE CONVERT VIEW INTO THE VIEW INSTANCE vi **/
            vi = inflater.inflate(R.layout.user_pets_dropdown_row, parent, false);

            /** INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /*****	CAST THE LAYOUT ELEMENTS	*****/
            holder.txtPetName = (AppCompatTextView) vi.findViewById(R.id.txtPetName);
            holder.txtPetName.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTW1G-Cn.otf"));

            /** SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /** CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /** SET THE PET NAME **/
        String strPetName = arrPets.get(position).getPetName();
        if (strPetName != null)	{
            holder.txtPetName.setText(strPetName);
        }

        return vi;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        /** A VIEW HOLDER INSTANCE **/
        ViewHolder holder;

        /** CAST THE CONVERT VIEW IN A VIEW INSTANCE **/
        View vi = convertView;

        /** CHECK CONVERT VIEW STATUS **/
        if (convertView == null)	{
            /** CAST THE CONVERT VIEW INTO THE VIEW INSTANCE vi **/
            vi = inflater.inflate(R.layout.user_pets_row, parent, false);

            /** INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /*****	CAST THE LAYOUT ELEMENTS	*****/
            holder.txtPetName = (AppCompatTextView) vi.findViewById(R.id.txtPetName);
            holder.txtPetName.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTW1G-Cn.otf"));

            /** SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /** CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /** SET THE PET NAME **/
        String strPetName = arrPets.get(position).getPetName();
        if (strPetName != null)	{
            holder.txtPetName.setText(strPetName);
        }

        return vi;
    }

    private static class ViewHolder	{
        AppCompatTextView txtPetName;
    }
}