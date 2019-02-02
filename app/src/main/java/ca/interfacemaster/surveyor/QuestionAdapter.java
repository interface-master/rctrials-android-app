package ca.interfacemaster.surveyor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class QuestionAdapter extends BaseAdapter {
    private Context mContext;
    private List<Question> questionList;

    public QuestionAdapter(Context mContext, Question[] questionAry) {
        this.mContext = mContext;
        questionList = Arrays.asList(questionAry);
    }

    @Override
    public int getCount() {
        return questionList.size();
    }

    @Override
    public Object getItem(int position) {
        return questionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return questionList.get(position).getQuestionID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.card_question, parent, false);
        }

        // refs
        final Question q = questionList.get(position);
        final Answer a = new Answer(q.getQuestionID());
        final TextView question = convertView.findViewById(R.id.textQuestion);
        final EditText answer = convertView.findViewById(R.id.editTextAnswer);
        final SeekBar slider = convertView.findViewById(R.id.sliderAnswer);
        final TextView tooltip = convertView.findViewById(R.id.sliderTooltip);
        // confs
        question.setText(q.getText());
        // type
        switch (q.getType()) {
            case "text":
                answer.setVisibility(View.VISIBLE);
                answer.setHorizontallyScrolling(false);
                answer.setLines(3);
                break;
            case "mc":
                final String[] opts = q.getOptions();
                a.setAnswer(opts[0]);
                tooltip.setText(a.getAnswer());
                tooltip.setVisibility(View.VISIBLE);
                slider.setMax(opts.length-1);
                slider.setVisibility(View.VISIBLE);
                slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        a.setAnswer( opts[progress] );
                        tooltip.setText( a.getAnswer() );
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // track start
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // track stop
                    }
                });
                break;
        }


        return convertView;
    }
}