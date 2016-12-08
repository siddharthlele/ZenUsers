package com.zenpets.users.consultations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zenpets.users.R;
import com.zenpets.users.creators.ConsultationCreatorActivity;
import com.zenpets.users.utils.TypefaceSpan;
import com.zenpets.users.utils.models.consultations.ConsultationsData;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConsultationList extends AppCompatActivity {

    /** THE FIREBASE ADAPTER **/
    private FirebaseRecyclerAdapter adapter;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaHeaderProgress) LinearLayout linlaHeaderProgress;
    @BindView(R.id.listConsultations) RecyclerView listConsultations;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ASK A FREE QUESTION **/
    @OnClick(R.id.linlaEmpty) void askQuestion()    {
        Intent intent = new Intent(this, ConsultationCreatorActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultation_list);
        ButterKnife.bind(this);

        /***** CONFIGURE THE TOOLBAR *****/
        configTB();

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /** FETCH THE LIST OF PUBLIC CONSULTATIONS **/
        fetchPublicConsultations();
    }

    /** FETCH THE LIST OF PUBLIC CONSULTATIONS **/
    private void fetchPublicConsultations() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Consultations");
        Query query = reference.orderByChild("consultStatus").equalTo("Public");

        /** SETUP THE REVIEWS FIREBASE RECYCLER ADAPTER **/
        adapter = new FirebaseRecyclerAdapter<ConsultationsData, ConsultationsVH>
                (ConsultationsData.class, R.layout.consultation_item, ConsultationsVH.class, query) {
            @Override
            protected void populateViewHolder(ConsultationsVH viewHolder, ConsultationsData model, final int position) {
                if (model != null)  {

                    /** GET THE CONSULTATION HEADER **/
                    String strConsultHeader = model.getConsultHeader();
                    if (!TextUtils.isEmpty(strConsultHeader))   {
                        viewHolder.txtConsultHeader.setText(strConsultHeader);
                    }

                    /** GET THE CONSULTATION DESCRIPTION **/
                    String strConsultDescription = model.getConsultDescription();
                    if (!TextUtils.isEmpty(strConsultDescription))   {
                        viewHolder.txtConsultDescription.setText(strConsultDescription);
                    }

                    /** GET THE CONSULTATION TIME STAMP **/
                    String strTimeStamp = model.getTimeStamp();
                    long lngTimeStamp = Long.parseLong(strTimeStamp) * 1000;
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(lngTimeStamp);
                    Date date = calendar.getTime();

                    PrettyTime prettyTime = new PrettyTime();
                    String strDate = prettyTime.format(date);
//                    Log.e("DATE", strDate);

                    if (!TextUtils.isEmpty(strDate))   {
                        viewHolder.txtTimeStamp.setText(strDate);
                    }

                    /** GET THE CONSULTATION VIEWS **/
                    int intConsultViews = model.getConsultViews();
                    viewHolder.txtViews.setText(String.valueOf(intConsultViews) + " Views");

                    /** SHOW THE CONSULTATION DETAILS **/
                    viewHolder.linlaConsultContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ConsultationList.this, ConsultationDetailsActivity.class);
                            intent.putExtra("CONSULT_ID", adapter.getRef(position).getKey());
                            startActivity(intent);
                        }
                    });
                }
            }
        };

        query.addChildEventListener(consultationsChildEventListener);
        query.addListenerForSingleValueEvent(consultationsValueEventListener);

        /** SET THE ADAPTER **/
        listConsultations.setAdapter(adapter);
    }

    /** THE REVIEWS VIEW HOLDER **/
    private static class ConsultationsVH extends RecyclerView.ViewHolder {
        LinearLayout linlaConsultContainer;
        AppCompatTextView txtConsultHeader;
        AppCompatTextView txtConsultDescription;
        AppCompatTextView txtTimeStamp;
        AppCompatTextView txtViews;

        public ConsultationsVH(View itemView) {
            super(itemView);
            linlaConsultContainer = (LinearLayout) itemView.findViewById(R.id.linlaConsultContainer);
            txtConsultHeader = (AppCompatTextView) itemView.findViewById(R.id.txtConsultHeader);
            txtConsultDescription = (AppCompatTextView) itemView.findViewById(R.id.txtConsultDescription);
            txtTimeStamp = (AppCompatTextView) itemView.findViewById(R.id.txtTimeStamp);
            txtViews = (AppCompatTextView) itemView.findViewById(R.id.txtViews);
        }
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listConsultations.setLayoutManager(manager);
        listConsultations.setHasFixedSize(true);
    }

    /***** CONFIGURE THE TOOLBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configTB() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = "Consultations Feed";
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

    /** THE CONSULTATIONS CHILD EVENT LISTENER **/
    private ChildEventListener consultationsChildEventListener = new ChildEventListener() {
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
    };

    /** THE CONSULTATIONS VALUE EVENT LISTENER **/
    private ValueEventListener consultationsValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            /** SHOW OR HIDE THE EMPTY LAYOUT **/
            emptyShowOrHideReviews(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    private void emptyShowOrHideReviews(DataSnapshot dataSnapshot)  {
        if (dataSnapshot.hasChildren()) {
            /* SHOW THE RECYCLER VIEW CONTAINER */
            listConsultations.setVisibility(View.VISIBLE);
            linlaEmpty.setVisibility(View.GONE);
        } else {
            /* SHOW THE NO REVIEWS CONTAINER */
            linlaEmpty.setVisibility(View.VISIBLE);
            listConsultations.setVisibility(View.GONE);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}