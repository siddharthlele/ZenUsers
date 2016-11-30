package com.zenpets.users.landing;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zenpets.users.R;
import com.zenpets.users.landing.modules.ProfileFragList;
import com.zenpets.users.landing.modules.ServicesFragList;
import com.zenpets.users.landing.modules.ShopsFragList;
import com.zenpets.users.landing.modules.SocialFragList;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewMainLandingActivity extends AppCompatActivity {

    /** THE TAB LAYOUT **/
    TabLayout tabLayout;

    /** THE VIEW PAGER **/
    ViewPager viewPager;

    /** DECLARE THE DRAWABLES **/
    private int[] tabIcons = {
            R.drawable.ic_search_white_24dp,
            R.drawable.ic_store_mall_directory_white_24dp,
            R.drawable.ic_message_white_24dp,
            R.drawable.ic_person_white_24dp
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main_landing_activity);

        /** INSTANTIATE THE VIEW PAGER INSTANCE **/
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        /** INSTANTIATE THE TAB LAYOUT **/
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        /** SETUP THE TAB ICONS **/
        setupTabIcons();

        /** APPLY THE CUSTOM FONT TO THE TITLES **/
        changeTabsFont();
    }

    /** SETUP THE TAB ICONS **/
    @SuppressWarnings("ConstantConditions")
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    /** THE PAGER ADAPTER **/
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /** SETUP THE VIEW PAGER **/
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ServicesFragList(), "Services");
        adapter.addFragment(new ShopsFragList(), "Shop");
        adapter.addFragment(new SocialFragList(), "Consult");
        adapter.addFragment(new ProfileFragList(), "Me");
        viewPager.setAdapter(adapter);
    }

    /** APPLY THE CUSTOM FONT TO THE TITLES **/
    private void changeTabsFont() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Exo2-Medium.otf"));
                }
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}