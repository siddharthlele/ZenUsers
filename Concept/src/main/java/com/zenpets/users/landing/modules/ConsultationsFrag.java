package com.zenpets.users.landing.modules;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zenpets.users.R;
import com.zenpets.users.consultations.ConsultationList;
import com.zenpets.users.utils.TypefaceSpan;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConsultationsFrag extends Fragment {

    /** SHOW THE LIST OF PUBLIC QUESTIONS **/
    @OnClick(R.id.linlaConsultations) void showPublicQuestions()    {
        Intent intent = new Intent(getActivity(), ConsultationList.class);
        startActivity(intent);
    }

    /** ASK A FREE PUBLIC QUESTION **/
    @OnClick(R.id.linlaGraphicAskQuestion) void graphicAskPublicQuestion() {
    }

    /** ASK A FREE PUBLIC QUESTION **/
    @OnClick(R.id.linlaAskQuestion) void askPublicQuestion()    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** CAST THE LAYOUT TO A NEW VIEW INSTANCE **/
        View view = inflater.inflate(R.layout.landing_consulation_frag, container, false);
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
    }

    /***** CONFIGURE THE TOOLBAR *****/
    private void configTB() {
//        String strTitle = getResources().getString(R.string.account_manager_title);
        String strTitle = "Consultations";
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