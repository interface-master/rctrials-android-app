package ca.interfacemaster.surveyor.classes;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import ca.interfacemaster.surveyor.classes.Answer;

public class Question implements Serializable {
    private int qid;
    private String text;
    private String type;
    private String options;
    private Answer answer;

    public Question() {}
    public Question(JSONObject obj) {
        Log.d("Question Constructor", obj.toString());
        try {
            this.qid = obj.getInt("qid");
        } catch (JSONException e) {
            this.qid = -1;
        }
        try {
            this.text = obj.getString("text");
        } catch (JSONException e) {
            this.text = "---";
        }
        try {
            this.type = obj.getString("type");
        } catch (JSONException e) {
            this.type = "text";
        }
        try {
            this.options = obj.getString("options");
        } catch (JSONException e) {
            this.options = "";
        }
        try {
            this.answer = new Answer(obj.getJSONObject("answer"));
        } catch (JSONException e) {
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
        if( this.options.indexOf("|") > 0 ) {
            // array delimited by pipe "|"
            return this.options
//                    .replaceAll("\\[\\d\\]","")
                    .trim()
                    .split("\\s*\\|\\s*");
        } else if( this.options.indexOf("..") > 0 ) {
            // range delimited by double dot ".."
            int min, max;
            try {
                min = Integer.parseInt(this.options.split("\\.\\.")[0].trim());
            } catch( NullPointerException e ) {
                min = 0;
            }
            try {
                max = Integer.parseInt(this.options.split("\\.\\.")[1].trim());
            } catch( NullPointerException e ) {
                max = 4; // TODO: replace this with something else
            }
            String range[] = new String[(1+max-min)];
            for( int i = min; i <= max; i++ ) {
                range[i-min] = String.valueOf(i);
            }
            return range;
        } else {
            return new String[0];
        }
    }

    public String[] getOptionLabels() {
        String[] retval = this.getOptions();
        for( int i = 0; i < retval.length; i++ ) {
            retval[i] = retval[i].replaceAll("\\[\\d\\]", "").trim();
        }
        return retval;
    }

    public int getOptionIndex(String opt) {
        String[] opts = this.getOptions();
        for( int i = 0; i < opts.length; i++ ) {
            if( opts[i]
//                    .replaceAll("\\[\\d\\]","")
                    .equalsIgnoreCase(opt) ) {
                return i;
            }
        }
        return -1;
    }

    public void setAnswer(Answer ans) {
        this.answer = ans;
    }
    public Boolean hasAnswer() {
        return answer != null;
    }
    public Answer getAnswer() {
        if( hasAnswer() ) {
            return answer;
        } else {
            return null;
        }
    }
    public int getAnswerVal() {
        if(
            this.type.equalsIgnoreCase("slider")
            ||
            this.type.equalsIgnoreCase("likert")
            ||
            this.type.equalsIgnoreCase("radio")
            ||
            this.type.equalsIgnoreCase("check")
        ) {
            String ans = this.answer.getAnswer();
            int idx = this.getOptionIndex(ans);
            if( idx > -1 ) {
                return idx;
            }

            // TODO: handle the range out of bounds exception
            // when answer doesn't match the options
            // return this.getOptions()[ Integer.parseInt(this.answer.getAnswer()) ];

        }
        return -1;
    }
    public String getAnswerText() {
        if( this.answer != null ) {
            return this.answer.getAnswer();
        }
        return "NULL";
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("qid", this.qid);
            obj.put("text", this.text);
            obj.put("type", this.type);
            obj.put("options", this.options);
            if( hasAnswer() ) {
                obj.put("answer", this.answer.getJSONObject());
            }
        } catch( JSONException e ) {
            // todo: something about it
        }
        return obj;
    }

    @Override
    public String toString() {
        if( hasAnswer() ) {
            return String.format("Question [ID:%d, Text:%s, Type:%s, Options:%s, Answer:%s]", getQuestionID(), getText(), getType(), getOptionsString(), getAnswer().toJSON());
        } else {
            return String.format("Question [ID:%d, Text:%s, Type:%s, Options:%s, Answer:%s]", getQuestionID(), getText(), getType(), getOptionsString(), "none");
        }
    }
}
