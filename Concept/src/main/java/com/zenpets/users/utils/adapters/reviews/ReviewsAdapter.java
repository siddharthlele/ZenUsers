package com.zenpets.users.utils.adapters.reviews;

import android.app.Activity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zenpets.users.R;
import com.zenpets.users.utils.models.reviews.ReviewsData;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private ArrayList<ReviewsData> arrReviews;

    public ReviewsAdapter(Activity activity, ArrayList<ReviewsData> arrReviews) {

        /** CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrReviews = arrReviews;
    }

    @Override
    public int getItemCount() {
        return arrReviews.size();
    }

    @Override
    public void onBindViewHolder(ReviewsVH holder, final int position) {
        ReviewsData data = arrReviews.get(position);

        /** SET THE VISIT REASON **/
        String visitReason = data.getVisitReason();
        if (visitReason != null)	{
            holder.txtVisitReason.setText(visitReason);
        }

        /** SET THE DOCTOR EXPERIENCE **/
        if (data.getDoctorExperience() != null)  {
            holder.txtVisitExperience.setText(data.getDoctorExperience());
        }

        /** SET THE USER NAME **/
        if (data.getUserName() != null) {
            holder.txtUserName.setText(data.getUserName());
        }
    }

    @Override
    public ReviewsVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.doctor_profile_reviews_item, parent, false);

        return new ReviewsVH(itemView);
    }

    class ReviewsVH extends RecyclerView.ViewHolder   {
        AppCompatTextView txtVisitReason;
        AppCompatTextView txtVisitExperience;
        AppCompatTextView txtUserName;
        AppCompatTextView txtPostedAgo;

        ReviewsVH(View v) {
            super(v);
            txtVisitReason = (AppCompatTextView) v.findViewById(R.id.txtVisitReason);
            txtVisitExperience = (AppCompatTextView) v.findViewById(R.id.txtVisitExperience);
            txtUserName = (AppCompatTextView) v.findViewById(R.id.txtUserName);
            txtPostedAgo = (AppCompatTextView) v.findViewById(R.id.txtPostedAgo);
        }
    }
}