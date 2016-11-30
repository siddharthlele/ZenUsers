package com.zenpets.users.utils.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zenpets.users.R;
import com.zenpets.users.utils.models.VaccinationsData;

import java.util.ArrayList;

public class VaccinationsAdapter extends RecyclerView.Adapter<VaccinationsAdapter.VaccinationsVH> {

    /** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER **/
    private Activity activity;

    /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
    private ArrayList<VaccinationsData> arrVaccinations;

    public VaccinationsAdapter(Activity activity, ArrayList<VaccinationsData> arrVaccinations) {

        /** CAST THE ACTIVITY FROM THE METHOD TO THE LOCAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrVaccinations = arrVaccinations;
    }

    @Override
    public int getItemCount() {
        return arrVaccinations.size();
    }

    @Override
    public void onBindViewHolder(VaccinationsVH holder, final int position) {
        VaccinationsData data = arrVaccinations.get(position);

        /** SET THE VACCINE NAME **/
        String vaccineName = data.getVaccineName();
        if (vaccineName != null)	{
            holder.txtVaccineName.setText(vaccineName);
        }

        /** SET THE VACCINATION DATE **/
        if (data.getVaccineDate() != null)  {
            holder.txtVaccineDate.setText(data.getVaccineDate());
        }

        /** SET THE VACCINATION NOTES **/
        if (data.getVaccineNotes() != null) {
            holder.txtVaccineNotes.setText(data.getVaccineNotes());
        } else {
            holder.txtVaccineNotes.setText("No notes added....");
        }
    }

    @Override
    public VaccinationsVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.user_profile_vaccinations_item, parent, false);

        return new VaccinationsVH(itemView);
    }

    class VaccinationsVH extends RecyclerView.ViewHolder   {

        AppCompatTextView txtVaccineName;
        AppCompatTextView txtVaccineDate;
        AppCompatTextView txtVaccineNotes;

        VaccinationsVH(View v) {
            super(v);

            txtVaccineName = (AppCompatTextView) v.findViewById(R.id.txtVaccineName);
            txtVaccineDate = (AppCompatTextView) v.findViewById(R.id.txtVaccineDate);
            txtVaccineNotes = (AppCompatTextView) v.findViewById(R.id.txtVaccineNotes);
        }
    }
}