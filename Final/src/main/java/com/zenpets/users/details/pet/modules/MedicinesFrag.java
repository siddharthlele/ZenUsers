package com.zenpets.users.details.pet.modules;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zenpets.users.R;
import com.zenpets.users.utils.models.profile.MedicinesData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MedicinesFrag extends Fragment {

    /** A FIREBASE DATABASE REFERENCE INSTANCE **/
    private DatabaseReference reference;

    /** THE PET ID **/
    private String PET_ID;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaHeaderProgress) LinearLayout linlaHeaderProgress;
    @BindView(R.id.listMedicines) RecyclerView listMedicines;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** THE STATIC REQUEST CODES **/
    private static final int CREATE_NEW_MEDICINE = 101;

    @OnClick(R.id.linlaEmpty) void newMedicine()   {
//        Intent addNewPet = new Intent(getActivity(), MedicationCreator.class);
//        addNewPet.putExtra("PET_ID_KEY", PET_ID);
//        startActivityForResult(addNewPet, CREATE_NEW_MEDICINE);
    }

    @OnClick(R.id.fabNewMedicine) void fabNewMedicine()   {
//        Intent addNewPet = new Intent(getActivity(), MedicationCreator.class);
//        addNewPet.putExtra("PET_ID_KEY", PET_ID);
//        startActivityForResult(addNewPet, CREATE_NEW_MEDICINE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.pet_medication_frag, container, false);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /** GET THE PET ID **/
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null && bundle.containsKey("PET_ID_KEY"))  {
            PET_ID = bundle.getString("PET_ID_KEY");
            if (PET_ID != null) {

                /** GET THE FIREBASE USER DETAILS **/
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String USER_ID = user.getUid();
                    reference = FirebaseDatabase.getInstance().getReference()
                            .child("UserPets").child(USER_ID).child(PET_ID).child("Medicines");

                    /** SETUP THE FIREBASE ADAPTER **/
                    setupFirebaseAdapter();
                }
            }
        }

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecycler();
    }

    /** SETUP THE FIREBASE ADAPTER **/
    private void setupFirebaseAdapter() {

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<MedicinesData, PetMedicinesViewHolder>
                (MedicinesData.class, R.layout.pet_medication_item, PetMedicinesViewHolder.class, reference) {

            @Override
            protected void populateViewHolder(PetMedicinesViewHolder viewHolder, MedicinesData model, final int position) {

                /** SET THE MEDICINE NAME **/
                viewHolder.txtMedicineName.setText(model.getMedicineName());

                /** SET THE USER MEDICINE IMAGE**/
                if (model.getMedicineImage() != null) {
                    Picasso.with(getActivity())
                            .load(model.getMedicineImage())
                            .resize(600, 400)
                            .centerInside()
                            .into(viewHolder.imgvwMedicineImage);
                }

//                /***** SHOW THE PET DETAILS *****/
//                viewHolder.imgvwMedicineImage.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.e("DETAILS", adapter.getRef(position).getKey());
//                        Intent intent = new Intent(getActivity(), UserPetDetailsActivity.class);
//                        intent.putExtra("PET_ID_KEY", adapter.getRef(position).getKey());
//                        startActivity(intent);
//                    }
//                });
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
        listMedicines.setAdapter(adapter);
    }

    private static class PetMedicinesViewHolder extends RecyclerView.ViewHolder {

        final AppCompatTextView txtMedicineName;
        final AppCompatImageView imgvwMedicineImage;
        final AppCompatImageView imgvwMedicineOptions;

        public PetMedicinesViewHolder(View itemView) {
            super(itemView);

            txtMedicineName = (AppCompatTextView) itemView.findViewById(R.id.txtMedicineName);
            imgvwMedicineImage = (AppCompatImageView) itemView.findViewById(R.id.imgvwMedicineImage);
            imgvwMedicineOptions = (AppCompatImageView) itemView.findViewById(R.id.imgvwMedicineOptions);
        }
    }

    /** SHOW OR HIDE THE EMPTY LAYOUT **/
    private void emptyShowOrHide(DataSnapshot dataSnapshot) {
        if (dataSnapshot.hasChildren()) {
            /** SHOW THE RECYCLER VIEW AND HIDE THE EMPTY LAYOUT **/
            listMedicines.setVisibility(View.VISIBLE);
            linlaEmpty.setVisibility(View.GONE);
        } else {
            /** HIDE THE RECYCLER VIEW AND SHOW THE EMPTY LAYOUT **/
            listMedicines.setVisibility(View.GONE);
            linlaEmpty.setVisibility(View.VISIBLE);
        }
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        int intOrientation = getActivity().getResources().getConfiguration().orientation;
        listMedicines.setHasFixedSize(true);
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
        listMedicines.setLayoutManager(glm);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
}