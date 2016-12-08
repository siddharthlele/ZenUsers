package com.zenpets.users.review;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zenpets.users.R;
import com.zenpets.users.utils.models.doctors.VisitReasonsData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VisitReasonActivity extends AppCompatActivity {

    /** THE ADAPTER AND THE ARRAY LIST **/
    private VisitReasonsAdapter adapter;
    private ArrayList<VisitReasonsData> arrReasons = new ArrayList<>();

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.edtSearch) AppCompatEditText edtSearch;
    @BindView(R.id.listVisitReasons) RecyclerView listVisitReasons;

    /** CANCEL **/
    @OnClick(R.id.btnFeedbackExit) void goBack()    {
        finish();
    }

    /** SAVE AND SEND THE SELECTIONS BACK **/
    @OnClick(R.id.btnSubmit) void saveSelection()   {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visit_reason_list);
        ButterKnife.bind(this);

        /** CONFIGURE THE RECYCLER VIEW **/
        configRecycler();

        /** INSTANTIATE THE ADAPTER **/
        adapter = new VisitReasonsAdapter(arrReasons);

        /** GET THE LIST OF REASONS **/
        getReasonsList();
    }

    /** GET THE LIST OF REASONS **/
    private void getReasonsList() {

        /** CLEAR THE ARRAY LIST **/
        arrReasons.clear();

        /** FETCH THE LIST OF REASONS **/
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Visit Reasons");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {

                    /** AN INSTANCE OF THE VISIT REASONS DATA DATA CLASS **/
                    VisitReasonsData data;

                    for (DataSnapshot reasons : dataSnapshot.getChildren()) {

                        /** INSTANTIATE THE VISIT REASONS DATA DATA CLASS INSTANCE **/
                        data = new VisitReasonsData();

                        /** GET THE PET ID **/
                        String visitReason = reasons.child("visitReason").getValue(String.class);
                        data.setVisitReason(visitReason);

                        /** ADD THE COLLECTED DATA TO THE ARRAY LIST **/
                        arrReasons.add(data);
                    }

                    /** SET THE ADAPTER TO THE RECYCLER VIEW **/
                    listVisitReasons.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /** CONFIGURE THE RECYCLER VIEW **/
    private void configRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listVisitReasons.setLayoutManager(manager);
        listVisitReasons.setHasFixedSize(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private class VisitReasonsAdapter extends RecyclerView.Adapter<VisitReasonsAdapter.VaccinationsVH> {

        /** ARRAY LIST TO GET DATA FROM THE ACTIVITY **/
        private ArrayList<VisitReasonsData> arrReasons;

        public VisitReasonsAdapter(ArrayList<VisitReasonsData> arrReasons) {

            /** CAST THE CONTENTS OF THE ARRAY LIST IN THE METHOD TO THE LOCAL INSTANCE **/
            this.arrReasons = arrReasons;
        }

        @Override
        public int getItemCount() {
            return arrReasons.size();
        }

        @Override
        public void onBindViewHolder(VisitReasonsAdapter.VaccinationsVH holder, final int position) {
            final VisitReasonsData data = arrReasons.get(position);

            /** SET THE VISIT REASON **/
            String vaccineName = data.getVisitReason();
            if (vaccineName != null)	{
                holder.txtVisitReason.setText(vaccineName);
            }

            holder.txtVisitReason.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("REASON", data.getVisitReason());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }

        @Override
        public VisitReasonsAdapter.VaccinationsVH onCreateViewHolder(ViewGroup parent, int i) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.visit_reason_item, parent, false);

            return new VisitReasonsAdapter.VaccinationsVH(itemView);
        }

        class VaccinationsVH extends RecyclerView.ViewHolder   {

            AppCompatTextView txtVisitReason;

            VaccinationsVH(View v) {
                super(v);

                txtVisitReason = (AppCompatTextView) v.findViewById(R.id.txtVisitReason);
            }
        }
    }
}