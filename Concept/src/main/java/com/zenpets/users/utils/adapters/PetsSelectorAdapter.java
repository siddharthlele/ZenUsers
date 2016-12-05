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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zenpets.users.R;
import com.zenpets.users.utils.models.PetData;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetsSelectorAdapter extends ArrayAdapter<PetData> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private final Activity activity;

    /***** LAYOUT INFLATER TO USE A CUSTOM LAYOUT *****/
    private LayoutInflater inflater = null;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private final ArrayList<PetData> arrMyPets;

    public PetsSelectorAdapter(Activity activity, ArrayList<PetData> arrMyPets) {
        super(activity, R.layout.breeds_row);

        /** CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrMyPets = arrMyPets;

        /** INSTANTIATE THE LAYOUT INFLATER **/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrMyPets.size();
    }

    @Override
    public PetData getItem(int position) {
        return arrMyPets.get(position);
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
            vi = inflater.inflate(R.layout.pet_selector_dropdown_row, parent, false);

            /** INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /*****	CAST THE LAYOUT ELEMENTS	*****/
            holder.imgvwPetPicture = (CircleImageView) vi.findViewById(R.id.imgvwPetPicture);
            holder.txtPetName = (AppCompatTextView) vi.findViewById(R.id.txtPetName);
            holder.txtPetName.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTW1G-Cn.otf"));

            /** SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /** CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /** SET THE PET NAME **/
        String strPetName = arrMyPets.get(position).getPetName();
        if (strPetName != null)	{
            holder.txtPetName.setText(strPetName);
        }

        /** SET THE PET PROFILE PICTURE **/
        String strPetPicture = arrMyPets.get(position).getPetPicture();
        Glide.with(activity)
                .load(strPetPicture)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .into(holder.imgvwPetPicture);

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
            vi = inflater.inflate(R.layout.pet_selector_row, parent, false);

            /** INSTANTIATE THE VIEW HOLDER INSTANCE **/
            holder = new ViewHolder();

            /*****	CAST THE LAYOUT ELEMENTS	*****/
            holder.imgvwPetPicture = (CircleImageView) vi.findViewById(R.id.imgvwPetPicture);
            holder.txtPetName = (AppCompatTextView) vi.findViewById(R.id.txtPetName);
            holder.txtPetName.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/HelveticaNeueLTW1G-Cn.otf"));

            /** SET THE TAG TO "vi" **/
            vi.setTag(holder);
        } else {
            /** CAST THE VIEW HOLDER INSTANCE **/
            holder = (ViewHolder) vi.getTag();
        }

        /** SET THE PET NAME **/
        String strPetName = arrMyPets.get(position).getPetName();
        if (strPetName != null)	{
            holder.txtPetName.setText(strPetName);
        }

        /** SET THE PET PROFILE PICTURE **/
        String strPetPicture = arrMyPets.get(position).getPetPicture();
        Glide.with(activity)
                .load(strPetPicture)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .into(holder.imgvwPetPicture);

        return vi;
    }

    private static class ViewHolder	{
        CircleImageView imgvwPetPicture;
        AppCompatTextView txtPetName;
    }
}