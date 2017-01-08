package com.zenpets.users.services;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
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
import com.zenpets.users.utils.models.adoptions.AdoptionsData;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AdoptionListActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /** A GOOGLE API CLIENT INSTANCE **/
    private GoogleApiClient mGoogleApiClient;

    /** A LOCATION REQUEST INSTANCE **/
    private LocationRequest mLocationRequest;

    /** THE USERS CURRENT LOCATION **/
    private Location currentLocation;

    /** PLAY SERVICE REQUEST CODE **/
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaHeaderProgress) LinearLayout linlaHeaderProgress;
    @BindView(R.id.listAdoptions) RecyclerView listAdoptions;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adoptions_list);
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

            /** GET THE LIST OF OPEN ADOPTIONS **/
            fetchOpenAdoptions();
        }
    }

    /** GET THE LIST OF OPEN ADOPTIONS **/
    private void fetchOpenAdoptions() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Adoptions");
        Query query = reference.orderByChild("adoptionStatus").equalTo("Open");
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter< AdoptionsData, AdoptionsVH>
                (AdoptionsData.class, R.layout.adoptions_item, AdoptionsVH.class, query)  {

            @Override
            protected void populateViewHolder(AdoptionsVH viewHolder, AdoptionsData model, int position) {

            }
        };

        query.addListenerForSingleValueEvent(new ValueEventListener() {
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
        listAdoptions.setAdapter(adapter);
    }

    /** SHOW OR HIDE THE EMPTY LAYOUT **/
    private void emptyShowOrHide(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChildren()) {
            /** SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT **/
            listAdoptions.setVisibility(View.VISIBLE);
            linlaEmpty.setVisibility(View.GONE);
        } else {
            /** HIDE THE RECYCLER VIEW AND SHOW THE EMPTY LAYOUT **/
            listAdoptions.setVisibility(View.GONE);
            linlaEmpty.setVisibility(View.VISIBLE);
        }
    }

    private static class AdoptionsVH extends RecyclerView.ViewHolder {

        public AdoptionsVH(View itemView) {
            super(itemView);
        }
    }

    /***** CONFIGURE THE TOOLBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configTB() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = "Adopt a Pet";
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
//        Toast.makeText(getApplicationContext(), "CURRENT COORDINATES: " + String.valueOf(currentCoordinates), Toast.LENGTH_SHORT).show();
        currentLocation = location;
        currentLocation.setLatitude(location.getLatitude());
        currentLocation.setLongitude(location.getLongitude());

        if (mGoogleApiClient != null)   {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
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