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
    private Answer answer;

    public Question() {}
    public Question(JSONObject obj) {
        Log.d("Question Constructor",obj.toString());
        try {
            this.qid = obj.getInt("qid");
            this.text = obj.getString("text");
            this.type = obj.getString("type");
            this.options = obj.getString("options");
            this.answer = null;
        } catch(JSONException e) {
            this.qid = -1;
            this.text = "---";
            this.type = "text";
            this.options = "";
            this.answer = null;
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

    public String getOptionsString() {
        return this.options;
    }

    public String[] getOptions() {
        return this.options.split("\\|");
    }

    public void setAnswer(Answer ans) {
        this.answer = ans;
    }
    public Boolean hasAnswer() {
        return answer != null;
    }
    public int getAnswerVal() {
        if( this.type.equalsIgnoreCase("mc") ) {
            return Integer.parseInt(this.answer.getAnswer());
        }
        return -1;
    }
    public String getAnswerText() {
        if( this.answer != null ) {
            if(this.type.equalsIgnoreCase("text")) {
                return this.answer.getAnswer();
            } else if(this.type.equalsIgnoreCase("mc")) {
                // TODO: handle the range out of bounds exception
                // when answer doesn't match the options
                return this.getOptions()[ Integer.parseInt(this.answer.getAnswer()) ];
            }
        }
        return "NULL";
    }

    @Override
    public String toString() {
        return String.format("Question [ID:%d, Text:%s, Type:%s, Options:%s]", getQuestionID(), getText(), getType(), getOptionsString());
    }
}
