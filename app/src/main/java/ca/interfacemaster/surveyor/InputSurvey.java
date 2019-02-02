package ca.interfacemaster.surveyor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        TextView num = findViewById(R.id.textQuestionNofM);
        Button cancel = findViewById(R.id.btnCancel);
        // text
        name.setText(survey.getName());
        num.setText( String.format("Question %d of %d",0,survey.getQuestionsLength()) );
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
