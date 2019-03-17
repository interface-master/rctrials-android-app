package ca.interfacemaster.surveyor;

import android.preference.PreferenceManager;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.interfacemaster.surveyor.adapters.QuestionAdapter;
import ca.interfacemaster.surveyor.classes.Answer;
import ca.interfacemaster.surveyor.classes.Question;
import ca.interfacemaster.surveyor.classes.Survey;
import ca.interfacemaster.surveyor.services.SharedPrefService;

public class InputSurvey extends AppCompatActivity {
    private static Survey survey;
    private static Question[] questions;
    private DrawerLayout mDrawerLayout;
    private AdapterViewFlipper mAdapterViewFlipper;
    private SharedPrefService pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.transition.slide_over, R.transition.fadeout);
        // services
        pref = new SharedPrefService(this);
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
        Answer answer = q.hasAnswer() ? q.getAnswer() : new Answer(q.getQuestionID());
        // save answer
        if (q.getType().equalsIgnoreCase("text")) {
            // text input
            EditText ans = v.findViewById(R.id.editTextAnswer);
            answer.setAnswer(ans.getText().toString());

        } else if (q.getType().equalsIgnoreCase("likert")) {
            // slider input
            SeekBar ans = v.findViewById(R.id.sliderAnswer);
            if( q.hasAnswer() && q.getAnswer().isDirty() ) {
                answer.setAnswer("" + q.getOptions()[ans.getProgress()]);
            } else if( !q.hasAnswer() ) {
                answer.setAnswer("");
            }

        } else if (q.getType().equalsIgnoreCase("slider")) {
            // slider input
            SeekBar ans = v.findViewById(R.id.sliderAnswer);
            String ansValue = "";
            if( q.hasAnswer() && q.getAnswer().isDirty() ) {
                try {
                    int ansIntVal = Integer.parseInt(q.getOptions()[0]);
                    ansIntVal += ans.getProgress();
                    ansValue = String.format("%d", ansIntVal);
                } catch (NumberFormatException e) {
                    ansValue = q.getOptions()[ ans.getProgress() ];
                }
            }
            answer.setAnswer("" + ansValue);

        } else if (q.getType().equalsIgnoreCase("radio")) {
            // radio buttons
            RadioGroup ansGroup = v.findViewById(R.id.radioAnswer);
            RadioButton ans = findViewById( ansGroup.getCheckedRadioButtonId() );
            if( ans != null ) {
                answer.setAnswer("" + ans.getText());
            }

        } else if (q.getType().equalsIgnoreCase("check")) {
            // check boxes
            String ans = "";
            RadioGroup ansGroup = v.findViewById(R.id.radioAnswer);
            for( int i = 0; i < ansGroup.getChildCount(); i++ ) {
                CheckBox child = (CheckBox)ansGroup.getChildAt(i);
                if( child.isChecked() ) {
                    if (!ans.isEmpty()) ans = ans + "|";
                    ans = ans + child.getText();
                }
            }
            answer.setAnswer("" + ans);
        }
        Log.d("SETTING ANSWER",answer.toString());
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
        pref.updateSurvey(survey);
        super.onPause();
    }
}
