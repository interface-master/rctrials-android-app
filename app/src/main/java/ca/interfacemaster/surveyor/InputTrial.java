package ca.interfacemaster.surveyor;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class InputTrial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_trial);

        configureRegisterButton();
    }

    private void configureRegisterButton() {
        final Button btn = findViewById(R.id.buttonRegister);
        btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // get refs to views
            final TextView log = findViewById(R.id.inputTrialStatus);
            final ProgressBar bar = findViewById(R.id.progressBarTID);
            final EditText input = findViewById(R.id.editTextTID);
            final String tid = input.getText().toString();
            // validate with server
            ApiService.registerIntoTrial(tid, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    bar.setProgress(25);
                    log.setText(getString(R.string.registering));
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    bar.setProgress(75);
                    log.setText(getString(R.string.receiving));
                    try {
                        String uuid = response.getString("uuid");
                        JSONArray surveys = response.getJSONArray("surveys");
                        if(!uuid.isEmpty()) {
                            bar.setProgress(100);
                            log.setText(getString(R.string.registered));
                            // save UUID
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(InputTrial.this);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(getString(R.string.pref_tid), tid);
                            editor.putString(getString(R.string.pref_uuid), uuid);
                            editor.putString(getString(R.string.pref_surveys), surveys.toString());
                            editor.commit();
                            // TODO: goto survey view to process pre-test surveys
                            Log.d("InputTrial",response.getString("surveys"));
                            // finished
                            finish();
                        } else {
                            // TODO: error dialog
                            bar.setProgress(0);
                            log.setText("ERROR\nResponse did not return a UUID.");
                        }
                    } catch(JSONException e) {
                        // TODO: error dialog
                        bar.setProgress(0);
                        log.setText("ERROR\nCould not parse the response.");
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                    // Pull out the first event on the public timeline
                    log.setText("ERROR\nReceived unexpected array!");
                    // http://loopj.com/android-async-http/
                    // https://stackoverflow.com/questions/29339565/calling-rest-api-from-an-android-app
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    bar.setProgress(0);
                    String out = "ERROR\n";
                    try {
                        out += errorResponse.getString("message");
                    } catch (JSONException e) {
                        out += "Reason unknown.";
                    }
                    log.setText(out);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    bar.setProgress(0);
                    log.setText("ERROR\nFailed to register into trial.\nStatus Code: "+statusCode+"\n"+responseString);
                }
            });
            }
        });
    }
}
