package ca.interfacemaster.surveyor;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.Serializable;

public class Answer implements Serializable {
    private int qid; // question ID
    private String answer;

    public Answer(int qid) {
        this.qid = qid;
    }
//    public Answer(int qid, String answer) {
//        this.qid = qid;
//        this.answer = answer;
//    }
    public Answer(JSONObject obj) {
        try {
            this.qid = obj.getInt("qid");
        } catch(JSONException e) {
            this.qid = -1;
        }
        try {
            this.answer = obj.getString("answer");
        } catch(JSONException e) {
            this.answer = "---";
        }
    }

    public int getQuestionID() {
        return this.qid;
    }

    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("qid", this.qid);
            obj.put("answer", this.answer);
        } catch (JSONException e) {
            // todo: something about it
        }
        return obj;
    }

    @Override
    public String toString() {
        return String.format("Answer [QID:%d, Text:%s]",qid,getAnswer());
    }

    public String toJSON() {
        try {
            return new JSONStringer()
                .object()
                    .key("qid")
                    .value(this.qid)
                    .key("answer")
                    .value(this.answer)
                .endObject()
                .toString();
        } catch (JSONException e) {
            // TODO: handle exception
            return "";
        }
    }
}
