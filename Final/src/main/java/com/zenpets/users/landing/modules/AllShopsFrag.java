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
import com.zenpets.users.utils.adapters.shops.ShopsAdapter;
import com.zenpets.users.utils.models.shops.ShopsData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllShopsFrag extends Fragment {

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.listShops) RecyclerView listShops;

    /** THE ARRAY LIST AND ADAPTER **/
    private ArrayList<ShopsData> arrShops = new ArrayList<>();
    private ShopsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.shops_frag_list, container, false);
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

        /** FETCH ALL SHOPS **/
        fetchShops();
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {

        Toolbar myToolbar = (Toolbar) getActivity().findViewById(R.id.myToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);

//        String strTitle = getString(R.string.add_a_new_pet);
        String strTitle = "Shop";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(s);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(null);
    }

    /** FETCH ALL SHOPS **/
    private void fetchShops() {
        /** CLEAR THE ARRAY LIST **/
        arrShops.clear();

        ShopsData s1 = new ShopsData();
        s1.setShopName("Pet Food");
        s1.setShopDesc("The tastiest food for your cute and cuddly");
        arrShops.add(s1);

        ShopsData s2 = new ShopsData();
        s2.setShopName("Accessories");
        s2.setShopDesc("Get some bling for your pet");
        arrShops.add(s2);

        /** CONFIGURE THE SERVICES DATA **/
        ShopsData s3 = new ShopsData();
        s3.setShopName("Medicines");
        s3.setShopDesc("Keeping your pet's well being well....");
        arrShops.add(s3);

        /** SET THE ADAPTER TO THE RECYCLER VIEW **/
        adapter = new ShopsAdapter(getActivity(), arrShops);
        listShops.setAdapter(adapter);
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listShops.setLayoutManager(manager);
    }
}