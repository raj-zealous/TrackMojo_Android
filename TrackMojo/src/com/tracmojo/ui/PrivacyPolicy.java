package com.tracmojo.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.tracmojo.R;
import com.tracmojo.util.Util;
import com.tracmojo.webservice.VolleySetup;
import com.tracmojo.webservice.VolleyStringRequest;
import com.tracmojo.webservice.Webservices;

import android.app.ProgressDialog;
import android.content.Context;import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class PrivacyPolicy extends BaseActivity {

    //WEB SERVICE PRIVACY POLICY
    public static final String PAGE_TITLE = "title";
    public static final String PAGE_TITLE_VALUE = "privacy";

    private Context mContext;
    private WebView wbPrivacyPolicy;
    private ProgressDialog mProgress;
    private RequestQueue mQueue;
    ProgressBar progressBar1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        mContext = this;
        
        
        wbPrivacyPolicy = (WebView) findViewById(R.id.activity_privacy_policy_wbPrivacy);
        progressBar1= (ProgressBar) findViewById(R.id.progressBar1);
        mQueue = VolleySetup.getRequestQueue();

        
        
        
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
            if(bundle.containsKey("link")) {
                String  link = bundle.getString("link");
                
                if(Util.checkConnection(mContext)){
                progressBar1.setVisibility(View.VISIBLE);
                wbPrivacyPolicy.loadUrl(link);
                
	                wbPrivacyPolicy.setWebChromeClient(new WebChromeClient() {
	                    public void onProgressChanged(WebView view, int progress) {
	                        
	
	                       if(progress >= 80)
	                    	   progressBar1.setVisibility(View.GONE); 
	                    }
	                 });
                }
                
            }
        } else {
        	if(Util.checkConnection(mContext))
                getPrivacyPolicy();

        }
        
    }

    private void getPrivacyPolicy() {

        VolleyStringRequest privacyRequest = new VolleyStringRequest(
                Request.Method.POST, Webservices.PRIVACY_POLICY,
                privacySuccessLisner(), errorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(PAGE_TITLE, PAGE_TITLE_VALUE);
                return params;
            }
        };

        // ***************Requesting Queue

        showProgress();
        mQueue.add(privacyRequest);
    }

    private com.android.volley.Response.Listener<String> privacySuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String arg0) {
                // TODO Auto-generated method stub
                Log.e("Json", "==> " + arg0);
                stopProgress();

                try {
                    JSONObject jsonObject = new JSONObject(arg0);
                    if (jsonObject != null) {
                        String content = jsonObject.getString("content");

                        wbPrivacyPolicy.loadDataWithBaseURL("", content, "text/html", "UTF-8", "");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener errorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Json Error", "==> " + error.getMessage());
                stopProgress();
                Util.showMessage(mContext, error.getMessage());
            }
        };
    }

    private void showProgress() {
        mProgress = ProgressDialog.show(mContext, "", getString(R.string.loading_message));
    }

    private void stopProgress() {
        if (mProgress != null && mProgress.isShowing())
            mProgress.cancel();
    }
}
