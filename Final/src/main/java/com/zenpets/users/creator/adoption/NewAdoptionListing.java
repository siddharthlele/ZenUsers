package com.zenpets.users.creator.adoption;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.zenpets.users.R;
import com.zenpets.users.utils.TypefaceSpan;
import com.zenpets.users.utils.adapters.pet.BreedsSpinnerAdapter;
import com.zenpets.users.utils.adapters.pet.PetTypeSpinnerAdapter;
import com.zenpets.users.utils.models.adoptions.AdoptionsData;
import com.zenpets.users.utils.models.adoptions.UserAdoptionData;
import com.zenpets.users.utils.models.pet.BreedTypesData;
import com.zenpets.users.utils.models.pet.PetTypesData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewAdoptionListing extends AppCompatActivity {

    /** THE USER DETAILS **/
    private String USER_ID = null;
    private String USER_KEY = null;
    private String USER_NAME = null;

    /** THE TIMESTAMP **/
    private String TIME_STAMP = null;

    /****** DATA TYPES FOR CATEGORY DETAILS *****/
    private String PET_TYPE_NAME = null;
    private String PET_BREED_NAME = null;
    private String PET_NAME = null;
    private String PET_GENDER = "Male";
    private String PET_VACCINATED = "Yes";
    private String PET_DEWORMED = "Yes";
    private String PET_NEUTERED = "Yes";
    private String PET_DESCRIPTION = null;

    /** THE PET TYPES AND BREED NAMES ARRAY LISTS **/
    private final ArrayList<PetTypesData> arrPetTypes = new ArrayList<>();
    private final ArrayList<BreedTypesData> arrBreeds = new ArrayList<>();

    /** THE PROGRESS DIALOG **/
    ProgressDialog dialog;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnPetTypes) AppCompatSpinner spnPetTypes;
    @BindView(R.id.spnBreeds) AppCompatSpinner spnBreeds;
    @BindView(R.id.inputPetName) TextInputLayout inputPetName;
    @BindView(R.id.edtPetName) AppCompatEditText edtPetName;
    @BindView(R.id.groupGender) SegmentedButtonGroup groupGender;
    @BindView(R.id.groupVaccination) SegmentedButtonGroup groupVaccination;
    @BindView(R.id.groupDewormed) SegmentedButtonGroup groupDewormed;
    @BindView(R.id.groupNeutered) SegmentedButtonGroup groupNeutered;
    @BindView(R.id.edtDescription) AppCompatEditText edtDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_adoption_listing);
        ButterKnife.bind(this);

        /** GET THE USER DETAILS **/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            USER_ID = user.getUid();
            Log.e("USER ID", USER_ID);
            if (TextUtils.isEmpty(USER_ID))    {
                Toast.makeText(getApplicationContext(), "Failed to get required data....", Toast.LENGTH_SHORT).show();
                finish();
            } else {

                /* GET THE TIME STAMP */
                Long aLong = System.currentTimeMillis() / 1000;
                TIME_STAMP = String.valueOf(aLong);
                Log.e("TIME STAMP", TIME_STAMP);

                /* GET THE USER NAME */
                USER_NAME = user.getDisplayName();
                Log.e("USER NAME", USER_NAME);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                Query query = reference.orderByChild("userID").equalTo(USER_ID);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            /* GET THE USER KEY */
                            USER_KEY = postSnapshot.getKey();
                            Log.e("USER KEY", USER_KEY);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required data....", Toast.LENGTH_SHORT).show();
            finish();
        }

        /***** CONFIGURE THE TOOLBAR *****/
        configTB();

        /** FETCH ALL PET TYPES **/
        fetchPetTypes();

        /** SELECT A PET TYPE **/
        spnPetTypes.setOnItemSelectedListener(selectPetType);

        /** SELECT THE BREED **/
        spnBreeds.setOnItemSelectedListener(selectBreed);

        /***** SELECT THE PET'S GENDER *****/
        groupGender.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                if (position == 0)  {
                    PET_GENDER = "Male";
                } else if (position == 1)   {
                    PET_GENDER = "Female";
                }
            }
        });

        /***** SELECT THE PET'S VACCINATION STATUS *****/
        groupVaccination.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                if (position == 0)  {
                    PET_VACCINATED = "Yes";
                } else if (position == 1)   {
                    PET_VACCINATED = "No";
                }
            }
        });

        /***** SELECT THE PET'S DEWORMING STATUS *****/
        groupDewormed.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                if (position == 0)  {
                    PET_DEWORMED = "Yes";
                } else if (position == 1)   {
                    PET_DEWORMED = "No";
                }
            }
        });

        /***** SELECT THE PET'S NEUTERED STATUS *****/
        groupNeutered.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                if (position == 0)  {
                    PET_NEUTERED = "Yes";
                } else if (position == 1)   {
                    PET_NEUTERED = "No";
                }
            }
        });
    }

    /***** CHECK FOR ALL PET ADOPTION DETAILS  *****/
    private void checkDetails() {

        /** HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtPetName.getWindowToken(), 0);

        /** COLLECT THE REQUIRED DATA **/
        PET_NAME = edtPetName.getText().toString().trim();
        PET_DESCRIPTION = edtDescription.getText().toString().trim();

        if (TextUtils.isEmpty(PET_DESCRIPTION)) {
            edtDescription.setError("Please provide the Pet's Description");
            edtDescription.requestFocus();
        } else  {
            /** POST THE ADOPTION LISTING **/
            postListing();
        }
    }

    /** POST THE ADOPTION LISTING **/
    private void postListing() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Adoptions");
        AdoptionsData data = new AdoptionsData("Open", PET_TYPE_NAME, PET_BREED_NAME, PET_NAME, PET_GENDER, PET_VACCINATED, PET_DEWORMED, PET_NEUTERED, PET_DESCRIPTION, USER_KEY, USER_ID, USER_NAME, TIME_STAMP);

        reference.push().setValue(data, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null)  {
                    Toast.makeText(
                            getApplicationContext(),
                            "Failed to publish your Adoption listing. Please post again.",
                            Toast.LENGTH_LONG).show();
                } else {
                    /** POST THE REFERENCE IN THE USERS RECORD **/
                    final String adoptionID = databaseReference.getKey();
                    DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(USER_KEY).child("Adoptions");
                    UserAdoptionData userAdoptionData = new UserAdoptionData(adoptionID);
                    refUser.push().setValue(userAdoptionData, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null)  {
                                /** FINISH THE ACTIVITY **/
                                Toast.makeText(getApplicationContext(), "Successfully published your new Adoption listing", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();

                                /** DISMISS THE DIALOG **/
                                dialog.dismiss();

                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Failed to publish your Adoption listing. Please post again.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    /** SELECT A PET TYPE **/
    private final AdapterView.OnItemSelectedListener selectPetType = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            /** CHANGE THE SELECTED PET TYPE **/
            PET_TYPE_NAME = arrPetTypes.get(position).getPetTypeName();
            DatabaseReference refBreed = FirebaseDatabase.getInstance().getReference().child("Breeds").child(PET_TYPE_NAME);
//            Log.e("REFERENCE", String.valueOf(refBreed));

            /** CLEAR THE BREEDS ARRAY LIST **/
            arrBreeds.clear();

            /** GET THE LIST OF BREEDS **/
            refBreed.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getBreedsSpinner(dataSnapshot);
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

    /** SELECT A BREED **/
    private final AdapterView.OnItemSelectedListener selectBreed = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            PET_BREED_NAME = arrBreeds.get(position).getBreedName();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void getBreedsSpinner(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            BreedTypesData types = postSnapshot.getValue(BreedTypesData.class);
            arrBreeds.add(types);
        }

        /** SET THE BREEDS ADAPTER TO THE BREEDS SPINNER **/
        spnBreeds.setAdapter(new BreedsSpinnerAdapter(this, arrBreeds));
    }

    /** FETCH ALL PET TYPES **/
    private void fetchPetTypes() {
        PetTypesData pet1 = new PetTypesData();
        pet1.setPetTypeID("1");
        pet1.setPetTypeName("Dog");
        arrPetTypes.add(pet1);

        PetTypesData pet2 = new PetTypesData();
        pet2.setPetTypeID("2");
        pet2.setPetTypeName("Cat");
        arrPetTypes.add(pet2);

        PetTypesData pet3 = new PetTypesData();
        pet3.setPetTypeID("3");
        pet3.setPetTypeName("Bird");
        arrPetTypes.add(pet3);

        PetTypesData pet4 = new PetTypesData();
        pet4.setPetTypeID("4");
        pet4.setPetTypeName("Rabbit");
        arrPetTypes.add(pet4);

        PetTypesData pet5 = new PetTypesData();
        pet5.setPetTypeID("5");
        pet5.setPetTypeName("Small & Furry");
        arrPetTypes.add(pet5);

        PetTypesData pet6 = new PetTypesData();
        pet6.setPetTypeID("6");
        pet6.setPetTypeName("Horse");
        arrPetTypes.add(pet6);

        PetTypesData pet7 = new PetTypesData();
        pet7.setPetTypeID("7");
        pet7.setPetTypeName("Reptile");
        arrPetTypes.add(pet7);

        PetTypesData pet8 = new PetTypesData();
        pet8.setPetTypeID("8");
        pet8.setPetTypeName("Barn Yard");
        arrPetTypes.add(pet8);

        /** SET THE PET TYPES ADAPTER TO THE SPINNER DROPDOWN **/
        spnPetTypes.setAdapter(new PetTypeSpinnerAdapter(
                NewAdoptionListing.this,
                arrPetTypes));
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = getString(R.string.add_a_new_pet);
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
        MenuInflater inflater = new MenuInflater(NewAdoptionListing.this);
        inflater.inflate(R.menu.activity_save_cancel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menuSave:
                /***** CHECK FOR ALL PET ADOPTION DETAILS  *****/
                checkDetails();
                break;
            case R.id.menuCancel:
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