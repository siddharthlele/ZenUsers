package com.zenpets.users.consultations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.zenpets.users.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConsultationList extends AppCompatActivity {

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.linlaHeaderProgress) LinearLayout linlaHeaderProgress;
    @BindView(R.id.listConsultations) RecyclerView listConsultations;
    @BindView(R.id.linlaEmpty) LinearLayout linlaEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultation_list);
        ButterKnife.bind(this);

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecycler();
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listConsultations.setLayoutManager(manager);
        listConsultations.setHasFixedSize(true);
    }
}