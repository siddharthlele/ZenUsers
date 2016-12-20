package com.zenpets.users.details.doctor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.mikepenz.iconics.view.IconicsImageView;
import com.zenpets.users.R;
import com.zenpets.users.details.doctor.review.DoctorReviewsActivity;
import com.zenpets.users.details.doctor.review.ReviewCreatorActivity;
import com.zenpets.users.utils.TypefaceSpan;
import com.zenpets.users.utils.models.clinics.ClinicsData;
import com.zenpets.users.utils.models.reviews.ReviewsData;
import com.zenpets.users.utils.models.services.ServicesData;
import com.zenpets.users.utils.models.services.doctors.DoctorsData;
import com.zenpets.users.utils.models.services.doctors.TimingsData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DoctorsProfile extends AppCompatActivity {

    /** THE DOCTOR ID **/
    private String DOCTOR_ID = null;

    /** TODAY'S DAY **/
    private String TODAY_DAY = null;

    /** THE FIREBASE RECYCLER ADAPTER INSTANCES **/
    private FirebaseRecyclerAdapter adapServices;
    private FirebaseRecyclerAdapter adapReviews;

//    /** THE CLINIC IMAGES ADAPTER, THE ARRAY LIST AND THE STRING ARRAY **/
//    private ClinicImagesAdapter adapter;
//    private List<String> arrImages = new ArrayList<>();
//    private String[] strImages;

    /** DATA TYPE TO STORE THE DATA **/
    private String CLINIC_ID;
    private String CLINIC_OWNER;
    private String CLINIC_NAME;
    private String CLINIC_CURRENCY;
    private String CLINIC_COVER;
    private String CLINIC_ADDRESS;
    private Double CLINIC_LATITUDE;
    private Double CLINIC_LONGITUDE;
    private String DOCTOR_PREFIX;
    private String DOCTOR_NAME;
    private String DOCTOR_PROFILE;
    private String DOCTOR_EXPERIENCE;
    private String DOCTOR_CHARGES;

    /** THE TIMING STRINGS **/
    private String SUN_MOR_FROM = null;
    private String SUN_MOR_TO = null;
    private String SUN_AFT_FROM = null;
    private String SUN_AFT_TO = null;
    private String MON_MOR_FROM = null;
    private String MON_MOR_TO = null;
    private String MON_AFT_FROM = null;
    private String MON_AFT_TO = null;
    private String TUE_MOR_FROM = null;
    private String TUE_MOR_TO = null;
    private String TUE_AFT_FROM = null;
    private String TUE_AFT_TO = null;
    private String WED_MOR_FROM = null;
    private String WED_MOR_TO = null;
    private String WED_AFT_FROM = null;
    private String WED_AFT_TO = null;
    private String THU_MOR_FROM = null;
    private String THU_MOR_TO = null;
    private String THU_AFT_FROM = null;
    private String THU_AFT_TO = null;
    private String FRI_MOR_FROM = null;
    private String FRI_MOR_TO = null;
    private String FRI_AFT_FROM = null;
    private String FRI_AFT_TO = null;
    private String SAT_MOR_FROM = null;
    private String SAT_MOR_TO = null;
    private String SAT_AFT_FROM = null;
    private String SAT_AFT_TO = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwClinicCover) AppCompatImageView imgvwClinicCover;
    @BindView(R.id.imgvwDoctorProfile) CircleImageView imgvwDoctorProfile;
    @BindView(R.id.txtDoctorName) AppCompatTextView txtDoctorName;
    @BindView(R.id.linlaExperience) LinearLayout linlaExperience;
    @BindView(R.id.txtExperience) AppCompatTextView txtExperience;
    @BindView(R.id.linlaVotes) LinearLayout linlaVotes;
    @BindView(R.id.txtVotes) AppCompatTextView txtVotes;
    @BindView(R.id.txtDoctorCharges) AppCompatTextView txtDoctorCharges;
    @BindView(R.id.txtClinicName) AppCompatTextView txtClinicName;
    @BindView(R.id.txtClinicAddress) AppCompatTextView txtClinicAddress;
    @BindView(R.id.txtOpen) AppCompatTextView txtOpen;
    @BindView(R.id.txtClosed) AppCompatTextView txtClosed;
    @BindView(R.id.txtTimingsMorning) AppCompatTextView txtTimingsMorning;
    @BindView(R.id.txtTimingAfternoon) AppCompatTextView txtTimingAfternoon;
    @BindView(R.id.linlaReviews) LinearLayout linlaReviews;
    @BindView(R.id.listReviews) RecyclerView listReviews;
    @BindView(R.id.linlaNoReviews) LinearLayout linlaNoReviews;
