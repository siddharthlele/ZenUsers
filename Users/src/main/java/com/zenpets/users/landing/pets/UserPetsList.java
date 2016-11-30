package com.zenpets.users.landing.pets;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.zenpets.users.R;
import com.zenpets.users.creators.PetCreatorActivity;
import com.zenpets.users.modifier.PetModifierActivity;
import com.zenpets.users.pets.PetDetailsContainer;
import com.zenpets.users.utils.TypefaceSpan;
import com.zenpets.users.utils.models.PetData;

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

public class UserPetsList extends Fragment {

    /** A FIREBASE DATABASE REFERENCE INSTANCE **/
    private DatabaseReference reference;

    /** THE FIREBASE RECYCLER ADAPTER INSTANCE **/
    private FirebaseRecyclerAdapter adapter;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaHeaderProgress) LinearLayout linlaHeaderProgress;
    @BindView(R.id.gridUserPets) RecyclerView gridUserPets;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** THE STATIC REQUEST CODES **/
    private static final int CREATE_NEW_PET = 101;

    /** ADD A NEW PET **/
    @OnClick(R.id.linlaEmpty) void newPet() {
        Intent addNewPet = new Intent(getActivity(), PetCreatorActivity.class);
        startActivityForResult(addNewPet, CREATE_NEW_PET);
    }

    @OnClick(R.id.fabNewUserPet) void fabNewUserPet()   {
        Intent addNewPet = new Intent(getActivity(), PetCreatorActivity.class);
        startActivityForResult(addNewPet, CREATE_NEW_PET);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.pets_list_frag, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /** INDICATE THAT THE FRAGMENT SHOULD RETAIN IT'S STATE **/
        setRetainInstance(true);

        /** INDICATE THAT THE FRAGMENT HAS AN OPTIONS MENU **/
        setHasOptionsMenu(true);

        /** INVALIDATE THE EARLIER OPTIONS MENU SET IN OTHER FRAGMENTS / ACTIVITIES **/
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /***** CONFIGURE THE ACTIONBAR *****/
        configAB();

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /** GET THE FIREBASE USER DETAILS **/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String USER_ID = user.getUid();
            reference = FirebaseDatabase.getInstance().getReference().child("UserPets").child(USER_ID);

            /** SETUP THE FIREBASE ADAPTER **/
            setupFirebaseAdapter();
        }
    }

    /** SETUP THE FIREBASE ADAPTER **/
    private void setupFirebaseAdapter() {

        adapter = new FirebaseRecyclerAdapter<PetData, UserPetsViewHolder>
                (PetData.class, R.layout.pets_list_item, UserPetsViewHolder.class, reference) {

            @Override
            protected void populateViewHolder(UserPetsViewHolder viewHolder, final PetData model, final int position) {

                if (model != null)  {

                    /** SET THE PET NAME **/
                    viewHolder.txtPetName.setText(model.getPetName());

                    /** SET THE PET DOB **/
                    String strDOB = model.getPetDOB();
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
                        viewHolder.txtGender.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_blue_dark));
                    } else if (model.getPetGender().equalsIgnoreCase("female")) {
                        viewHolder.txtGender.setText(getResources().getString(R.string.gender_female));
                        viewHolder.txtGender.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark));
                    }

                    /** SET THE USER PET IMAGE**/
                    Picasso.with(getActivity())
                            .load(model.getPetPicture())
                            .resize(1024, 768)
                            .centerInside()
                            .into(viewHolder.imgvwPetPicture);

                    /***** SHOW THE PET DETAILS *****/
                    viewHolder.imgvwPetPicture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), PetDetailsContainer.class);
                            intent.putExtra("PET_ID_KEY", adapter.getRef(position).getKey());
                            startActivity(intent);
                        }
                    });

                    /** SHOW THE PETS POPUP OPTIONS **/
                    viewHolder.imgvwPetOptions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PopupMenu pm = new PopupMenu(getActivity(), v);
                            pm.getMenuInflater().inflate(R.menu.pm_user_pet_item, pm.getMenu());
                            pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId())   {
                                        case R.id.menuDetails:
                                            Intent intent = new Intent(getActivity(), PetDetailsContainer.class);
                                            intent.putExtra("PET_ID_KEY", adapter.getRef(position).getKey());
                                            startActivity(intent);
                                            break;
                                        case R.id.menuEdit:
                                            Intent edit = new Intent(getActivity(), PetModifierActivity.class);
                                            edit.putExtra("PET_ID_KEY", adapter.getRef(position).getKey());
                                            startActivity(edit);
                                            break;
                                        case R.id.menuDelete:
                                            String strTitle = "Delete \"" + model.getPetName() + "\"";
                                            String strMessage = getResources().getString(R.string.pet_delete_message);
                                            String strYes = getResources().getString(R.string.generic_mb_yes);
                                            String strNo = getResources().getString(R.string.generic_mb_no);

                                            /** SHOW THE DELETE PROMPT **/
                                            new MaterialDialog.Builder(getActivity())
                                                    .icon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_outline_black_24dp))
                                                    .title(strTitle)
                                                    .cancelable(true)
                                                    .content(strMessage)
                                                    .positiveText(strYes)
                                                    .negativeText(strNo)
                                                    .theme(Theme.LIGHT)
                                                    .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
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
                                                                    Toast.makeText(getActivity(), "The Pet was successfully deleted", Toast.LENGTH_SHORT).show();

                                                                    /** FETCH THE LIST AGAIN **/
                                                                    setupFirebaseAdapter();

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                    /** SHOW THE ERROR **/
                                                                    Toast.makeText(getActivity(), "The Pet could not be deleted. Please try again", Toast.LENGTH_LONG).show();
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
        gridUserPets.setAdapter(adapter);
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
            gridUserPets.setVisibility(View.VISIBLE);
            linlaEmpty.setVisibility(View.GONE);
        } else {
            /** HIDE THE RECYCLER VIEW AND SHOW THE EMPTY LAYOUT **/
            gridUserPets.setVisibility(View.GONE);
            linlaEmpty.setVisibility(View.VISIBLE);
        }
    }

    private static class UserPetsViewHolder extends RecyclerView.ViewHolder {

        final AppCompatTextView txtPetName;
        final AppCompatTextView txtGender;
        final AppCompatImageView imgvwPetPicture;
        final AppCompatTextView txtPetAge;
        final AppCompatImageView imgvwPetOptions;

        public UserPetsViewHolder(View itemView) {
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
        int intOrientation = getActivity().getResources().getConfiguration().orientation;
        gridUserPets.setHasFixedSize(true);
        GridLayoutManager glm = null;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet)   {
            if (intOrientation == 1)	{
                glm = new GridLayoutManager(getActivity(), 2);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(getActivity(), 3);
            }
        } else {
            if (intOrientation == 1)    {
                glm = new GridLayoutManager(getActivity(), 1);
            } else if (intOrientation == 2) {
                glm = new GridLayoutManager(getActivity(), 2);
            }
        }
        gridUserPets.setLayoutManager(glm);
    }

    /***** CONFIGURE THE ACTIONBAR *****/
    private void configAB() {
        String strTitle = getResources().getString(R.string.dash_btn_lbl_pets);
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(s);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }
}