package ca.interfacemaster.surveyor.adapters;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import ca.interfacemaster.surveyor.R;
import ca.interfacemaster.surveyor.classes.Answer;
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
        final String[] optLabels = q.getOptionLabels();
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
                final LinearLayout labelContainer = convertView.findViewById(R.id.sliderLabels);
                final SeekBar slider = convertView.findViewById(R.id.sliderAnswer);
                final TextView tooltip = convertView.findViewById(R.id.sliderTooltip);
                // render labels - max of 5
                // if odd - great
                // if even - take intervals + last one
                String[] labels = new String[0];
                int maxLabels = ( opts.length % 2 == 0 ) ? 4 : 5;
                if( opts.length > maxLabels ) {
                    labels = new String[maxLabels];
                    int interval = (int) Math.round(Math.floor((double) opts.length / (maxLabels-1)));
                    for (int i = 0; i < maxLabels; i++) {
                        labels[i] = optLabels[ i*interval ];
                    }
                    if( opts.length % 2 == 0 ) {
                        labels[labels.length-1] = optLabels[opts.length-1];
                    }
                } else {
                    labels = new String[opts.length];
                    for( int i = 0; i < opts.length; i++ ) {
                        labels[i] = optLabels[i];
                    }
                }
//                Log.d("LABELS",":::::::::::::");
//                Log.d("LABELS size", String.format("%d",labels.length) );
                for( int i = 0; i < labels.length; i++ ) {
//                    Log.d("LABEL "+i, labels[i] );
                    TextView newLabel = new TextView(mContext);
                    newLabel.setLayoutParams(
                            new TableLayout.LayoutParams(1, TableLayout.LayoutParams.WRAP_CONTENT, 1)
                    );
                    newLabel.setText(labels[i]);
                    // if first then left
                    if(i==0) newLabel.setGravity(Gravity.LEFT);
                    // if last then right
                    else if(i==labels.length-1) newLabel.setGravity(Gravity.RIGHT);
                    // if middle then center
                    else newLabel.setGravity(Gravity.CENTER_HORIZONTAL);
                    // add
                    labelContainer.addView( newLabel );
                }
                labelContainer.setVisibility(View.VISIBLE);
                // render tooltip
                tooltip.setText("");
                tooltip.setVisibility(View.VISIBLE);
                // render slider
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
                        tooltip.setText( opts[seekBar.getProgress()] );
                        // mark answer interaction
                        if( q.hasAnswer() ) {
                            q.getAnswer().markAsDirty();
                        } else {
                            q.setAnswer( new Answer(q.getQuestionID(),true) );
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // track stop
                    }
                });
                break;

            case "radio": // one-of-many (radio buttons)
                for( int i = 0; i < opts.length; i++ ) {
                    RadioButton r = new RadioButton(mContext);
                    r.setText(opts[i]);
                    radioGroup.addView(r);
                    if(q.hasAnswer() && q.getAnswerText().equalsIgnoreCase(opts[i])) {
                        radioGroup.check( r.getId() );
                    }
                }
                break;
            case "check": // many-of-many (check boxes)
                for( int i = 0; i < opts.length; i++ ) {
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
