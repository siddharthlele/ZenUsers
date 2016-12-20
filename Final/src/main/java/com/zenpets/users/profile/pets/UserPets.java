package com.zenpets.users.profile.pets;

import android.content.Context;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zenpets.users.R;
import com.zenpets.users.creator.pet.PetCreator;
import com.zenpets.users.details.pet.PetDetailsContainer;
import com.zenpets.users.editor.pet.PetEditor;
import com.zenpets.users.utils.TypefaceSpan;
import com.zenpets.users.utils.models.pet.PetData;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserPets extends AppCompatActivity {

    /** A FIREBASE DATABASE REFERENCE INSTANCE **/
    private DatabaseReference reference;

    /** THE FIREBASE RECYCLER ADAPTER INSTANCE **/
    private FirebaseRecyclerAdapter adapter;

    /** THE USER KEY **/
    private String USER_KEY = null;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaHeaderProgress) LinearLayout linlaHeaderProgress;
    @BindView(R.id.listPets) RecyclerView listPets;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD A NEW PET **/
    @OnClick(R.id.linlaEmpty) void newPet() {
        Intent addNewPet = new Intent(this, PetCreator.class);
        startActivity(addNewPet);
    }

    @OnClick(R.id.fabNewUserPet) void fabNewUserPet()   {
        Intent addNewPet = new Intent(this, PetCreator.class);
        startActivity(addNewPet);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_pets_list);
        ButterKnife.bind(this);

        /***** CONFIGURE THE TOOLBAR *****/
        configTB();

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

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
//                        Log.e("USER KEY", USER_KEY);
                    }

                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(USER_KEY).child("Pets");

                    /** SETUP THE FIREBASE ADAPTER **/
                    setupFirebaseAdapter();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Failed to get required information....", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /** SETUP THE FIREBASE ADAPTER **/
    private void setupFirebaseAdapter() {

        adapter = new FirebaseRecyclerAdapter<PetData, PetsVH>
                (PetData.class, R.layout.user_pets_item, PetsVH.class, reference) {

            @Override
            protected void populateViewHolder(PetsVH viewHolder, final PetData model, final int position) {

                if (model != null)  {

                    /** SET THE PET NAME **/
                    viewHolder.txtPetName.setText(model.getPetName());

                    /** SET THE PET DOB **/
                    String strDOB = model.getPetDOB();
//                    Log.e("DOB", strDOB);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    try {
                        Date dtDOB = format.parse(strDOB);
                        Calendar calDOB = Calendar.getInstance();
                        calDOB.setTime(dtDOB);
                        int dobYear = calDOB.get(Calendar.YEAR);
                        int dobMonth = calDOB.get(Calendar.MONTH) + 1;
                        int dobDate = calDOB.get(Calendar.DATE);

                        /** CALCULATE THE PET'S AGE **/
                        Period petAge = getPetAge(dobYear, dobMonth, dobDate);
                        String strPetAge = "I am " + petAge.getYears() + " Years " + petAge.getMonths() + " Months and " + petAge.getDays() + " Days old";
                        viewHolder.txtPetAge.setText(strPetAge);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    /** SET THE PET GENDER **/
                    if (model.getPetGender().equalsIgnoreCase("male"))  {
                        viewHolder.txtGender.setText(getResources().getString(R.string.gender_male));
                        viewHolder.txtGender.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_blue_dark));
                    } else if (model.getPetGender().equalsIgnoreCase("female")) {
                        viewHolder.txtGender.setText(getResources().getString(R.string.gender_female));
                        viewHolder.txtGender.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_dark));
                    }

                    /** SET THE USER PET IMAGE**/
                    if (model.getPetPicture() != null)  {
                        Glide.with(getApplicationContext())
                                .load(model.getPetPicture())
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .centerCrop()
                                .into(viewHolder.imgvwPetPicture);
                    }

                    /***** SHOW THE PET DETAILS *****/
                    viewHolder.imgvwPetPicture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), PetDetailsContainer.class);
                            intent.putExtra("PET_ID_KEY", adapter.getRef(position).getKey());
                            startActivity(intent);
                        }
                    });

                    /** SHOW THE PETS POPUP OPTIONS **/
                    viewHolder.imgvwPetOptions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupMenu pm = new PopupMenu(getApplicationContext(), v);
                            pm.getMenuInflater().inflate(R.menu.pm_user_pet_item, pm.getMenu());
                            pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId())   {
                                        case R.id.menuDetails:
                                            Intent intent = new Intent(getApplicationContext(), PetDetailsContainer.class);
                                            intent.putExtra("PET_ID_KEY", adapter.getRef(position).getKey());
                                            startActivity(intent);
                                            break;
                                        case R.id.menuEdit:
                                            Intent edit = new Intent(getApplicationContext(), PetEditor.class);
                                            edit.putExtra("PET_ID_KEY", adapter.getRef(position).getKey());
                                            startActivity(edit);
                                            break;
                                        case R.id.menuDelete:
                                            String strTitle = "Delete \"" + model.getPetName() + "\"";
                                            String strMessage = getResources().getString(R.string.pet_delete_message);
                                            String strYes = getResources().getString(R.string.generic_mb_yes);
                                            String strNo = getResources().getString(R.string.generic_mb_no);

                                            /** SHOW THE DELETE PROMPT **/
                                            new MaterialDialog.Builder(UserPets.this)
                                                    .icon(ContextCompat.getDrawable(UserPets.this, R.drawable.ic_info_outline_black_24dp))
                                                    .title(strTitle)
                                                    .cancelable(true)
                                                    .content(strMessage)
                                                    .positiveText(strYes)
                                                    .negativeText(strNo)
                                                    .theme(Theme.LIGHT)
                                                    .typeface("Roboto-Medium.ttf", "Roboto-Regular.ttf")
                                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            /** GET THE STORAGE REFERENCE **/
                                                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                                            StorageReference strRefFile = storageReference.getStorage().getReferenceFromUrl(model.getPetPicture());
                                                            strRefFile.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    /** DELETE THE USER PET  **/
                                                                    reference.removeValue();

                                                                    /** SHOW THE CONFIRMATION **/
                                                                    Toast.makeText(getApplicationContext(), "The Pet was successfully deleted", Toast.LENGTH_SHORT).show();

                                                                    /** FETCH THE LIST AGAIN **/
                                                                    setupFirebaseAdapter();

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                    /** SHOW THE ERROR **/
                                                                    Toast.makeText(getApplicationContext(), "The Pet could not be deleted. Please try again", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }
                                                    })
                                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            dialog.dismiss();
                                                        }
                                                    }).show();
                                            break;
                                        default:
                                            break;
                                    }
                                    return true;
                                }
                            }); pm.show();
                        }
                    });
                }
            }
        };

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHide(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHide(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHide(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHide(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /** SHOW OR HIDE THE EMPTY LAYOUT **/
                emptyShowOrHide(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        /** SET THE ADAPTER **/
        listPets.setAdapter(adapter);
    }

    /** CALCULATE THE PET'S AGE **/
    private Period getPetAge(int year, int month, int date) {
        LocalDate dob = new LocalDate(year, month, date);
        LocalDate now = new LocalDate();
        return Period.fieldDifference(dob, now);
    }

    /** SHOW OR HIDE THE EMPTY LAYOUT **/
    private void emptyShowOrHide(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChildren()) {
            /** SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT **/
            listPets.setVisibility(View.VISIBLE);
            linlaEmpty.setVisibility(View.GONE);
        } else {
            /** HIDE THE RECYCLER VIEW AND SHOW THE EMPTY LAYOUT **/
            listPets.setVisibility(View.GONE);
            linlaEmpty.setVisibility(View.VISIBLE);
        }
    }

    private static class PetsVH extends RecyclerView.ViewHolder {

        final AppCompatTextView txtPetName;
        final AppCompatTextView txtGender;
        final AppCompatImageView imgvwPetPicture;
        final AppCompatTextView txtPetAge;
        final AppCompatImageView imgvwPetOptions;

        public PetsVH(View itemView) {
            super(itemView);

            txtPetName = (AppCompatTextView) itemView.findViewById(R.id.txtPetName);
            txtGender = (AppCompatTextView) itemView.findViewById(R.id.txtGender);
            imgvwPetPicture = (AppCompatImageView) itemView.findViewById(R.id.imgvwPetPicture);
            txtPetAge = (AppCompatTextView) itemView.findViewById(R.id.txtPetAge);
            imgvwPetOptions = (AppCompatImageView) itemView.findViewById(R.id.imgvwPetOptions);
        }
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listPets.setLayoutManager(manager);
        listPets.setHasFixedSize(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }

    /***** CONFIGURE THE TOOLBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configTB() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        String strTitle = "My Pets";
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