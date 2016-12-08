package com.zenpets.users.utils.adapters.doctors;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zenpets.users.R;
import com.zenpets.users.doctors.DoctorGalleryActivity;

import java.util.List;

public class ClinicImagesAdapter extends RecyclerView.Adapter<ClinicImagesAdapter.VaccinationsVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private List<String> arrImages;

    /** THE STRING ARRAY **/
    private String[] strImages;

    /** THE CLINIC ID **/
    private String CLINIC_ID;

    public ClinicImagesAdapter(Activity activity, List<String> arrImages, String CLINIC_ID) {

        /** CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrImages = arrImages;

        /** CONVERT THE LIST ARRAY TO A STRING ARRAY **/
        strImages = arrImages.toArray(new String[arrImages.size()]);

        /** GET THE CLINIC ID **/
        this.CLINIC_ID = CLINIC_ID;
    }

    @Override
    public int getItemCount() {
        return arrImages.size();
    }

    @Override
    public void onBindViewHolder(ClinicImagesAdapter.VaccinationsVH holder, final int position) {
        /** SET THE CLINIC IMAGE **/
        String strClinicImage = arrImages.get(position);
        Glide.with(activity)
                .load(strClinicImage)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .into(holder.imgvwClinicImage);

        /** SHOW THE FULL SIZE CLINIC IMAGE**/
        holder.imgvwClinicImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, DoctorGalleryActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("array", strImages);
                intent.putExtra("clinicID", CLINIC_ID);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public ClinicImagesAdapter.VaccinationsVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.doctor_clinic_images_item, parent, false);

        return new ClinicImagesAdapter.VaccinationsVH(itemView);
    }

    class VaccinationsVH extends RecyclerView.ViewHolder   {

        AppCompatImageView imgvwClinicImage;

        VaccinationsVH(View v) {
            super(v);

            imgvwClinicImage = (AppCompatImageView) v.findViewById(R.id.imgvwClinicImage);
        }
    }
}