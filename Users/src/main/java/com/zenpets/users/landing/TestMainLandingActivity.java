package com.zenpets.users.landing;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.zenpets.users.R;
import com.zenpets.users.landing.modules.ProfileFragList;
import com.zenpets.users.landing.modules.ServicesFragList;
import com.zenpets.users.landing.modules.ShopsFragList;
import com.zenpets.users.landing.modules.SocialFragList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TestMainLandingActivity extends AppCompatActivity {
    private static final String SELECTED_ITEM = "arg_selected_item";

    private int mSelectedItem;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.bottomNavigationView) BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_main_landing_activity);
        ButterKnife.bind(this);

        /** SWITCH THE FRAGMENT **/
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                /** CHANGE THE SELECTION **/
                selectFragment(item);
                return true;
            }
        });

        MenuItem selectedItem;
        if (savedInstanceState != null) {
            mSelectedItem = savedInstanceState.getInt(SELECTED_ITEM, 0);
            selectedItem = bottomNavigationView.getMenu().findItem(mSelectedItem);
        } else {
            selectedItem = bottomNavigationView.getMenu().getItem(0);
        }
        selectFragment(selectedItem);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, mSelectedItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        MenuItem homeItem = bottomNavigationView.getMenu().getItem(0);
        if (mSelectedItem != homeItem.getItemId()) {
            selectFragment(homeItem);
        } else {
            super.onBackPressed();
        }
    }

    /** CHANGE THE SELECTION **/
    private void selectFragment(MenuItem item) {
        Fragment frag;
        switch (item.getItemId()) {
            case R.id.menuServices:
                frag = new ServicesFragList();
                switchFragment(frag);
                break;
            case R.id.menuShop:
                frag = new ShopsFragList();
                switchFragment(frag);
                break;
            case R.id.menuConsult:
                frag = new SocialFragList();
                switchFragment(frag);
                break;
            case R.id.menuProfile:
                frag = new ProfileFragList();
                switchFragment(frag);
                break;
        }

        mSelectedItem = item.getItemId();

        for (int i = 0; i< bottomNavigationView.getMenu().size(); i++) {
            MenuItem menuItem = bottomNavigationView.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
        }
    }

    /** METHOD TO CHANGE THE FRAGMENT **/
    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}