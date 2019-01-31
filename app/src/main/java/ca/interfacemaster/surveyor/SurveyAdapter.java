package ca.interfacemaster.surveyor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.MyViewHolder> {

    private Context mContext;
    private List<Survey> surveyList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.surveyTitle);
            // TODO: add more details to card
        }
    }

    public SurveyAdapter(Context mContext, List<Survey> surveyList) {
        this.mContext = mContext;
        this.surveyList = surveyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_survey, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = Dashboard.mRecyclerView.getChildAdapterPosition(v);
                openSurvey(surveyList.get(pos));
            }
        });
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Survey survey = surveyList.get(position);
        holder.title.setText(survey.getTitle());
        // TODO: add more details to card
    }

    @Override
    public int getItemCount() {
        return surveyList.size();
    }

    private void openSurvey(Survey survey) {
        Toast.makeText(mContext, "Greetings from "+survey.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
