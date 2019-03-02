package ca.interfacemaster.surveyor.services;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

public class ApiService {
//    private static final String BASE_URL = "http://10.0.2.2/api/"; // localhost when running through emulator
    private static final String BASE_URL = "http://rctrials.tk/api/"; // rctrials.tk server // TODO: switch to HTTPS
    private static AsyncHttpClient client = new AsyncHttpClient();

    // public api functions

    public static void registerIntoTrial(String tid, AsyncHttpResponseHandler responseHandler) {
        String url = String.format("register/%s",tid);
        post(url, null, responseHandler);
    }

    public static void queryForSurveys(String tid, String uuid, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.add("uuid",uuid);
        String url = String.format("trial/%s/surveys",tid);
        get(url, params, responseHandler);
    }

    public static void postSurvey(String tid, int sid, String uuid, JSONArray answers, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.add("uuid",uuid);
        params.add("answers",answers.toString());
        String url = String.format("trial/%s/survey/%d",tid,sid);
        post(url, params, responseHandler);
    }

    // private supporting functions

    private static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    private static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
