package ca.interfacemaster.surveyor;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ApiService {
    private static final String BASE_URL = "http://10.0.2.2/api/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    // public api functions

    public static void registerIntoTrial(String tid, AsyncHttpResponseHandler responseHandler) {
        post( "register/"+tid, null, responseHandler);
    }

    public static void queryForSurveys(String tid, String uuid, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.add("uuid",uuid);
        Log.i("dashboard","getting surveys for "+uuid+" in "+tid);
        get( "trial/"+tid+"/surveys", params, responseHandler );
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
