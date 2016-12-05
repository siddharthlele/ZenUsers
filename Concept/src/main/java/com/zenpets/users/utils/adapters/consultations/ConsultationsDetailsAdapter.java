package com.zenpets.users.utils.adapters.consultations;

import android.app.Activity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
            ConsultationAnswersData data = getItem(position - 1);
            VHItem vhItem = (VHItem) holder;
            vhItem.txtConsultAnswer.setText(data.getConsultAnswer());
            vhItem.txtConsultNextSteps.setText(data.getConsultNextSteps());
            vhItem.txtAnswerTimeStamp.setText(data.getTimeStamp());
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

        VHItem(View v) {
            super(v);
            imgvwDoctorProfile = (CircleImageView) v.findViewById(R.id.imgvwDoctorProfile);
            txtDoctorName = (AppCompatTextView) v.findViewById(R.id.txtDoctorName);
            txtAnswerTimeStamp = (AppCompatTextView) v.findViewById(R.id.txtAnswerTimeStamp);
            txtConsultAnswer = (AppCompatTextView) v.findViewById(R.id.txtConsultAnswer);
            txtConsultNextSteps = (AppCompatTextView) v.findViewById(R.id.txtConsultNextSteps);
            txtHelpful = (AppCompatTextView) v.findViewById(R.id.txtHelpful);
            imgvwFlagAnswer = (IconicsImageView) v.findViewById(R.id.imgvwFlagAnswer);
        }
    }
}