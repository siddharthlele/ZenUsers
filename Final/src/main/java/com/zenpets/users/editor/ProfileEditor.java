package com.zenpets.users.editor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.zenpets.users.R;
import com.zenpets.users.utils.TypefaceSpan;
import com.zenpets.users.utils.adapters.location.CitiesAdapter;
import com.zenpets.users.utils.adapters.location.StatesAdapter;
import com.zenpets.users.utils.helpers.StatesArrayCreator;
import com.zenpets.users.utils.models.location.CitiesData;
import com.zenpets.users.utils.models.location.StatesData;
import com.zenpets.users.utils.models.user.UserData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileEditor extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    /** THE USER ID AND KEY **/
    String USER_ID = null;
    String USER_KEY = null;

    /** USER_STATE ARRAY LIST, ADAPTER AND HELPER **/
    ArrayList<StatesData> arrStates = new ArrayList<>();
    StatesArrayCreator stateCreator;
    StatesAdapter adapStates;

    /** CITIES ADAPTER AND ARRAY LIST **/
    CitiesAdapter adapCities;
    ArrayList<CitiesData> arrCities = new ArrayList<>();

    /** STRINGS TO HOLD THE USER DETAILS **/
    private String USER_PROFILE_PICTURE = null;
    private String USER_NAME = null;
    private String USER_EMAIL_ADDRESS = null;
    private String USER_PHONE_PREFIX = null;
    private String USER_PHONE_NUMBER = null;
    private String USER_STATE = null;
    private String USER_CITY = null;
    private String USER_GENDER = "Male";
    private String USER_DATE_OF_BIRTH = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwProfilePicture) AppCompatImageView imgvwProfilePicture;
    @BindView(R.id.edtDisplayName) AppCompatEditText edtDisplayName;
    @BindView(R.id.txtEmailAddress) AppCompatTextView txtEmailAddress;
    @BindView(R.id.txtISDCode) AppCompatTextView txtISDCode;
    @BindView(R.id.edtPhoneNumber) AppCompatEditText edtPhoneNumber;
    @BindView(R.id.spnState) AppCompatSpinner spnState;
    @BindView(R.id.spnCity) AppCompatSpinner spnCity;
    @BindView(R.id.groupGender) SegmentedButtonGroup groupGender;
    @BindView(R.id.txtDOB) AppCompatTextView txtDOB;

    /** SELECT THE PET'S DATE OF BIRTH **/
    @OnClick(R.id.linlaDOB) void selectDOB()   {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(
                ProfileEditor.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_editor);
        ButterKnife.bind(this);

        /***** CONFIGURE THE TOOLBAR *****/
        configTB();

        /***** SELECT THE USER'S GENDER *****/
        groupGender.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                if (position == 0)  {
                    USER_GENDER = "Male";
                } else if (position == 1)   {
                    USER_GENDER = "Female";
                }
            }
        });

        /** SET THE CURRENT DATE **/
        setCurrentDate();

        /** GET THE LIST OF STATES **/
        stateCreator = new StatesArrayCreator(ProfileEditor.this);
        arrStates = stateCreator.generateStatesArray();

        /** INSTANTIATE THE STATES ADAPTER **/
        adapStates = new StatesAdapter(this, arrStates);

        /** INSTANTIATE THE CITIES ADAPTER **/
        adapCities = new CitiesAdapter(this, arrCities);

        /** SET THE ADAPTER TO THE SPINNER **/
        spnState.setAdapter(adapStates);

        /** SELECT THE STATE **/
        spnState.setOnItemSelectedListener(selectState);

        /** SELECT THE CITY **/
        spnCity.setOnItemSelectedListener(selectCity);

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
                edtDisplayName.setText(USER_NAME);
            }

            /** SET THE EMAIL ADDRESS **/
            if (USER_EMAIL_ADDRESS != null)  {
                txtEmailAddress.setText(USER_EMAIL_ADDRESS);
            }

            /** SET THE PROFILE PICTURE **/
            if (USER_PROFILE_PICTURE != null)   {
                Glide.with(ProfileEditor.this)
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

                                    /** GET THE PHONE NUMBER **/
                                    USER_PHONE_NUMBER = user.child("userPhoneNumber").getValue(String.class);
                                    if (TextUtils.isEmpty(USER_PHONE_NUMBER))   {
                                        edtPhoneNumber.setHint("Mobile Number");
                                    } else {
                                        edtPhoneNumber.setText(USER_PHONE_NUMBER);
                                    }

                                    /** GET THE STATE AND CITY **/
                                    USER_STATE = user.child("userState").getValue(String.class);
                                    USER_CITY = user.child("userCity").getValue(String.class);

                                    int intStatePosition = getStateIndex(arrStates, USER_STATE);
                                    spnState.setSelection(intStatePosition);

                                    int intCityPosition = getCityIndex(arrCities, USER_CITY);
                                    spnCity.setSelection(intCityPosition);

                                    /** GET THE GENDER **/
                                    USER_GENDER = user.child("userGender").getValue(String.class);
                                    if (TextUtils.isEmpty(USER_GENDER)) {
                                        groupGender.setPosition(0, true);
                                    } else {
                                        if (USER_GENDER.equalsIgnoreCase("Male"))   {
                                            groupGender.setPosition(0, true);
                                        } else {
                                            groupGender.setPosition(1, true);
                                        }
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

    /** GET THE USER'S CITY POSITION **/
    private int getCityIndex(ArrayList<CitiesData> arrCities, String userCity) {
        int index = 0;
        for (int i =0; i < arrCities.size(); i++) {
            if (arrCities.get(i).getCityName().equalsIgnoreCase(userCity))   {
                index = i;
                break;
            }
        }
        return index;
    }

    /** GET THE USER'S STATE POSITION **/
    private int getStateIndex(ArrayList<StatesData> arrStates, String userState) {
        int index = 0;
        for (int i =0; i < arrStates.size(); i++) {
            if (arrStates.get(i).getStateName().equalsIgnoreCase(userState))   {
                index = i;
                break;
            }
        }
        return index;
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
        MenuInflater inflater = new MenuInflater(ProfileEditor.this);
        inflater.inflate(R.menu.profile_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuSave:
                /** UPDATE THE USER DETAILS **/
                updateUserDetails();
                // TODO: CODE THE SAVE PROFILE FEATURE
                break;
            default:
                break;
        }
        return false;
    }

    /** UPDATE THE USER DETAILS **/
    private void updateUserDetails() {

        /** HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtDisplayName.getWindowToken(), 0);

        /** COLLECT THE INFORMATION **/
        USER_PHONE_PREFIX = txtISDCode.getText().toString().trim();
        USER_PHONE_NUMBER = edtPhoneNumber.getText().toString().trim();

        /** VALIDATE THE PHONE NUMBER **/
        if (TextUtils.isEmpty(USER_PHONE_NUMBER))   {
            edtPhoneNumber.setError("Please provide your Mobile Number");
        } else {
            /** UPDATE THE USER PROFILE **/
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
            Query query = reference.orderByChild("userID").equalTo(USER_ID);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        /* GET THE USER KEY */
                        USER_KEY = postSnapshot.getKey();

                        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(USER_KEY);
                        UserData userData = new UserData(USER_ID, USER_NAME, USER_EMAIL_ADDRESS, USER_PHONE_PREFIX, USER_PHONE_NUMBER, USER_STATE, USER_CITY, USER_GENDER, USER_DATE_OF_BIRTH);
                        refUser.setValue(userData, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null)  {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Failed to update your profile. Please try again.....",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Your profile was updated successfully....",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                }
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

    /** SELECT A USER_STATE **/
    private final AdapterView.OnItemSelectedListener selectState = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            USER_STATE = arrStates.get(position).getStateName();

            /** CLEAR THE CITIES ARRAY LIST **/
            arrCities.clear();

            /** FETCH THE LIST OF CITIES **/
            DatabaseReference refCities = FirebaseDatabase.getInstance().getReference().child("States").child(USER_STATE);
            refCities.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getCitiesSpinner(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /** POPULATE THE CITIES SPINNER **/
    private void getCitiesSpinner(DataSnapshot dataSnapshot) {
        CitiesData data;
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            data = new CitiesData();
            String cityID = postSnapshot.getKey();
            String cityName = postSnapshot.child("cityName").getValue(String.class);
            data.setCityID(cityID);
            data.setCityName(cityName);
            arrCities.add(data);
        }

        /** SET THE BREEDS ADAPTER TO THE BREEDS SPINNER **/
        spnCity.setAdapter(new CitiesAdapter(this, arrCities));
    }

    /** SELECT THE CITY **/
    private final AdapterView.OnItemSelectedListener selectCity = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            USER_CITY = arrCities.get(position).getCityName();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /** SET THE CURRENT DATE **/
    private void setCurrentDate() {
        /** SET THE CURRENT DATE (DISPLAY ONLY !!!!) **/
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String formattedDate = dateFormat.format(new Date());
        txtDOB.setText(formattedDate);

        /** SET THE CURRENT DATE (DATABASE ONLY !!!!)**/
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        USER_DATE_OF_BIRTH = sdf.format(cal.getTime());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int date) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        /** FOR THE DATABASE ONLY !!!!**/
        USER_DATE_OF_BIRTH = sdf.format(cal.getTime());

        /** FOR DISPLAY ONLY !!!! **/
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String selectedDate = dateFormat.format(cal.getTime());
        txtDOB.setText(selectedDate);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}