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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zenpets.users.R;
import com.zenpets.users.utils.TypefaceSpan;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileDetailsActivity extends AppCompatActivity {

    /** STRINGS TO HOLD THE USER DETAILS **/
    String USER_NAME = null;
    String EMAIL_ADDRESS = null;
    String USER_PROFILE_PICTURE = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwProfilePicture) AppCompatImageView imgvwProfilePicture;
    @BindView(R.id.txtUserName) AppCompatTextView txtUserName;
    @BindView(R.id.txtEmailAddress) AppCompatTextView txtEmailAddress;
    @BindView(R.id.txtPhoneNumber) AppCompatTextView txtPhoneNumber;
    @BindView(R.id.txtCity) AppCompatTextView txtCity;
    @BindView(R.id.txtGender) AppCompatTextView txtGender;
    @BindView(R.id.txtDOB) AppCompatTextView txtDOB;

    /** SHOW THE PROFILE EDITOR **/
    @OnClick(R.id.txtEditProfile) void profileEditor()  {
        Intent intent = new Intent(ProfileDetailsActivity.this, ProfileEditorActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_details_activity);
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

            /** GET THE USER NAME **/
            USER_NAME = user.getDisplayName();

            /** GET THE EMAIL ADDRESS **/
            EMAIL_ADDRESS = user.getEmail();

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
            if (EMAIL_ADDRESS != null)  {
                txtEmailAddress.setText(EMAIL_ADDRESS);
            }

            /** SET THE PROFILE PICTURE **/
            if (USER_PROFILE_PICTURE != null)   {
                Glide.with(ProfileDetailsActivity.this)
                        .load(USER_PROFILE_PICTURE)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .into(imgvwProfilePicture);
            }
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}