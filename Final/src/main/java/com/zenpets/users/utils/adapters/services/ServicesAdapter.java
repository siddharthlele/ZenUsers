package com.zenpets.users.utils.adapters.services;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zenpets.users.R;
import com.zenpets.users.services.AdoptionListActivity;
import com.zenpets.users.services.DoctorsListActivity;
import com.zenpets.users.utils.models.services.ServicesData;

import java.util.ArrayList;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServicesVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private ArrayList<ServicesData> arrServices;

    public ServicesAdapter(Activity activity, ArrayList<ServicesData> arrServices) {

        /** CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrServices = arrServices;
    }

    @Override
    public int getItemCount() {
        return arrServices.size();
    }

    @Override
    public void onBindViewHolder(ServicesVH holder, final int position) {
        final ServicesData data = arrServices.get(position);

        /** SET THE SERVICE NAME **/
        holder.txtServiceName.setText(data.getServiceName());

        /** SET THE SERVICE DESCRIPTION **/
        holder.txtServiceDescription.setText(data.getServiceDesc());

        /** SET THE ON CLICK LISTENER **/
        holder.linlaService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strServiceName = data.getServiceName();
                Intent intent;
                if (strServiceName.equalsIgnoreCase("doctors"))    {
                    intent = new Intent(activity, DoctorsListActivity.class);
                    activity.startActivity(intent);
                } else if (strServiceName.equalsIgnoreCase("breeders"))  {
                    Toast.makeText(activity, "Coming soon....", Toast.LENGTH_SHORT).show();
//                    intent = new Intent(activity, BreedersActivity.class);
//                    activity.startActivity(intent);
                } else if (strServiceName.equalsIgnoreCase("handlers")) {
                    Toast.makeText(activity, "Coming soon....", Toast.LENGTH_SHORT).show();
//                    intent = new Intent(activity, HandlersActivity.class);
//                    activity.startActivity(intent);
                } else if (strServiceName.equalsIgnoreCase("groomers")) {
                    Toast.makeText(activity, "Coming soon....", Toast.LENGTH_SHORT).show();
//                    intent = new Intent(activity, GroomersActivity.class);
//                    activity.startActivity(intent);
                } else if (strServiceName.equalsIgnoreCase("home boarding")) {
                    Toast.makeText(activity, "Coming soon....", Toast.LENGTH_SHORT).show();
//                    intent = new Intent(activity, HomeBoardingActivity.class);
//                    activity.startActivity(intent);
                } else if (strServiceName.equalsIgnoreCase("pet hostels")) {
                    Toast.makeText(activity, "Coming soon....", Toast.LENGTH_SHORT).show();
//                    intent = new Intent(activity, PetHostelsActivity.class);
//                    activity.startActivity(intent);
                } else if (strServiceName.equalsIgnoreCase("trainers")) {
                    Toast.makeText(activity, "Coming soon....", Toast.LENGTH_SHORT).show();
//                    intent = new Intent(activity, TrainersActivity.class);
//                    activity.startActivity(intent);
                } else if (strServiceName.equalsIgnoreCase("walkers")) {
                    Toast.makeText(activity, "Coming soon....", Toast.LENGTH_SHORT).show();
//                    intent = new Intent(activity, WalkersActivity.class);
//                    activity.startActivity(intent);
                } else if (strServiceName.equalsIgnoreCase("adoptions")) {
                    Toast.makeText(activity, "Coming soon....", Toast.LENGTH_SHORT).show();
//                    intent = new Intent(activity, AdoptionListActivity.class);
//                    activity.startActivity(intent);
                }
            }
        });
    }

    @Override
    public ServicesVH onCreateViewHolder(ViewGroup parent, int i) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.services_frag_item, parent, false);

        return new ServicesVH(itemView);
    }

    class ServicesVH extends RecyclerView.ViewHolder	{
        LinearLayout linlaService;
        AppCompatTextView txtServiceName;
        AppCompatTextView txtServiceDescription;

        ServicesVH(View v) {
            super(v);

            /*****	CAST THE LAYOUT ELEMENTS	*****/
            linlaService = (LinearLayout) v.findViewById(R.id.linlaService);
            txtServiceName = (AppCompatTextView) v.findViewById(R.id.txtServiceName);
            txtServiceDescription = (AppCompatTextView) v.findViewById(R.id.txtServiceDescription);
        }
    }
}