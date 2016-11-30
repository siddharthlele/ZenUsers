package com.zenpets.users.landing.modules;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zenpets.users.R;
import com.zenpets.users.profile.ProfileDetailsActivity;
import com.zenpets.users.utils.TypefaceSpan;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends Fragment {

    /** THE USER DETAILS **/
    String USER_NAME;
    String USER_PROFILE_PICTURE;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.imgvwProfile) CircleImageView imgvwProfile;
    @BindView(R.id.txtUserName) AppCompatTextView txtUserName;

    /** SHOW THE PROFILE DETAILS **/
    @OnClick(R.id.txtViewProfile) void showProfile()    {
        Intent intent = new Intent(getActivity(), ProfileDetailsActivity.class);
        startActivity(intent);
    }

    /** SHOW THE USER'S DOCTORS **/
    @OnClick(R.id.linlaMyDoctors) void showDoctors()    {
    }

    /** SHOW USER'S CONSULTATION QUERIES **/
    @OnClick(R.id.linlaMyConsultQueries) void showQueries() {
    }

    /** SHOW USER'S SAVED ARTICLES **/
    @OnClick(R.id.linlaMySavedArticles) void showArticles() {
    }

    /** SHOW USER'S REMINDERS **/
    @OnClick(R.id.linlaMyReminders) void showReminders()    {
    }

    /** SHOW THE USER'S PREFERENCES **/
    @OnClick(R.id.linlaMyPreferences) void showPreferences()    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.user_profile_frag, container, false);
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

        /***** CONFIGURE THE TOOLBAR *****/
        configTB();

        /** GET THE USER DETAILS **/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            /** GET THE USER NAME **/
            USER_NAME = user.getDisplayName();

            /** GET THE PROFILE PICTURE **/
            if (user.getProviderData().get(1).getProviderId().equalsIgnoreCase("google.com"))   {
                USER_PROFILE_PICTURE = String.valueOf(user.getProviderData().get(1).getPhotoUrl()) + "?sz=600";
            } else if (user.getProviderData().get(1).getProviderId().equalsIgnoreCase("facebook.com")){
                USER_PROFILE_PICTURE = "https://graph.facebook.com/" + user.getProviderData().get(1).getUid() + "/picture?width=4000";
            }

            /** SET THE USER NAME **/
            if (USER_NAME != null)  {
                txtUserName.setText(USER_NAME);
            }

            /** SET THE PROFILE PICTURE **/
            if (USER_PROFILE_PICTURE != null)   {
                Glide.with(getActivity())
                        .load(USER_PROFILE_PICTURE)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .into(imgvwProfile);
            }
        }
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
//        String strTitle = getResources().getString(R.string.account_manager_title);
        String strTitle = "Profile";
        SpannableString s = new SpannableString(strTitle);
        s.setSpan(new TypefaceSpan(getActivity()), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(s);
    }
}