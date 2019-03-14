package ca.interfacemaster.surveyor.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import ca.interfacemaster.surveyor.R;
import ca.interfacemaster.surveyor.classes.Question;

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
        final String[] opts = q.getOptions();
        final TextView question = convertView.findViewById(R.id.textQuestion);
        final RadioGroup radioGroup = convertView.findViewById(R.id.radioAnswer);
        // confs
        question.setText(q.getText());
        // type
        switch (q.getType()) {
            case "text":
                final EditText answer = convertView.findViewById(R.id.editTextAnswer);
                answer.setVisibility(View.VISIBLE);
                answer.setHorizontallyScrolling(false);
                answer.setLines(3);
                if(q.hasAnswer()) {
                    answer.setText( q.getAnswerText() );
                }
                break;

            case "likert":
            case "slider":
                final SeekBar slider = convertView.findViewById(R.id.sliderAnswer);
                final TextView tooltip = convertView.findViewById(R.id.sliderTooltip);
                tooltip.setText("");
                tooltip.setVisibility(View.VISIBLE);
                slider.setMax(opts.length-1);
                if(q.hasAnswer() && !q.getAnswerText().equalsIgnoreCase("")) {
                    tooltip.setText(q.getAnswerText());
                    slider.setProgress(q.getAnswerVal());
                    slider.setThumb( convertView.getResources().getDrawable(R.drawable.custom_slider_thumb) );
                } else {
                    slider.setProgress(-1);
                    slider.setThumb( convertView.getResources().getDrawable(R.drawable.custom_slider_thumb_blank) );
                }
                slider.setVisibility(View.VISIBLE);
                slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        tooltip.setText( opts[progress] );
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // track start
                        seekBar.setThumb( seekBar.getResources().getDrawable(R.drawable.custom_slider_thumb) );
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // track stop
                    }
                });
                break;

            case "radio": // one-of-many (radio buttons)
                for( int i = 0; i < opts.length-1; i++ ) {
                    RadioButton r = new RadioButton(mContext);
                    r.setText(opts[i]);
                    radioGroup.addView(r);
                    if(q.hasAnswer() && q.getAnswerText().equalsIgnoreCase(opts[i])) {
                        radioGroup.check( r.getId() );
                    }
                }
                break;
            case "check": // many-of-many (check boxes)
                for( int i = 0; i < opts.length-1; i++ ) {
                    CheckBox c = new CheckBox(mContext);
                    c.setText(opts[i]);
                    if(q.hasAnswer()) {
                        String[] selected = q.getAnswerText().trim().split("\\s*\\|\\s*");
                        for( int j = 0; j < selected.length; j++ ) {
                            if( selected[j].equalsIgnoreCase(opts[i]) ) {
                                c.setChecked(true);
                            }
                        }
                    }
                    radioGroup.addView(c);
                }
                break;

        }


        return convertView;
    }
}
