package com.zenpets.users.review;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Toast;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReviewCreatorActivity extends AppCompatActivity {

    /** THE USER ID **/
    private String USER_ID = null;
    private String USER_KEY = null;

    /** THE DOCTOR ID **/
    String DOCTOR_ID = null;

    /** DATA TYPES TO HOLD THE USER SELECTIONS **/
    private String RECOMMEND_STATUS = null;
    private String APPOINTMENT_STATUS = null;
    private int DOCTOR_RATING = 0;
    private String VISIT_REASON = null;
    private String DOCTOR_EXPERIENCE = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwDoctorProfile) CircleImageView imgvwDoctorProfile;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.scrollContainer) ScrollView scrollContainer;
    @BindView(R.id.groupRecommend) RadioGroup groupRecommend;
    @BindView(R.id.btnYes) AppCompatRadioButton btnYes;
    @BindView(R.id.btnNo) AppCompatRadioButton btnNo;
    @BindView(R.id.groupStartTime) RadioGroup groupStartTime;
    @BindView(R.id.btnOnTime) AppCompatRadioButton btnOnTime;
    @BindView(R.id.btnTenLate) AppCompatRadioButton btnTenLate;
    @BindView(R.id.btnHalfHourLate) AppCompatRadioButton btnHalfHourLate;
    @BindView(R.id.btnHourLate) AppCompatRadioButton btnHourLate;
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
        ReviewsData data = new ReviewsData(RECOMMEND_STATUS, APPOINTMENT_STATUS, DOCTOR_RATING, VISIT_REASON, DOCTOR_EXPERIENCE, USER_KEY);

        /** POST THE REVIEW DATA **/
        refDoctor.push().setValue(data, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                if (databaseError != null)  {
                    Toast.makeText(
                            getApplicationContext(),
                            "Failed to add your review. Please submit again",
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
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                Query query = reference.orderByChild("userID").equalTo(USER_ID);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            USER_KEY = postSnapshot.getKey();
//                            Log.e("USER KEY", USER_KEY);
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
        groupRecommend.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.btnYes:
                        /** SET RECOMMEND STATUS TO YES **/
                        RECOMMEND_STATUS = "Yes";
                        break;
                    case R.id.btnNo:
                        /** SET RECOMMEND STATUS TO NO **/
                        RECOMMEND_STATUS = "No";
                        break;
                    default:
                        break;
                }
            }
        });

        /** CHECK THE APPOINTMENT START STATUS **/
        groupStartTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.btnOnTime:
                        /** SET APPOINTMENT STATUS TO YES **/
                        APPOINTMENT_STATUS = "On Time";
                        break;
                    case R.id.btnTenLate:
                        /** SET APPOINTMENT STATUS TO NO **/
                        APPOINTMENT_STATUS = "Ten Minutes Late";
                        break;
                    case R.id.btnHalfHourLate:
                        /** SET APPOINTMENT STATUS TO YES **/
                        APPOINTMENT_STATUS = "30 Minutes Late";
                        break;
                    case R.id.btnHourLate:
                        /** SET APPOINTMENT STATUS TO NO **/
                        APPOINTMENT_STATUS = "More Than An Hour late";
                        break;
                    default:
                        break;
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
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required data....", Toast.LENGTH_SHORT).show();
            finish();
        }
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