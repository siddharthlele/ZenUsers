package com.zenpets.users.details.doctor.review;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zenpets.users.R;
import com.zenpets.users.utils.models.reviews.ReviewsData;
import com.zenpets.users.utils.models.services.doctors.DoctorsData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReviewCreatorActivity extends AppCompatActivity {

    /** THE USER ID **/
    private String USER_ID = null;
    private String USER_KEY = null;
    private String USER_NAME = null;

    /** THE TIMESTAMP **/
    private String TIME_STAMP = null;

    /** THE DOCTOR ID **/
    private String DOCTOR_ID = null;

    /** DATA TYPES TO HOLD THE USER SELECTIONS **/
    private String RECOMMEND_STATUS = "Yes";
    private String APPOINTMENT_STATUS = "On Time";
    private int DOCTOR_RATING = 0;
    private String VISIT_REASON = null;
    private String DOCTOR_EXPERIENCE = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwDoctorProfile) CircleImageView imgvwDoctorProfile;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.scrollContainer) ScrollView scrollContainer;
    @BindView(R.id.groupRecommend) SegmentedButtonGroup groupRecommend;
    @BindView(R.id.groupStartTime) SegmentedButtonGroup groupStartTime;
    @BindView(R.id.linlaRating) LinearLayout linlaRating;
    @BindView(R.id.ratingOverallExperience) AppCompatRatingBar ratingOverallExperience;
    @BindView(R.id.edtTreatment)AppCompatEditText edtTreatment;
    @BindView(R.id.edtExperience) AppCompatEditText edtExperience;
    @BindView(R.id.txtTermsOfService) AppCompatTextView txtTermsOfService;

    /** CANCEL THE REVIEW **/
    @OnClick(R.id.btnFeedbackExit) void cancelFeedback()    {
        finish();
    }

    /** GET THE REASON FOR VISITING THE VET **/
    @OnClick(R.id.edtTreatment) void getReason()    {
        Intent intent = new Intent(this, VisitReasonActivity.class);
        startActivityForResult(intent, 101);
    }

    /** SUBMIT THE REVIEW **/
    @OnClick(R.id.btnSubmit) void submitFeedback()  {
        /** HIDE THE KEYBOARD **/

        /** GET THE DATA **/
        DOCTOR_EXPERIENCE = edtExperience.getText().toString().trim();

        /** VERIFY ALL REQUIRED DATA **/
        if (TextUtils.isEmpty(RECOMMEND_STATUS))    {
            groupRecommend.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.error_background));
            scrollContainer.smoothScrollTo(0, groupRecommend.getTop());
        } else if (TextUtils.isEmpty(APPOINTMENT_STATUS))   {
            groupStartTime.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.error_background));
            scrollContainer.smoothScrollTo(0, groupStartTime.getTop());
        } else if (DOCTOR_RATING == 0)  {
            ratingOverallExperience.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.error_background));
            scrollContainer.smoothScrollTo(0, ratingOverallExperience.getTop());
        } else if (TextUtils.isEmpty(VISIT_REASON)) {
            edtTreatment.setError("Select the reason for the visit");
            edtTreatment.requestFocus();
        } else if (TextUtils.isEmpty(DOCTOR_EXPERIENCE))    {
            edtExperience.setError("Please provide your experience");
            edtExperience.requestFocus();
        } else {
            edtTreatment.setError(null);
            edtExperience.setError(null);

            /** POST THE REVIEW **/
            postReview();
        }
    }

    /** POST THE REVIEW **/
    private void postReview() {
        final DatabaseReference refDoctor = FirebaseDatabase.getInstance().getReference().child("Doctors").child(DOCTOR_ID).child("Reviews");

        /** COLLECT THE DATA **/
        ReviewsData data = new ReviewsData(RECOMMEND_STATUS, APPOINTMENT_STATUS, DOCTOR_RATING, VISIT_REASON, DOCTOR_EXPERIENCE, USER_KEY, USER_ID, USER_NAME, TIME_STAMP);

        /** POST THE REVIEW DATA **/
        refDoctor.push().setValue(data, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                if (databaseError != null)  {
                    Toast.makeText(
                            getApplicationContext(),
                            "Failed to submit your review. Please post again.",
                            Toast.LENGTH_LONG).show();
                } else {
                    /** POST THE REFERENCE IN THE USERS RECORD **/
                    String reviewID = reference.getKey();
                    DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(USER_KEY).child("Reviews").push();
                    refUser.child("reviewID").setValue(reviewID);
                    refUser.child("doctorID").setValue(DOCTOR_ID);

                    Toast.makeText(getApplicationContext(), "Review added", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_creator_activity);
        ButterKnife.bind(this);

        /** GET THE USER DETAILS **/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            USER_ID = user.getUid();
            if (TextUtils.isEmpty(USER_ID))    {
                Toast.makeText(getApplicationContext(), "Failed to get required data....", Toast.LENGTH_SHORT).show();
                finish();
            } else {

                /* GET THE TIME STAMP */
                Long aLong = System.currentTimeMillis() / 1000;
                TIME_STAMP = String.valueOf(aLong);

                /* GET THE USER NAME */
                USER_NAME = user.getDisplayName();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                Query query = reference.orderByChild("userID").equalTo(USER_ID);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            /* GET THE USER KEY */
                            USER_KEY = postSnapshot.getKey();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required data....", Toast.LENGTH_SHORT).show();
            finish();
        }

        /** GET THE INCOMING DATA **/
        getIncomingData();

        /** CHECK THE RECOMMENDATION STATUS **/
        groupRecommend.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                if (position == 0)  {
                    RECOMMEND_STATUS = "Yes";
                } else if (position == 1)   {
                    RECOMMEND_STATUS = "No";
                }
            }
        });

        /** CHECK THE APPOINTMENT START STATUS **/
        groupStartTime.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                if (position == 0)  {
                    APPOINTMENT_STATUS = "On Time";
                } else if (position == 1)   {
                    APPOINTMENT_STATUS = "Ten Minutes Late";
                } else if (position == 2)   {
                    APPOINTMENT_STATUS = "30 Minutes Late";
                } else if (position == 3)   {
                    APPOINTMENT_STATUS = "More Than An Hour late";
                }
            }
        });

        /** CHANGE THE RATING **/
        ratingOverallExperience.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                DOCTOR_RATING = (int) rating;
            }
        });
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("DOCTOR_ID")) {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
//            Log.e("DOCTOR ID", DOCTOR_ID);
            if (TextUtils.isEmpty(DOCTOR_ID))  {
                Toast.makeText(getApplicationContext(), "Failed to get required data....", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                /** GET THE DOCTOR DETAILS **/
                getDoctorDetails();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required data....", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /** GET THE DOCTOR DETAILS **/
    private void getDoctorDetails() {
        DatabaseReference refDoctor = FirebaseDatabase.getInstance().getReference().child("Doctors").child(DOCTOR_ID);
        refDoctor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    DoctorsData doctors = dataSnapshot.getValue(DoctorsData.class);

                    /* SET THE DOCTORS NAMES */
                    String DOCTOR_PREFIX = doctors.getDoctorPrefix();
                    String DOCTOR_NAME = doctors.getDoctorName();
                    txtDoctorName.setText(DOCTOR_PREFIX + " " + DOCTOR_NAME);

                    /* SET THE DOCTOR'S PROFILE */
                    String DOCTOR_PROFILE = doctors.getDoctorProfile();
                    Glide.with(getApplicationContext())
                            .load(DOCTOR_PROFILE)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .centerCrop()
                            .into(imgvwDoctorProfile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK)  {
            Bundle bundle = data.getExtras();
            VISIT_REASON = bundle.getString("REASON");
            if (!TextUtils.isEmpty(VISIT_REASON))    {
                edtTreatment.setText(VISIT_REASON);
                edtTreatment.setSelection(edtTreatment.getText().length());
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}