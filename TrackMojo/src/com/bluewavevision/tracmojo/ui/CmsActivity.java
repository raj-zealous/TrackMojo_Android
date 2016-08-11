package com.bluewavevision.tracmojo.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.bluewavevision.tracmojo.R;
import com.bluewavevision.tracmojo.util.AppSession;
import com.bluewavevision.tracmojo.util.Util;
import com.bluewavevision.tracmojo.webservice.VolleySetup;
import com.bluewavevision.tracmojo.webservice.VolleyStringRequest;
import com.bluewavevision.tracmojo.webservice.Webservices;

public class CmsActivity extends BaseActivity {

    private Context mContext;
    private WebView wvData;
    private String page,header;
    private ProgressDialog mProgress;
    public RequestQueue mQueue;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cms);
        mContext = this;
        mQueue = VolleySetup.getRequestQueue();
        AppSession session = new AppSession(mContext);
        prefs = session.getPreferences();


        Intent intent = getIntent();
        page = "" + intent.getStringExtra("page");
        header = "" + intent.getStringExtra("header");

        ivLogo.setVisibility(View.GONE);
        tvHeader.setVisibility(View.VISIBLE);
        tvHeader.setText(header);

        wvData = (WebView) findViewById(R.id.activity_cms_wvData);

        if(Util.checkConnection(mContext)){
            getCmsDetail();
        }
    }

    private void getCmsDetail() {
        VolleyStringRequest request = new VolleyStringRequest(
                Request.Method.POST, Webservices.GET_CMS,
                addCommentSuccessLisner(), addRateErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("title", ""+page);
                return params;
            }
        };

        // ***************Requesting Queue

        showProgress();
        mQueue.add(request);
    }

    private com.android.volley.Response.Listener<String> addCommentSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String arg0) {
                // TODO Auto-generated method stub
                Log.e("Json", "==> " + arg0);
                stopProgress();

                try {
                    JSONObject jsonObject = new JSONObject(arg0);
                    if (jsonObject != null) {
                        String htmlContent = jsonObject.getString("content");
                        wvData.loadDataWithBaseURL("", htmlContent, "text/html", "UTF-8", "");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener addRateErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Json Error", "==> " + error.getMessage());
                stopProgress();
                Util.showMessage(mContext, error.getMessage());
            }
        };
    }

    public void showProgress() {
        mProgress = ProgressDialog.show(mContext, "", getString(R.string.loading_message));
    }

    public void stopProgress() {
        if (mProgress != null && mProgress.isShowing())
            mProgress.cancel();
    }
}
