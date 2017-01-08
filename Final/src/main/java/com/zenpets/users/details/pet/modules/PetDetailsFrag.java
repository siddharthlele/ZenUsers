package com.zenpets.users.details.pet.modules;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zenpets.users.R;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class PetDetailsFrag extends Fragment {

    /** THE FIREBASE DATABASE REFERENCE INSTANCE **/
    private DatabaseReference refPet;

    /** THE USER KEY AND THE PET ID **/
    String USER_KEY = null;
    private String PET_ID;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwPetCircular) CircleImageView imgvwPetCircular;
    @BindView(R.id.txtPetName) AppCompatTextView txtPetName;
    @BindView(R.id.txtPetGender) AppCompatTextView txtPetGender;
    @BindView(R.id.txtPetAge) AppCompatTextView txtPetAge;
    @BindView(R.id.txtPetBreed) AppCompatTextView txtPetBreed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.pet_details_frag, container, false);
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

        /** GET THE INCOMING DATA **/
        getIncomingData();
    }

    /** SHOW THE PET DETAILS **/
    private void showPetDetails() {
        refPet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    PetData pet = dataSnapshot.getValue(PetData.class);

                    /** SET THE NAME **/
                    txtPetName.setText(pet.getPetName().toUpperCase());

                    /** SET THE GENDER **/
                    txtPetGender.setText(pet.getPetGender());

                    /** SET THE AGE **/
                    String strDOB = pet.getPetDOB();
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
                        String strPetAge = petAge.getYears() + " Years Old";
                        txtPetAge.setText(strPetAge);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    /** SET THE BREED **/
                    txtPetBreed.setText(pet.getPetBreedName());

                    /** SET THE CIRCULAR IMAGE VIEW PROFILE **/
                    if (pet.getPetPicture() != null)   {
                        Picasso.with(getActivity())
                                .load(pet.getPetPicture())
                                .centerInside()
                                .resize(1024, 768)
                                .into(imgvwPetCircular);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /** GET THE INCOMING DATA **/
    private void getIncomingData() {

        /** GET THE CATEGORY ID **/
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null && bundle.containsKey("PET_ID_KEY") && bundle.containsKey("USER_KEY")) {
            PET_ID = bundle.getString("PET_ID_KEY");
            USER_KEY = bundle.getString("USER_KEY");
            if (PET_ID == null) {
                Toast.makeText(getActivity(), "failed to get required information", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            } else {
                /** GET THE FIREBASE USER ID **/
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String USER_ID = user.getUid();
                    /** INSTANTIATE THE refPet  **/
                    refPet = FirebaseDatabase.getInstance().getReference().child("Users").child(USER_KEY).child("Pets").child(PET_ID);

                    /** SHOW THE PET DETAILS **/
                    showPetDetails();

                } else {
                    Toast.makeText(getActivity(), "failed to get required information", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        } else {
            Toast.makeText(getActivity(), "failed to get required information", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    /** CALCULATE THE PET'S AGE **/
    private Period getPetAge(int year, int month, int date) {
        LocalDate dob = new LocalDate(year, month, date);
        LocalDate now = new LocalDate();
        return Period.fieldDifference(dob, now);
    }
}