//    @BindView(R.id.listClinicImages) RecyclerView listClinicImages;
    @BindView(R.id.linlaServices) LinearLayout linlaServices;
    @BindView(R.id.listServices) RecyclerView listServices;
    @BindView(R.id.linlaNoServices) LinearLayout linlaNoServices;

    /** THE CLINIC MAP VIEW **/
    private MapView clinicMap;

    /** THE ALL CUSTOM VIEWS **/
    private View custAllTimings;
    private View custAllServices;

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
        Intent intent = new Intent(DoctorsProfile.this, DoctorReviewsActivity.class);
        intent.putExtra("DOCTOR_ID", DOCTOR_ID);
        startActivity(intent);
    }

    /** SHOW ALL SERVICES **/
    @OnClick(R.id.txtAllServices) void showAllServices()    {
        showDoctorServices();
    }

    /** PROVIDE FEEDBACK **/
    @OnClick(R.id.txtFeedback) void provideFeedback()   {
        Intent intent = new Intent(this, ReviewCreatorActivity.class);
        intent.putExtra("DOCTOR_ID", DOCTOR_ID);
        startActivity(intent);
    }

    /** MAKE AN APPOINTMENT **/
    @OnClick(R.id.txtBook) void makeAppointment()   {
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctors_profile);
        ButterKnife.bind(this);
        clinicMap = (MapView) findViewById(R.id.clinicMap);
        clinicMap.onCreate(savedInstanceState);
        clinicMap.onResume();
        clinicMap.setClickable(false);

        /** INFLATE THE CUSTOM VIEWS **/
        custAllTimings = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_all_timings_view, null);
        custAllServices = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_all_services_list, null);

        /** GET TODAY'S DAY **/
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date d = new Date();
        TODAY_DAY = sdf.format(d);

        /** GET THE INCOMING DATA **/
        getIncomingData();

        /** CONFIGURE THE RECYCLER VIEWS **/
        configRecyclers();

        /***** CONFIGURE THE TOOLBAR *****/
        configTB();
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
                                    final ClinicsData clinic = child.getValue(ClinicsData.class);

                                    /* GET THE CLINIC ID */
                                    CLINIC_ID = child.getKey();

                                    /* GET THE CLINIC NAME */
                                    CLINIC_NAME = clinic.getClinicName();

                                    /* GET THE CLINIC CURRENCY */
                                    CLINIC_CURRENCY = clinic.getClinicCurrency();

                                    /* GET THE CLINIC COVER PICTURE */
                                    CLINIC_COVER = clinic.getClinicLogo();
                                    if (CLINIC_COVER != null) {
                                        Glide.with(getApplicationContext())
                                                .load(CLINIC_COVER)
                                                .crossFade()
                                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                .centerCrop()
                                                .into(imgvwClinicCover);
                                    }

                                    /* GET THE CLINIC NAME AND ADDRESS */
                                    txtClinicName.setText(CLINIC_NAME);
                                    CLINIC_ADDRESS = clinic.getClinicAddress();
                                    txtClinicAddress.setText(CLINIC_ADDRESS);

                                    /* GET THE CLINIC LATITUDE */
                                    CLINIC_LATITUDE = clinic.getClinicLatitude();

                                    /* GET THE CLINIC LONGITUDE */
                                    CLINIC_LONGITUDE = clinic.getClinicLongitude();

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
                                                googleMap.setTrafficEnabled(false);
                                                googleMap.setIndoorEnabled(false);
                                                MarkerOptions options = new MarkerOptions();
                                                options.position(latLng);
                                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                                Marker mMarker = googleMap.addMarker(options);
                                                googleMap.addMarker(options);

                                                /** MOVE THE MAP CAMERA **/
                                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 18));
                                                googleMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
                                            }
                                        });
                                    }

                                    /* SET THE DOCTORS NAMES */
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
                                    Glide.with(getApplicationContext())
                                            .load(DOCTOR_PROFILE)
                                            .crossFade()
                                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                            .centerCrop()
                                            .into(imgvwDoctorProfile);

                                    /** GET THE DOCTOR'S TIMINGS **/
                                    DatabaseReference refTimings = FirebaseDatabase.getInstance().getReference().child("Doctors").child(DOCTOR_ID).child("Timings");
                                    refTimings.addValueEventListener(new ValueEventListener() {
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

                                    /** GET THE DOCTOR'S REVIEWS **/
                                    DatabaseReference refReviews = FirebaseDatabase.getInstance().getReference().child("Doctors").child(DOCTOR_ID).child("Reviews");
                                    Query qryReviews = refReviews.limitToLast(3);

                                    /** SETUP THE REVIEWS FIREBASE RECYCLER ADAPTER **/
                                    adapReviews = new FirebaseRecyclerAdapter<ReviewsData, ReviewsVH>
                                            (ReviewsData.class, R.layout.doctors_profile_reviews_item, ReviewsVH.class, qryReviews) {
                                        @Override
                                        protected void populateViewHolder(ReviewsVH viewHolder, ReviewsData model, int position) {
                                            if (model != null)  {
                                                /** GET THE VISIT REASON **/
                                                viewHolder.txtVisitReason.setText(model.getVisitReason());

                                                /** GET THE DOCTOR EXPERIENCE **/
                                                viewHolder.txtVisitExperience.setText(model.getDoctorExperience());

                                                /** GET THE LIKE STATUS **/
                                                String strLikeStatus = model.getRecommendStatus();
                                                if (strLikeStatus.equalsIgnoreCase("Yes"))  {
                                                    viewHolder.imgvwLikeStatus.setIcon("faw-thumbs-o-up");
                                                    viewHolder.imgvwLikeStatus.setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_blue_dark));
                                                } else if (strLikeStatus.equalsIgnoreCase("No"))    {
                                                    viewHolder.imgvwLikeStatus.setIcon("faw-thumbs-o-down");
                                                    viewHolder.imgvwLikeStatus.setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
                                                }

                                                /** GET THE USER NAME **/
                                                viewHolder.txtUserName.setText(model.getUserName());

                                                /** GET THE TIME STAMP **/
                                                String strTimeStamp = model.getTimeStamp();
//                                                viewHolder.txtPostedAgo.setText(model.getTimeStamp());
                                            }
                                        }
                                    };

                                    qryReviews.addChildEventListener(reviewChildEventListener);
                                    qryReviews.addListenerForSingleValueEvent(reviewValueEventListener);

                                    /** SET THE ADAPTER **/
                                    listReviews.setAdapter(adapReviews);

                                    /** GET THE DOCTOR'S SERVICES **/
                                    DatabaseReference refServices = FirebaseDatabase.getInstance().getReference().child("Doctors").child(DOCTOR_ID).child("Services");
                                    Query qryServices = refServices.limitToLast(3);

                                    /** SETUP THE SERVICES FIREBASE RECYCLER ADAPTER **/
                                    adapServices = new FirebaseRecyclerAdapter<ServicesData, ServicesVH>
                                            (ServicesData.class, R.layout.doctors_profile_services_item, ServicesVH.class, qryServices) {
                                        @Override
                                        protected void populateViewHolder(ServicesVH viewHolder, ServicesData model, int position) {
                                            if (model != null)  {
                                                viewHolder.txtDoctorService.setText(model.getServiceName());
                                            }
                                        }
                                    };

                                    qryServices.addChildEventListener(servicesChildEventListener);
                                    qryServices.addListenerForSingleValueEvent(servicesValueEventListener);

                                    /** SET THE ADAPTER **/
                                    listServices.setAdapter(adapServices);

