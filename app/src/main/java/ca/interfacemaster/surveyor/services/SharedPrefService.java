package ca.interfacemaster.surveyor.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.interfacemaster.surveyor.R;
import ca.interfacemaster.surveyor.classes.Survey;

public class SharedPrefService {
    public static String PREF_SEL;
    public static String PREF_TID;
    public static String PREF_UUID;
    public static String PREF_SURVEYS;

    private static Context mContext;
    private static SharedPreferences prefs;
    private static int selected;                   // 0
    private static String storedTID;               // "ab12"
    private static JSONArray storedTIDJSON;        // ["ab12","cd34",...]
    private static String storedUUID;              // "uvw-xyz..."
    private static String storedSurveys;           // "{...}"
    private static JSONArray storedSurveysAry;     // [...]
    private static List<Survey> storedSurveyList;  // ...

    //**********
    // Model:
    // selected:       v
    // TID: [ a1, b2, c3, ... ]
    // UID.a1 = 'x';
    // ...
    // SURVEYS.a1 = {}
    // ...
    //**********

    public SharedPrefService(Context context) {
        // set context
        this.mContext = context;
        // set consts
        PREF_SEL = mContext.getString(R.string.pref_selected);
        PREF_TID = mContext.getString(R.string.pref_tid);
        PREF_UUID = mContext.getString(R.string.pref_uuid);
        PREF_SURVEYS = mContext.getString(R.string.pref_surveys);
        // set shared prefs
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        // retrieve data
        retrieveSelected();
        retrieveTID();
        Log.d("RETRIEVING", "uuid with tid:"+storedTID);
        retrieveUUID(storedTID);
        retrieveSurveys(storedTID);
    }

    // passthrough

    public Boolean contains(String str) {
        return prefs.contains(str);
    }

    // TID

    private void retrieveSelected() {
        selected = prefs.getInt(PREF_SEL, 0);
    }

    private int getSelected() {
        return selected;
    }

    private void retrieveTID() {
        try {
            storedTIDJSON = new JSONArray( prefs.getString(PREF_TID, null) );
            storedTID = storedTIDJSON.getString( getSelected() );
        } catch (Exception e) {
            storedTIDJSON = new JSONArray();
            storedTID = null;
        }
    }

    public String getTID() {
        try {
            return storedTIDJSON.get(selected).toString();
        } catch (JSONException e) {
            return null;
        }
    }

    public String[] getTIDs() {
        String[] ary = new String[storedTIDJSON.length()];
        for(int i = 0; i < storedTIDJSON.length(); i++ ) {
            String t;
            try {
                t = storedTIDJSON.getString(i);
            } catch (JSONException e) {
                t = "";
            }
            ary[i] = t;
        }
        return ary;
    }

    public boolean hasTID(String tid) {
        return Arrays.asList(getTIDs()).contains(tid);
    }

    public void setTID(String tid) {
        storedTIDJSON.put(tid);
        storedTID = storedTIDJSON.toString();
        storeTID();
    }

    private void storeTID() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_TID, storedTID);
        editor.commit();
    }

    // UUID

    private void retrieveUUID(String tid) {
        Log.d("RetrieveUUID","tid:"+tid);
        storedUUID = prefs.getString(PREF_UUID + "." + tid, null);
    }

    public String getUUID() {
        return storedUUID;
    }

    public void setUUID(String tid, String uuid) {
        storedUUID = uuid;
        storeUUID(tid);
    }

    private void storeUUID(String tid) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_UUID + "." + tid, storedUUID);
        editor.commit();
    }

    // SURVEYS

    private void retrieveSurveys(String tid) {
        // remember string
        storedSurveys = prefs.getString(PREF_SURVEYS + "." + tid, "");
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

    public void setSurveys(JSONArray jAry) {
        setSurveys(storedTID, jAry);
    }

    public void setSurveys(String tid, JSONArray jAry) {
        // update array
        // merge incoming surveys into existing array
        for( int i = 0; i < jAry.length(); i++ ) {
            try {
                JSONObject newObj = jAry.getJSONObject(i);
                int newID = newObj.getInt("sid");
                // compare agains existing surveys
                boolean found = false;
                for( int j = 0; j < storedSurveysAry.length(); j++ ) {
                    int oldID = storedSurveysAry.getJSONObject(j).getInt("sid");
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
        storeSurveys(tid);
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
        storeSurveys(storedTID);
    }

    public void removeSurvey(Survey survey) {
        JSONArray newAry = new JSONArray();
        // look for this survey in array
        // push all but the matching one
        for( int i = 0; i < storedSurveysAry.length(); i++ ) {
            try {
                JSONObject s = storedSurveysAry.getJSONObject(i);
                if (s.getInt("sid") != survey.getSurveyID()) {
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
        storeSurveys(storedTID);
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

    private void storeSurveys(String tid) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_SURVEYS + "." + tid, storedSurveys);
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