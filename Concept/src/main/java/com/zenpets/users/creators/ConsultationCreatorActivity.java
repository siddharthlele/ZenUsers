package com.zenpets.users.creators;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Toast;

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
import com.zenpets.users.utils.adapters.PetsSelectorAdapter;
import com.zenpets.users.utils.models.PetData;
import com.zenpets.users.utils.models.consultations.ConsultationsData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConsultationCreatorActivity extends AppCompatActivity {

    /** THE USER KEY **/
    private String USER_KEY = null;

    /** THE USER PETS ARRAY LIST **/
    private ArrayList<PetData> arrMyPets = new ArrayList<>();

    /** FOR KEEPING TRACK OF THE HEADER AND DESCRIPTION CHARACTER COUNT **/
    private int headerCharCount;
    private int descriptionCharCount;

    /** STRINGS TO HOLD THE COLLECTED INFORMATION **/
    private String CONSULT_STATUS ="Public";
    private String PET_ID = null;
    private String QUESTION_HEADER = null;
    private String QUESTION_DESCRIPTION = null;
    private String TIME_STAMP = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.spnMyPets) AppCompatSpinner spnMyPets;
    @BindView(R.id.txtHeaderCount) AppCompatTextView txtHeaderCount;
    @BindView(R.id.edtHeader) AppCompatEditText edtHeader;
    @BindView(R.id.txtDescriptionCount) AppCompatTextView txtDescriptionCount;
    @BindView(R.id.edtDescription) AppCompatEditText edtDescription;
    @BindView(R.id.chkbxAcceptTerms) AppCompatCheckBox chkbxAcceptTerms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultation_creator_activity);
        ButterKnife.bind(this);

        /***** CONFIGURE THE TOOLBAR *****/
        configTB();

        /** GET THE FIREBASE USER DETAILS **/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String USER_ID = user.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
            Query query = ref.orderByChild("userID").equalTo(USER_ID);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        USER_KEY = postSnapshot.getKey();
                    }

                    /** FETCH THE LIST OF PETS **/
                    fetchUserPets();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required information....", Toast.LENGTH_SHORT).show();
            finish();
        }

        /** GET THE TIME STAMP **/
        Long aLong = System.currentTimeMillis() / 1000;
        TIME_STAMP = String.valueOf(aLong);

        /*****	THE TEXT WATCHER TO SHOW THE HEADER AND DESCRIPTION CHARACTER COUNT		*****/
        edtHeader.addTextChangedListener(headerTextChange);
        edtDescription.addTextChangedListener(descriptionTextChange);

        /** ACCEPT THE TERMS **/
        chkbxAcceptTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getApplicationContext(), String.valueOf(b), Toast.LENGTH_SHORT).show();
            }
        });

        /** SELECT THE PET ID **/
        spnMyPets.setOnItemSelectedListener(selectPet);
    }

    /** SELECT A PET ID **/
    private final AdapterView.OnItemSelectedListener selectPet = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            PET_ID = arrMyPets.get(position).getPetID();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /** POST THE QUESTION **/
    private void postQuestion() {

        /** SHOW THE PROGRESS DIALOG WHILE UPLOADING THE IMAGE **/
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait while we add your consultation question....");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Consultations");

        /** COLLECT THE DATA **/
        ConsultationsData data = new ConsultationsData(CONSULT_STATUS, QUESTION_HEADER, QUESTION_DESCRIPTION, PET_ID, USER_KEY, TIME_STAMP, 0);
        reference.push().setValue(data, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference reference) {
                if (databaseError != null)  {
                    Toast.makeText(
                            getApplicationContext(),
                            "Failed to submit your question. Please post again.",
                            Toast.LENGTH_LONG).show();
                } else {
                    /** POST THE CONSULTATION REFERENCE IN THE USERS RECORD **/
                    String consultID = reference.getKey();
                    DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(USER_KEY).child("Consultations").push();
                    refUser.child("consultID").setValue(consultID);

                    /** DISMISS THE DIALOG AND FINISH THE ACTIVITY **/
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Successfully added your consultation question", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    /***** CHECK FOR ALL QUESTION DETAILS  *****/
    private void checkQuestionDetails() {

        /** HIDE THE KEYBOARD **/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtHeader.getWindowToken(), 0);

        /** COLLECT THE INFORMATION **/
        QUESTION_HEADER = edtHeader.getText().toString().trim();
        QUESTION_DESCRIPTION = edtDescription.getText().toString().trim();

        /** VALIDATE THE INFORMATION **/
        if (headerCharCount < 10)   {
            edtHeader.setError("Question title is less than 10 chars");
        } else if (headerCharCount > 25)    {
            edtHeader.setError("Question title is more than 25 chars");
        } else if (descriptionCharCount < 100)  {
            edtDescription.setError("Description is less than 100 chars");
        } else if (descriptionCharCount > 1000) {
            edtDescription.setError("Description is more than 1000 chars");
        } else if (TextUtils.isEmpty(QUESTION_HEADER))  {
            edtHeader.setError("Please provide the question Header / Title");
        } else if (TextUtils.isEmpty(QUESTION_DESCRIPTION)) {
            edtDescription.setError("Please provide the question Description");
        } else {
            /** POST THE QUESTION **/
            postQuestion();
        }
    }

    /*****	THE TEXT WATCHER TO SHOW THE DESCRIPTION CHARACTER COUNT		*****/
    private TextWatcher descriptionTextChange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            descriptionCharCount = s.length();
            txtDescriptionCount.setText(String.valueOf(descriptionCharCount) + "/1000");
            if (descriptionCharCount < 100)   {
                txtDescriptionCount.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
            } else if (descriptionCharCount > 1000)    {
                txtDescriptionCount.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
            } else {
                txtDescriptionCount.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.tertiary_text_dark));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    /*****	THE TEXT WATCHER TO SHOW THE DESCRIPTION CHARACTER COUNT		*****/
    private TextWatcher headerTextChange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            headerCharCount = s.length();
            txtHeaderCount.setText(String.valueOf(headerCharCount) + "/25");
            if (headerCharCount < 10)   {
                txtHeaderCount.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
            } else if (headerCharCount > 25)    {
                txtHeaderCount.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
            } else {
                txtHeaderCount.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.tertiary_text_dark));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    /** FETCH THE LIST OF PETS **/
    private void fetchUserPets() {
        DatabaseReference refPets = FirebaseDatabase.getInstance().getReference().child("Users").child(USER_KEY).child("Pets");

        /** CLEAR THE BREEDS ARRAY LIST **/
        arrMyPets.clear();

        /** GET THE LIST OF BREEDS **/
        refPets.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getMyPetsData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    /** GET THE LIST OF BREEDS **/
    private void getMyPetsData(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            PetData data = postSnapshot.getValue(PetData.class);
            arrMyPets.add(data);
        }

        /** SET THE BREEDS ADAPTER TO THE BREEDS SPINNER **/
        spnMyPets.setAdapter(new PetsSelectorAdapter(this, arrMyPets));
    }

    /***** CONFIGURE THE TOOLBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configTB() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

//        String strTitle = getString(R.string.add_a_new_pet);
        String strTitle = "Ask Free Question";
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
        MenuInflater inflater = new MenuInflater(this);
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
                /***** CHECK FOR ALL QUESTION DETAILS  *****/
                checkQuestionDetails();
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