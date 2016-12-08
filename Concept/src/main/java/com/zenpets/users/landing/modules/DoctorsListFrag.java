package com.zenpets.users.landing.modules;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.zenpets.users.R;
import com.zenpets.users.utils.models.DoctorsData;
import com.zenpets.users.utils.models.clinic.ClinicsData;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorsListFrag extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /** A GOOGLE API CLIENT INSTANCE **/
    private GoogleApiClient mGoogleApiClient;

    /** A LOCATION REQUEST INSTANCE **/
    private LocationRequest mLocationRequest;

    /** THE USERS CURRENT COORDINATES **/
    private LatLng currentCoordinates;
    private Location currentLocation;

    /** PLAY SERVICE REQUEST CODE **/
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    /** A DATABASE REFERENCE **/
    private DatabaseReference refDoctors;

    /** THE FIREBASE RECYCLER ADAPTER INSTANCE **/
    private FirebaseRecyclerAdapter adapter;

    /** STRINGS TO HOLD THE DATA TYPES **/
    private String CLINIC_OWNER;
    private String DOCTOR_PREFIX = null;
    private String DOCTOR_NAME = null;
    private String DOCTORS_PROFILE_URL;
    private String DOCTOR_EXPERIENCE;
    private String DOCTOR_CHARGES;
    private String CLINIC_ADDRESS;
    private String CLINIC_CURRENCY;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaHeaderProgress) LinearLayout linlaHeaderProgress;
    @BindView(R.id.listDoctors) RecyclerView listDoctors;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.doctors_frag_list, container, false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /** CHECK PLAY SERVICES AVAILABILITY **/
        if (checkPlayServices()) {
            /* CONFIGURE THE GOOGLE API CLIENT */
            buildGoogleApiClient();
        }

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecycler();
    }

    /** SETUP THE FIREBASE ADAPTER **/
    private void setupFirebaseAdapter() {

        if (mGoogleApiClient != null)   {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        adapter = new FirebaseRecyclerAdapter<DoctorsData, DoctorsVH>
                (DoctorsData.class, R.layout.doctors_frag_item, DoctorsVH.class, refDoctors) {

            @Override
            protected void populateViewHolder(final DoctorsVH viewHolder, final DoctorsData model, final int position) {
                if (model != null) {

                    /** GET THE CLINIC OWNER **/
                    CLINIC_OWNER = model.getClinicOwner();

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
                                    CLINIC_ADDRESS = data.getClinicAddress();
                                    if (CLINIC_ADDRESS != null)    {
                                        viewHolder.txtClinicAddress.setText(CLINIC_ADDRESS);
                                    }

                                    /* GET THE CLINIC CURRENCY */
                                    CLINIC_CURRENCY = data.getClinicCurrency();

                                    /** GET THE CLINIC LATITUDE AND LONGITUDE **/
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

                                    /** GET THE DOCTORS LIKES **/
                                    DatabaseReference refLikes = FirebaseDatabase.getInstance().getReference().child("Doctor Likes");
                                    Query qryLikes = refLikes.orderByChild("doctorID").equalTo(adapter.getRef(position).getKey());
                                    qryLikes.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()) {
                                            } else {
                                                viewHolder.txtDoctorLikes.setText("0 likes");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });

                                    /** GET THE DOCTOR'S PROFILE PICTURE **/
                                    DatabaseReference refDoctor = FirebaseDatabase.getInstance().getReference().child("Doctors");
                                    Query qryDoctor = refDoctor.orderByChild("clinicOwner").equalTo(CLINIC_OWNER);
                                    qryDoctor.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()) {
                                                for (DataSnapshot child: dataSnapshot.getChildren()) {
                                                    DoctorsData data = child.getValue(DoctorsData.class);

                                                    /** SET THE DOCTOR'S NAME **/
                                                    DOCTOR_PREFIX = model.getDoctorPrefix();
                                                    DOCTOR_NAME = model.getDoctorName();
                                                    viewHolder.txtDoctorName.setText(DOCTOR_PREFIX + " " + DOCTOR_NAME);

                                                    /** GET THE PROFILE URL **/
                                                    DOCTORS_PROFILE_URL = data.getDoctorProfile();
                                                    if (DOCTORS_PROFILE_URL != null) {
                                                        Glide.with(getActivity())
                                                                .load(DOCTORS_PROFILE_URL)
                                                                .crossFade()
                                                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                                .centerCrop()
                                                                .into(viewHolder.imgvwDoctorProfile);
                                                    }

                                                    /** GET THE DOCTOR'S EXPERIENCE **/
                                                    DOCTOR_EXPERIENCE = data.getDoctorExperience();
                                                    viewHolder.txtDoctorExp.setText(DOCTOR_EXPERIENCE + " yrs exp");

                                                    /** GET THE DOCTOR'S CHARGES **/
                                                    DOCTOR_CHARGES = data.getDoctorCharges();
                                                    viewHolder.txtDoctorCharges.setText(CLINIC_CURRENCY + " " + DOCTOR_CHARGES);

                                                }
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
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    /** SHOW THE DOCTORS PROFILE **/
//                    viewHolder.linlaDoctorContainer.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(getActivity(), DoctorsProfileActivity.class);
//                            intent.putExtra("DOCTOR_ID", adapter.getRef(position).getKey());
//                            startActivity(intent);
//                        }
//                    });
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

    /** THE DOCTOR'S VIEW HOLDER**/
    private static class DoctorsVH extends RecyclerView.ViewHolder {

        LinearLayout linlaDoctorContainer;
        CircleImageView imgvwDoctorProfile;
        AppCompatTextView txtDoctorCharges;
        AppCompatTextView txtDoctorName;
        AppCompatTextView txtClinicAddress;
        LinearLayout linlaDoctorExp;
        AppCompatTextView txtDoctorExp;
        LinearLayout linlaDoctorLikes;
        AppCompatTextView txtDoctorLikes;
        LinearLayout linlaDoctorReviews;
        AppCompatTextView txtDoctorReviews;
        LinearLayout linlaDoctorDistance;
        AppCompatTextView txtDoctorDistance;

        public DoctorsVH(View itemView) {
            super(itemView);

            linlaDoctorContainer = (LinearLayout) itemView.findViewById(R.id.linlaDoctorContainer);
            imgvwDoctorProfile = (CircleImageView) itemView.findViewById(R.id.imgvwDoctorProfile);
            txtDoctorCharges = (AppCompatTextView) itemView.findViewById(R.id.txtDoctorCharges);
            txtDoctorName = (AppCompatTextView) itemView.findViewById(R.id.txtDoctorName);
            txtClinicAddress = (AppCompatTextView) itemView.findViewById(R.id.txtClinicAddress);
            linlaDoctorExp = (LinearLayout) itemView.findViewById(R.id.linlaDoctorExp);
            txtDoctorExp = (AppCompatTextView) itemView.findViewById(R.id.txtDoctorExp);
            linlaDoctorLikes = (LinearLayout) itemView.findViewById(R.id.linlaDoctorLikes);
            txtDoctorLikes = (AppCompatTextView) itemView.findViewById(R.id.txtDoctorLikes);
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
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listDoctors.setLayoutManager(manager);
        listDoctors.setHasFixedSize(true);
    }

    /** ROUND UP THE DISTANCE **/
    private static float roundUpDistance(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    /***** VERIFY GOOGLE PLAY SERVICES AVAILABLE ON THE DEVICE *****/
    private boolean checkPlayServices() {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        int result = availability.isGooglePlayServicesAvailable(getActivity());
        if (result != ConnectionResult.SUCCESS) {
            if (availability.isUserResolvableError(result)) {
                availability.getErrorDialog(getActivity(), result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity(), "This device is not supported.", Toast.LENGTH_LONG) .show();
            }
            return false;
        }
        return true;
    }

    /***** CREATE AND INSTANTIATE THE GOOGLE API CLIENT INSTANCE *****/
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("CONNECTION FAILED", String.valueOf(connectionResult.getErrorCode()));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Toast.makeText(getActivity(), "Resumed....", Toast.LENGTH_SHORT).show();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onPause() {
        super.onPause();

        Toast.makeText(getActivity(), "Paused....", Toast.LENGTH_SHORT).show();

        /** STOP LOCATION UPDATES **/
        stopLocationUpdates();
    }

    /** STOP LOCATION UPDATES **/
    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {

        currentCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(getActivity(), "CURRENT COORDINATES: " + String.valueOf(currentCoordinates), Toast.LENGTH_SHORT).show();
        currentLocation = location;
        currentLocation.setLatitude(location.getLatitude());
        currentLocation.setLongitude(location.getLongitude());

        /** GET THE FIREBASE USER DETAILS **/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            refDoctors = FirebaseDatabase.getInstance().getReference().child("Doctors");
            /** SETUP THE FIREBASE ADAPTER **/
            setupFirebaseAdapter();
        }

//        if (mGoogleApiClient != null)   {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        }
    }
}