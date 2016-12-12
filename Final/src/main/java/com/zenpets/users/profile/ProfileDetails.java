package com.zenpets.users.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zenpets.users.R;
import com.zenpets.users.editor.ProfileEditor;
import com.zenpets.users.utils.TypefaceSpan;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileDetails extends AppCompatActivity {

    /** THE USER ID AND KEY **/
    String USER_ID = null;
    String USER_KEY = null;

    /** STRINGS TO HOLD THE USER DETAILS **/
    private String USER_PROFILE_PICTURE = null;
    String USER_PHONE_PREFIX = null;
    private String USER_PHONE_NUMBER = null;
    private String USER_STATE = null;
    private String USER_CITY = null;
    private String USER_GENDER = "Male";
    private String USER_DATE_OF_BIRTH = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwProfilePicture) AppCompatImageView imgvwProfilePicture;
    @BindView(R.id.txtUserName) AppCompatTextView txtUserName;
    @BindView(R.id.txtEmailAddress) AppCompatTextView txtEmailAddress;
    @BindView(R.id.txtPhoneNumber) AppCompatTextView txtPhoneNumber;
    @BindView(R.id.txtCity) AppCompatTextView txtCity;
    @BindView(R.id.txtGender) AppCompatTextView txtGender;
    @BindView(R.id.txtDOB) AppCompatTextView txtDOB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_details);
        ButterKnife.bind(this);

        /***** CONFIGURE THE TOOLBAR *****/
        configTB();

        /** GET THE PROFILE DETAILS (USER NAME AND PROFILE PICTURE) **/
        getProfile();
    }

    /** GET THE PROFILE DETAILS (USER NAME AND PROFILE PICTURE) **/
    private void getProfile() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            /** GET THE USER ID **/
            USER_ID = user.getUid();

            /** GET THE USER NAME **/
            String USER_NAME = user.getDisplayName();

            /** GET THE EMAIL ADDRESS **/
            String USER_EMAIL_ADDRESS = user.getEmail();

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

            /** SET THE EMAIL ADDRESS **/
            if (USER_EMAIL_ADDRESS != null)  {
                txtEmailAddress.setText(USER_EMAIL_ADDRESS);
            }

            /** SET THE PROFILE PICTURE **/
            if (USER_PROFILE_PICTURE != null)   {
                Glide.with(ProfileDetails.this)
                        .load(USER_PROFILE_PICTURE)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .into(imgvwProfilePicture);
            }
            
            /** GET THE PROFILE DETAILS **/
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
            Query query = reference.orderByChild("userID").equalTo(USER_ID);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        /* GET THE USER KEY */
                        USER_KEY = postSnapshot.getKey();

                        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("Users");
                        Query query = refUser.orderByChild("userID").equalTo(USER_ID);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot userData) {
                                for (DataSnapshot user : userData.getChildren()) {
//                                    Log.e("USER", String.valueOf(user));

                                    /** GET THE PHONE PREFIX AND NUMBER **/
                                    USER_PHONE_PREFIX = user.child("userPhonePrefix").getValue(String.class);
                                    USER_PHONE_NUMBER = user.child("userPhoneNumber").getValue(String.class);
                                    if (TextUtils.isEmpty(USER_PHONE_NUMBER) || TextUtils.isEmpty(USER_PHONE_PREFIX))   {
                                        txtPhoneNumber.setText("Number unspecified");
                                    } else {
                                        txtPhoneNumber.setText(USER_PHONE_PREFIX + USER_PHONE_NUMBER);
                                    }

                                    /** GET THE STATE AND CITY **/
                                    USER_STATE = user.child("userState").getValue(String.class);
                                    USER_CITY = user.child("userCity").getValue(String.class);
                                    if (TextUtils.isEmpty(USER_STATE) || TextUtils.isEmpty(USER_CITY))  {
                                        txtCity.setText("Location unspecified");
                                    } else {
                                        txtCity.setText(USER_STATE + ", " + USER_CITY);
                                    }

                                    /** GET THE GENDER **/
                                    USER_GENDER = user.child("userGender").getValue(String.class);
                                    if (TextUtils.isEmpty(USER_GENDER)) {
                                        txtGender.setText("Gender unspecified");
                                    } else {
                                        txtGender.setText(USER_GENDER);
                                    }

                                    /** GET THE DATE OF BIRTH  **/
                                    USER_DATE_OF_BIRTH = user.child("userDOB").getValue(String.class);
                                    if (TextUtils.isEmpty(USER_DATE_OF_BIRTH))  {
                                        txtDOB.setText("Date of Birth unspecified");
                                    } else {
                                        Calendar calendar = Calendar.getInstance();
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                        try {
                                            calendar.setTime(sdf.parse(USER_DATE_OF_BIRTH));
                                            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
                                            String selectedDate = dateFormat.format(calendar.getTime());
                                            txtDOB.setText(selectedDate);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
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
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

//        String strTitle = getString(R.string.add_a_new_pet);
        String strTitle = "Profile";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getApplicationContext()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setSubtitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(ProfileDetails.this);
        inflater.inflate(R.menu.profile_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuEdit:
                /***** EDIT THE PROFILE *****/
                Intent intent = new Intent(getApplicationContext(), ProfileEditor.class);
                startActivity(intent);
                break;
            case R.id.cancel:
                /** CANCEL CATEGORY CREATION **/
                finish();
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