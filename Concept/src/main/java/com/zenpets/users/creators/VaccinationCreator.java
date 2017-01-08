package com.zenpets.users.creators;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.zenpets.users.R;
import com.zenpets.users.utils.TypefaceSpan;
import com.zenpets.users.utils.adapters.UserPetsSpinnerAdapter;
import com.zenpets.users.utils.adapters.VaccineTypesSpinnerAdapter;
import com.zenpets.users.utils.models.profile.PetData;
import com.zenpets.users.utils.models.VaccinesData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VaccinationCreator extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    /** THE USER ID **/
    private String USER_ID;

    /** DATA TYPES TO HOLD THE COLLECTED DATA **/
    private String PET_ID = null;
    private String VACCINATION_NAME = null;
    private String VACCINATION_DATE = null;
    private String VACCINATION_NOTES = null;

    /** THE PET TYPES AND BREED NAMES ARRAY LISTS **/
    private final ArrayList<VaccinesData> arrVaccines = new ArrayList<>();
    private final ArrayList<PetData> arrPets = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnPet) AppCompatSpinner spnPet;
    @BindView(R.id.spnVaccineTypes) AppCompatSpinner spnVaccineTypes;
    @BindView(R.id.txtVaccinationDate) AppCompatTextView txtVaccinationDate;
    @BindView(R.id.inpVaccineNotes) TextInputLayout inpVaccineNotes;
    @BindView(R.id.edtVaccineNotes) AppCompatEditText edtVaccineNotes;

    /** SELECT THE DATE OF VACCINATION **/
    @OnClick(R.id.btnVaccinationDate) void vaccinationDate()    {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(
                VaccinationCreator.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        pickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vaccination_creator);
        ButterKnife.bind(this);

        /** GET THE USER DETAILS AND POPULATE THE VACCINATION SPINNER **/
        getUserDetails();

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** SET THE CURRENT DATE **/
        setCurrentDate();
        
        /** GET USER PETS **/
        getUserPets();

        /** SELECT A PET **/
        spnPet.setOnItemSelectedListener(selectPet);

        /** SELECT THE VACCINE TYPE **/
        spnVaccineTypes.setOnItemSelectedListener(selectVaccine);
    }

    /** SHOW THE "ADD A PET DIALOG" **/
    private void showNoPetsDialog() {{
        new MaterialDialog.Builder(VaccinationCreator.this)
                .icon(ContextCompat.getDrawable(VaccinationCreator.this, R.drawable.ic_info_outline_black_24dp))
                .title("No Pets On Record")
                .cancelable(true)
                .content("Before you can add a Vaccination Reminder, you must add at least one pet to your account. To add a new Pet now, click the \"Add pet\" button.")
                .positiveText("Add Pet")
                .negativeText("Later")
                .theme(Theme.LIGHT)
                .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent showProfileCreator = new Intent(VaccinationCreator.this, PetCreator.class);
                        startActivity(showProfileCreator);
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }
    }

    /** POST THE VACCINATION DETAILS TO FIREBASE **/
    private void startPosting() {

        /** SHOW THE PROGRESS DIALOG WHILE UPLOADING THE IMAGE **/
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we add the new Vaccination report to your account....");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = reference.child("Vaccinations").push();
        myRef.child("userID").setValue(USER_ID);
        myRef.child("petID").setValue(PET_ID);
        myRef.child("vaccineName").setValue(VACCINATION_NAME);
        myRef.child("vaccineDate").setValue(VACCINATION_DATE);
        myRef.child("vaccineNotes").setValue(VACCINATION_NOTES);

        /** DISMISS THE DIALOG **/
        dialog.dismiss();

        /** FINISH THE ACTIVITY **/
        Toast.makeText(getApplicationContext(), "Record added successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    /** CHECK VACCINATION DETAILS **/
    private void checkVaccineDetails() {

        /** HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtVaccineNotes.getWindowToken(), 0);

        /** GET THE REQUIRED FIELDS **/
        if (TextUtils.isEmpty(edtVaccineNotes.getText().toString().trim())) {
            VACCINATION_NOTES = null;
        } else {
            VACCINATION_NOTES = edtVaccineNotes.getText().toString().trim();
        }

        /** POST THE VACCINATION DETAILS TO FIREBASE **/
        startPosting();
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configAB() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getString(R.string.add_a_new_vaccination_record);
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
        MenuInflater inflater = new MenuInflater(VaccinationCreator.this);
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
                /** CHECK VACCINATION DETAILS **/
                checkVaccineDetails();
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

    /** GET THE USER DETAILS AND POPULATE THE VACCINATION SPINNER **/
    private void getUserDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            USER_ID = user.getUid();

            /** GET THE LIST OF VACCINES **/
            DatabaseReference refVaccines = FirebaseDatabase.getInstance().getReference().child("Vaccines");
            refVaccines.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    populateVaccines(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "failed to get required information", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /** GET THE LIST OF VACCINES **/
    private void populateVaccines(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            VaccinesData types = postSnapshot.getValue(VaccinesData.class);
            arrVaccines.add(types);
        }

        /** SET THE BREEDS ADAPTER TO THE BREEDS SPINNER **/
        spnVaccineTypes.setAdapter(new VaccineTypesSpinnerAdapter(
                VaccinationCreator.this,
                arrVaccines));
    }

    /** GET USER PETS **/
    private void getUserPets() {

        /** CLEAR THE ARRAY LIST **/
        arrPets.clear();

        /** GET THE LIST OF VACCINES **/
        DatabaseReference refPets = FirebaseDatabase.getInstance().getReference().child("Pets").child(USER_ID);
        refPets.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    PetData data;
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                        /** INSTANTIATE THE PETS DATA CLASS INSTANCE **/
                        data = new PetData();

                        /* GET THE PET ID (KEY) */
                        data.setPetID(postSnapshot.getKey());
//                        Log.e("PET ID", postSnapshot.getKey());

                        /* GET THE PET NAME */
                        data.setPetName(postSnapshot.child("petName").getValue(String.class));
//                        Log.e("PET NAME", postSnapshot.child("petName").getValue(String.class));

                        /** ADD  THE COLLECTED DATA TO THE ARRAY LIST **/
                        arrPets.add(data);
                    }

                    /** SET THE BREEDS ADAPTER TO THE BREEDS SPINNER **/
                    spnPet.setAdapter(new UserPetsSpinnerAdapter(
                            VaccinationCreator.this,
                            arrPets));
                } else {
                    /** SHOW THE "ADD A PET DIALOG" **/
                    showNoPetsDialog();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /** SELECT A PET **/
    private final AdapterView.OnItemSelectedListener selectPet = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            PET_ID = arrPets.get(position).getPetID();
//            Log.e("SELECTED PET ID", PET_ID);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /** SELECT A VACCINE TYPE **/
    private final AdapterView.OnItemSelectedListener selectVaccine = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            VACCINATION_NAME = arrVaccines.get(position).getVaccineName();
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
        txtVaccinationDate.setText(formattedDate);

        /** SET THE CURRENT DATE (DATABASE ONLY !!!!)**/
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        VACCINATION_DATE = sdf.format(cal.getTime());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int date) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        /** FOR THE DATABASE ONLY !!!!**/
        VACCINATION_DATE = sdf.format(cal.getTime());

        /** FOR DISPLAY ONLY !!!! **/
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String selectedDate = dateFormat.format(cal.getTime());
        txtVaccinationDate.setText(selectedDate);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}