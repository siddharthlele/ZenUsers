package com.zenpets.users.details.doctor.review;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.iconics.view.IconicsImageView;
import com.zenpets.users.R;
import com.zenpets.users.utils.models.reviews.ReviewsData;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DoctorReviewsActivity extends AppCompatActivity {

    /** THE DOCTOR ID **/
    private String DOCTOR_ID = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.listDoctorReviews) RecyclerView listDoctorReviews;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_all_reviews_list);
        ButterKnife.bind(this);

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /** GET THE INCOMING DATA **/
        getIncomingData();
    }

    /** GET THE DOCTOR'S REVIEWS **/
    private void getDoctorReviews() {
        DatabaseReference refReviews = FirebaseDatabase.getInstance().getReference().child("Doctors").child(DOCTOR_ID).child("Reviews");
//        Log.e("REFERENCE", String.valueOf(refReviews));

        /** SETUP THE FIREBASE RECYCLER ADAPTER **/
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<ReviewsData, ReviewsVH>
                (ReviewsData.class, R.layout.custom_all_reviews_item, ReviewsVH.class, refReviews) {
            @Override
            protected void populateViewHolder(final ReviewsVH viewHolder, ReviewsData model, int position) {
                if (model != null)  {

                    /** SET THE RECOMMEND STATUS **/
                    String strRecommendStatus = model.getRecommendStatus();
                    if (strRecommendStatus.equalsIgnoreCase("Yes")) {
                        viewHolder.imgvwLikeStatus.setIcon("faw-thumbs-o-up");
                        viewHolder.imgvwLikeStatus.setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_blue_dark));
                    } else if (strRecommendStatus.equalsIgnoreCase("No"))   {
                        viewHolder.imgvwLikeStatus.setIcon("faw-thumbs-o-down");
                        viewHolder.imgvwLikeStatus.setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
                    }

                    /** GET THE USER KEY AND THE USER NAME **/
                    String strUserKey = model.getUserKey();
//                    Log.e("USER KEY", strUserKey);
                    DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(strUserKey);
//                    Log.e("REFERENCE", String.valueOf(refUser));
                    refUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            /** GET THE USER NAME **/
                            String userName = dataSnapshot.child("userName").getValue(String.class);
                            viewHolder.txtUserName.setText(userName);

                            /** GET THE FIRST CHARACTER OF THE USER NAME **/
                            String strFirstChar = userName.substring(0, 1);
                            viewHolder.txtNameStart.setText(strFirstChar);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    /** SET THE VISIT REASON **/
                    viewHolder.txtVisitReason.setText(model.getVisitReason());

                    /** SET THE VISIT EXPERIENCE **/
                    viewHolder.txtVisitExperience.setText(model.getDoctorExperience());
                }
            }
        };

        refReviews.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHideReviews(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHideReviews(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHideReviews(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHideReviews(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        refReviews.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHideReviews(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        /** SET THE ADAPTER **/
        listDoctorReviews.setAdapter(adapter);
    }

    /** THE REVIEWS VIEW HOLDER **/
    private static class ReviewsVH extends RecyclerView.ViewHolder {
        AppCompatTextView txtNameStart;
        AppCompatTextView txtUserName;
        AppCompatTextView txtTimeStamp;
        IconicsImageView imgvwLikeStatus;
        AppCompatTextView txtVisitReason;
        AppCompatTextView txtVisitExperience;
        AppCompatTextView txtHelpfulNo;
        AppCompatTextView txtHelpfulYes;

        public ReviewsVH(View itemView) {
            super(itemView);
            txtNameStart = (AppCompatTextView) itemView.findViewById(R.id.txtNameStart);
            txtUserName = (AppCompatTextView) itemView.findViewById(R.id.txtUserName);
            txtTimeStamp = (AppCompatTextView) itemView.findViewById(R.id.txtTimeStamp);
            imgvwLikeStatus = (IconicsImageView) itemView.findViewById(R.id.imgvwLikeStatus);
            txtVisitReason = (AppCompatTextView) itemView.findViewById(R.id.txtVisitReason);
            txtVisitExperience = (AppCompatTextView) itemView.findViewById(R.id.txtVisitExperience);
            txtHelpfulNo = (AppCompatTextView) itemView.findViewById(R.id.txtHelpfulNo);
            txtHelpfulYes = (AppCompatTextView) itemView.findViewById(R.id.txtHelpfulYes);
        }
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("DOCTOR_ID")) {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            if (DOCTOR_ID != null)  {
                /** GET THE DOCTOR'S REVIEWS **/
                getDoctorReviews();
//                Log.e("DOCTOR ID", DOCTOR_ID);
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required information", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required information", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void emptyShowOrHideReviews(DataSnapshot dataSnapshot)  {
        if (dataSnapshot.hasChildren()) {
            /* SHOW THE RECYCLER VIEW CONTAINER */
            listDoctorReviews.setVisibility(View.VISIBLE);
            linlaEmpty.setVisibility(View.GONE);
        } else {
            /* SHOW THE NO REVIEWS CONTAINER */
            linlaEmpty.setVisibility(View.VISIBLE);
            listDoctorReviews.setVisibility(View.GONE);
        }
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager llmReviews = new LinearLayoutManager(this);
        llmReviews.setOrientation(LinearLayoutManager.VERTICAL);
        llmReviews.setAutoMeasureEnabled(true);
        listDoctorReviews.setLayoutManager(llmReviews);
        listDoctorReviews.setHasFixedSize(false);
        listDoctorReviews.setNestedScrollingEnabled(false);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}