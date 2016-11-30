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
import com.zenpets.users.utils.models.PetTypesData;

import java.util.ArrayList;

public class PetTypeSpinnerAdapter extends ArrayAdapter<PetTypesData> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** LAYOUT INFLATER TO USE A CUSTOM LAYOUT *****/
    private LayoutInflater inflater = null;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<PetTypesData> arrPetType;

    public PetTypeSpinnerAdapter(Activity activity, ArrayList<PetTypesData> arrPetType) {
        super(activity, R.layout.pet_types_row);

        /** CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrPetType = arrPetType;

        /** INSTANTIATE THE LAYOUT INFLATER **/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrPetType.size();
    }

    @Override
    public PetTypesData getItem(int position) {
        return arrPetType.get(position);
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
            vi = inflater.inflate(R.layout.pet_types_dropdown_row, parent, false);

            /** INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /*****	CAST THE LAYOUT ELEMENTS	*****/
            holder.txtPetType = (AppCompatTextView) vi.findViewById(R.id.txtPetType);
            holder.txtPetType.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTW1G-Cn.otf"));

            /** SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /** CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /** SET THE PET TYPE **/
        String strPetType = arrPetType.get(position).getPetTypeName();
        if (strPetType != null)	{
            holder.txtPetType.setText(strPetType);
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
            vi = inflater.inflate(R.layout.pet_types_row, parent, false);

            /** INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /*****	CAST THE LAYOUT ELEMENTS	*****/
            holder.txtPetType = (AppCompatTextView) vi.findViewById(R.id.txtPetType);
            holder.txtPetType.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTW1G-Cn.otf"));

            /** SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /** CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /** SET THE PET TYPE **/
        String strPetType = arrPetType.get(position).getPetTypeName();
        if (strPetType != null)	{
            holder.txtPetType.setText(strPetType);
        }

        return vi;
    }

    private static class ViewHolder	{
        AppCompatTextView txtPetType;
    }
}