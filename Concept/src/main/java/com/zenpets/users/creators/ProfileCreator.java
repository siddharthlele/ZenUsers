package com.zenpets.users.creators;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.zenpets.users.R;
import com.zenpets.users.utils.TypefaceSpan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileCreator extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.edtUserName) AppCompatEditText edtUserName;
    @BindView(R.id.edtUserDescription) AppCompatEditText edtUserDescription;
    @BindView(R.id.edtPhoneNumber) AppCompatEditText edtPhoneNumber;
    @BindView(R.id.txtDOB) AppCompatTextView txtDOB;
    @BindView(R.id.rgGender) RadioGroup rgGender;
    @BindView(R.id.imgvwUserProfile) AppCompatImageView imgvwUserProfile;

    /** DATA TYPES FOR CREATING THE USER'S PROFILE **/
    private String USER_ID = null;
    private String USER_DESCRIPTION = null;
    private String PHONE_NUMBER = null;
    private String DATE_OF_BIRTH = null;
    private String GENDER = "Male";

    /** SELECT THE PET'S DATE OF BIRTH **/
    @OnClick(R.id.btnDOB) void selectDOB()   {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(
                ProfileCreator.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_creator);
        ButterKnife.bind(this);

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** SET THE CURRENT DATE **/
        setCurrentDate();

        /** FETCH THE USER DETAILS AND SHOW / HIDE THE UNNECESSARY ELEMENTS **/
        fetchUserDetails();

        /***** SELECT THE USER'S GENDER *****/
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                switch (checkedId) {
                    case R.id.rdbtnMale:
                        /** SET THE GENDER TO MALE **/
                        GENDER = "Male";
                        break;
                    case R.id.rdbtnFemale:
                        /** SET THE GENDER TO FEMALE **/
                        GENDER= "Female";
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configAB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getString(R.string.profile_creator);
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
        MenuInflater inflater = new MenuInflater(ProfileCreator.this);
        inflater.inflate(R.menu.activity_save_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.save:
                /***** CHECK FOR ALL USER PROFILE DETAILS  *****/
                checkProfileDetails();
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

    /***** CHECK FOR ALL USER PROFILE DETAILS  *****/
    private void checkProfileDetails() {

        /** HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtUserName.getWindowToken(), 0);

        /** COLLECT THE DATA **/
        USER_DESCRIPTION = edtUserDescription.getText().toString().trim();
        PHONE_NUMBER = edtPhoneNumber.getText().toString().trim();

        /** SAVE THE USER PROFILE **/
        createUserProfile();
    }

    /** SAVE THE USER PROFILE **/
    private void createUserProfile() {

        /** SHOW THE PROGRESS DIALOG WHILE UPLOADING THE IMAGE **/
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we create your profile on The Pet Store");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        /** SAVE THE PROFILE IN THE FIREBASE DATABASE **/
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = reference.child("Users").push();
        myRef.child("userID").setValue(USER_ID);
        myRef.child("userDescription").setValue(USER_DESCRIPTION);
        myRef.child("userPhoneNumber").setValue(PHONE_NUMBER);
        myRef.child("userDOB").setValue(DATE_OF_BIRTH);
        myRef.child("userGender").setValue(GENDER);

        /** FINISH THE ACTIVITY **/
        dialog.dismiss();
        Toast.makeText(getApplicationContext(), "Profile created", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    /** SET THE CURRENT DATE **/
    private void setCurrentDate() {
        /** SET THE CURRENT DATE (DISPLAY ONLY !!!!) **/
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String formattedDate = dateFormat.format(new Date());
        txtDOB.setText(formattedDate);

        /** SET THE CURRENT DATE (DATABASE ONLY !!!!)**/
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        DATE_OF_BIRTH = sdf.format(cal.getTime());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int date) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        /** FOR THE DATABASE ONLY !!!!**/
        DATE_OF_BIRTH = sdf.format(cal.getTime());

        /** FOR DISPLAY ONLY !!!! **/
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String selectedDate = dateFormat.format(cal.getTime());
        txtDOB.setText(selectedDate);
    }

    /** FETCH THE USER DETAILS AND SHOW / HIDE THE UNNECESSARY ELEMENTS **/
    private void fetchUserDetails() {

        /** GET THE USER DETAILS **/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            USER_ID = user.getUid();

            /** FIND THE LOGIN PROVIDER AND FETCH THE USER DETAILS **/
            String USER_NAME;
            String PROFILE_PICTURE_URL;
            if (user.getProviderData().get(1).getProviderId().equalsIgnoreCase("google.com")) {

                /** SET THE USER DETAILS **/
                USER_NAME = user.getDisplayName();
                edtUserName.setText(USER_NAME);
                PROFILE_PICTURE_URL = String.valueOf(user.getProviderData().get(1).getPhotoUrl()) + "?sz=600";
                Picasso.with(ProfileCreator.this)
                        .load(PROFILE_PICTURE_URL)
                        .centerInside()
                        .fit()
                        .into(imgvwUserProfile);

            } else if (user.getProviderData().get(1).getProviderId().equalsIgnoreCase("facebook.com")) {

                /** SET THE USER DETAILS **/
                USER_NAME = user.getDisplayName();
                edtUserName.setText(USER_NAME);
                PROFILE_PICTURE_URL = "https://graph.facebook.com/" + user.getProviderData().get(1).getUid() + "/picture?width=4000";
                Picasso.with(ProfileCreator.this)
                        .load(PROFILE_PICTURE_URL)
                        .centerInside()
                        .fit()
                        .into(imgvwUserProfile);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}