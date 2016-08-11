package com.bluewavevision.tracmojo.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;

import com.android.volley.RequestQueue;
import com.bluewavevision.tracmojo.R;
import com.bluewavevision.tracmojo.ui.DashboardActivity;
import com.bluewavevision.tracmojo.util.AppSession;
import com.bluewavevision.tracmojo.webservice.VolleySetup;

/**
 * Created by Admin on 3/27/2015.
 */
public class BaseFragment extends Fragment{
    public DashboardActivity mContext;
    public SharedPreferences prefs;
    public ProgressDialog progressDialog;

    float scalefactor;
    int width;

    public ProgressDialog mProgress;
    public RequestQueue mQueue;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (DashboardActivity) getActivity();
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels-20;
        scalefactor = metrics.density;
        AppSession session = new AppSession(mContext);
        prefs = session.getPreferences();
        mQueue = VolleySetup.getRequestQueue();
    }

    public void showProgress() {
        mProgress = ProgressDialog.show(mContext, "", getString(R.string.loading_message));
    }

    public void stopProgress() {
        if (mProgress != null && mProgress.isShowing())
            mProgress.cancel();
    }
}
