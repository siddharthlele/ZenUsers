package com.zenpets.users.landing.modules;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zenpets.users.R;
import com.zenpets.users.utils.TypefaceSpan;
import com.zenpets.users.utils.adapters.services.ServicesAdapter;
import com.zenpets.users.utils.models.services.ServicesData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllServicesFrag extends Fragment {

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.listServices) RecyclerView listServices;

    /** THE ARRAY LIST AND ADAPTER **/
    private ArrayList<ServicesData> arrServices = new ArrayList<>();
    private ServicesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.services_frag_list, container, false);
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

        /***** CONFIGURE THE TOOLBAR *****/
        configTB();

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecyclerView();

        /** FETCH ALL SERVICES **/
        fetchServices();
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {

        Toolbar myToolbar = (Toolbar) getActivity().findViewById(R.id.myToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);

//        String strTitle = getString(R.string.add_a_new_pet);
        String strTitle = "Services";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(s);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(null);
    }

    /** FETCH ALL SERVICES **/
    private void fetchServices() {
        /** CLEAR THE ARRAY LIST **/
        arrServices.clear();

        /** CONFIGURE THE SERVICES DATA **/
        ServicesData s3 = new ServicesData();
        s3.setServiceName("Doctors");
        s3.setServiceDesc("Find a vet near you");
        arrServices.add(s3);

        ServicesData s2 = new ServicesData();
        s2.setServiceName("Breeders");
        s2.setServiceDesc("Find a breeder for your cute and cuddly");
        arrServices.add(s2);

        ServicesData s4 = new ServicesData();
        s4.setServiceName("Handlers");
        s4.setServiceDesc("Need help with breeding your pet?");
        arrServices.add(s4);

        ServicesData s5 = new ServicesData();
        s5.setServiceName("Groomers");
        s5.setServiceDesc("Make your cute and cuddly, cuter and cuddlier");
        arrServices.add(s5);

        ServicesData s6 = new ServicesData();
        s6.setServiceName("Home Boarding");
        s6.setServiceDesc("Find a home away from home for your pet");
        arrServices.add(s6);

        ServicesData s7 = new ServicesData();
        s7.setServiceName("Pet Hostels");
        s7.setServiceDesc("Find a pet hostel");
        arrServices.add(s7);

        ServicesData s8 = new ServicesData();
        s8.setServiceName("Trainers");
        s8.setServiceDesc("A Trainer to train your pet.");
        arrServices.add(s8);

        ServicesData s9 = new ServicesData();
        s9.setServiceName("Walkers");
        s9.setServiceDesc("For the lazy ones to walk your pet for you ;-)");
        arrServices.add(s9);

        ServicesData s1 = new ServicesData();
        s1.setServiceName("Adoptions");
        s1.setServiceDesc("Adopt a pet");
        arrServices.add(s1);

        /** SET THE ADAPTER TO THE RECYCLER VIEW **/
        adapter = new ServicesAdapter(getActivity(), arrServices);
        listServices.setAdapter(adapter);
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listServices.setLayoutManager(manager);
    }
}