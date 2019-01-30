package ca.interfacemaster.surveyor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        checkConfig();
    }

    /**
     * Will check for config changes when coming back from another activity.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        checkConfig();
    }

    /**
     * Checks for presence of TID and UUID and configures the view accordingly.
     */
    private void checkConfig() {
        String PREF_TID = getString(R.string.pref_tid);
        String PREF_UUID = getString(R.string.pref_uuid);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if( pref.contains(PREF_TID) && pref.contains(PREF_UUID) ) {
            configureSurveys();
        } else {
            configureSetUpButton();
        }
    }

    /**
     * Sets up the view to register with a trial.
     */
    private void configureSetUpButton() {
        TextView header = findViewById(R.id.textDashboardHeader);
        TextView subHeader = findViewById(R.id.textDashboardSubheader);
        Button button = findViewById(R.id.buttonSetUpTID);
        header.setText(getString(R.string.welcome_header));
        subHeader.setText(getString(R.string.welcome_sub_header));
        button.setText(getString(R.string.welcome_set_up));
        button.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(
                        new Intent(Dashboard.this, InputTrial.class),
                        1
                    );
                }
            }
        );
    }

    /**
     * Sets up the view to check for available surveys.
     */
    private void configureSurveys() {
        String PREF_TID = getString(R.string.pref_tid);
        String PREF_UUID = getString(R.string.pref_uuid);
        TextView header = findViewById(R.id.textDashboardHeader);
        TextView subHeader = findViewById(R.id.textDashboardSubheader);
        Button button = findViewById(R.id.buttonSetUpTID);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String storedTID = sharedPreferences.getString(PREF_TID, null);
        final String storedUUID = sharedPreferences.getString(PREF_UUID, null);
        header.setText(getString(R.string.welcome_header));
        subHeader.setText(getString(R.string.registered_sub_header));
        button.setText(getString(R.string.registered_check));
        button.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApiService.queryForSurveys(storedTID, storedUUID, new JsonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // If the response is JSONObject instead of expected JSONArray
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                            // Pull out the first event on the public timeline
                            Log.i("dashboard","success");
                            Bundle bundle = new Bundle();
                            bundle.putString("json", timeline.toString());
                            Log.d("dashboard", bundle.toString());

                            Log.d("success:",timeline.toString());
//                            Intent gotoSurvey = new Intent(Dashboard.this, InputSurvey.class);
//                            gotoSurvey.putExtra( "surveys", timeline. );
//                            startActivity( gotoSurvey );
                            // http://loopj.com/android-async-http/
                            // https://stackoverflow.com/questions/29339565/calling-rest-api-from-an-android-app
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.i("dashboard","failed fetching surveys ("+responseString+")");
                        }
                    });
                }
            }
        );

    }
}
