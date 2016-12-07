package com.zenpets.users.utils.adapters.consultations;

import android.app.Activity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.iconics.view.IconicsImageView;
import com.zenpets.users.R;
import com.zenpets.users.utils.models.consultations.ConsultationAnswersData;
import com.zenpets.users.utils.models.consultations.ConsultationHeaderData;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConsultationsDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Activity activity;
    private ConsultationHeaderData header;
    private ArrayList<ConsultationAnswersData> arrAnswers;

    public ConsultationsDetailsAdapter(Activity activity, ConsultationHeaderData header, ArrayList<ConsultationAnswersData> arrAnswers) {
        this.activity = activity;
        this.header = header;
        this.arrAnswers = arrAnswers;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER)    {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.consultation_details_header, parent, false);
            return new VHHeader(v);
        } else if (viewType == TYPE_ITEM)   {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.consultation_details_item, parent, false);
            return new VHItem(v);
        }
        return null;
    }

    private ConsultationAnswersData getItem(int position) {
        return arrAnswers.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHHeader) {
            VHHeader vhHeader = (VHHeader) holder;
            vhHeader.txtQuestionTitle.setText(header.getConsultHeader());
            vhHeader.txtQuestionDescription.setText(header.getConsultDescription());
            vhHeader.txtTimeStamp.setText(header.getTimeStamp());
            vhHeader.txtViews.setText(header.getConsultViews() + " Views");

        } else if (holder instanceof VHItem)    {
            final ConsultationAnswersData data = getItem(position - 1);
            final VHItem vhItem = (VHItem) holder;
            vhItem.txtConsultAnswer.setText(data.getConsultAnswer());
            vhItem.txtConsultNextSteps.setText(data.getConsultNextSteps());
            vhItem.txtAnswerTimeStamp.setText(data.getTimeStamp());

            /** GET THE USER KEY **/
            final String USER_KEY = data.getUserKey();

            /** GET THE ANSWER KEY **/
            final String ANSWER_KEY = data.getAnswerKey();

            /** GET THE HELPFUL YES COUNT **/
            final int intHelpfulYes = data.getHelpfulYes();

            /** GET THE HELPFUL NO COUNT **/
            int intHelpfulNo = data.getHelpfulNo();

            /** SET THE HELPFUL COUNTS **/
            String strHelpful = String.valueOf(intHelpfulYes) + "/" + String.valueOf(intHelpfulNo) + " Found this helpful";
            vhItem.txtHelpful.setText(strHelpful);

            /** MARK THE ANSWER AS HELPFUL **/
            vhItem.txtHelpfulYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /** CHECK IF THE USER HAS ALREADY LIKED THE ANSWER **/
                    DatabaseReference refVotes =
                            FirebaseDatabase.getInstance().getReference().child("Users").child(USER_KEY).child("Votes");
                    Query query = refVotes.orderByChild("answerID").equalTo(ANSWER_KEY);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {

                                /** CHECK IF THE USER HAS VOTED ON THIS ANSWER **/
                                Toast.makeText(activity, "You have already marked this answer helpful", Toast.LENGTH_SHORT).show();

                            } else {

                                DatabaseReference refPostVote = FirebaseDatabase.getInstance().getReference().child("Answers").child(ANSWER_KEY);
                                refPostVote.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        dataSnapshot.getRef().child("helpfulYes").setValue(intHelpfulYes + 1, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                DatabaseReference refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(USER_KEY).child("Votes").push();
                                                refUser.child("answerID").setValue(ANSWER_KEY);
                                                refUser.child("voteStatus").setValue("Helpful Yes");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }
            });

            /** GET THE DOCTOR'S DETAILS **/
            String doctorID = data.getDoctorID();
            DatabaseReference refDoctors = FirebaseDatabase.getInstance().getReference().child("Doctors").child(doctorID);
            refDoctors.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    /** SET THE DOCTOR'S NAME **/
                    String doctorName = dataSnapshot.child("doctorName").getValue(String.class);
                    vhItem.txtDoctorName.setText(doctorName);

                    /** SET THE DOCTOR'S PROFILE PICTURE **/
                    String doctorProfile = dataSnapshot.child("doctorProfile").getValue(String.class);
                    Glide.with(activity)
                            .load(doctorProfile)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .centerCrop()
                            .into(vhItem.imgvwDoctorProfile);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return arrAnswers.size() + 1;
    }

    /** THE HEADER VIEW HOLDER **/
    private class VHHeader extends RecyclerView.ViewHolder {
        AppCompatTextView txtQuestionTitle;
        AppCompatTextView txtTimeStamp;
        AppCompatTextView txtQuestionDescription;
        AppCompatTextView txtViews;

        VHHeader(View v) {
            super(v);
            txtQuestionTitle = (AppCompatTextView) v.findViewById(R.id.txtQuestionTitle);
            txtTimeStamp = (AppCompatTextView) v.findViewById(R.id.txtTimeStamp);
            txtQuestionDescription = (AppCompatTextView) v.findViewById(R.id.txtQuestionDescription);
            txtViews = (AppCompatTextView) v.findViewById(R.id.txtViews);
        }
    }

    /** THE ANSWERS ITEM VIEW HOLDER **/
    private class VHItem extends RecyclerView.ViewHolder {
        CircleImageView imgvwDoctorProfile;
        AppCompatTextView txtDoctorName;
        AppCompatTextView txtAnswerTimeStamp;
        AppCompatTextView txtConsultAnswer;
        AppCompatTextView txtConsultNextSteps;
        AppCompatTextView txtHelpful;
        IconicsImageView imgvwFlagAnswer;
        AppCompatTextView txtHelpfulYes;
        AppCompatTextView txtHelpfulNo;

        VHItem(View v) {
            super(v);
            imgvwDoctorProfile = (CircleImageView) v.findViewById(R.id.imgvwDoctorProfile);
            txtDoctorName = (AppCompatTextView) v.findViewById(R.id.txtDoctorName);
            txtAnswerTimeStamp = (AppCompatTextView) v.findViewById(R.id.txtAnswerTimeStamp);
            txtConsultAnswer = (AppCompatTextView) v.findViewById(R.id.txtConsultAnswer);
            txtConsultNextSteps = (AppCompatTextView) v.findViewById(R.id.txtConsultNextSteps);
            txtHelpful = (AppCompatTextView) v.findViewById(R.id.txtHelpful);
            imgvwFlagAnswer = (IconicsImageView) v.findViewById(R.id.imgvwFlagAnswer);
            txtHelpfulYes = (AppCompatTextView) v.findViewById(R.id.txtHelpfulYes);
            txtHelpfulNo = (AppCompatTextView) v.findViewById(R.id.txtHelpfulNo);
        }
    }
}