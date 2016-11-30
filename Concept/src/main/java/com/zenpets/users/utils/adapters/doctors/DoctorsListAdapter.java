package com.zenpets.users.utils.adapters.doctors;

import android.app.Activity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.zenpets.users.R;
import com.zenpets.users.utils.models.DoctorsData;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorsListAdapter extends RecyclerView.Adapter<DoctorsListAdapter.DoctorsVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private ArrayList<DoctorsData> arrDoctors;

    public DoctorsListAdapter(Activity activity, ArrayList<DoctorsData> arrDoctors) {

        /** CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrDoctors = arrDoctors;
    }

    @Override
    public int getItemCount() {
        return arrDoctors.size();
    }

    @Override
    public void onBindViewHolder(DoctorsVH holder, final int position) {
        DoctorsData data = arrDoctors.get(position);

        /** SET THE DOCTOR'S NAME **/
        holder.txtDoctorName.setText(data.getDoctorName());

        /** SET THE CLINIC ADDRESS **/
        holder.txtClinicAddress.setText(data.getClinicAddress());

        /** SET THE CHARGES **/
        holder.txtDoctorCharges.setText(data.getDoctorCharges());

        /** SET THE DISTANCE **/
        holder.txtDoctorDistance.setText(data.getClinicDistance());

        /** SET THE EXPERIENCE **/
        holder.txtDoctorExp.setText(data.getDoctorExperience());

        /** SET THE LIKES **/
        holder.txtDoctorLikes.setText(data.getDoctorLikes());

        /** SET THE REVIEWS **/
        holder.txtDoctorReviews.setText(data.getDoctorReviews());

        /** SET THE DOCTOR'S PROFILE **/
        Picasso.with(activity)
                .load(data.getDoctorProfile())
                .centerInside()
                .resize(1024, 768)
                .into(holder.imgvwDoctorProfile);
    }

    @Override
    public DoctorsVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.services_doctors_item, parent, false);

        return new DoctorsVH(itemView);
    }

    class DoctorsVH extends RecyclerView.ViewHolder   {

        LinearLayout linlaDoctorContainer;
        CircleImageView imgvwDoctorProfile;
        AppCompatTextView txtDoctorCharges;
        AppCompatTextView txtDoctorName;
        AppCompatTextView txtClinicAddress;
        LinearLayout linlaDoctorExp;
        AppCompatTextView txtDoctorExp;
        LinearLayout linlaDoctorLikes;
        AppCompatTextView txtDoctorLikes;
        LinearLayout linlaDoctorReviews;
        AppCompatTextView txtDoctorReviews;
        LinearLayout linlaDoctorDistance;
        AppCompatTextView txtDoctorDistance;

        DoctorsVH(View v) {
            super(v);

            linlaDoctorContainer = (LinearLayout) v.findViewById(R.id.linlaDoctorContainer);
            imgvwDoctorProfile = (CircleImageView) v.findViewById(R.id.imgvwDoctorProfile);
            txtDoctorCharges = (AppCompatTextView) v.findViewById(R.id.txtDoctorCharges);
            txtDoctorName = (AppCompatTextView) v.findViewById(R.id.txtDoctorName);
            txtClinicAddress = (AppCompatTextView) v.findViewById(R.id.txtClinicAddress);
            linlaDoctorExp = (LinearLayout) v.findViewById(R.id.linlaDoctorExp);
            txtDoctorExp = (AppCompatTextView) v.findViewById(R.id.txtDoctorExp);
            linlaDoctorLikes = (LinearLayout) v.findViewById(R.id.linlaDoctorLikes);
            txtDoctorLikes = (AppCompatTextView) v.findViewById(R.id.txtDoctorLikes);
            linlaDoctorReviews = (LinearLayout) v.findViewById(R.id.linlaDoctorReviews);
            txtDoctorReviews = (AppCompatTextView) v.findViewById(R.id.txtDoctorReviews);
            linlaDoctorDistance = (LinearLayout) v.findViewById(R.id.linlaDoctorDistance);
            txtDoctorDistance = (AppCompatTextView) v.findViewById(R.id.txtDoctorDistance);
        }
    }
}