package ca.interfacemaster.surveyor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class InputSurvey extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_survey);

        configureView();
    }

    private void configureView() {
        Log.d("InputSurvey config", "x");
        Bundle extras = getIntent().getExtras();
        Survey s = (Survey) extras.getSerializable("survey");
        Log.d("InputSurvey survey", s.toString());
    }
}
