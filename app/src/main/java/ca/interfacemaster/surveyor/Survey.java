package ca.interfacemaster.surveyor;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Survey implements Serializable {
    private String name;

    public Survey() {
        this.name = "default";
    }
    public Survey(JSONObject obj) {
        Log.d("Survey Constructor",obj.toString());
        try {
            this.name = obj.getString("name");
        } catch(JSONException e) {
            this.name = "Unknown Name";
        }
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Survey [id:_, name:"+this.name+"]";
    }
}
