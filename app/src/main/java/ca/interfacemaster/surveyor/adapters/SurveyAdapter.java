package ca.interfacemaster.surveyor.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ca.interfacemaster.surveyor.Dashboard;
import ca.interfacemaster.surveyor.InputSurvey;
import ca.interfacemaster.surveyor.R;
import ca.interfacemaster.surveyor.classes.Survey;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.MyViewHolder> {

    private Context mContext;
    private List<Survey> surveyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Button action;
        public TextView status;

        public MyViewHolder(View itemView) {
            super(itemView);
            action = itemView.findViewById(R.id.actionButton);
            status = itemView.findViewById(R.id.statusText);
            // TODO: add more details to card
        }
    }

    public SurveyAdapter(Context mContext, List<Survey> surveyList) {
        this.mContext = mContext;
        this.surveyList = surveyList;
    }

    public void setSurveyList(List<Survey> list) {
        this.surveyList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_survey, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemView.setEnabled(false);
                int pos = Dashboard.mRecyclerView.getChildAdapterPosition(v);
                Survey survey = surveyList.get(pos);
                if( survey.getState() != Survey.SENDING ) {
                    openSurvey(survey);
                }
            }
        });
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Survey survey = surveyList.get(position);
        holder.action.setText(survey.getName());
        // set icon based on survey answer state
        Drawable icon = ContextCompat.getDrawable(mContext, R.drawable.ic_assignment);
        switch (survey.getState()) {
            case Survey.INCOMPLETE:
                icon = ContextCompat.getDrawable(mContext, R.drawable.ic_assignment_late);
                break;
            case Survey.COMPLETE:
                icon = ContextCompat.getDrawable(mContext, R.drawable.ic_assignment_turned_in);
                break;
            case Survey.SENDING:
                icon = ContextCompat.getDrawable(mContext, R.drawable.ic_assignment_return);
                holder.status.setText("sending");
                break;
        }
        holder.action.setCompoundDrawablesWithIntrinsicBounds(null,null, icon, null);
        // TODO: add more details to card
    }

    @Override
    public int getItemCount() {
        return surveyList.size();
    }

    private void openSurvey(Survey survey) {
        ((AppCompatActivity)mContext).startActivityForResult(
                new Intent(mContext, InputSurvey.class)
                .putExtra("survey", survey),
                2
        );
    }
}

// TODO: menus for cards: https://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/