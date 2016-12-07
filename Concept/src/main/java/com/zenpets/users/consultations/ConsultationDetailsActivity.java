package com.zenpets.users.consultations;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
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
import com.zenpets.users.utils.TypefaceSpan;
import com.zenpets.users.utils.adapters.consultations.ConsultationsDetailsAdapter;
import com.zenpets.users.utils.models.consultations.ConsultationAnswersData;
import com.zenpets.users.utils.models.consultations.ConsultationHeaderData;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConsultationDetailsActivity extends AppCompatActivity {

    /** THE USER KEY **/
    String USER_KEY = null;

    /** THE INCOMING CONSULT ID **/
    String CONSULT_ID = null;

    /** THE CONSULTATION DETAILS ADAPTER AND ARRAY LIST **/
    ConsultationAnswersData answersData;
    ConsultationHeaderData data;
    ConsultationsDetailsAdapter adapter;
    ArrayList<ConsultationAnswersData> arrAnswers = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaHeaderProgress) LinearLayout linlaHeaderProgress;
    @BindView(R.id.listAnswers) RecyclerView listAnswers;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultation_details_activity);
        ButterKnife.bind(this);

        /***** CONFIGURE THE TOOLBAR *****/
        configTB();

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /** GET THE USER DETAILS **/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String USER_ID = user.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
            Query query = reference.orderByChild("userID").equalTo(USER_ID);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        USER_KEY = postSnapshot.getKey();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            finish();
        }

        /** GET THE INCOMING DATA **/
        fetchIncomingData();
    }

    /** FETCH THE CONSULTATION DETAILS **/
    private void fetchConsultDetails() {
        final DatabaseReference refConsult = FirebaseDatabase.getInstance().getReference().child("Consultations").child(CONSULT_ID);
        refConsult.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.e("REFERENCE", String.valueOf(dataSnapshot));

                /** GET THE CONSULTATION HEADER **/
                data = new ConsultationHeaderData();

                /** SET THE CONSULT HEADER **/
                String consultHeader = dataSnapshot.child("consultHeader").getValue(String.class);
                data.setConsultHeader(consultHeader);

                /** SET THE CONSULT DESCRIPTION **/
                String consultDescription = dataSnapshot.child("consultDescription").getValue(String.class);
                data.setConsultDescription(consultDescription);

                /** SET THE TIME STAMP **/
                String timeStamp = dataSnapshot.child("timeStamp").getValue(String.class);
                long lngTimeStamp = Long.parseLong(timeStamp) * 1000;
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTimeInMillis(lngTimeStamp);
                Date date = calendar.getTime();

                PrettyTime prettyTime = new PrettyTime();
                String strDate = prettyTime.format(date);
                data.setTimeStamp(strDate);

                /** SET THE CONSULT DESCRIPTION **/
                int consultViews = dataSnapshot.child("consultViews").getValue(Integer.class);
                data.setConsultViews(consultViews);

                DatabaseReference refAnswer = FirebaseDatabase.getInstance().getReference().child("Answers");
                Query query = refAnswer.orderByChild("consultID").equalTo(CONSULT_ID);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.e("ANSWERS", String.valueOf(dataSnapshot));

                        /** AN INSTANCE OF THE CONSULTATION ANSWERS DATA CLASS **/
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            /** INSTANTIATE THE CONSULTATION ANSWERS DATA CLASS INSTANCE **/
                            answersData = new ConsultationAnswersData();

                            /** SET THE ANSWER KEY **/
                            answersData.setAnswerKey(postSnapshot.getKey());

                            /** SET THE NEXT STEPS **/
                            String consultNextSteps = postSnapshot.child("consultNextSteps").getValue(String.class);
                            answersData.setConsultNextSteps(consultNextSteps);
//                            Log.e("NEXT STEPS", consultNextSteps);

                            /** SET THE ANSWER **/
                            String consultAnswer = postSnapshot.child("consultAnswer").getValue(String.class);
                            answersData.setConsultAnswer(consultAnswer);
//                            Log.e("ANSWER", consultAnswer);

                            /** GET THE DOCTOR'S ID **/
                            String doctorID = postSnapshot.child("doctorID").getValue(String.class);
                            answersData.setDoctorID(doctorID);
//                            Log.e("DOCTOR ID", doctorID);

                            /** SET THE TIME STAMP **/
                            String timeStamp = postSnapshot.child("timeStamp").getValue(String.class);
                            long lngTimeStamp = Long.parseLong(timeStamp) * 1000;
                            Calendar calendar = Calendar.getInstance(Locale.getDefault());
                            calendar.setTimeInMillis(lngTimeStamp);
                            Date date = calendar.getTime();

                            PrettyTime prettyTime = new PrettyTime();
                            String strDate = prettyTime.format(date);
                            answersData.setTimeStamp(strDate);

                            /** GET THE HELPFUL YES COUNT **/
                            int intHelpfulYes = postSnapshot.child("helpfulYes").getValue(Integer.class);
                            answersData.setHelpfulYes(intHelpfulYes);

                            /** GET THE HELPFUL NO COUNT **/
                            int intHelpfulNo = postSnapshot.child("helpfulNo").getValue(Integer.class);
                            answersData.setHelpfulNo(intHelpfulNo);

                            /** SET THE TOTAL HELPFUL VOTES (YES + NO) **/
                            int totalVotes = intHelpfulYes + intHelpfulNo;
                            answersData.setTotalVotes(totalVotes);

                            /** ADD THE USER KEY **/
                            answersData.setUserKey(USER_KEY);

                            /** ADD THE COLLECTED DATA TO THE ARRAY LIST **/
                            arrAnswers.add(answersData);
                        }

                        /** INSTANTIATE THE ADAPTER **/
                        adapter = new ConsultationsDetailsAdapter(ConsultationDetailsActivity.this, data, arrAnswers);

                        /** SET THE ADAPTER TO THE RECYCLER VIEW **/
                        listAnswers.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
//                query.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot)
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /** GET THE INCOMING DATA **/
    private void fetchIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("CONSULT_ID")) {
            CONSULT_ID = bundle.getString("CONSULT_ID");
            if (!TextUtils.isEmpty(CONSULT_ID)) {
                /** FETCH THE CONSULTATION DETAILS **/
                fetchConsultDetails();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get necessary data", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get necessary data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /***** CONFIGURE THE TOOLBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configTB() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = "Question";
//        String strTitle = getString(R.string.add_a_new_medicine_record);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getApplicationContext()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return false;
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listAnswers.setLayoutManager(manager);
        listAnswers.setHasFixedSize(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}