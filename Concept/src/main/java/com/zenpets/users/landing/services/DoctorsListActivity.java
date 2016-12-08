package com.zenpets.users.landing.services;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zenpets.users.R;
import com.zenpets.users.doctors.DoctorsProfileActivity;
import com.zenpets.users.utils.TypefaceSpan;
import com.zenpets.users.utils.models.DoctorsData;
import com.zenpets.users.utils.models.clinic.ClinicsData;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DoctorsListActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /** A DATABASE REFERENCE **/
    private DatabaseReference refDoctors;

    /** THE FIREBASE RECYCLER ADAPTER **/
    private FirebaseRecyclerAdapter adapter;

    /** A GOOGLE API CLIENT INSTANCE **/
    private GoogleApiClient mGoogleApiClient;

    /** A LOCATION REQUEST INSTANCE **/
    private LocationRequest mLocationRequest;

    /** THE USERS CURRENT COORDINATES **/
    private LatLng currentCoordinates;
    private Location currentLocation;

    /** PLAY SERVICE REQUEST CODE **/
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    /** STRINGS TO HOLD THE DATA TYPES **/
    private String CLINIC_OWNER;
    private String CLINIC_NAME;
    private String CLINIC_CURRENCY;
    private String CLINIC_ADDRESS;
    private String CLINIC_LOCALITY;
    private String DOCTOR_PREFIX = null;
    private String DOCTOR_NAME = null;
    private String DOCTOR_EXPERIENCE;
    private String DOCTOR_CHARGES;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaHeaderProgress) LinearLayout linlaHeaderProgress;
    @BindView(R.id.listDoctors) RecyclerView listDoctors;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** FILTER THE LIST **/
    @OnClick(R.id.fabFilter) void filterDoctors()   {
        Intent intent = new Intent(this, FilterDoctorsActivity.class);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.services_doctors_list);
        ButterKnife.bind(this);

        /** CHECK PLAY SERVICES AVAILABILITY **/
        if (checkPlayServices()) {
            /* CONFIGURE THE GOOGLE API CLIENT */
            buildGoogleApiClient();
        }

        /***** CONFIGURE THE TOOLBAR *****/
        configTB();

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /** GET THE FIREBASE USER DETAILS **/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            /** SHOW THE PROGRESS BAR **/
            linlaHeaderProgress.setVisibility(View.VISIBLE);

            /** GET THE LIST OF DOCTORS **/
            setupFirebaseAdapter();
        }
    }

    /** SETUP THE FIREBASE ADAPTER **/
    private void setupFirebaseAdapter() {

        refDoctors = FirebaseDatabase.getInstance().getReference().child("Doctors");
        adapter = new FirebaseRecyclerAdapter<DoctorsData, DoctorsVH>
                (DoctorsData.class, R.layout.services_doctors_item, DoctorsVH.class, refDoctors) {

            @Override
            protected void populateViewHolder(final DoctorsVH viewHolder, final DoctorsData model, final int position) {

                /** GET THE CLINIC OWNER **/
                CLINIC_OWNER = model.getClinicOwner();

                /** GET THE CLINIC NAME **/
                DatabaseReference refClinic = FirebaseDatabase.getInstance().getReference().child("Clinics");
                Query query = refClinic.orderByChild("clinicOwner").equalTo(CLINIC_OWNER);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            ClinicsData data = child.getValue(ClinicsData.class);

                            /* GET THE CLINIC NAME */
                            CLINIC_NAME = data.getClinicName();
                            if (!TextUtils.isEmpty(CLINIC_NAME))    {
                                viewHolder.txtClinicName.setText(CLINIC_NAME);
                            }

                            /* GET THE CLINIC ADDRESS */
                            CLINIC_ADDRESS = data.getClinicAddress();
                            CLINIC_LOCALITY = data.getClinicLocality();
                            viewHolder.txtClinicAddress.setText(CLINIC_ADDRESS + "\n" + CLINIC_LOCALITY);

                            /* GET THE CLINIC CURRENCY */
                            CLINIC_CURRENCY = data.getClinicCurrency();

                            /* GET THE CLINIC LATITUDE AND LONGITUDE */
                            Double latitude = data.getClinicLatitude();
                            Double longitude = data.getClinicLongitude();
                            Location clinicLocation = new Location("Clinic");
                            clinicLocation.setLatitude(latitude);
                            clinicLocation.setLongitude(longitude);

                            /* CALCULATE THE DISTANCE */
                            float distance = currentLocation.distanceTo(clinicLocation) / 1000;

                            /* ROUND UP THE DISTANCE TO 3 DECIMALS */
                            float finalDistance = roundUpDistance(distance, 2);

                            viewHolder.txtDoctorDistance.setText("~ " + String.valueOf(finalDistance) + " " + "km");

                            /* GET THE DOCTOR'S PROFILE PICTURE */
                            DatabaseReference refDoctor = FirebaseDatabase.getInstance().getReference().child("Doctors");
                            Query qryDoctor = refDoctor.orderByChild("clinicOwner").equalTo(CLINIC_OWNER);
                            qryDoctor.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChildren()) {
                                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                                            final DoctorsData data = child.getValue(DoctorsData.class);

                                            String DOCTOR_ID = child.getKey();
//                                            Log.e("DOCTOR ID", DOCTOR_ID);

                                            /** SET THE DOCTOR'S NAME **/
                                            DOCTOR_PREFIX = model.getDoctorPrefix();
                                            DOCTOR_NAME = model.getDoctorName();
                                            viewHolder.txtDoctorName.setText(DOCTOR_PREFIX + " " + DOCTOR_NAME);

                                            /** GET THE DOCTOR'S EXPERIENCE **/
                                            DOCTOR_EXPERIENCE = data.getDoctorExperience();
                                            viewHolder.txtDoctorExp.setText(DOCTOR_EXPERIENCE + " yrs exp");

                                            /** GET THE DOCTOR'S CHARGES **/
                                            DOCTOR_CHARGES = data.getDoctorCharges();
                                            viewHolder.txtDoctorCharges.setText(CLINIC_CURRENCY + " " + DOCTOR_CHARGES);

                                            /** GET THE DOCTOR'S REVIEWS **/
                                            DatabaseReference refReviews = FirebaseDatabase.getInstance().getReference().child("Doctors").child(DOCTOR_ID).child("Reviews");
                                            refReviews.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChildren()) {

                                                        /** GET THE NUMBER OF REVIEWS **/
                                                        Resources resReviews = getResources();
                                                        String reviewQuantity = null;
                                                        if (dataSnapshot.getChildrenCount() == 0)   {
                                                            reviewQuantity = resReviews.getQuantityString(R.plurals.reviews, (int) dataSnapshot.getChildrenCount(), dataSnapshot.getChildrenCount());
                                                        } else if (dataSnapshot.getChildrenCount() == 1)    {
                                                            reviewQuantity = resReviews.getQuantityString(R.plurals.reviews, (int) dataSnapshot.getChildrenCount(), dataSnapshot.getChildrenCount());
                                                        } else if (dataSnapshot.getChildrenCount() > 1) {
                                                            reviewQuantity = resReviews.getQuantityString(R.plurals.reviews, (int) dataSnapshot.getChildrenCount(), dataSnapshot.getChildrenCount());
                                                        }
                                                        viewHolder.txtDoctorReviews.setText(reviewQuantity);

                                                        /** GET THE NUMBER LIKES **/
                                                        int totalLikes = (int) dataSnapshot.getChildrenCount();
                                                        viewHolder.txtDoctorLikesTotal.setText(String.valueOf(totalLikes) + " Votes");

                                                        /** GET THE NUMBER OF "YES" **/
                                                        int noOfYes = 0;
                                                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                                                            Log.e("REVIEWS", String.valueOf(postSnapshot));
                                                            String recommendStatus = postSnapshot.child("recommendStatus").getValue(String.class);
//                                                            Log.e("recommendStatus", recommendStatus);
                                                            if (recommendStatus.equalsIgnoreCase("Yes"))    {
                                                                noOfYes++;
                                                            }
                                                        }

                                                        /** CALCULATE THE PERCENTAGE OF LIKES **/
                                                        double percentLikes = ((double)noOfYes / totalLikes) * 100;
                                                        int finalPercentLikes = (int)percentLikes;
//                                                        Log.e("YES PERCENTAGE", String.valueOf(finalPercentLikes));

                                                        /** SET THE PERCENTAGE **/
                                                        viewHolder.txtDoctorLikesPercent.setText(String.valueOf(finalPercentLikes) + "%");

                                                    } else {
                                                        Resources resources = getResources();
                                                        String reviewQuantity = resources.getQuantityString(R.plurals.reviews, 0, 0);
                                                        viewHolder.txtDoctorReviews.setText(reviewQuantity);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                /** SHOW THE DOCTORS PROFILE **/
                viewHolder.linlaDoctorContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DoctorsListActivity.this, DoctorsProfileActivity.class);
                        intent.putExtra("DOCTOR_ID", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };

        refDoctors.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHide(dataSnapshot);

                /** HIDE THE PROGRESS BAR **/
                linlaHeaderProgress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        /** SET THE ADAPTER **/
        listDoctors.setAdapter(adapter);
    }

    /***** VERIFY GOOGLE PLAY SERVICES AVAILABLE ON THE DEVICE *****/
    private boolean checkPlayServices() {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        int result = availability.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (availability.isUserResolvableError(result)) {
                availability.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG) .show();
            }
            return false;
        }
        return true;
    }

    /***** CREATE AND INSTANTIATE THE GOOGLE API CLIENT INSTANCE *****/
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("CONNECTION FAILED", String.valueOf(connectionResult.getErrorCode()));
    }

    @Override
    public void onLocationChanged(Location location) {

        currentCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
//        Toast.makeText(getApplicationContext(), "CURRENT COORDINATES: " + String.valueOf(currentCoordinates), Toast.LENGTH_SHORT).show();
        currentLocation = location;
        currentLocation.setLatitude(location.getLatitude());
        currentLocation.setLongitude(location.getLongitude());

        if (mGoogleApiClient != null)   {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    private static class DoctorsVH extends RecyclerView.ViewHolder {

        LinearLayout linlaDoctorContainer;
        AppCompatTextView txtDoctorName;
        AppCompatTextView txtDoctorLikesPercent;
        AppCompatTextView txtDoctorLikesTotal;
        AppCompatTextView txtClinicName;
        AppCompatTextView txtClinicAddress;
        LinearLayout linlaDoctorExp;
        AppCompatTextView txtDoctorExp;
        LinearLayout linlaDoctorCharges;
        AppCompatTextView txtDoctorCharges;
        LinearLayout linlaDoctorReviews;
        AppCompatTextView txtDoctorReviews;
        LinearLayout linlaDoctorDistance;
        AppCompatTextView txtDoctorDistance;

        public DoctorsVH(View itemView) {
            super(itemView);

            linlaDoctorContainer = (LinearLayout) itemView.findViewById(R.id.linlaDoctorContainer);
            txtDoctorName = (AppCompatTextView) itemView.findViewById(R.id.txtDoctorName);
            txtDoctorLikesPercent = (AppCompatTextView) itemView.findViewById(R.id.txtDoctorLikesPercent);
            txtDoctorLikesTotal = (AppCompatTextView) itemView.findViewById(R.id.txtDoctorLikesTotal);
            txtClinicName = (AppCompatTextView) itemView.findViewById(R.id.txtClinicName);
            txtClinicAddress = (AppCompatTextView) itemView.findViewById(R.id.txtClinicAddress);
            linlaDoctorExp = (LinearLayout) itemView.findViewById(R.id.linlaDoctorExp);
            txtDoctorExp = (AppCompatTextView) itemView.findViewById(R.id.txtDoctorExp);
            linlaDoctorCharges = (LinearLayout) itemView.findViewById(R.id.linlaDoctorCharges);
            txtDoctorCharges = (AppCompatTextView) itemView.findViewById(R.id.txtDoctorCharges);
            linlaDoctorReviews = (LinearLayout) itemView.findViewById(R.id.linlaDoctorReviews);
            txtDoctorReviews = (AppCompatTextView) itemView.findViewById(R.id.txtDoctorReviews);
            linlaDoctorDistance = (LinearLayout) itemView.findViewById(R.id.linlaDoctorDistance);
            txtDoctorDistance = (AppCompatTextView) itemView.findViewById(R.id.txtDoctorDistance);
        }
    }

    /** SHOW OR HIDE THE EMPTY LAYOUT **/
    private void emptyShowOrHide(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChildren()) {
            /** SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT **/
            listDoctors.setVisibility(View.VISIBLE);
            linlaEmpty.setVisibility(View.GONE);
        } else {
            /** HIDE THE RECYCLER VIEW AND SHOW THE EMPTY LAYOUT **/
            listDoctors.setVisibility(View.GONE);
            linlaEmpty.setVisibility(View.VISIBLE);
        }
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listDoctors.setLayoutManager(manager);
        listDoctors.setHasFixedSize(true);
    }

    /***** CONFIGURE THE TOOLBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configTB() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = "Find Doctors";
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

    /** ROUND UP THE DISTANCE **/
    public static float roundUpDistance(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}