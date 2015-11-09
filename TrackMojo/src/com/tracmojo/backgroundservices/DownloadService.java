package com.tracmojo.backgroundservices;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.tracmojo.database.RemDBAdapter;
import com.tracmojo.database.RemDBHelper;
import com.tracmojo.model.SyncTrac;
import com.tracmojo.util.AppSession;
import com.tracmojo.util.Util;
import com.tracmojo.webservice.VolleySetup;
import com.tracmojo.webservice.VolleyStringRequest;
import com.tracmojo.webservice.Webservices;

public class DownloadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "DownloadService";

    private RequestQueue mQueue;
    private ProgressDialog mProgress;
    private SharedPreferences preferences;

    ResultReceiver receiver;
    RemDBAdapter remDBAdapter;

    public DownloadService() {
        super(DownloadService.class.getName());
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "Service Started!");

        receiver = intent.getParcelableExtra("receiver");
        remDBAdapter = new RemDBAdapter(getApplicationContext());
        remDBAdapter.open();

        mQueue = VolleySetup.getRequestQueue();
        AppSession session = new AppSession(getApplicationContext());
        preferences = session.getPreferences();

        Bundle bundle = new Bundle();
        //receiver.send(STATUS_RUNNING, Bundle.EMPTY);

        try {
            //String[] results = downloadData(url);
            String[] results = null;

            if (Util.checkConnectionWithoutMessage(getApplicationContext())) {
                syncOfflineData();
            } else {
                Log.d(TAG, "Service Stopping!");
                this.stopSelf();
            }

        } catch (Exception e) {

                /* Sending error message back to activity */
            bundle.putString(Intent.EXTRA_TEXT, e.toString());
            receiver.send(STATUS_ERROR, bundle);
        }

    }

    private void syncOfflineData() {
        VolleyStringRequest syncOfflineDataRequest = new VolleyStringRequest(
                Request.Method.POST, Webservices.SYNC_OFFLINE_DATA,
                syncOfflineDataSuccessLisner(), syncOfflineDataErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", "" + preferences.getInt("userid", -1));
                Cursor cursor = remDBAdapter.getAllData();
                long timestamp = Calendar.getInstance().getTimeInMillis() / 1000l;
                if(cursor!=null && cursor.getCount()<1){
                    params.put("timestamp", "" + preferences.getLong("timestamp", 0));
                }else{
                    params.put("timestamp", "" +  preferences.getLong("timestamp", 0));
                }
                preferences.edit().putLong("timestamp",timestamp).commit();

                Log.e("TimeStam",":"+params.get("timestamp"));

                return params;
            }

            ;
        };

        // ***************Requesting Queue

        //showProgress();
        mQueue.add(syncOfflineDataRequest);
    }

    private com.android.volley.Response.Listener<String> syncOfflineDataSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String arg0) {
                // TODO Auto-generated method stub
                Log.e("Json", "==> " + arg0);
                //stopProgress();

                Bundle bundle = new Bundle();
                bundle.putStringArray("result", null);
                receiver.send(STATUS_FINISHED, bundle);
                try {
                    JSONObject jsonObject = new JSONObject(arg0);
                    if (jsonObject != null) {

                        JSONArray jsonArrayPersonalTrac = jsonObject.getJSONArray("personal_trac");
                        if (jsonArrayPersonalTrac != null) {
                            for (int i = 0; i < jsonArrayPersonalTrac.length(); i++) {
                                JSONObject jsonTrac = jsonArrayPersonalTrac.getJSONObject(i);
                                putTracInTheDB(jsonTrac, RemDBHelper.PERSONAL_TRAC);
                            }

                            JSONArray jsonArrayGroupTrac = jsonObject.getJSONArray("group_trac");
                            if(jsonArrayGroupTrac!=null){
                                for (int i = 0; i < jsonArrayGroupTrac.length(); i++) {
                                    JSONObject jsonTrac = jsonArrayGroupTrac.getJSONObject(i);
                                    putTracInTheDB(jsonTrac, RemDBHelper.GROUP_TRAC);
                                }
                            }

                            JSONArray jsonArrayFollowingTrac = jsonObject.getJSONArray("following_trac");
                            if(jsonArrayFollowingTrac!=null){
                                for (int i = 0; i < jsonArrayFollowingTrac.length(); i++) {
                                    JSONObject jsonTrac = jsonArrayFollowingTrac.getJSONObject(i);
                                    putTracInTheDB(jsonTrac, RemDBHelper.FOLLOWING_TRAC);
                                }
                            }
                        }

                        /*responseMessage = jsonObject.optString("message");
                        Util.showMessage(, responseMessage);*/

                        remDBAdapter.close();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    private void putTracInTheDB(JSONObject jsonTrac,String type) throws JSONException {

        int id = jsonTrac.getInt("id");
        boolean isDeleted = false;

        String strDeleted = jsonTrac.getString("is_deleted");
        if(strDeleted.equalsIgnoreCase("Y")){
            isDeleted = true;
        }else {
            isDeleted = false;
        }

        String jsonData = jsonTrac.getString("data");

        SyncTrac syncTrac = new SyncTrac(id,jsonData, type);
        remDBAdapter.insertTracIfNotExists(id, isDeleted, syncTrac);
    }

    private com.android.volley.Response.ErrorListener syncOfflineDataErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Json Error", "==> " + error.getMessage());
                //Util.showMessage(getApplicationContext(), error.getMessage());
            }
        };
    }
}