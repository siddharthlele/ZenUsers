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
import com.zenpets.users.utils.models.VaccinesData;

import java.util.ArrayList;

public class VaccineTypesSpinnerAdapter extends ArrayAdapter<VaccinesData> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** LAYOUT INFLATER TO USE A CUSTOM LAYOUT *****/
    private LayoutInflater inflater = null;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<VaccinesData> arrVaccineType;

    public VaccineTypesSpinnerAdapter(Activity activity, ArrayList<VaccinesData> arrVaccineType) {
        super(activity, R.layout.vaccine_types_row);

        /** CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrVaccineType = arrVaccineType;

        /** INSTANTIATE THE LAYOUT INFLATER **/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrVaccineType.size();
    }

    @Override
    public VaccinesData getItem(int position) {
        return arrVaccineType.get(position);
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
            vi = inflater.inflate(R.layout.vaccine_types_dropdown_row, parent, false);

            /** INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /*****	CAST THE LAYOUT ELEMENTS	*****/
            holder.txtVaccineName = (AppCompatTextView) vi.findViewById(R.id.txtVaccineName);
            holder.txtVaccineName.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTW1G-Cn.otf"));

            /** SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /** CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /** SET THE VACCINE TYPE NAME **/
        String strVaccineName = arrVaccineType.get(position).getVaccineName();
        if (strVaccineName != null)	{
            holder.txtVaccineName.setText(strVaccineName);
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
            vi = inflater.inflate(R.layout.vaccine_types_row, parent, false);

            /** INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /*****	CAST THE LAYOUT ELEMENTS	*****/
            holder.txtVaccineName = (AppCompatTextView) vi.findViewById(R.id.txtVaccineName);
            holder.txtVaccineName.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTW1G-Cn.otf"));

            /** SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /** CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /** SET THE VACCINE TYPE NAME **/
        String strVaccineName = arrVaccineType.get(position).getVaccineName();
        if (strVaccineName != null)	{
            holder.txtVaccineName.setText(strVaccineName);
        }

        return vi;
    }

    private static class ViewHolder	{
        AppCompatTextView txtVaccineName;
    }
}