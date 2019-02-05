package ca.interfacemaster.surveyor;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class InputSurvey extends AppCompatActivity {
    private static Survey survey;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.transition.slide_over, R.transition.fadeout);
        // extract survey
        Bundle extras = getIntent().getExtras();
        survey = (Survey) extras.getSerializable("survey");
        // configure view
        setupView();
        setUpSurvey();
    }


    private void setupView() {
        // set navigation as the view
        setContentView(R.layout.navigation);
        // grab frame and add content
        LinearLayout frame = findViewById(R.id.content_frame);
        View contentView = LayoutInflater.from(frame.getContext())
                .inflate(R.layout.activity_input_survey, frame, false);
        frame.addView( contentView );
        // configure toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(R.transition.fadein, R.transition.slide_away);
        super.onPause();
    }
}
