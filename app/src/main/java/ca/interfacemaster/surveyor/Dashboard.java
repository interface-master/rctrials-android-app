package ca.interfacemaster.surveyor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

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
        TextView header = findViewById(R.id.textDashboardHeader);
        TextView subHeader = findViewById(R.id.textDashboardSubheader);
        Button button = findViewById(R.id.buttonSetUpTID);
        header.setText(getString(R.string.welcome_header));
        subHeader.setText(getString(R.string.registered_sub_header));
        button.setText(getString(R.string.registered_check));
        button.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: add a way to check for surveys
                }
            }
        );
    }
}
