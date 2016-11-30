package com.zenpets.users.landing.services;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zenpets.users.R;
import com.zenpets.users.utils.TypefaceSpan;
import com.zenpets.users.utils.models.ClinicImagesData;
import com.zenpets.users.utils.models.ClinicsData;
import com.zenpets.users.utils.models.DoctorsData;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DoctorsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /** A DATABASE REFERENCE **/
    DatabaseReference refDoctors;

    /** THE FIREBASE RECYCLER ADAPTER INSTANCE **/
    private FirebaseRecyclerAdapter adapter;

    /** A GOOGLE API CLIENT INSTANCE **/
    GoogleApiClient mGoogleApiClient;

    /** A LOCATION REQUEST INSTANCE **/
    LocationRequest mLocationRequest;

    /** THE USERS CURRENT COORDINATES **/
    LatLng currentCoordinates;
    Location currentLocation;

    /** PLAY SERVICE REQUEST CODE **/
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaHeaderProgress) LinearLayout linlaHeaderProgress;
    @BindView(R.id.listDoctors) RecyclerView listDoctors;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

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

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /** GET THE FIREBASE USER DETAILS **/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            refDoctors = FirebaseDatabase.getInstance().getReference().child("Doctors");
            /** SETUP THE FIREBASE ADAPTER **/
            setupFirebaseAdapter();
        }
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

    /** SETUP THE FIREBASE ADAPTER **/
    private void setupFirebaseAdapter() {

        adapter = new FirebaseRecyclerAdapter<DoctorsData, DoctorsVH>
                (DoctorsData.class, R.layout.new_services_doctors_item, DoctorsVH.class, refDoctors) {

            @Override
            protected void populateViewHolder(final DoctorsVH viewHolder, final DoctorsData model, final int position) {
                if (model != null) {

                    /** GET THE DOCTORS LIKES **/
                    Log.e("DOCTOR KEY", adapter.getRef(position).getKey());
                    DatabaseReference refLikes = FirebaseDatabase.getInstance().getReference().child("Doctor Likes");
                    Query qryLikes = refLikes.orderByChild("doctorID").equalTo(adapter.getRef(position).getKey());
                    qryLikes.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                            } else {
                                viewHolder.txtNoOfVotes.setText("0");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    /** GET THE DOCTORS REVIEWS **/
                    DatabaseReference refReviews = FirebaseDatabase.getInstance().getReference().child("Doctor Reviews");
                    Query qryReviews = refReviews.orderByChild("doctorID").equalTo(adapter.getRef(position).getKey());
                    qryReviews.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                            } else {
                                viewHolder.txtDoctorReviews.setText("0 reviews");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    /** GET THE CLINIC OWNER **/
                    String CLINIC_OWNER = model.getClinicOwner();
                    Log.e("CLINIC OWNER", CLINIC_OWNER);

                    /** SET THE DOCTOR'S NAME **/
                    viewHolder.txtDoctorName.setText(model.getDoctorName());
                    Log.e("DOCTOR NAME", model.getDoctorName());

                    /** SHOW THE DOCTORS PROFILE **/
                    viewHolder.linlaDoctorContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "Will show doctor's profile", Toast.LENGTH_SHORT).show();
                        }
                    });

                    /** GET THE CLINIC NAME **/
                    DatabaseReference refClinic = FirebaseDatabase.getInstance().getReference().child("Clinics");
                    Query query = refClinic.orderByChild("clinicOwner").equalTo(CLINIC_OWNER);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot child: dataSnapshot.getChildren()) {
                                    ClinicsData data = child.getValue(ClinicsData.class);

                                    /* GET THE CLINIC NAME */
                                    String CLINIC_NAME = data.getClinicName();
                                    if (CLINIC_NAME != null)    {
                                        viewHolder.txtClinicName.setText(CLINIC_NAME);
                                    }

                                    /* GET THE CLINIC CURRENCY */
                                    String CLINIC_CURRENCY = data.getClinicCurrency();

                                    /* GET THE CLINIC CHARGES */
                                    String CLINIC_CHARGES = data.getClinicCharges();
                                    if (CLINIC_CHARGES != null && CLINIC_CURRENCY != null) {
                                        viewHolder.txtDoctorCharges.setText(CLINIC_CURRENCY + " " + CLINIC_CHARGES);
                                    }

                                    /** GET THE CLINIC LATITUDE AND LONGITUDE **/
                                    Double latitude = data.getClinicLatitude();
                                    Double longitude = data.getClinicLongitude();
                                    Location clinicLocation = new Location("Clinic");
                                    clinicLocation.setLatitude(latitude);
                                    clinicLocation.setLongitude(longitude);

                                    /* CALCULATE THE DISTANCE */
                                    float distance = currentLocation.distanceTo(clinicLocation) / 1000;
                                    Log.e("ORIGINAL DISTANCE", String.valueOf(distance));

                                    /* ROUND UP THE DISTANCE TO 3 DECIMALS */
                                    float finalDistance = roundUpDistance(distance, 2);
                                    Log.e("DISTANCE IN KILOMETERS", String.valueOf(finalDistance));

                                    viewHolder.txtDoctorDistance.setText("~ " + String.valueOf(finalDistance) + " " + "km");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    /** GET THE CLINIC IMAGES **/
                    DatabaseReference refImages = FirebaseDatabase.getInstance().getReference().child("Clinic Images");
                    Query qryImages = refImages.orderByChild("clinicOwner").equalTo(CLINIC_OWNER);
                    qryImages.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot child : dataSnapshot.getChildren())    {
                                    ClinicImagesData data = child.getValue(ClinicImagesData.class);

                                    /** GET THE CLINIC IMAGES **/
                                    String CLINIC_IMAGE = data.getClinicImage();
//                                    Log.e("CLINIC IMAGE", CLINIC_IMAGE);

                                    AppCompatImageView imageView = new AppCompatImageView(getApplicationContext());

                                    /** SET THE IMAGE VIEW DIMENSIONS **/
                                    LinearLayout.LayoutParams imgvwDimens = new LinearLayout.LayoutParams(150, 150);
                                    imageView.setLayoutParams(imgvwDimens);

                                    /** SET THE SCALE TYPE **/
                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                    /** SET THE MARGIN **/
                                    int dimensMargin = 5;
                                    float densityMargin = getResources().getDisplayMetrics().density;
                                    int finalDimensMargin = (int)(dimensMargin * densityMargin);

                                    LinearLayout.LayoutParams imgvwMargin = new LinearLayout.LayoutParams(150, 150);
                                    imgvwMargin.setMargins(finalDimensMargin, finalDimensMargin, finalDimensMargin, finalDimensMargin);

                                    /** SET THE CLINIC IMAGE **/
                                    if (CLINIC_IMAGE != null)   {
                                        Picasso.with(DoctorsActivity.this)
                                                .load(CLINIC_IMAGE)
                                                .into(imageView);
                                    }

                                    /** ADD THE IMAGE VIEW INSTANCE TO THE CONTAINER LINEAR LAYOUT **/
                                    viewHolder.linlaPhotos.addView(imageView, imgvwMargin);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        };

        refDoctors.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHide(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHide(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHide(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHide(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        refDoctors.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHide(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        /** SET THE ADAPTER **/
        listDoctors.setAdapter(adapter);
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
        Toast.makeText(getApplicationContext(), "CURRENT COORDINATES: " + String.valueOf(currentCoordinates), Toast.LENGTH_SHORT).show();
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
        AppCompatTextView txtNoOfVotes;
        AppCompatTextView txtClinicName;
        LinearLayout linlaPhotos;
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
            txtNoOfVotes = (AppCompatTextView) itemView.findViewById(R.id.txtNoOfVotes);
            txtClinicName = (AppCompatTextView) itemView.findViewById(R.id.txtClinicName);
            linlaPhotos = (LinearLayout) itemView.findViewById(R.id.linlaPhotos);
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
        int intOrientation = getResources().getConfiguration().orientation;
        listDoctors.setHasFixedSize(true);
        GridLayoutManager glm = null;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet)   {
            if (intOrientation == 1)	{
                glm = new GridLayoutManager(this, 2);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(this, 3);
            }
        } else {
            if (intOrientation == 1)    {
                glm = new GridLayoutManager(this, 1);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(this, 2);
            }
        }
        listDoctors.setLayoutManager(glm);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configAB() {
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