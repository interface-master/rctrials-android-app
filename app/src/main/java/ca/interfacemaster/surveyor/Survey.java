package ca.interfacemaster.surveyor;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Survey {
    private String title;

    public Survey() {
        this.title = "default";
    }
    public Survey(JSONObject obj) {
        Log.d("Survey Constructor",obj.toString());
        try {
            this.title = obj.getString("name");
        } catch(JSONException e) {
            this.title = "Unknown Title";
        }
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
