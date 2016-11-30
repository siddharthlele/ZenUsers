package com.zenpets.users.utils.adapters.shops;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zenpets.users.R;
import com.zenpets.users.landing.services.DoctorsListActivity;
import com.zenpets.users.utils.models.shops.ShopsData;

import java.util.ArrayList;

public class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.ServicesVH> {

    /***** THE ACTIVITY INSTANCE FOR USE IN THE ADAPTER *****/
    private Activity activity;

    /***** ARRAY LIST TO GET DATA FROM THE ACTIVITY *****/
    private ArrayList<ShopsData> arrShops;

    public ShopsAdapter(Activity activity, ArrayList<ShopsData> arrShops) {

        /** CAST THE ACTIVITY IN THE GLOBAL ACTIVITY INSTANCE **/
        this.activity = activity;

        /** CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
        this.arrShops = arrShops;
    }

    @Override
    public int getItemCount() {
        return arrShops.size();
    }

    @Override
    public void onBindViewHolder(ServicesVH holder, final int position) {
        final ShopsData data = arrShops.get(position);

        /** SET THE SERVICE NAME **/
        holder.txtServiceName.setText(data.getShopName());

        /** SET THE SERVICE DESCRIPTION **/
        holder.txtServiceDescription.setText(data.getShopDesc());

        /** SET THE ON CLICK LISTENER **/
        holder.linlaService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strServiceName = data.getShopName();
                Intent intent;
                if (strServiceName.equalsIgnoreCase("doctors"))    {
                    intent = new Intent(activity, DoctorsListActivity.class);
                    activity.startActivity(intent);
//                } else if (strServiceName.equalsIgnoreCase("breeders"))  {
//                    intent = new Intent(activity, BreedersActivity.class);
//                    activity.startActivity(intent);
//                } else if (strServiceName.equalsIgnoreCase("handlers")) {
//                    intent = new Intent(activity, HandlersActivity.class);
//                    activity.startActivity(intent);
//                } else if (strServiceName.equalsIgnoreCase("groomers")) {
//                    intent = new Intent(activity, GroomersActivity.class);
//                    activity.startActivity(intent);
//                } else if (strServiceName.equalsIgnoreCase("home boarding")) {
//                    intent = new Intent(activity, HomeBoardingActivity.class);
//                    activity.startActivity(intent);
//                } else if (strServiceName.equalsIgnoreCase("pet hostels")) {
//                    intent = new Intent(activity, PetHostelsActivity.class);
//                    activity.startActivity(intent);
//                } else if (strServiceName.equalsIgnoreCase("trainers")) {
//                    intent = new Intent(activity, TrainersActivity.class);
//                    activity.startActivity(intent);
//                } else if (strServiceName.equalsIgnoreCase("walkers")) {
//                    intent = new Intent(activity, WalkersActivity.class);
//                    activity.startActivity(intent);
//                } else if (strServiceName.equalsIgnoreCase("adoptions")) {
//                    intent = new Intent(activity, AdoptionActivity.class);
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