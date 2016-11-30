package com.zenpets.users.doctors;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zenpets.users.R;
import com.zenpets.users.utils.models.ClinicImagesData;
import com.zenpets.users.utils.models.ClinicsData;
import com.zenpets.users.utils.models.DoctorsData;
import com.zenpets.users.utils.models.doctors.ReviewsData;
import com.zenpets.users.utils.models.doctors.ServicesData;
import com.zenpets.users.utils.models.doctors.TimingsData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DoctorsProfileActivity extends AppCompatActivity {

    /** THE DOCTOR ID **/
    String DOCTOR_ID = null;

    /** TODAY'S DAY **/
    String TODAY_DAY = null;

    /** THE FIREBASE RECYCLER ADAPTER INSTANCES **/
    FirebaseRecyclerAdapter adapServices;

    /** DATA TYPE TO STORE THE DATA **/
    String CLINIC_OWNER;
    String CLINIC_NAME;
    String CLINIC_CURRENCY;
    String CLINIC_COVER;
    String CLINIC_ADDRESS;
    Double CLINIC_LATITUDE;
    Double CLINIC_LONGITUDE;
    String DOCTOR_PREFIX;
    String DOCTOR_NAME;
    String DOCTOR_PROFILE;
    String DOCTOR_EXPERIENCE;
    String DOCTOR_CHARGES;

    /** THE TIMING STRINGS **/
    String SUN_MOR_FROM = null;
    String SUN_MOR_TO = null;
    String SUN_AFT_FROM = null;
    String SUN_AFT_TO = null;
    String MON_MOR_FROM = null;
    String MON_MOR_TO = null;
    String MON_AFT_FROM = null;
    String MON_AFT_TO = null;
    String TUE_MOR_FROM = null;
    String TUE_MOR_TO = null;
    String TUE_AFT_FROM = null;
    String TUE_AFT_TO = null;
    String WED_MOR_FROM = null;
    String WED_MOR_TO = null;
    String WED_AFT_FROM = null;
    String WED_AFT_TO = null;
    String THU_MOR_FROM = null;
    String THU_MOR_TO = null;
    String THU_AFT_FROM = null;
    String THU_AFT_TO = null;
    String FRI_MOR_FROM = null;
    String FRI_MOR_TO = null;
    String FRI_AFT_FROM = null;
    String FRI_AFT_TO = null;
    String SAT_MOR_FROM = null;
    String SAT_MOR_TO = null;
    String SAT_AFT_FROM = null;
    String SAT_AFT_TO = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwClinicCover) AppCompatImageView imgvwClinicCover;
    @BindView(R.id.imgvwDoctorProfile) CircleImageView imgvwDoctorProfile;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.txtDoctorEducation) AppCompatTextView txtDoctorEducation;
    @BindView(R.id.linlaExperience) LinearLayout linlaExperience;
    @BindView(R.id.txtExperience) AppCompatTextView txtExperience;
    @BindView(R.id.linlaVotes) LinearLayout linlaVotes;
    @BindView(R.id.txtVotes) AppCompatTextView txtVotes;
    @BindView(R.id.txtDoctorCharges) AppCompatTextView txtDoctorCharges;
    @BindView(R.id.txtClinicAddress) AppCompatTextView txtClinicAddress;
    @BindView(R.id.txtTimingsMorning) AppCompatTextView txtTimingsMorning;
    @BindView(R.id.txtTimingAfternoon) AppCompatTextView txtTimingAfternoon;
    @BindView(R.id.linlaReviews) LinearLayout linlaReviews;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.linlaNoReviews) LinearLayout linlaNoReviews;
    @BindView(R.id.linlaPhotos) LinearLayout linlaPhotos;
    @BindView(R.id.linlaServices) LinearLayout linlaServices;
    @BindView(R.id.listServices) RecyclerView listServices;
    @BindView(R.id.linlaNoServices) LinearLayout linlaNoServices;

    /** THE CLINIC MAP VIEW **/
    MapView clinicMap;

    /** THE ALL CUSTOM VIEWS **/
    View custAllTimings;
    View custAllreviews;
    View custAllServices;

    /** SHOW THE CHARGES DIALOG **/
    @OnClick(R.id.btnChargesInfo) void showChargesInfo()    {
        showChargesDialog();
    }

    /** SHOW ALL TIMINGS **/
    @OnClick(R.id.txtAllTimings) void showAllTimings()  {
        showDoctorTimings();
    }

    /** SHOW ALL REVIEWS **/
    @OnClick(R.id.txtAllReviews) void showAllReviews()  {
        showDoctorReviews();
    }

    /** SHOW ALL SERVICES **/
    @OnClick(R.id.txtAllServices) void showAllServices()    {
        showDoctorServices();
    }

    /** PROVIDE FEEDBACK **/
    @OnClick(R.id.txtFeedback) void provideFeedback()   {
    }

    /** MAKE AN APPOINTMENT **/
    @OnClick(R.id.txtBook) void makeAppointment()   {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_doctor_profile_activity);
        ButterKnife.bind(this);
        clinicMap = (MapView) findViewById(R.id.clinicMap);
        clinicMap.onCreate(savedInstanceState);
        clinicMap.onResume();
        clinicMap.setClickable(false);

        /** INFLATE THE CUSTOM VIEWS **/
        custAllTimings = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_all_timings_view, null);
        custAllreviews = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_all_reviews_list, null);
        custAllServices = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_all_services_list, null);

        /** GET TODAY'S DAY **/
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date d = new Date();
        TODAY_DAY = sdf.format(d);

        /** GET THE INCOMING DATA **/
        getIncomingData();

        /** CONFIGURE THE RECYCLER VIEWS **/
        configRecyclers();
    }

    /** GET THE DOCTOR'S COMPLETE PROFILE **/
    private void getDoctorProfile() {
        DatabaseReference refDoctor = FirebaseDatabase.getInstance().getReference().child("Doctors").child(DOCTOR_ID);
        refDoctor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    final DoctorsData doctors = dataSnapshot.getValue(DoctorsData.class);

                    /* GET THE CLINIC OWNER */
                    CLINIC_OWNER = doctors.getClinicOwner();

                    /* GET THE CLINIC COVER PICTURE */
                    DatabaseReference refClinic = FirebaseDatabase.getInstance().getReference().child("Clinics");
                    Query query = refClinic.orderByChild("clinicOwner").equalTo(CLINIC_OWNER);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    final ClinicsData data = child.getValue(ClinicsData.class);

                                    /* GET THE CLINIC NAME */
                                    CLINIC_NAME = data.getClinicName();

                                    /* GET THE CLINIC CURRENCY */
                                    CLINIC_CURRENCY = data.getClinicCurrency();

                                    /* GET THE CLINIC COVER PICTURE */
                                    CLINIC_COVER = data.getClinicLogo();
                                    if (CLINIC_COVER != null) {
                                        Picasso.with(DoctorsProfileActivity.this)
                                                .load(CLINIC_COVER)
                                                .centerInside()
                                                .resize(1024, 768)
                                                .into(imgvwClinicCover);
                                    }

                                    /* GET THE CLINIC ADDRESS */
                                    CLINIC_ADDRESS = data.getClinicAddress();
                                    txtClinicAddress.setText(CLINIC_ADDRESS);

                                    /* GET THE CLINIC LATITUDE */
                                    CLINIC_LATITUDE = data.getClinicLatitude();

                                    /* GET THE CLINIC LONGITUDE */
                                    CLINIC_LONGITUDE = data.getClinicLongitude();

                                    /* GET THE CLINIC LOCATION */
                                    if (CLINIC_LATITUDE != null && CLINIC_LONGITUDE != null)    {
                                        final LatLng latLng = new LatLng(CLINIC_LATITUDE, CLINIC_LONGITUDE);
                                        clinicMap.getMapAsync(new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(GoogleMap googleMap) {
                                                googleMap.getUiSettings().setMapToolbarEnabled(false);
                                                googleMap.getUiSettings().setAllGesturesEnabled(false);
                                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                                googleMap.setBuildingsEnabled(true);
                                                googleMap.setTrafficEnabled(true);
                                                googleMap.setIndoorEnabled(true);
                                                MarkerOptions options = new MarkerOptions();
                                                options.position(latLng);
                                                options.title(CLINIC_NAME);
                                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                Marker mMarker = googleMap.addMarker(options);
                                                googleMap.addMarker(new MarkerOptions().position(latLng).title(CLINIC_NAME));

                                                /** MOVE THE MAP CAMERA **/
                                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 18));
                                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
                                            }
                                        });
                                    }

                                    /* SET THE DOCTORS NAMES */
                                    String
                                    DOCTOR_PREFIX = doctors.getDoctorPrefix();
                                    DOCTOR_NAME = doctors.getDoctorName();
                                    txtDoctorName.setText(DOCTOR_PREFIX + " " + DOCTOR_NAME);

                                    /** GET THE DOCTOR'S EXPERIENCE **/
                                    DOCTOR_EXPERIENCE = doctors.getDoctorExperience();
                                    txtExperience.setText(DOCTOR_EXPERIENCE + " yrs experience");

                                    /** GET THE DOCTOR'S CHARGES **/
                                    DOCTOR_CHARGES = doctors.getDoctorCharges();
                                    txtDoctorCharges.setText(CLINIC_CURRENCY + " " + DOCTOR_CHARGES);

                                    /* SET THE DOCTOR'S PROFILE */
                                    DOCTOR_PROFILE = doctors.getDoctorProfile();
                                    Picasso.with(DoctorsProfileActivity.this)
                                            .load(DOCTOR_PROFILE)
                                            .centerInside()
                                            .resize(1024, 768)
                                            .into(imgvwDoctorProfile);

                                    /** GET THE DOCTOR'S TIMINGS **/
                                    DatabaseReference refTimings = FirebaseDatabase.getInstance().getReference().child("Doctor Timings");
                                    Query qryTimings = refTimings.orderByChild("doctorID").equalTo(DOCTOR_ID);
                                    qryTimings.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()) {
                                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                    TimingsData timings = child.getValue(TimingsData.class);
                                                    
                                                    /** GET TODAY'S TIMINGS **/
                                                    parseTimings(timings);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });

                                    /** GET THE DOCTOR'S EDUCATION **/
                                    DatabaseReference refEducation = FirebaseDatabase.getInstance().getReference().child("Doctor Education");
                                    Query qryEducation = refEducation.orderByChild("doctorID").equalTo(DOCTOR_ID);
                                    qryEducation.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()) {
                                                StringBuilder builder = new StringBuilder();
                                                for (DataSnapshot education : dataSnapshot.getChildren())   {
                                                    String qualificationName = education.child("qualificationName").getValue(String.class);
                                                    builder.append(qualificationName + ", ");
                                                }
                                                String strQualification = builder.toString();
                                                if (strQualification.endsWith(" "))    {
                                                    strQualification = strQualification.substring(0, strQualification.length() - 1);
                                                }
                                                if (strQualification.endsWith(",")) {
                                                    strQualification = strQualification.substring(0, strQualification.length() - 1);
                                                }

                                                /** SET THE EDUCATION **/
                                                txtDoctorEducation.setText(strQualification);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });

                                    /** GET THE DOCTOR'S REVIEWS **/
                                    DatabaseReference refReviews = FirebaseDatabase.getInstance().getReference().child("Doctor Reviews");
                                    Query qryReviews = refReviews.orderByChild("doctorID").equalTo(DOCTOR_ID);
                                    qryReviews.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()) {
                                                /* HIDE THE NO REVIEWS LAYOUT */
                                                linlaReviews.setVisibility(View.VISIBLE);
                                                linlaNoReviews.setVisibility(View.GONE);
                                            } else {
                                                /* HIDE THE REVIEWS LAYOUT */
                                                linlaNoReviews.setVisibility(View.VISIBLE);
                                                linlaReviews.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });

                                    /** GET THE CLINIC VOTES **/
                                    DatabaseReference refVotes = FirebaseDatabase.getInstance().getReference().child("Doctor Votes");
                                    Query qryVotes = refVotes.orderByChild("doctorID").equalTo(DOCTOR_ID);
                                    qryVotes.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()) {
                                                /* SHOW THE VOTES LAYOUT */
                                                linlaVotes.setVisibility(View.VISIBLE);
                                            } else {
                                                /* HIDE THE VOTES LAYOUT */
                                                linlaVotes.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });

                                    /** GET THE DOCTOR'S SERVICES **/
                                    DatabaseReference refServices = FirebaseDatabase.getInstance().getReference().child("Doctor Services");
                                    Query qryServices = refServices.orderByChild("doctorID").equalTo(DOCTOR_ID);

                                    /** SETUP THE FIREBASE RECYCLER ADAPTER **/
                                    adapServices = new FirebaseRecyclerAdapter<ServicesData, ServicesVH>
                                            (ServicesData.class, R.layout.doctor_services_item, ServicesVH.class, qryServices) {
                                        @Override
                                        protected void populateViewHolder(ServicesVH viewHolder, ServicesData model, int position) {
                                            if (model != null)  {
                                                viewHolder.txtDoctorService.setText(model.getServiceName());
                                            }
                                        }
                                    };

                                    qryServices.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            /** SHOW OR HIDE THE EMPTY LAYOUT **/
                                            emptyShowOrHideServices(dataSnapshot);
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                            /** SHOW OR HIDE THE EMPTY LAYOUT **/
                                            emptyShowOrHideServices(dataSnapshot);
                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                                            /** SHOW OR HIDE THE EMPTY LAYOUT **/
                                            emptyShowOrHideServices(dataSnapshot);
                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                            /** SHOW OR HIDE THE EMPTY LAYOUT **/
                                            emptyShowOrHideServices(dataSnapshot);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });

                                    qryServices.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            /** SHOW OR HIDE THE EMPTY LAYOUT **/
                                            emptyShowOrHideServices(dataSnapshot);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });

                                    /** SET THE ADAPTER **/
                                    listServices.setAdapter(adapServices);

                                    /** GET THE CLINIC IMAGES **/
                                    DatabaseReference refImages = FirebaseDatabase.getInstance().getReference().child("Clinic Images");
                                    Query qryImages = refImages.limitToFirst(4).orderByChild("clinicOwner").equalTo(CLINIC_OWNER);
                                    qryImages.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()) {
//                                                Log.e("IMAGES", String.valueOf(dataSnapshot));
                                                for (DataSnapshot child : dataSnapshot.getChildren())    {
                                                    ClinicImagesData data = child.getValue(ClinicImagesData.class);

                                                    /** GET THE CLINIC IMAGES **/
                                                    String CLINIC_IMAGE = data.getClinicImage();
//                                                    Log.e("CLINIC IMAGE", CLINIC_IMAGE);

                                                    AppCompatImageView imageView = new AppCompatImageView(getApplicationContext());

                                                    /** SET THE IMAGE VIEW DIMENSIONS **/
                                                    LinearLayout.LayoutParams imgvwDimens = new LinearLayout.LayoutParams(130, 130);
                                                    imageView.setLayoutParams(imgvwDimens);

                                                    /** SET THE SCALE TYPE **/
                                                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                                    /** SET THE MARGIN **/
                                                    int dimensMargin = 5;
                                                    float densityMargin = getResources().getDisplayMetrics().density;
                                                    int finalDimensMargin = (int)(dimensMargin * densityMargin);

                                                    LinearLayout.LayoutParams imgvwMargin = new LinearLayout.LayoutParams(130, 130);
                                                    imgvwMargin.setMargins(finalDimensMargin, finalDimensMargin, finalDimensMargin, finalDimensMargin);

                                                    /** SET THE CLINIC IMAGE **/
                                                    if (CLINIC_IMAGE != null)   {
                                                        Picasso.with(DoctorsProfileActivity.this)
                                                                .load(CLINIC_IMAGE)
                                                                .into(imageView);
                                                    }

                                                    /** ADD THE IMAGE VIEW INSTANCE TO THE CONTAINER LINEAR LAYOUT **/
                                                    linlaPhotos.addView(imageView, imgvwMargin);
                                                }
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
    }

    /** SHOW SERVICES OR HIDE SERVICE **/
    private void emptyShowOrHideServices(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChildren()) {
            /* SHOW THE RECYCLER VIEW CONTAINER */
            linlaServices.setVisibility(View.VISIBLE);
            linlaNoServices.setVisibility(View.GONE);
        } else {
            /* SHOW THE NO REVIEWS CONTAINER */
            linlaNoServices.setVisibility(View.VISIBLE);
            linlaServices.setVisibility(View.GONE);
        }
    }

    /** THE SERVICES VIEW HOLDER **/
    private static class ServicesVH extends RecyclerView.ViewHolder {
        AppCompatTextView txtDoctorService;

        public ServicesVH(View itemView) {
            super(itemView);
            txtDoctorService = (AppCompatTextView) itemView.findViewById(R.id.txtDoctorService);
        }
    }

    /** THE REVIEWS VIEW HOLDER **/
    private static class ReviewsVH extends RecyclerView.ViewHolder {
        AppCompatTextView txtDoctorReviews;

        public ReviewsVH(View itemView) {
            super(itemView);
            txtDoctorReviews = (AppCompatTextView) itemView.findViewById(R.id.txtDoctorReviews);
        }
    }

    private void parseTimings(TimingsData timings) {
        /** GET TODAY'S TIMINGS **/
        if (TODAY_DAY.equalsIgnoreCase("Sunday"))  {
            SUN_MOR_FROM = timings.getSunMorFrom();
            SUN_MOR_TO = timings.getSunMorTo();
            SUN_AFT_FROM = timings.getSunAftFrom();
            SUN_AFT_TO = timings.getSunAftTo();
            if (SUN_MOR_FROM != null && SUN_MOR_TO != null) {
                txtTimingsMorning.setText(SUN_MOR_FROM + " - " + SUN_MOR_TO);
            } else {
                txtTimingsMorning.setText("Closed Today");
            }
            if (SUN_AFT_FROM != null && SUN_AFT_TO != null) {
                txtTimingAfternoon.setText(SUN_AFT_FROM + " - " + SUN_AFT_TO);
            } else {
                txtTimingAfternoon.setText("Closed Today");
            }
        } else if (TODAY_DAY.equalsIgnoreCase("Monday"))  {
            MON_MOR_FROM = timings.getMonMorFrom();
            MON_MOR_TO = timings.getMonMorTo();
            MON_AFT_FROM = timings.getMonAftFrom();
            MON_AFT_TO = timings.getMonAftTo();
            if (MON_MOR_FROM != null && MON_MOR_TO != null)  {
                txtTimingsMorning.setText(MON_MOR_FROM + " - " + MON_MOR_TO);
            } else {
                txtTimingsMorning.setText("Closed Today");
            }
            if (MON_AFT_FROM != null && MON_AFT_TO != null) {
                txtTimingAfternoon.setText(MON_AFT_FROM + " - " + MON_AFT_TO);
            } else {
                txtTimingAfternoon.setText("Closed Today");
            }
        } else if (TODAY_DAY.equalsIgnoreCase("Tuesday"))   {
            TUE_MOR_FROM = timings.getTueMorFrom();
            TUE_MOR_TO = timings.getTueMorTo();
            TUE_AFT_FROM = timings.getTueAftFrom();
            TUE_AFT_TO = timings.getTueAftTo();
            if (TUE_MOR_FROM != null && TUE_MOR_TO != null) {
                txtTimingsMorning.setText(TUE_MOR_FROM + " - " + TUE_MOR_TO);
            } else {
                txtTimingsMorning.setText("Closed Today");
            }
            if (TUE_AFT_FROM != null && TUE_AFT_TO != null) {
                txtTimingAfternoon.setText(TUE_AFT_FROM + " - " + TUE_AFT_TO);
            } else {
                txtTimingAfternoon.setText("Closed Today");
            }
        } else if (TODAY_DAY.equalsIgnoreCase("Wednesday")) {
            WED_MOR_FROM = timings.getWedMorFrom();
            WED_MOR_TO = timings.getWedMorTo();
            WED_AFT_FROM = timings.getWedAftFrom();
            WED_AFT_TO = timings.getWedAftTo();
            if (WED_MOR_FROM != null && WED_MOR_TO != null) {
                txtTimingsMorning.setText(WED_MOR_FROM + " - " + WED_MOR_TO);
            } else {
                txtTimingsMorning.setText("Closed Today");
            }
            if (WED_AFT_FROM != null && WED_AFT_TO != null) {
                txtTimingAfternoon.setText(WED_AFT_FROM + " - " + WED_AFT_TO);
            } else {
                txtTimingAfternoon.setText("Closed Today");
            }
        } else if (TODAY_DAY.equalsIgnoreCase("Thursday"))  {
            THU_MOR_FROM = timings.getThuMorFrom();
            THU_MOR_TO = timings.getThuMorTo();
            THU_AFT_FROM = timings.getThuAftFrom();
            THU_AFT_TO = timings.getThuAftTo();
            if (THU_MOR_FROM != null && THU_MOR_TO != null) {
                txtTimingsMorning.setText(THU_MOR_FROM + " - " + THU_MOR_TO);
            } else {
                txtTimingsMorning.setText("Closed Today");
            }
            if (THU_AFT_FROM != null && THU_AFT_TO != null) {
                txtTimingAfternoon.setText(THU_AFT_FROM + " - " + THU_AFT_TO);
            } else {
                txtTimingAfternoon.setText("Closed Today");
            }
        } else if (TODAY_DAY.equalsIgnoreCase("Friday"))    {
            FRI_MOR_FROM = timings.getFriMorFrom();
            FRI_MOR_TO = timings.getFriMorTo();
            FRI_AFT_FROM = timings.getFriAftFrom();
            FRI_AFT_TO = timings.getFriAftTo();
            if (FRI_MOR_FROM != null && FRI_MOR_TO != null) {
                txtTimingsMorning.setText(FRI_MOR_FROM + " - " + FRI_MOR_TO);
            } else {
                txtTimingsMorning.setText("Closed Today");
            }
            if (FRI_AFT_FROM != null && FRI_AFT_TO != null) {
                txtTimingAfternoon.setText(FRI_AFT_FROM + " - " + FRI_AFT_TO);
            } else {
                txtTimingAfternoon.setText("Closed Today");
            }
        } else if (TODAY_DAY.equalsIgnoreCase("Saturday"))  {
            SAT_MOR_FROM = timings.getSatMorFrom();
            SAT_MOR_TO = timings.getSatMorTo();
            SAT_AFT_FROM = timings.getSatAftFrom();
            SAT_AFT_TO = timings.getSatAftTo();
            if (SAT_MOR_FROM != null && SAT_MOR_TO != null) {
                txtTimingsMorning.setText(SAT_MOR_FROM + " - " + SAT_MOR_TO);
            } else {
                txtTimingsMorning.setText("Closed Today");
            }
            if (SAT_AFT_FROM != null && SAT_AFT_TO != null) {
                txtTimingAfternoon.setText(SAT_AFT_FROM + " - " + SAT_AFT_TO);
            } else {
                txtTimingAfternoon.setText("Closed Today");
            }
        }
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("DOCTOR_ID")) {
            DOCTOR_ID = bundle.getString("DOCTOR_ID");
            if (DOCTOR_ID != null)  {
                /** GET THE DOCTOR'S COMPLETE PROFILE **/
                getDoctorProfile();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get required information", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required information", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        clinicMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        clinicMap.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        clinicMap.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clinicMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        clinicMap.onLowMemory();
    }

    /** CONFIGURE THE RECYCLER VIEWS **/
    private void configRecyclers() {
        LinearLayoutManager llmServices = new LinearLayoutManager(this);
        llmServices.setOrientation(LinearLayoutManager.VERTICAL);
        llmServices.setAutoMeasureEnabled(true);
        listServices.setLayoutManager(llmServices);
        listServices.setHasFixedSize(true);
        listServices.setNestedScrollingEnabled(false);

        LinearLayoutManager llmReviews = new LinearLayoutManager(this);
        llmReviews.setAutoMeasureEnabled(true);
        llmReviews.setOrientation(LinearLayoutManager.VERTICAL);
        listReviews.setLayoutManager(llmReviews);
        listReviews.setHasFixedSize(true);
        listReviews.setHasFixedSize(false);
    }

    /** SHOW THE CHARGES DIALOG **/
    private void showChargesDialog() {
        new MaterialDialog.Builder(DoctorsProfileActivity.this)
                .icon(ContextCompat.getDrawable(DoctorsProfileActivity.this, R.drawable.ic_info_outline_black_24dp))
                .title("Consultation Fees")
                .cancelable(true)
                .content("The fees are indicative and might vary depending on the services required and offered. \n\nNOTE: the fees are payable at the Clinic. There are no charges for booking an appointment")
                .positiveText("Dismiss")
                .theme(Theme.LIGHT)
                .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /** SHOW ALL DOCTOR SERVICES **/
    private void showDoctorServices() {

        /** CONFIGURE THE DIALOG **/
        final MaterialDialog dialog = new MaterialDialog.Builder(DoctorsProfileActivity.this)
                .theme(Theme.LIGHT)
                .typeface("Exo2-Regular.otf", "Exo2-Light.otf")
                .title("ALL SERVICES")
                .customView(custAllServices, false)
                .positiveText("Dismiss")
                .build();

        /** GET THE INCOMING DATA **/
        getIncomingData();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Doctor Services");
        Query query = reference.orderByChild("doctorID").equalTo(DOCTOR_ID);

        /** SETUP THE FIREBASE RECYCLER ADAPTER **/
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<ServicesData, ServicesVH>
                (ServicesData.class, R.layout.custom_all_services_item, ServicesVH.class, query) {
            @Override
            protected void populateViewHolder(ServicesVH viewHolder, ServicesData model, int position) {
                if (model != null)  {
                    viewHolder.txtDoctorService.setText(model.getServiceName());
                }
            }
        };

        /** CAST AND CONFIGURE THE RECYCLER VIEW **/
        RecyclerView listDoctorServices = (RecyclerView) dialog.getCustomView().findViewById(R.id.listDoctorServices);
        LinearLayoutManager llmServices = new LinearLayoutManager(this);
        llmServices.setOrientation(LinearLayoutManager.VERTICAL);
        listDoctorServices.setLayoutManager(llmServices);

        /** SET THE ADAPTER **/
        listDoctorServices.setAdapter(adapter);

        /** SHOW THE DIALOG **/
        dialog.show();
    }

    /** SHOW ALL DOCTOR REVIEWS **/
    private void showDoctorReviews() {

        /** CONFIGURE THE DIALOG **/
        final MaterialDialog dialog = new MaterialDialog.Builder(DoctorsProfileActivity.this)
                .theme(Theme.LIGHT)
                .typeface("Exo2-Regular.otf", "Exo2-Light.otf")
                .title("ALL REVIEWS")
                .customView(custAllreviews, false)
                .positiveText("Dismiss")
                .build();

        /** GET THE INCOMING DATA **/
        getIncomingData();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Doctor Reviews");
        Query query = reference.orderByChild("doctorID").equalTo(DOCTOR_ID);

        /** SETUP THE FIREBASE RECYCLER ADAPTER **/
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<ReviewsData, ReviewsVH>
                (ReviewsData.class, R.layout.custom_all_reviews_item, ReviewsVH.class, query) {
            @Override
            protected void populateViewHolder(ReviewsVH viewHolder, ReviewsData model, int position) {
                if (model != null)  {
//                    viewHolder.txtDoctorReviews.setText(model.getServiceName());
                }
            }
        };

        /** CAST AND CONFIGURE THE RECYCLER VIEW **/
        RecyclerView listDoctorServices = (RecyclerView) dialog.getCustomView().findViewById(R.id.listDoctorServices);
        LinearLayoutManager llmServices = new LinearLayoutManager(this);
        llmServices.setOrientation(LinearLayoutManager.VERTICAL);
        listDoctorServices.setLayoutManager(llmServices);

        /** SET THE ADAPTER **/
        listDoctorServices.setAdapter(adapter);

        /** SHOW THE DIALOG **/
        dialog.show();
    }

    /** SHOW ALL TIMINGS **/
    private void showDoctorTimings() {

        /** CONFIGURE THE DIALOG **/
        final MaterialDialog dialog = new MaterialDialog.Builder(DoctorsProfileActivity.this)
                .theme(Theme.LIGHT)
                .typeface("Exo2-Regular.otf", "Exo2-Light.otf")
                .title("ALL TIMINGS")
                .customView(custAllTimings, false)
                .positiveText("Dismiss")
                .build();

        /** GET THE INCOMING DATA **/
        getIncomingData();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Doctor Timings");
        Query query = reference.orderByChild("doctorID").equalTo(DOCTOR_ID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /** THE LAYOUT ELEMENTS **/
                AppCompatTextView txtSunMorning, txtSunAfternoon;
                AppCompatTextView txtMonMorning, txtMonAfternoon;
                AppCompatTextView txtTueMorning, txtTueAfternoon;
                AppCompatTextView txtWedMorning, txtWedAfternoon;
                AppCompatTextView txtThuMorning, txtThuAfternoon;
                AppCompatTextView txtFriMorning, txtFriAfternoon;
                AppCompatTextView txtSatMorning, txtSatAfternoon;

                /** CAST THE LAYOUT ELEMENTS **/
                txtSunMorning = (AppCompatTextView) dialog.getCustomView().findViewById(R.id.txtSunMorning);
                txtSunAfternoon = (AppCompatTextView) dialog.getCustomView().findViewById(R.id.txtSunAfternoon);
                txtMonMorning = (AppCompatTextView) dialog.getCustomView().findViewById(R.id.txtMonMorning);
                txtMonAfternoon = (AppCompatTextView) dialog.getCustomView().findViewById(R.id.txtMonAfternoon);
                txtTueMorning = (AppCompatTextView) dialog.getCustomView().findViewById(R.id.txtTueMorning);
                txtTueAfternoon = (AppCompatTextView) dialog.getCustomView().findViewById(R.id.txtTueAfternoon);
                txtWedMorning = (AppCompatTextView) dialog.getCustomView().findViewById(R.id.txtWedMorning);
                txtWedAfternoon = (AppCompatTextView) dialog.getCustomView().findViewById(R.id.txtWedAfternoon);
                txtThuMorning = (AppCompatTextView) dialog.getCustomView().findViewById(R.id.txtThuMorning);
                txtThuAfternoon = (AppCompatTextView) dialog.getCustomView().findViewById(R.id.txtThuAfternoon);
                txtFriMorning = (AppCompatTextView) dialog.getCustomView().findViewById(R.id.txtFriMorning);
                txtFriAfternoon = (AppCompatTextView) dialog.getCustomView().findViewById(R.id.txtFriAfternoon);
                txtSatMorning = (AppCompatTextView) dialog.getCustomView().findViewById(R.id.txtSatMorning);
                txtSatAfternoon = (AppCompatTextView) dialog.getCustomView().findViewById(R.id.txtSatAfternoon);

                /** SET THE CUSTOM FONT **/
                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Exo2-Light.otf");
                txtSunMorning.setTypeface(typeface);
                txtSunAfternoon.setTypeface(typeface);
                txtMonMorning.setTypeface(typeface);
                txtMonAfternoon.setTypeface(typeface);
                txtTueMorning.setTypeface(typeface);
                txtTueAfternoon.setTypeface(typeface);
                txtWedMorning.setTypeface(typeface);
                txtWedAfternoon.setTypeface(typeface);
                txtThuMorning.setTypeface(typeface);
                txtThuAfternoon.setTypeface(typeface);
                txtFriMorning.setTypeface(typeface);
                txtFriAfternoon.setTypeface(typeface);
                txtSatMorning.setTypeface(typeface);
                txtSatAfternoon.setTypeface(typeface);

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        TimingsData data = child.getValue(TimingsData.class);
                        Log.e("DATA", String.valueOf(dataSnapshot));

                        /** GET THE SUNDAY TIMINGS **/
                        SUN_MOR_FROM = data.getSunMorFrom();
                        SUN_MOR_TO = data.getSunMorTo();
                        SUN_AFT_FROM = data.getSunAftFrom();
                        SUN_AFT_TO = data.getSunAftTo();
                        if (SUN_MOR_FROM != null && SUN_MOR_TO != null) {
                            txtSunMorning.setText(SUN_MOR_FROM + " - " + SUN_MOR_TO);
                        } else {
                            txtSunMorning.setText("Closed");
                        }
                        if (SUN_AFT_FROM != null && SUN_AFT_TO != null) {
                            txtSunAfternoon.setText(SUN_AFT_FROM + " - " + SUN_AFT_TO);
                        } else {
                            txtSunAfternoon.setText("Closed");
                        }

                        /** THE MONDAY TIMINGS **/
                        MON_MOR_FROM = data.getMonMorFrom();
                        MON_MOR_TO = data.getMonMorTo();
                        MON_AFT_FROM = data.getMonAftFrom();
                        MON_AFT_TO = data.getMonAftTo();
                        if (MON_MOR_FROM != null && MON_MOR_TO != null)  {
                            txtMonMorning.setText(MON_MOR_FROM + " - " + MON_MOR_TO);
                        } else {
                            txtMonMorning.setText("Closed");
                        }
                        if (MON_AFT_FROM != null && MON_AFT_TO != null) {
                            txtMonAfternoon.setText(MON_AFT_FROM + " - " + MON_AFT_TO);
                        } else {
                            txtMonAfternoon.setText("Closed");
                        }

                        /** THE TUESDAY TIMINGS **/
                        TUE_MOR_FROM = data.getTueMorFrom();
                        TUE_MOR_TO = data.getTueMorTo();
                        TUE_AFT_FROM = data.getTueAftFrom();
                        TUE_AFT_TO = data.getTueAftTo();
                        if (TUE_MOR_FROM != null && TUE_MOR_TO != null) {
                            txtTueMorning.setText(TUE_MOR_FROM + " - " + TUE_MOR_TO);
                        } else {
                            txtTueMorning.setText("Closed");
                        }
                        if (TUE_AFT_FROM != null && TUE_AFT_TO != null) {
                            txtTueAfternoon.setText(TUE_AFT_FROM + " - " + TUE_AFT_TO);
                        } else {
                            txtTueAfternoon.setText("Closed");
                        }

                        /** THE WEDNESDAY TIMINGS **/
                        WED_MOR_FROM = data.getWedMorFrom();
                        WED_MOR_TO = data.getWedMorTo();
                        WED_AFT_FROM = data.getWedAftFrom();
                        WED_AFT_TO = data.getWedAftTo();
                        if (WED_MOR_FROM != null && WED_MOR_TO != null) {
                            txtWedMorning.setText(WED_MOR_FROM + " - " + WED_MOR_TO);
                        } else {
                            txtWedMorning.setText("Closed");
                        }
                        if (WED_AFT_FROM != null && WED_AFT_TO != null) {
                            txtWedAfternoon.setText(WED_AFT_FROM + " - " + WED_AFT_TO);
                        } else {
                            txtWedAfternoon.setText("Closed");
                        }

                        /** THE THURSDAY TIMINGS **/
                        THU_MOR_FROM = data.getThuMorFrom();
                        THU_MOR_TO = data.getThuMorTo();
                        THU_AFT_FROM = data.getThuAftFrom();
                        THU_AFT_TO = data.getThuAftTo();
                        if (THU_MOR_FROM != null && THU_MOR_TO != null) {
                            txtThuMorning.setText(THU_MOR_FROM + " - " + THU_MOR_TO);
                        } else {
                            txtThuMorning.setText("Closed");
                        }
                        if (THU_AFT_FROM != null && THU_AFT_TO != null) {
                            txtThuAfternoon.setText(THU_AFT_FROM + " - " + THU_AFT_TO);
                        } else {
                            txtThuAfternoon.setText("Closed");
                        }

                        /** THE FRIDAY TIMINGS **/
                        FRI_MOR_FROM = data.getFriMorFrom();
                        FRI_MOR_TO = data.getFriMorTo();
                        FRI_AFT_FROM = data.getFriAftFrom();
                        FRI_AFT_TO = data.getFriAftTo();
                        if (FRI_MOR_FROM != null && FRI_MOR_TO != null) {
                            txtFriMorning.setText(FRI_MOR_FROM + " - " + FRI_MOR_TO);
                        } else {
                            txtFriMorning.setText("Closed");
                        }
                        if (FRI_AFT_FROM != null && FRI_AFT_TO != null) {
                            txtFriAfternoon.setText(FRI_AFT_FROM + " - " + FRI_AFT_TO);
                        } else {
                            txtFriAfternoon.setText("Closed");
                        }

                        /** THE SATURDAY TIMINGS **/
                        SAT_MOR_FROM = data.getSatMorFrom();
                        SAT_MOR_TO = data.getSatMorTo();
                        SAT_AFT_FROM = data.getSatAftFrom();
                        SAT_AFT_TO = data.getSatAftTo();
                        if (SAT_MOR_FROM != null && SAT_MOR_TO != null) {
                            txtSatMorning.setText(SAT_MOR_FROM + " - " + SAT_MOR_TO);
                        } else {
                            txtSatMorning.setText("Closed");
                        }
                        if (SAT_AFT_FROM != null && SAT_AFT_TO != null) {
                            txtSatAfternoon.setText(SAT_AFT_FROM + " - " + SAT_AFT_TO);
                        } else {
                            txtSatAfternoon.setText("Closed");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        /** SHOW THE DIALOG **/
        dialog.show();
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}