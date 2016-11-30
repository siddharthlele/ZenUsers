package com.zenpets.users.landing;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.zenpets.users.R;
import com.zenpets.users.landing.others.DiscussionsFragment;
import com.zenpets.users.landing.others.HelpActivity;
import com.zenpets.users.landing.others.ServicesDashboard;
import com.zenpets.users.landing.others.SettingsActivity;
import com.zenpets.users.landing.others.ShopsDashboard;
import com.zenpets.users.landing.others.TipsFragment;
import com.zenpets.users.landing.pets.UserPetsList;
import com.zenpets.users.landing.services.DoctorsListFrag;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainLandingActivity extends AppCompatActivity {

    /** DECLARE THE USER PROFILE HEADER **/
    private AppCompatImageView imgvwProfile;
    private AppCompatTextView txtUserName;

    private String USER_PROFILE_PICTURE;

    /** DECLARE THE LAYOUT ELEMENTS **/
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    /** A FRAGMENT INSTANCE **/
    private Fragment mContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_landing_activity);

        /** CONFIGURE THE TOOLBAR **/
        configToolbar();

        /** CONFIGURE THE NAVIGATION BAR **/
        configureNavBar();

        /** GET THE USER DETAILS **/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getProviderData().get(1).getProviderId().equalsIgnoreCase("google.com"))   {
                USER_PROFILE_PICTURE = String.valueOf(user.getProviderData().get(1).getPhotoUrl()) + "?sz=600";
            } else if (user.getProviderData().get(1).getProviderId().equalsIgnoreCase("facebook.com")){
                USER_PROFILE_PICTURE = "https://graph.facebook.com/" + user.getProviderData().get(1).getUid() + "/picture?width=4000";
//                Log.e("USER PROFILE", USER_PROFILE_PICTURE);
            }

            /** SET THE PROFILE PICTURE **/
            String USER_NAME = user.getDisplayName();
            if (USER_PROFILE_PICTURE != null)   {
                Picasso.with(MainLandingActivity.this)
                        .load(USER_PROFILE_PICTURE)
                        .centerInside()
                        .fit()
                        .into(imgvwProfile);
            }

            /** SET THE USER NAME **/
            if (USER_NAME != null)  {
                txtUserName.setText(USER_NAME);
            }
        }

        /** SHOW THE FIRST FRAGMENT (DASHBOARD) **/
        if (savedInstanceState == null) {
            mContent = new DoctorsListFrag();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, mContent, "KEY_FRAG")
                    .commit();
        }
    }

    /** CONFIGURE THE NAVIGATION BAR **/
    private void configureNavBar() {

        /** INITIALIZE THE NAVIGATION VIEW **/
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        View view = navigationView.getHeaderView(0);
        imgvwProfile = (AppCompatImageView) view.findViewById(R.id.imgvwProfile);
        txtUserName = (AppCompatTextView) view.findViewById(R.id.txtUserName);

        /** CHANGE THE FRAGMENTS ON NAVIGATION ITEM SELECTION **/
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                /** CHECK IF AN ITEM IS CHECKED / NOT CHECKED **/
                if (menuItem.isChecked())   {
                    menuItem.setChecked(false);
                }  else {
                    menuItem.setChecked(true);
                }

                /** Closing drawer on item click **/
                drawerLayout.closeDrawers();

                /** CHECK SELECTED ITEM AND SHOW APPROPRIATE FRAGMENT **/
                switch (menuItem.getItemId()){

                    case R.id.dashDoctors:
                        mContent = new DoctorsListFrag();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashHostels:
                        mContent = new ServicesDashboard();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashTrainers:
                        mContent = new ShopsDashboard();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashAllServices:
                        mContent = new DiscussionsFragment();
                        switchFragment(mContent);
                        return true;

                    case R.id.dashMedicines:
                        mContent = new TipsFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashFood:
                        mContent = new TipsFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashAccessories:
                        mContent = new TipsFragment();
                        switchFragment(mContent);
                        return true;

                    case R.id.grpUserPets:
                        mContent = new UserPetsList();
                        switchFragment(mContent);
                        return true;

                    case R.id.dashDiscuss:
                        mContent = new DiscussionsFragment();
                        switchFragment(mContent);
                        return true;
                    case R.id.dashTips:
                        mContent = new TipsFragment();
                        switchFragment(mContent);
                        return true;

                    case R.id.dashSettings:
                        Intent showSettings = new Intent(MainLandingActivity.this, SettingsActivity.class);
                        startActivity(showSettings);
                        return true;
                    case R.id.dashHelp:
                        Intent showHelp = new Intent(MainLandingActivity.this, HelpActivity.class);
                        startActivity(showHelp);
                        return true;

                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });
    }

    /***** CONFIGURE THE TOOLBAR *****/
    @SuppressWarnings("ConstantConditions")
    private void configToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.myToolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawerLayout != null) {
            drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };
        drawerLayout.addDrawerListener(mDrawerToggle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())   {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(navigationView))    {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
                return  true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /** METHOD TO CHANGE THE FRAGMENT **/
    private void switchFragment(Fragment fragment) {

        /** HIDE THE NAV DRAWER **/
        drawerLayout.closeDrawer(GravityCompat.START);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, fragment)
                .commit();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}