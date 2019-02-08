package ca.interfacemaster.surveyor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Dashboard extends AppCompatActivity {

    public static RecyclerView mRecyclerView;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        setupNav();
        checkConfig();
    }

    private void setupView() {
        // set navigation as the view
        setContentView(R.layout.navigation);
        // grab frame and add content
        LinearLayout frame = findViewById(R.id.content_frame);
        View contentView = LayoutInflater.from(frame.getContext())
                .inflate(R.layout.activity_dashboard, frame, false);
        frame.addView( contentView );
        // configure toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void setupNav() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    // TODO: add code here to actually do stuff when items are clicked
                    return true;
                }
            }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Will check for config changes when coming back from another activity.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        checkConfig();
        updateSurveyCards();
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
        TextView subHeader = findViewById(R.id.textDashboardSubHeader);
        Button button = findViewById(R.id.buttonSetUpTID);
        header.setText(getString(R.string.welcome_header));
        subHeader.setText(getString(R.string.welcome_sub_header));
        button.setText(getString(R.string.welcome_set_up));
        button.setVisibility(View.VISIBLE);
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
        // refs
        String PREF_TID = getString(R.string.pref_tid);
        String PREF_UUID = getString(R.string.pref_uuid);
        TextView header = findViewById(R.id.textDashboardHeader);
        TextView subHeader = findViewById(R.id.textDashboardSubHeader);
        Button button = findViewById(R.id.buttonSetUpTID);
        TextView listHeader = findViewById(R.id.textAvailableSurveys);
        View listSurveys = findViewById(R.id.includeListSurveys);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String storedTID = prefs.getString(PREF_TID, null);
        final String storedUUID = prefs.getString(PREF_UUID, null);
        // text
        header.setText(getString(R.string.welcome_header));
        subHeader.setText(getString(R.string.registered_sub_header));
        button.setText(getString(R.string.registered_check));
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApiService.queryForSurveys(storedTID, storedUUID, new JsonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            // TODO: add progress spinner
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
                            Log.d("dashboard bundle", bundle.toString());
                            Log.d("dashboard timeline",timeline.toString());

                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Dashboard.this);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(getString(R.string.pref_surveys), timeline.toString());
                            editor.commit();



                            // TODO: goto survey view to process surveys
                            // Intent gotoSurvey = new Intent(Dashboard.this, InputSurvey.class);
                            // gotoSurvey.putExtra( "surveys", timeline. );
                            // startActivity( gotoSurvey );
                            // http://loopj.com/android-async-http/
                            // https://stackoverflow.com/questions/29339565/calling-rest-api-from-an-android-app
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.i("dashboard","failed fetching surveys ("+responseString+")");
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            // TODO: turn off spinner
                        }
                    });
                }
            }
        );
        listHeader.setVisibility(View.VISIBLE);
        listSurveys.setVisibility(View.VISIBLE);

        renderSurveyCards();
    }

    private void renderSurveyCards() {
        Log.d("RENDER SURVEY CARDS","XXX");
        // show cards
        mRecyclerView = findViewById(R.id.recyclerView);
        // get list of surveys
        List<Survey> surveyList = getStoredSurveys();
        // set up recycler with adapter and layout manager
        RecyclerView.Adapter adapter = new SurveyAdapter(this, surveyList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
    }

    private void updateSurveyCards() {
        List<Survey> surveyList = getStoredSurveys();
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        ((SurveyAdapter)adapter).setSurveyList(surveyList);
        mRecyclerView.getAdapter().notifyDataSetChanged();
        Log.d("BANG BANG","BOOOOM");
    }

    // generate list by converting stored string into array
    private List<Survey> getStoredSurveys() {
        String PREF_SURVEYS = getString(R.string.pref_surveys);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final String storedSurveys = pref.getString(PREF_SURVEYS, null);
        List<Survey> surveyList = new ArrayList<>();
        JSONArray arySurveys;
        try {
            arySurveys = new JSONArray(storedSurveys);
        } catch (JSONException e) {
            arySurveys = new JSONArray();
        }
        for(int i = 0; i < arySurveys.length(); i++ ) {
            Survey s;
            try {
                s = new Survey((JSONObject) arySurveys.get(i));
            } catch (JSONException e) {
                s = new Survey();
            }
            surveyList.add(s);
        }
        return surveyList;
    }

}