//                                    /** GET THE CLINIC IMAGES **/
//                                    DatabaseReference refImages = FirebaseDatabase.getInstance().getReference().child("Clinics").child(CLINIC_ID).child("Images");
//                                    refImages.addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot.hasChildren()) {
//
//                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//
//                                                    /** GET THE CLINIC IMAGE **/
//                                                    String clinicImage = postSnapshot.child("clinicImage").getValue(String.class);
//                                                    arrImages.add(clinicImage);
//                                                }
//
//                                                /** CONVERT THE LIST ARRAY TO A STRING ARRAY **/
//                                                strImages = arrImages.toArray(new String[arrImages.size()]);
//
//                                                /** INSTANTIATE THE CLINIC IMAGES ADAPTER **/
//                                                adapter = new ClinicImagesAdapter(DoctorsProfile.this, arrImages, CLINIC_ID);
//
//                                                /** SET THE ADAPTER TO THE RECYCLER VIEW **/
//                                                listClinicImages.setAdapter(adapter);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//                                        }
//                                    });

                                    /** GET THE DOCTOR'S REVIEWS (LIKES) **/
                                    DatabaseReference refLikes = FirebaseDatabase.getInstance().getReference().child("Doctors").child(DOCTOR_ID).child("Reviews");
                                    refLikes.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()) {

                                                /** GET THE NUMBER LIKES **/
                                                int totalLikes = (int) dataSnapshot.getChildrenCount();

                                                /** GET THE NUMBER OF "YES" **/
                                                int noOfYes = 0;
                                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                                    String recommendStatus = postSnapshot.child("recommendStatus").getValue(String.class);
                                                    if (recommendStatus.equalsIgnoreCase("Yes"))    {
                                                        noOfYes++;
                                                    }
                                                }

                                                /** CALCULATE THE PERCENTAGE OF LIKES **/
                                                double percentLikes = ((double)noOfYes / totalLikes) * 100;
                                                int finalPercentLikes = (int)percentLikes;

                                                /** SET THE PERCENTAGE AND THE TOTAL LIKES **/
                                                txtVotes.setText(String.valueOf(finalPercentLikes) + "% (" + String.valueOf(totalLikes) + " Votes)");

                                            } else {
                                                txtVotes.setText("No votes yet");
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

    private void emptyShowOrHideReviews(DataSnapshot dataSnapshot)  {
        if (dataSnapshot.hasChildren()) {
            /* SHOW THE RECYCLER VIEW CONTAINER */
            linlaReviews.setVisibility(View.VISIBLE);
            linlaNoReviews.setVisibility(View.GONE);
        } else {
            /* SHOW THE NO REVIEWS CONTAINER */
            linlaNoReviews.setVisibility(View.VISIBLE);
            linlaReviews.setVisibility(View.GONE);
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
        AppCompatTextView txtVisitReason;
        AppCompatTextView txtVisitExperience;
        IconicsImageView imgvwLikeStatus;
        AppCompatTextView txtUserName;
        AppCompatTextView txtPostedAgo;

        public ReviewsVH(View itemView) {
            super(itemView);
            txtVisitReason = (AppCompatTextView) itemView.findViewById(R.id.txtVisitReason);
            txtVisitExperience = (AppCompatTextView) itemView.findViewById(R.id.txtVisitExperience);
            imgvwLikeStatus = (IconicsImageView) itemView.findViewById(R.id.imgvwLikeStatus);
            txtUserName = (AppCompatTextView) itemView.findViewById(R.id.txtUserName);
            txtPostedAgo = (AppCompatTextView) itemView.findViewById(R.id.txtPostedAgo);
        }
    }

    private void parseTimings(TimingsData timings) {
        /** GET TODAY'S TIMINGS **/
        if (TODAY_DAY.equalsIgnoreCase("Sunday"))  {
            SUN_MOR_FROM = timings.getSunMorFrom();
            SUN_MOR_TO = timings.getSunMorTo();
            SUN_AFT_FROM = timings.getSunAftFrom();
            SUN_AFT_TO = timings.getSunAftTo();

            /** SET THE OPEN / CLOSED STATUS **/
            if (SUN_MOR_FROM != null && SUN_MOR_TO != null || SUN_AFT_FROM != null && SUN_AFT_TO != null)   {
                txtOpen.setVisibility(View.VISIBLE);
                txtClosed.setVisibility(View.GONE);
            } else {
                txtClosed.setVisibility(View.VISIBLE);
                txtOpen.setVisibility(View.GONE);
            }

            if (SUN_MOR_FROM != null && SUN_MOR_TO != null) {
                txtTimingsMorning.setText(SUN_MOR_FROM + " - " + SUN_MOR_TO);
                txtTimingsMorning.setVisibility(View.VISIBLE);
            } else {
                txtTimingsMorning.setVisibility(View.GONE);
            }
            if (SUN_AFT_FROM != null && SUN_AFT_TO != null) {
                txtTimingAfternoon.setText(SUN_AFT_FROM + " - " + SUN_AFT_TO);
                txtTimingAfternoon.setVisibility(View.VISIBLE);
            } else {
                txtTimingAfternoon.setVisibility(View.GONE);
            }
        } else if (TODAY_DAY.equalsIgnoreCase("Monday"))  {
            MON_MOR_FROM = timings.getMonMorFrom();
            MON_MOR_TO = timings.getMonMorTo();
            MON_AFT_FROM = timings.getMonAftFrom();
            MON_AFT_TO = timings.getMonAftTo();

            /** SET THE OPEN / CLOSED STATUS **/
            if (MON_MOR_FROM != null && MON_MOR_TO != null || MON_AFT_FROM != null && MON_AFT_TO != null)   {
                txtOpen.setVisibility(View.VISIBLE);
                txtClosed.setVisibility(View.GONE);
            } else {
                txtClosed.setVisibility(View.VISIBLE);
                txtOpen.setVisibility(View.GONE);
            }

            if (MON_MOR_FROM != null && MON_MOR_TO != null)  {
                txtTimingsMorning.setText(MON_MOR_FROM + " - " + MON_MOR_TO);
                txtTimingsMorning.setVisibility(View.VISIBLE);
            } else {
                txtTimingsMorning.setVisibility(View.GONE);
            }
            if (MON_AFT_FROM != null && MON_AFT_TO != null) {
                txtTimingAfternoon.setText(MON_AFT_FROM + " - " + MON_AFT_TO);
                txtTimingAfternoon.setVisibility(View.VISIBLE);
            } else {
                txtTimingAfternoon.setVisibility(View.GONE);
            }
        } else if (TODAY_DAY.equalsIgnoreCase("Tuesday"))   {
            TUE_MOR_FROM = timings.getTueMorFrom();
            TUE_MOR_TO = timings.getTueMorTo();
            TUE_AFT_FROM = timings.getTueAftFrom();
            TUE_AFT_TO = timings.getTueAftTo();

            /** SET THE OPEN / CLOSED STATUS **/
            if (TUE_MOR_FROM != null && TUE_MOR_TO != null || TUE_AFT_FROM != null && TUE_AFT_TO != null)   {
                txtOpen.setVisibility(View.VISIBLE);
                txtClosed.setVisibility(View.GONE);
            } else {
                txtClosed.setVisibility(View.VISIBLE);
                txtOpen.setVisibility(View.GONE);
            }

            if (TUE_MOR_FROM != null && TUE_MOR_TO != null) {
                txtTimingsMorning.setText(TUE_MOR_FROM + " - " + TUE_MOR_TO);
                txtTimingsMorning.setVisibility(View.VISIBLE);
            } else {
                txtTimingsMorning.setVisibility(View.GONE);
            }
            if (TUE_AFT_FROM != null && TUE_AFT_TO != null) {
                txtTimingAfternoon.setText(TUE_AFT_FROM + " - " + TUE_AFT_TO);
                txtTimingAfternoon.setVisibility(View.VISIBLE);
            } else {
                txtTimingAfternoon.setVisibility(View.GONE);
            }
        } else if (TODAY_DAY.equalsIgnoreCase("Wednesday")) {
            WED_MOR_FROM = timings.getWedMorFrom();
            WED_MOR_TO = timings.getWedMorTo();
            WED_AFT_FROM = timings.getWedAftFrom();
            WED_AFT_TO = timings.getWedAftTo();

            /** SET THE OPEN / CLOSED STATUS **/
            if (WED_MOR_FROM != null && WED_MOR_TO != null || WED_AFT_FROM != null && WED_AFT_TO != null)   {
                txtOpen.setVisibility(View.VISIBLE);
                txtClosed.setVisibility(View.GONE);
            } else {
                txtClosed.setVisibility(View.VISIBLE);
                txtOpen.setVisibility(View.GONE);
            }

            if (WED_MOR_FROM != null && WED_MOR_TO != null) {
                txtTimingsMorning.setText(WED_MOR_FROM + " - " + WED_MOR_TO);
                txtTimingsMorning.setVisibility(View.VISIBLE);
            } else {
                txtTimingsMorning.setVisibility(View.GONE);
            }
            if (WED_AFT_FROM != null && WED_AFT_TO != null) {
                txtTimingAfternoon.setText(WED_AFT_FROM + " - " + WED_AFT_TO);
                txtTimingAfternoon.setVisibility(View.VISIBLE);
            } else {
                txtTimingAfternoon.setVisibility(View.GONE);
            }
        } else if (TODAY_DAY.equalsIgnoreCase("Thursday"))  {
            THU_MOR_FROM = timings.getThuMorFrom();
            THU_MOR_TO = timings.getThuMorTo();
            THU_AFT_FROM = timings.getThuAftFrom();
            THU_AFT_TO = timings.getThuAftTo();

            /** SET THE OPEN / CLOSED STATUS **/
            if (THU_MOR_FROM != null && THU_MOR_TO != null || THU_AFT_FROM != null && THU_AFT_TO != null)   {
                txtOpen.setVisibility(View.VISIBLE);
                txtClosed.setVisibility(View.GONE);
            } else {
                txtClosed.setVisibility(View.VISIBLE);
                txtOpen.setVisibility(View.GONE);
            }

            if (THU_MOR_FROM != null && THU_MOR_TO != null) {
                txtTimingsMorning.setText(THU_MOR_FROM + " - " + THU_MOR_TO);
                txtTimingsMorning.setVisibility(View.VISIBLE);
            } else {
                txtTimingsMorning.setVisibility(View.GONE);
            }
            if (THU_AFT_FROM != null && THU_AFT_TO != null) {
                txtTimingAfternoon.setText(THU_AFT_FROM + " - " + THU_AFT_TO);
                txtTimingAfternoon.setVisibility(View.VISIBLE);
            } else {
                txtTimingAfternoon.setVisibility(View.GONE);
            }
        } else if (TODAY_DAY.equalsIgnoreCase("Friday"))    {
            FRI_MOR_FROM = timings.getFriMorFrom();
            FRI_MOR_TO = timings.getFriMorTo();
            FRI_AFT_FROM = timings.getFriAftFrom();
            FRI_AFT_TO = timings.getFriAftTo();

            /** SET THE OPEN / CLOSED STATUS **/
            if (FRI_MOR_FROM != null && FRI_MOR_TO != null || FRI_AFT_FROM != null && FRI_AFT_TO != null)   {
                txtOpen.setVisibility(View.VISIBLE);
                txtClosed.setVisibility(View.GONE);
            } else {
                txtClosed.setVisibility(View.VISIBLE);
                txtOpen.setVisibility(View.GONE);
            }

            if (FRI_MOR_FROM != null && FRI_MOR_TO != null) {
                txtTimingsMorning.setText(FRI_MOR_FROM + " - " + FRI_MOR_TO);
                txtTimingsMorning.setVisibility(View.VISIBLE);
            } else {
                txtTimingsMorning.setVisibility(View.GONE);
            }
            if (FRI_AFT_FROM != null && FRI_AFT_TO != null) {
                txtTimingAfternoon.setText(FRI_AFT_FROM + " - " + FRI_AFT_TO);
                txtTimingAfternoon.setVisibility(View.VISIBLE);
            } else {
                txtTimingAfternoon.setVisibility(View.GONE);
            }
        } else if (TODAY_DAY.equalsIgnoreCase("Saturday"))  {
            SAT_MOR_FROM = timings.getSatMorFrom();
            SAT_MOR_TO = timings.getSatMorTo();
            SAT_AFT_FROM = timings.getSatAftFrom();
            SAT_AFT_TO = timings.getSatAftTo();

            /** SET THE OPEN / CLOSED STATUS **/
            if (SAT_MOR_FROM != null && SAT_MOR_TO != null || SAT_AFT_FROM != null && SAT_AFT_TO != null)   {
                txtOpen.setVisibility(View.VISIBLE);
                txtClosed.setVisibility(View.GONE);
            } else {
                txtClosed.setVisibility(View.VISIBLE);
                txtOpen.setVisibility(View.GONE);
            }

            if (SAT_MOR_FROM != null && SAT_MOR_TO != null) {
                txtTimingsMorning.setText(SAT_MOR_FROM + " - " + SAT_MOR_TO);
                txtTimingsMorning.setVisibility(View.VISIBLE);
            } else {
                txtTimingsMorning.setVisibility(View.GONE);
            }
            if (SAT_AFT_FROM != null && SAT_AFT_TO != null) {
                txtTimingAfternoon.setText(SAT_AFT_FROM + " - " + SAT_AFT_TO);
                txtTimingAfternoon.setVisibility(View.VISIBLE);
            } else {
                txtTimingAfternoon.setVisibility(View.GONE);
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
        listServices.setHasFixedSize(false);
        listServices.setNestedScrollingEnabled(false);

        LinearLayoutManager llmReviews = new LinearLayoutManager(this);
        llmReviews.setOrientation(LinearLayoutManager.VERTICAL);
        llmReviews.setAutoMeasureEnabled(true);
        listReviews.setLayoutManager(llmReviews);
        listReviews.setHasFixedSize(false);
        listReviews.setNestedScrollingEnabled(false);

//        LinearLayoutManager llmImages = new LinearLayoutManager(this);
//        llmImages.setOrientation(LinearLayoutManager.HORIZONTAL);
//        llmImages.setAutoMeasureEnabled(true);
//        listClinicImages.setLayoutManager(llmImages);
//        listClinicImages.setHasFixedSize(false);
//        listClinicImages.setNestedScrollingEnabled(false);
    }

    /** SHOW THE CHARGES DIALOG **/
    private void showChargesDialog() {
        new MaterialDialog.Builder(DoctorsProfile.this)
                .icon(ContextCompat.getDrawable(DoctorsProfile.this, R.drawable.ic_info_outline_black_24dp))
                .title("Consultation Fees")
                .cancelable(true)
                .content("The fees are indicative and might vary depending on the services required and offered. \n\nNOTE: the fees are payable at the Clinic. There are no charges for booking an appointment")
                .positiveText("Dismiss")
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
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
        final MaterialDialog dialog = new MaterialDialog.Builder(DoctorsProfile.this)
                .theme(Theme.LIGHT)
                .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                .title("ALL SERVICES")
                .customView(custAllServices, false)
                .positiveText("Dismiss")
                .build();

        /** GET THE INCOMING DATA **/
        getIncomingData();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Doctors").child(DOCTOR_ID).child("Services");

        /** SETUP THE FIREBASE RECYCLER ADAPTER **/
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<ServicesData, ServicesVH>
                (ServicesData.class, R.layout.custom_all_services_item, ServicesVH.class, reference) {
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

    /** SHOW ALL TIMINGS **/
    private void showDoctorTimings() {

        /** CONFIGURE THE DIALOG **/
        final MaterialDialog dialog = new MaterialDialog.Builder(DoctorsProfile.this)
                .theme(Theme.LIGHT)
                .typeface("Roboto-Regular.ttf", "Roboto-Light.ttf")
                .title("ALL TIMINGS")
                .customView(custAllTimings, false)
                .positiveText("Dismiss")
                .build();

        /** GET THE INCOMING DATA **/
        getIncomingData();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Doctors").child(DOCTOR_ID).child("Timings");
        reference.addValueEventListener(new ValueEventListener() {
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
                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
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
//                        Log.e("DATA", String.valueOf(dataSnapshot));

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

    /** THE DOCTOR SERVICES CHILD EVENT LISTENER **/
    private ChildEventListener servicesChildEventListener = new ChildEventListener() {
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
    };

    /** THE DOCTOR SERVICES VALUE EVENT LISTENER **/
    private ValueEventListener servicesValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            /** SHOW OR HIDE THE EMPTY LAYOUT **/
            emptyShowOrHideServices(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    /** THE DOCTOR REVIEWS CHILD EVENT LISTENER **/
    private ChildEventListener reviewChildEventListener = new ChildEventListener() {
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

    /** THE DOCTOR REVIEWS VALUE EVENT LISTENER **/
    private ValueEventListener reviewValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            /** SHOW OR HIDE THE EMPTY LAYOUT **/
            emptyShowOrHideReviews(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    /***** CONFIGURE THE TOOLBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configTB() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = "Doctor Details";
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}