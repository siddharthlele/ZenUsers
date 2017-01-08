package com.zenpets.users.profile.adoptions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zenpets.users.R;
import com.zenpets.users.creator.adoption.NewAdoptionListing;
import com.zenpets.users.utils.TypefaceSpan;
import com.zenpets.users.utils.models.adoptions.UserAdoptionData;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserAdoptionsList extends AppCompatActivity {

    /** THE USER KEY **/
    private String USER_KEY = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaHeaderProgress) LinearLayout linlaHeaderProgress;
    @BindView(R.id.listAdoptions) RecyclerView listAdoptions;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** CREATE A NEW ADOPTION LISTING **/
    @OnClick(R.id.linlaEmpty) void newAdoptionListing()    {
        Intent intent = new Intent(UserAdoptionsList.this, NewAdoptionListing.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_adoptions_list);
        ButterKnife.bind(this);

        /***** CONFIGURE THE TOOLBAR *****/
        configTB();

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /** GET THE FIREBASE USER DETAILS **/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String USER_ID = user.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
            Query query = ref.orderByChild("userID").equalTo(USER_ID);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        USER_KEY = postSnapshot.getKey();
                        Log.e("USER KEY", USER_KEY);
                    }

                    /** SETUP THE FIREBASE ADAPTER **/
                    setupFirebaseAdapter();

                    /** SHOW THE PROGRESS BAR **/
                    linlaHeaderProgress.setVisibility(View.VISIBLE);

                    /** GET THE LIST OF ADOPTION POSTINGS **/
                    setupFirebaseAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    /** GET THE LIST OF ADOPTION POSTINGS **/
    private void setupFirebaseAdapter() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(USER_KEY).child("Adoptions");
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<UserAdoptionData, AdoptionsVH>
                (UserAdoptionData.class, R.layout.adoptions_item, AdoptionsVH.class, reference) {
            @Override
            protected void populateViewHolder(final AdoptionsVH viewHolder, final UserAdoptionData model, int position) {
                if (model != null)  {
                    /** GET THE ADOPTION ID **/
                    String adoptionID = model.getAdoptionID();
                    Log.e("ADOPTION ID", adoptionID);

                    DatabaseReference refAdoptions = FirebaseDatabase.getInstance().getReference().child("Adoptions").child(adoptionID);
                    refAdoptions.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            /** GET THE ADOPTION STATUS **/
                            String adoptionStatus = dataSnapshot.child("adoptionStatus").getValue(String.class);
                            if (adoptionStatus.equalsIgnoreCase("open")) {
                                viewHolder.txtAdoptionStatus.setText("Available for adoption");
                            } else {
                                viewHolder.txtAdoptionStatus.setText("Adopted");
                            }

//                            /** GET THE PET TYPE **/
//                            String petType = dataSnapshot.child("petType").getValue(String.class);
//
//                            /** GET THE PET BREED **/
//                            String petBreed = dataSnapshot.child("petBreed").getValue(String.class);

                            /** GET THE PET NAME **/
                            String petName = dataSnapshot.child("petName").getValue(String.class);
                            if (TextUtils.isEmpty(petName)) {
                                viewHolder.txtAdoptionName.setVisibility(View.GONE);
                            } else {
                                viewHolder.txtAdoptionName.setText(petName);
                                viewHolder.txtAdoptionName.setVisibility(View.VISIBLE);
                            }

                            /** GET THE PET GENDER **/
                            String petGender = dataSnapshot.child("petGender").getValue(String.class);
                            viewHolder.txtGender.setText(petGender);
//                            if (petGender.equalsIgnoreCase("male"))  {
//                                viewHolder.txtGender.setText(getResources().getString(R.string.gender_male));
//                                viewHolder.txtGender.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_blue_dark));
//                            } else if (petGender.equalsIgnoreCase("female")) {
//                                viewHolder.txtGender.setText(getResources().getString(R.string.gender_female));
//                                viewHolder.txtGender.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
//                            }

                            /** GET THE PET DESCRIPTION **/
                            String petDescription = dataSnapshot.child("petDescription").getValue(String.class);
                            viewHolder.txtAdoptionDescription.setText(petDescription);
//                            Log.e("DESCRIPTION", petDescription);

                            /** GET THE TIME STAMP **/
                            String timeStamp = dataSnapshot.child("timeStamp").getValue(String.class);
//                            Log.e("TIME STAMP", timeStamp);
                            long lngTimeStamp = Long.parseLong(timeStamp) * 1000;
                            Calendar calendar = Calendar.getInstance(Locale.getDefault());
                            calendar.setTimeInMillis(lngTimeStamp);
                            Date date = calendar.getTime();

                            PrettyTime prettyTime = new PrettyTime();
                            String strDate = prettyTime.format(date);
                            viewHolder.txtTimeStamp.setText(strDate);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        };

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHideAdoptions(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHideAdoptions(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHideAdoptions(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHideAdoptions(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHideAdoptions(dataSnapshot);

//                /** HIDE THE PROGRESS BAR **/
//                linlaHeaderProgress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        /** SET THE ADAPTER **/
        listAdoptions.setAdapter(adapter);

        /** HIDE THE PROGRESS BAR **/
        linlaHeaderProgress.setVisibility(View.GONE);
    }

    /** THE REVIEWS VIEW HOLDER **/
    private static class AdoptionsVH extends RecyclerView.ViewHolder {

        AppCompatTextView txtGender;
        AppCompatTextView txtAdoptionName;
        AppCompatTextView txtAdoptionDescription;
        AppCompatTextView txtAdoptionStatus;
        AppCompatTextView txtTimeStamp;

        public AdoptionsVH(View itemView) {
            super(itemView);

            txtGender = (AppCompatTextView) itemView.findViewById(R.id.txtGender);
            txtAdoptionName = (AppCompatTextView) itemView.findViewById(R.id.txtAdoptionName);
            txtAdoptionDescription = (AppCompatTextView) itemView.findViewById(R.id.txtAdoptionDescription);
            txtAdoptionStatus = (AppCompatTextView) itemView.findViewById(R.id.txtAdoptionStatus);
            txtTimeStamp = (AppCompatTextView) itemView.findViewById(R.id.txtTimeStamp);
        }
    }

    private void emptyShowOrHideAdoptions(DataSnapshot dataSnapshot)  {
        if (dataSnapshot.hasChildren()) {
            /* SHOW THE RECYCLER VIEW CONTAINER */
            listAdoptions.setVisibility(View.VISIBLE);
            linlaEmpty.setVisibility(View.GONE);
        } else {
            /* SHOW THE NO REVIEWS CONTAINER */
            linlaEmpty.setVisibility(View.VISIBLE);
            listAdoptions.setVisibility(View.GONE);
        }
    }

    /***** CONFIGURE THE TOOLBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configTB() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = "Your Adoption Posts";
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
        listAdoptions.setLayoutManager(manager);
        listAdoptions.setHasFixedSize(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}