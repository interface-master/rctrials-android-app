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

    public static final int DEFAULT = 0;
    public static final int INCOMPLETE = 1;
    public static final int COMPLETE = 2;

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

    public Question getQuestion(int id) {
        for(int i=0; i<this.questions.length; i++) {
            if(this.questions[i].getQuestionID() == id) {
                return this.questions[i];
            }
        }
        return null;
    }

    public int getQuestionsLength() {
        return this.questions.length;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        JSONArray qs = new JSONArray();
        try {
            obj.put("tid", this.tid);
            obj.put("sid", this.sid);
            obj.put("name", this.name);
            for( int i = 0; i < this.questions.length; i++ ) {
                qs.put(questions[i].getJSONObject());
            }
            obj.put("questions", qs);
        } catch (JSONException e) {
            // todo: something about it
        }
        return obj;
    }

    public int getState() {
        int countQs = getQuestionsLength();
        int countAs = 0;
        Log.d("SURVEY GETSTATE: Survey", String.format("ID:%d",getSurveyID()));
        for( int i = 0; i < this.questions.length; i++ ) {
            Answer a = this.questions[i].getAnswer();
            if( null != a ) {
                countAs++;
            }
        }
        Log.d("SURVEY GETSTATE: DONE",String.format("Q:%d, A:%d", countQs, countAs));
        if( countAs > 0 && countQs > countAs ) {
            return Survey.INCOMPLETE;
        } else if ( countQs == countAs ) {
            return Survey.COMPLETE;
        }
        return Survey.DEFAULT;
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