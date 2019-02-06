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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class InputSurvey extends AppCompatActivity {
    private static Survey survey;
    private static Question[] questions;
    private static Answer[] answers;
    private DrawerLayout mDrawerLayout;
    private AdapterViewFlipper mAdapterViewFlipper;

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
        actionbar.setTitle(survey.getName());
    }

    private void setUpSurvey() {
        Log.d("InputSurvey survey", survey.toString());
        // refs
        Button cancel = findViewById(R.id.btnCancel);
        Button next = findViewById(R.id.btnNext);
        mAdapterViewFlipper = findViewById(R.id.adapterViewFlipper);
        questions = survey.getQuestions();
        answers = new Answer[questions.length];

        // adapter
        mAdapterViewFlipper.setAdapter(new QuestionAdapter(this, questions));
        mAdapterViewFlipper.setDisplayedChild(0);

        // text
        updateQuestionNofM();

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
                gotoNextQuestion();
            }
        });
    }

    private void gotoNextQuestion() {
        // refs
        int pos = mAdapterViewFlipper.getDisplayedChild();
        View v = mAdapterViewFlipper.getCurrentView();
        Question q = questions[pos];
        // save answer
        Answer answer = new Answer(q.getQuestionID());
        if (q.getType().equalsIgnoreCase("text")) {
            EditText ans1 = v.findViewById(R.id.editTextAnswer);
            answer.setAnswer(ans1.getText().toString());
        } else if (q.getType().equalsIgnoreCase("mc")) {
            SeekBar ans2 = v.findViewById(R.id.sliderAnswer);
            answer.setAnswer("" + ans2.getProgress());
        }
        answers[pos] = answer;
        q.setAnswer(answer);
        // flip
        if( pos == questions.length-1 ) {
            // FINISH
            finish();
        } else {
            // NEXT
            // flip to next
            mAdapterViewFlipper.showNext();
            // update view
            updateQuestionNofM();
        }
    }

    private void updateQuestionNofM() {
        // refs
        TextView num = findViewById(R.id.textQuestionNofM);
        ProgressBar progress = findViewById(R.id.progressBar);
        int n = mAdapterViewFlipper.getDisplayedChild()+1;
        int m = survey.getQuestionsLength();
        // text
        num.setText( String.format("Question %d of %d",n,m) );
        // progress
        double percentDbl = ((double)n / (double)m) * 100;
        int percent = (int) Math.round(percentDbl);
        progress.setProgress(percent);
        // check if at the end
        if(n == m) {
            Button next = findViewById(R.id.btnNext);
            next.setText("Finish");
        }
        Log.d("input survey progress", String.format("%d",percent) );
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

    @Override
    protected void onDestroy() {
        Log.d("DESTROY SURVEY save:", String.format("Answers: %d",answers.length));
        for(int i=0; i<answers.length; i++ ) {
            if( answers[i] != null ) {
                Log.d("answer", String.format("i:%d a:%s", i, answers[i].getAnswer()));
            }
        }
        super.onDestroy();
    }
}
