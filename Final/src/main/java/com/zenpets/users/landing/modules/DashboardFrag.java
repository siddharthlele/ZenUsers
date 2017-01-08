package com.zenpets.users.landing.modules;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.zenpets.users.details.appointments.AppointmentsActivity;
import com.zenpets.users.editor.ProfileEditor;
import com.zenpets.users.profile.ProfileDetails;
import com.zenpets.users.utils.TypefaceSpan;
import com.zenpets.users.utils.models.reviews.ReviewsData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardFrag extends Fragment {

    /** THE USER ID AND USER KEY **/
    String USER_ID = null;
    String USER_KEY = null;

    /** THE FIREBASE RECYCLER ADAPTER INSTANCES **/
    private FirebaseRecyclerAdapter adapAppointments;
    private FirebaseRecyclerAdapter adapVaccinations;

    /** USER PROFILE STRINGS **/
    String USER_NAME;
    String USER_PROFILE_PICTURE;
    
    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwProfilePicture) AppCompatImageView imgvwProfilePicture;
    @BindView(R.id.txtUserName) AppCompatTextView txtUserName;
    @BindView(R.id.linlaAppointments) LinearLayout linlaAppointments;
    @BindView(R.id.listAppointments) RecyclerView listAppointments;
    @BindView(R.id.linlaNoAppointments) LinearLayout linlaNoAppointments;
    @BindView(R.id.linlaVaccinations) LinearLayout linlaVaccinations;
    @BindView(R.id.listVaccinations) RecyclerView listVaccinations;
    @BindView(R.id.linlaNoVaccinations) LinearLayout linlaNoVaccinations;

    /** SHOW THE PROFILE **/
    @OnClick(R.id.linlaProfile) void showProfile()  {
        Intent intent = new Intent(getActivity(), ProfileDetails.class);
        startActivity(intent);
    }

    /** EDIT THE PROFILE **/
    @OnClick(R.id.imgvwProfileEdit) void editProfile()  {
        Intent intent = new Intent(getActivity(), ProfileEditor.class);
        startActivity(intent);
    }
    
    /** SHOW ALL APPOINTMENTS **/
    @OnClick(R.id.txtAllAppointments) void showAppointments()   {
        Intent intent = new Intent(getActivity(), AppointmentsActivity.class);
        startActivity(intent);
    }

    /** SHOW ALL VACCINATIONS **/
    @OnClick(R.id.txtAllVaccinations) void showVaccinations()   {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.dashboard_frag, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /** INDICATE THAT THE FRAGMENT SHOULD RETAIN IT'S STATE **/
        setRetainInstance(true);

        /** INDICATE THAT THE FRAGMENT HAS AN OPTIONS MENU **/
        setHasOptionsMenu(true);

        /** INVALIDATE THE EARLIER OPTIONS MENU SET IN OTHER FRAGMENTS / ACTIVITIES **/
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /***** CONFIGURE THE TOOLBAR *****/
        configTB();

        /** CONFIGURE THE RECYCLER VIEWS **/
        configRecyclers();
    }

    @Override
    public void onStart() {
        super.onStart();

        /** GET THE USER ID **/
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            USER_ID = user.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
            Query query = ref.orderByChild("userID").equalTo(USER_ID);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        USER_KEY = postSnapshot.getKey();
                    }

                    /** FETCH THE USER PROFILE **/
                    fetchUserProfile(user);

                    /** FETCH THE USERS UPCOMING APPOINTMENTS **/
                    fetchAppointments();

                    /** FETCH THE USER PETS UPCOMING VACCINATIONS **/
                    fetchVaccinations();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            Toast.makeText(getActivity(), "There was a problem fetching necessary data", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void fetchUserProfile(FirebaseUser user) {

        /** GET THE USER NAME **/
        USER_NAME = user.getDisplayName();

        /** GET THE PROFILE PICTURE **/
        if (user.getProviderData().get(1).getProviderId().equalsIgnoreCase("google.com"))   {
            USER_PROFILE_PICTURE = String.valueOf(user.getProviderData().get(1).getPhotoUrl()) + "?sz=600";
        } else if (user.getProviderData().get(1).getProviderId().equalsIgnoreCase("facebook.com")){
            USER_PROFILE_PICTURE = "https://graph.facebook.com/" + user.getProviderData().get(1).getUid() + "/picture?width=4000";
        }

        /** SET THE USER NAME **/
        if (USER_NAME != null)  {
            txtUserName.setText(USER_NAME);
        }

        /** SET THE PROFILE PICTURE **/
        if (USER_PROFILE_PICTURE != null)   {
            Glide.with(getActivity())
                    .load(USER_PROFILE_PICTURE)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .into(imgvwProfilePicture);
        }
    }

    /** FETCH THE USER PETS UPCOMING VACCINATIONS **/
    private void fetchVaccinations() {
        DatabaseReference refVaccinations = FirebaseDatabase.getInstance().getReference().child("Users").child(USER_KEY).child("Vaccinations");

        /** SETUP THE REVIEWS FIREBASE RECYCLER ADAPTER **/
        adapVaccinations = new FirebaseRecyclerAdapter<ReviewsData, AppointmentsVH>
                (ReviewsData.class, R.layout.dashboard_appointments_item, AppointmentsVH.class, refVaccinations) {
            @Override
            protected void populateViewHolder(AppointmentsVH viewHolder, ReviewsData model, int position) {
                if (model != null)  {
                }
            }
        };

        refVaccinations.addChildEventListener(vaccinationsChildEventListener);
        refVaccinations.addListenerForSingleValueEvent(vaccinationsValueEventListener);

        /** SET THE ADAPTER **/
        listVaccinations.setAdapter(adapVaccinations);
    }

    /** FETCH THE USERS UPCOMING APPOINTMENTS **/
    private void fetchAppointments() {
        DatabaseReference refAppointments = FirebaseDatabase.getInstance().getReference().child("Users").child(USER_KEY).child("Appointments");

        /** SETUP THE REVIEWS FIREBASE RECYCLER ADAPTER **/
        adapAppointments = new FirebaseRecyclerAdapter<ReviewsData, AppointmentsVH>
                (ReviewsData.class, R.layout.dashboard_appointments_item, AppointmentsVH.class, refAppointments) {
            @Override
            protected void populateViewHolder(AppointmentsVH viewHolder, ReviewsData model, int position) {
                if (model != null)  {
                }
            }
        };

        refAppointments.addChildEventListener(appointmentsChildEventListener);
        refAppointments.addListenerForSingleValueEvent(appointmentsValueEventListener);

        /** SET THE ADAPTER **/
        listAppointments.setAdapter(adapAppointments);
    }

    /** THE REVIEWS VIEW HOLDER **/
    private static class AppointmentsVH extends RecyclerView.ViewHolder {
//        AppCompatTextView txtVisitReason;
//        AppCompatTextView txtVisitExperience;
//        IconicsImageView imgvwLikeStatus;
//        AppCompatTextView txtUserName;
//        AppCompatTextView txtPostedAgo;

        public AppointmentsVH(View itemView) {
            super(itemView);
//            txtVisitReason = (AppCompatTextView) itemView.findViewById(R.id.txtVisitReason);
//            txtVisitExperience = (AppCompatTextView) itemView.findViewById(R.id.txtVisitExperience);
//            imgvwLikeStatus = (IconicsImageView) itemView.findViewById(R.id.imgvwLikeStatus);
//            txtUserName = (AppCompatTextView) itemView.findViewById(R.id.txtUserName);
//            txtPostedAgo = (AppCompatTextView) itemView.findViewById(R.id.txtPostedAgo);
        }
    }

    /** THE APPOINTMENTS CHILD EVENT LISTENER **/
    private ChildEventListener appointmentsChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            /** SHOW OR HIDE THE EMPTY LAYOUT **/
            emptyShowOrHideAppointments(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            /** SHOW OR HIDE THE EMPTY LAYOUT **/
            emptyShowOrHideAppointments(dataSnapshot);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            /** SHOW OR HIDE THE EMPTY LAYOUT **/
            emptyShowOrHideAppointments(dataSnapshot);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            /** SHOW OR HIDE THE EMPTY LAYOUT **/
            emptyShowOrHideAppointments(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    /** THE APPOINTMENTS VALUE EVENT LISTENER **/
    private ValueEventListener appointmentsValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            /** SHOW OR HIDE THE EMPTY LAYOUT **/
            emptyShowOrHideAppointments(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    private void emptyShowOrHideAppointments(DataSnapshot dataSnapshot)  {
        if (dataSnapshot.hasChildren()) {
            /* SHOW THE RECYCLER VIEW CONTAINER */
            linlaAppointments.setVisibility(View.VISIBLE);
            linlaNoAppointments.setVisibility(View.GONE);
        } else {
            /* SHOW THE NO APPOINTMENTS CONTAINER */
            linlaNoAppointments.setVisibility(View.VISIBLE);
            linlaAppointments.setVisibility(View.GONE);
        }
    }

    /** THE VACCINATIONS CHILD EVENT LISTENER **/
    private ChildEventListener vaccinationsChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            /** SHOW OR HIDE THE EMPTY LAYOUT **/
            emptyShowOrHideVaccinations(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            /** SHOW OR HIDE THE EMPTY LAYOUT **/
            emptyShowOrHideVaccinations(dataSnapshot);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            /** SHOW OR HIDE THE EMPTY LAYOUT **/
            emptyShowOrHideVaccinations(dataSnapshot);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            /** SHOW OR HIDE THE EMPTY LAYOUT **/
            emptyShowOrHideVaccinations(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    /** THE VACCINATIONS VALUE EVENT LISTENER **/
    private ValueEventListener vaccinationsValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            /** SHOW OR HIDE THE EMPTY LAYOUT **/
            emptyShowOrHideVaccinations(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    private void emptyShowOrHideVaccinations(DataSnapshot dataSnapshot)  {
        if (dataSnapshot.hasChildren()) {
            /* SHOW THE RECYCLER VIEW CONTAINER */
            linlaVaccinations.setVisibility(View.VISIBLE);
            linlaNoVaccinations.setVisibility(View.GONE);
        } else {
            /* SHOW THE NO VACCINATIONS CONTAINER */
            linlaNoVaccinations.setVisibility(View.VISIBLE);
            linlaVaccinations.setVisibility(View.GONE);
        }
    }

    /** CONFIGURE THE RECYCLER VIEWS **/
    private void configRecyclers() {
        LinearLayoutManager llmAppointments = new LinearLayoutManager(getActivity());
        llmAppointments.setOrientation(LinearLayoutManager.VERTICAL);
        llmAppointments.setAutoMeasureEnabled(true);
        listAppointments.setLayoutManager(llmAppointments);
        listAppointments.setHasFixedSize(false);
        listAppointments.setNestedScrollingEnabled(false);

        LinearLayoutManager llmVaccinations = new LinearLayoutManager(getActivity());
        llmVaccinations.setOrientation(LinearLayoutManager.VERTICAL);
        llmVaccinations.setAutoMeasureEnabled(true);
        listVaccinations.setLayoutManager(llmVaccinations);
        listVaccinations.setHasFixedSize(false);
        listVaccinations.setNestedScrollingEnabled(false);
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {

        Toolbar myToolbar = (Toolbar) getActivity().findViewById(R.id.myToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);

//        String strTitle = getString(R.string.add_a_new_pet);
        String strTitle = "Dashboard";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(s);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(null);
    }
}