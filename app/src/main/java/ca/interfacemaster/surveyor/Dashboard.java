package ca.interfacemaster.surveyor;

import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ca.interfacemaster.surveyor.adapters.SurveyAdapter;
import ca.interfacemaster.surveyor.classes.Question;
import ca.interfacemaster.surveyor.classes.Survey;
import ca.interfacemaster.surveyor.services.ApiService;
import ca.interfacemaster.surveyor.services.SharedPrefService;
import cz.msebera.android.httpclient.Header;

public class Dashboard extends AppCompatActivity {

    public static RecyclerView mRecyclerView;
    private DrawerLayout mDrawerLayout;
    private SharedPrefService pref;
    private boolean sending;

    // menu
    private static final int MENU_ADD = Menu.FIRST;
    private static final int MENU_SETTINGS = Menu.FIRST + 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // services
        pref = new SharedPrefService(this);
        // view
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
        final NavigationView navigationView = findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();
        menu.clear();

        // add first item
        menu.add( R.id.menu_group_top, MENU_ADD, 0, R.string.menu_register )
                .setIcon(R.drawable.ic_add_black)
                .setCheckable(true);

        // add trials
        String[] tids = pref.getTIDs();
        for(int i = 1; i <= tids.length; i++ ) {
            Log.d("DASHBOARD NAV", String.format("%d : ",i) + tids[i-1] );
            menu.add( R.id.menu_group_middle, Menu.FIRST+i, Menu.FIRST+i, tids[i-1] )
                    .setIcon(R.drawable.ic_assignment_black)
                    .setCheckable(true);
        }

        // add last item
        menu.add( R.id.menu_group_bottom, MENU_SETTINGS, MENU_SETTINGS, R.string.menu_settings )
                .setIcon(R.drawable.ic_settings_black)
                .setCheckable(true);

        // set event listener
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    // uncheck all items
                    Menu menu = navigationView.getMenu();
                    for( int i = 0; i < menu.size(); i++ ) {
                        menu.getItem(i).setChecked(false);
                    }
                    menuItem.setChecked(true);
                    // close drawer
                    mDrawerLayout.closeDrawers();
                    // perform action based on what menu item is clicked
                    switch (menuItem.getItemId()) {
                        case MENU_ADD:
                            Log.d("HELLO A", "add new");
                            startActivityForResult(
                                    new Intent(Dashboard.this, InputTrial.class),
                                    1
                            );
                            return true;
                        case MENU_SETTINGS:
                            Log.d("HELLO C", "settings");
                            // todo: start activity for a settings screen
                            return true;
                        default:
                            Log.d("HELLO B", "view "+menuItem.getTitle() );
                            if( pref.selectTrial(menuItem.getTitle().toString()) ) {
                                configureSurveys();
                            }
                            // todo: switch trials
                            return Dashboard.super.onOptionsItemSelected(menuItem);
                    }
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
        if( pref.contains(pref.PREF_TID) ) {
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
        TextView header = findViewById(R.id.textDashboardHeader);
        TextView subHeader = findViewById(R.id.textDashboardSubHeader);
        Button button = findViewById(R.id.buttonSetUpTID);
        TextView listHeader = findViewById(R.id.textAvailableSurveys);
        View listSurveys = findViewById(R.id.includeListSurveys);
        // text
        header.setText( getString(R.string.welcome_trial_header) + " " + pref.getTID() );
        subHeader.setText(getString(R.string.registered_sub_header));
        button.setText(getString(R.string.registered_check));
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApiService.queryForSurveys(pref.getTID(), pref.getUUID(), new JsonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            Log.d("DASHBOARD","bleep bloop querying surveys /"+pref.getTID()+"/"+pref.getUUID());
                            findViewById(R.id.textAvailableSurveys).setVisibility(View.INVISIBLE);
                            findViewById(R.id.includeListSurveys).setVisibility(View.INVISIBLE);
                            findViewById(R.id.spinnerSurveyList).setVisibility(View.VISIBLE);
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

                            pref.setSurveys(timeline);
                            updateSurveyCards();

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
                            findViewById(R.id.spinnerSurveyList).setVisibility(View.INVISIBLE);
                            findViewById(R.id.textAvailableSurveys).setVisibility(View.VISIBLE);
                            findViewById(R.id.includeListSurveys).setVisibility(View.VISIBLE);
                            super.onFinish();
                        }
                    });
                }
            }
        );
        listHeader.setVisibility(View.VISIBLE);
        listSurveys.setVisibility(View.VISIBLE);

        setupNav();

        updateSurveyCards();
    }

    private void updateSurveyCards() {
        Log.d("DASHBOARD","updateSurveyCards: UPDATING");
        // refs
        mRecyclerView = findViewById(R.id.recyclerView);
        TextView header = findViewById(R.id.textAvailableSurveys);
        // header
        if(pref.getSurveyList().size() == 0) {
            header.setText(getText(R.string.no_available_surveys));
        } else {
            header.setText(getText(R.string.available_surveys));
        }
        // get list of surveys
        List<Survey> surveyList = pref.getSurveyList();
        sendCompletedSurveys(surveyList);
        // cards
        Log.d("CHECK ADAPTER:",String.format("%s",mRecyclerView.getAdapter() == null));
        RecyclerView.Adapter adapter;
        if( mRecyclerView.getAdapter() == null ) {
            adapter = new SurveyAdapter(this, surveyList);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(adapter);
        } else {
            adapter = mRecyclerView.getAdapter();
            ((SurveyAdapter)adapter).setSurveyList(surveyList);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
        Log.d("DASHBOARD","updateSurveyCards: UPDATED");
    }

    private void sendCompletedSurveys(List<Survey> surveyList) {
        Log.d("COMPLETED SURVEYS",String.format("Start with %d surveys; bool %s",surveyList.size(),sending));
        if( sending ) {
            Log.d("XXXXXXX","X");
            return;
        }
        for( int i = 0; i < surveyList.size(); i++ ) {
            final Survey survey = surveyList.get(i);
            if( survey.getState() == Survey.COMPLETE ) {
                Question[] questions = survey.getQuestions();
                JSONArray answers = new JSONArray();
                for( int j = 0; j < questions.length; j++ ) {
                    answers.put( questions[j].getAnswer().getJSONObject() );
                }
                Log.d("COMPLETED SURVEYS",String.format("POSTING %s / %s / %s",pref.getTID(),survey.getSurveyID(),pref.getUUID()));
                survey.markSending(true);
                sending = true;
                ApiService.postSurvey(pref.getTID(), survey.getSurveyID(), pref.getUUID(), answers, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        // TODO: add progress spinner
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
                        Log.d("RESPONSE1:",response.toString());
                        try {
                            if (response.getBoolean("success")) {
                                // todo: remove survey from shared prefs
                                pref.removeSurvey(survey);
                                updateSurveyCards();
                            }
                        } catch (JSONException e) {
                            // todo: something about it
                            Log.d("DASH:SEND:SUCS",e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.i("dashboard","failed pushing survey ("+responseString+")");
                    }

                    @Override
                    public void onFinish() {
                        sending = false;
                        super.onFinish();
                        // TODO: turn off spinner
                    }
                });
            }
        }
        Log.d("COMPLETED SURVEYS",String.format("DONE"));
    }
}
