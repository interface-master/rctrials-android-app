package ca.interfacemaster.surveyor;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Question implements Serializable {
    private int qid;
    private String text;
    private String type;
    private String options;

    public Question() {}
    public Question(JSONObject obj) {
        Log.d("Question Constructor",obj.toString());
        try {
            this.qid = obj.getInt("qid");
            this.text = obj.getString("text");
            this.type = obj.getString("type");
            this.options = obj.getString("options");
        } catch(JSONException e) {
            this.qid = -1;
            this.text = "---";
            this.type = "text";
            this.options = "";
        }
    }

    public int getQuestionID() {
        return this.qid;
    }

    public String getText() {
        return this.text;
    }

    public String getType() {
        return this.type;
    }

    public String getOptions() {
        return this.options;
    }


    @Override
    public String toString() {
        return String.format("Question [ID:%d, Text:%s, Type:%s, Options:%s]", getQuestionID(), getText(), getType(), getOptions());
    }
}
