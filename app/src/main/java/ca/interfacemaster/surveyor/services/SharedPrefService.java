package ca.interfacemaster.surveyor.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ca.interfacemaster.surveyor.R;
import ca.interfacemaster.surveyor.classes.Survey;

public class SharedPrefService {
    public static String PREF_TID;
    public static String PREF_UUID;
    public static String PREF_SURVEYS;

    private static Context mContext;
    private static SharedPreferences prefs;
    private static String storedTID;
    private static String storedUUID;
    private static String storedSurveys;
    private static JSONArray storedSurveysAry;
    private static List<Survey> storedSurveyList;

    public SharedPrefService(Context context) {
        // set context
        this.mContext = context;
        // set consts
        PREF_TID = mContext.getString(R.string.pref_tid);
        PREF_UUID = mContext.getString(R.string.pref_uuid);
        PREF_SURVEYS = mContext.getString(R.string.pref_surveys);
        // set shared prefs
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        // retrieve data
        retrieveTID();
        retrieveUUID();
        retrieveSurveys();
    }

    // passthrough

    public Boolean contains(String str) {
        return prefs.contains(str);
    }

    // TID

    private void retrieveTID() {
        storedTID = prefs.getString(PREF_TID, null);
    }

    public String getTID() {
        return storedTID;
    }

    public void updateTID(String tid) {
        storedTID = tid;
        storeTID();
    }

    private void storeTID() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_TID, storedTID);
        editor.commit();
    }

    // UUID

    private void retrieveUUID() {
        storedUUID = prefs.getString(PREF_UUID, null);
    }

    public String getUUID() {
        return storedUUID;
    }

    public void updateUUID(String uuid) {
        storedUUID = uuid;
        storeUUID();
    }

    private void storeUUID() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_UUID, storedUUID);
        editor.commit();
    }

    // SURVEYS

    private void retrieveSurveys() {
        // remember string
        storedSurveys = prefs.getString(PREF_SURVEYS, "");
        // generate ary
        generateArrayFromString();
        // generate list
        generateListFromArray();
    }

    public String getSurveysString() {
        return storedSurveys;
    }

    public JSONArray getSurveysJSONArray() {
        return storedSurveysAry;
    }

    public List<Survey> getSurveyList() {
        return storedSurveyList;
    }

    public void updateSurveys(JSONArray jAry) {
        // update array
        Log.d("STORED:",storedSurveysAry.toString());
        Log.d("STORING:",jAry.toString());
        // merge incoming surveys into existing array
        for( int i = 0; i < jAry.length(); i++ ) {
            try {
                JSONObject newObj = jAry.getJSONObject(i);
                int newID = newObj.getInt("sid");
                Log.d("STORING item ID:", String.format("%d",newID) );
                // compare agains existing surveys
                boolean found = false;
                for( int j = 0; j < storedSurveysAry.length(); j++ ) {
                    int oldID = storedSurveysAry.getJSONObject(j).getInt("sid");
                    Log.d("STORED item ID:", String.format("%d",oldID) );
                    if( newID == oldID ) {
                        found = true;
                    }
                }
                if( !found ) {
                    storedSurveysAry.put( storedSurveysAry.length(), newObj );
                }
            } catch( JSONException err ) {
                // TODO: something about it
            }
        }
        // update list
        generateListFromArray();
        // update string
        generateStringFromArray();
        // update shared prefs
        storeSurveys();
    }

    public void updateSurvey(Survey survey) {
        // look for this survey in array
        for( int i = 0; i < storedSurveysAry.length(); i++ ) {
            try {
                JSONObject s = storedSurveysAry.getJSONObject(i);
                if (s.getInt("sid") == survey.getSurveyID()) {
                    // update
                    storedSurveysAry.put( i, survey.getJSONObject() );
                    break;
                }
            } catch (JSONException e) {
                // todo: something about it
            }
        }
        // update list
        generateListFromArray();
        // update string
        generateStringFromArray();
        // update shared prefs
        storeSurveys();
    }

    public void removeSurvey(Survey survey) {
        Log.d("SHARED PREFS","Removing Survey "+survey.getName());
        JSONArray newAry = new JSONArray();
        // look for this survey in array
        // push all but the matching one
        for( int i = 0; i < storedSurveysAry.length(); i++ ) {
            try {
                JSONObject s = storedSurveysAry.getJSONObject(i);
                if (s.getInt("sid") == survey.getSurveyID()) {
                    // don't copy this one
                    break;
                } else {
                    newAry.put( storedSurveysAry.get(i) );
                }
            } catch (JSONException e) {
                // todo: something about it
            }
        }
        storedSurveysAry = newAry;
        // update list
        generateListFromArray();
        // update string
        generateStringFromArray();
        // update shared prefs
        storeSurveys();
    }

    private void generateStringFromArray() {
        storedSurveys = storedSurveysAry.toString();
    }

    private void generateArrayFromString() {
        try {
            storedSurveysAry = new JSONArray(storedSurveys);
        } catch (JSONException e) {
            storedSurveysAry = new JSONArray();
        }
    }

    private void generateListFromArray() {
        if(storedSurveyList == null) {
            storedSurveyList = new ArrayList<>();
        }
        if (storedSurveyList.size() > 0) {
            storedSurveyList.clear();
        }
        for(int i = 0; i < storedSurveysAry.length(); i++ ) {
            Survey s;
            try {
                s = new Survey((JSONObject) storedSurveysAry.get(i));
            } catch (JSONException e) {
                s = new Survey();
            }
            storedSurveyList.add(s);
        }
    }

    private void storeSurveys() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_SURVEYS, storedSurveys);
        editor.commit();
    }

}

// NOTE:
//
// the way this works is that it retrieves the string from shared prefs
// then turns it into an array and into a list
// the array and list are used by the application
//
// when the app updates the surveys and sends them here
// we must regenerate the array and the string
//
// when storing, the local string is used, so make sure to keep it updated