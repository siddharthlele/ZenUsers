package com.zenpets.users.landing.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.zenpets.users.creators.VaccinationCreator;
import com.zenpets.users.utils.adapters.VaccinationsAdapter;
import com.zenpets.users.utils.models.VaccinationsData;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VaccinationsFrag extends Fragment {

    /** THE USER ID **/
    private String USER_ID = null;

    /** THE ADAPTER AND THE ARRAY LIST **/
    private VaccinationsAdapter adapter;
    private ArrayList<VaccinationsData> arrVaccinations = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaHeaderProgress) LinearLayout linlaHeaderProgress;
    @BindView(R.id.listVaccinations) RecyclerView listVaccinations;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    /** ADD A NEW VACCINATION REMINDER **/
    @OnClick(R.id.txtAddVaccinationReminder) void newVaccination()   {
        Intent intent = new Intent(getActivity(), VaccinationCreator.class);
        startActivity(intent);
    }

    /** ADD A NEW VACCINATION REMINDER **/
    @OnClick(R.id.fabNewVaccination) void fabNewVaccination() {
        Intent intent = new Intent(getActivity(), VaccinationCreator.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.user_profile_vaccinations_list, container, false);
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

        /** GET THE USER ID **/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            USER_ID = user.getUid();
        } else {
            Toast.makeText(getActivity(), "There was a problem fetching required data....", Toast.LENGTH_SHORT).show();
        }

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /** INSTANTIATE THE ADAPTER **/
        adapter = new VaccinationsAdapter(getActivity(), arrVaccinations);

        /** GET THE VACCINATION REMINDERS **/
        getVaccinationReminders();
    }

    /** GET THE VACCINATION REMINDERS **/
    private void getVaccinationReminders() {
        /** CLEAR THE ARRAY LIST **/
        arrVaccinations.clear();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Vaccinations");
        Query query = reference.orderByChild("userID").equalTo(USER_ID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {

                    /** AN INSTANCE OF THE VACCINATIONS DATA CLASS **/
                    VaccinationsData data;

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                        /** INSTANTIATE THE VACCINATIONS DATA CLASS INSTANCE **/
                        data = new VaccinationsData();

                        /** GET THE PET ID **/
                        String petID = postSnapshot.child("petID").getValue(String.class);
                        data.setPetID(petID);
//                        Log.e("PET ID", petID);

                        /** GET THE VACCINE NAME **/
                        String vaccineName = postSnapshot.child("vaccineName").getValue(String.class);
                        data.setVaccineName(vaccineName);
//                        Log.e("NAME", vaccineName);

                        /** GET THE VACCINE DATE **/
                        String vaccineDate  = postSnapshot.child("vaccineDate").getValue(String.class);
//                        Log.e("DATE", vaccineDate);

                        /* CALCULATE THE DAYS LEFT */
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        try {
                            Date dtDOB = format.parse(vaccineDate);
                            Calendar calDOB = Calendar.getInstance();
                            calDOB.setTime(dtDOB);
                            int dobYear = calDOB.get(Calendar.YEAR);
                            int dobMonth = calDOB.get(Calendar.MONTH) + 1;
                            int dobDate = calDOB.get(Calendar.DATE);

                            /** CALCULATE THE DAYS LEFT **/
                            Period period = getDaysLeft(dobYear, dobMonth, dobDate);
                            String strDaysLeft = period.getDays() + " days left";
//                            Log.e("DAYS LEFT", strDaysLeft);
                            if (strDaysLeft.contains("-"))  {
                                Period periodDaysPast = getDaysPast(dobYear, dobMonth, dobDate);
                                String strDaysPast = periodDaysPast.getDays() + " days past";
                                data.setVaccineDate(vaccineDate + " (" + strDaysPast + ")");
                            } else {
                                data.setVaccineDate(vaccineDate + " (" + strDaysLeft + ")");
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        /** GET THE VACCINE NOTES **/
                        String vaccineNotes = postSnapshot.child("vaccineNotes").getValue(String.class);
                        data.setVaccineNotes(vaccineNotes);
//                        if (vaccineNotes != null)
//                            Log.e("NOTES", vaccineNotes);

                        /** ADD THE COLLECTED DATA TO THE ARRAY LIST **/
                        arrVaccinations.add(data);
                    }

                    /** SET THE ADAPTER TO THE RECYCLER VIEW **/
                    listVaccinations.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private Period getDaysPast(int year, int month, int date) {
        LocalDate vaccineDate = new LocalDate(year, month, date);
        LocalDate now = new LocalDate();
        return Period.fieldDifference(vaccineDate, now);
    }

    /** CALCULATE THE DAYS LEFT **/
    private Period getDaysLeft(int year, int month, int date) {
        LocalDate vaccineDate = new LocalDate(year, month, date);
        LocalDate now = new LocalDate();
        return Period.fieldDifference(now, vaccineDate);
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listVaccinations.setLayoutManager(manager);
        listVaccinations.setHasFixedSize(true);
    }
}