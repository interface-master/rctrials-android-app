package ca.interfacemaster.surveyor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class InputSurvey extends AppCompatActivity {
    private static Survey survey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_survey);
        // extract survey
        Bundle extras = getIntent().getExtras();
        survey = (Survey) extras.getSerializable("survey");
        // configure view
        setUpSurvey();
    }

    private void setUpSurvey() {
        Log.d("InputSurvey survey", survey.toString());
        // refs
        TextView name = findViewById(R.id.textSurveyName);
        ProgressBar progress = findViewById(R.id.progressBar);
        TextView num = findViewById(R.id.textQuestionNofM);
        Button cancel = findViewById(R.id.btnCancel);
        Button next = findViewById(R.id.btnNext);
        final AdapterViewFlipper adapterViewFlipper = findViewById(R.id.adapterViewFlipper);

        // adapter
        adapterViewFlipper.setAdapter(new QuestionAdapter(this, survey.getQuestions()));
        adapterViewFlipper.setDisplayedChild(0);

        // progress
        double percentDbl = (1.0 / survey.getQuestionsLength()) * 100;
        int percent = (int) Math.round(percentDbl);
        progress.setProgress(percent);
        Log.d("input survey progress", String.format("%d",percent) );

        // text
        name.setText(survey.getName());
        num.setText( String.format("Question %d of %d",1,survey.getQuestionsLength()) );

        // buttons
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // TODO: add logic for next/finish button
        next.setEnabled(true);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterViewFlipper.showNext();
            }
        });
    }
}
