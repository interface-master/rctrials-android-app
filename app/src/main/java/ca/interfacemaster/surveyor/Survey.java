package ca.interfacemaster.surveyor;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Survey implements Serializable {
    private String tid;  // Trial ID
    private int sid;     // Survey ID
    private String name; // Survey Name
    private transient Question[] questions;

    public Survey() {}
    public Survey(JSONObject obj) {
        Log.d("Survey Constructor",obj.toString());
        try {
            this.tid = obj.getString("tid");
        } catch(JSONException e) {
            this.tid = "XXXX";
        }
        Log.d("Survey Con.set.tid",this.tid);
        try {
            this.name = obj.getString("name");
        } catch(JSONException e) {
            this.name = "Unknown Name";
        }
        Log.d("Survey Con.set.name",this.name);
        try {
            this.sid = obj.getInt("sid");
        } catch(JSONException e) {
            this.sid = -1;
        }
        Log.d("Survey Con.set.sid", String.valueOf(this.sid));
        try {
            JSONArray ary = obj.getJSONArray("questions");
            this.questions = new Question[ ary.length() ];
            for(int i = 0; i < ary.length(); i++ ) {
                this.questions[i] = new Question( ary.getJSONObject(i) );
            }
        } catch(JSONException e) {
            this.questions = new Question[]{new Question()};
        }
        Log.d("Survey Con.set.quest",this.questions.toString());
    }

    public int getSurveyID() {
        return this.sid;
    }

    public String getName() {
        return this.name;
    }

    public Question[] getQuestions() {
        return this.questions;
    }

    @Override
    public String toString() {
        String retVal = String.format("Survey [ID:%d, Title:%s, Questions:%s]", getSurveyID(), getName(), getQuestions());
        for(int i=0; i<this.questions.length; i++) {
            retVal += "\n";
            retVal += this.questions[i].toString();
        }
        return retVal;
    }

    /**
     * For serializing the array of Question[] objects
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(this.questions);
    }

    /**
     * For deserializing the array of Question[] objects
     */
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException, JSONException {
        ois.defaultReadObject();
        this.questions = (Question[]) ois.readObject();
    }
}