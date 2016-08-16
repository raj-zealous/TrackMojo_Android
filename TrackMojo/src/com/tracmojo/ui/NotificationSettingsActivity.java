package com.tracmojo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.tracmojo.R;
import com.tracmojo.adapters.SingleItemSelectionAdapter;
import com.tracmojo.util.AppSession;
import com.tracmojo.util.Util;
import com.tracmojo.webservice.VolleySetup;
import com.tracmojo.webservice.VolleyStringRequest;
import com.tracmojo.webservice.Webservices;

public class NotificationSettingsActivity extends BaseActivity{

    private Context mContext;
    private ProgressDialog mProgress;
    public RequestQueue mQueue;
    private SharedPreferences prefs;

    private TextView tvPreferredTime;
    private ToggleButton tbItsTimeToTracMojo,tbReminderToTracMojo,tbLastChanceToTracMojo,
                                tbEveryTimeFollowedTracCompleted,tbWeeklyReminderToTrac,
                                           tbEveryTimeParticipantsJoinsOrLeaves,tbEveryTimeFollowerJoinsOrLeaves;

    private boolean isTimeToTracMojoChecked,isRemindToTracMojoChecked,isLastChanceToTracMojoChecked,
                            isFollowedTracCompletedChecked,isWeeklyReminderToReviewTracChecked,
                                    isEveryTimeParticipantsJoinsOrLeaveChecked,
                                            isEveryTimeFollowerJoinsOrLeaveChecked;
    private String defaultPreferredTime="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_setting);
        mContext = this;
        mQueue = VolleySetup.getRequestQueue();
        AppSession session = new AppSession(mContext);
        prefs = session.getPreferences();

        ivLogo.setVisibility(View.GONE);
        tvHeader.setVisibility(View.VISIBLE);
        ivHelp.setVisibility(View.VISIBLE);
        tvHeader.setText(getString(R.string.notification_settings_header));

        initializeComponents();

        if(Util.checkConnection(mContext))
            getNotificationSettings();

        ivHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	Intent intent = new Intent(NotificationSettingsActivity.this, HelpActivity.class);
        		startActivity(intent);
                //Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.help_text_for_notification_screen));
            }
        });

    }

    private void initializeComponents(){
        tvPreferredTime = (TextView) findViewById(R.id.activity_notification_settings_tvPreferredTime);
        tvPreferredTime.setOnClickListener(this);

        tbItsTimeToTracMojo = (ToggleButton) findViewById(R.id.activity_notification_settings_tbItsTimeToTracMojo);
        tbReminderToTracMojo = (ToggleButton) findViewById(R.id.activity_notification_settings_tbItsRemindToTracMojo);
        tbLastChanceToTracMojo = (ToggleButton) findViewById(R.id.activity_notification_settings_tbLastChanceToTracMojo);

        tbEveryTimeFollowedTracCompleted = (ToggleButton) findViewById(R.id.activity_notification_settings_tbEveryTimeFollowedTracIsCompleted);
        tbWeeklyReminderToTrac = (ToggleButton) findViewById(R.id.activity_notification_settings_tbWeeklyReminder);

        tbEveryTimeParticipantsJoinsOrLeaves = (ToggleButton) findViewById(R.id.activity_notification_settings_tbEveryTimeParticipantsJoinsOrLeaves);
        tbEveryTimeFollowerJoinsOrLeaves = (ToggleButton) findViewById(R.id.activity_notification_settings_tbEveryTimeFollowersJoinsOrLeaves);

        //tbItsTimeToTracMojo.setOnCheckedChangeListener(this);
        tbItsTimeToTracMojo.setOnClickListener(this);
        tbReminderToTracMojo.setOnClickListener(this);
        tbLastChanceToTracMojo.setOnClickListener(this);
        tbEveryTimeFollowedTracCompleted.setOnClickListener(this);
        tbWeeklyReminderToTrac.setOnClickListener(this);
        tbEveryTimeParticipantsJoinsOrLeaves.setOnClickListener(this);
        tbEveryTimeFollowerJoinsOrLeaves.setOnClickListener(this);
    }

    private void setNotificationSettings(final String notificationType,final String value) {
        VolleyStringRequest request = new VolleyStringRequest(
                Request.Method.POST, Webservices.SET_NOTIFICATION_SETTINGS,
                setNotificationSuccessLisner(), errorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", "" + prefs.getInt("userid",-1));
                params.put("notification_type", "" + notificationType);
                params.put("value", "" + value);

                return params;
            }
        };

        // ***************Requesting Queue

        showProgress();
        mQueue.add(request);
    }

    private com.android.volley.Response.Listener<String> setNotificationSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String arg0) {
                // TODO Auto-generated method stub
                Log.e("Json", "==> " + arg0);
                stopProgress();

                try {
                    JSONObject jsonObject = new JSONObject(arg0);
                    if (jsonObject != null) {
                        String message = "" + jsonObject.getString("message");
                        Util.showMessage(mContext,message);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    private void getNotificationSettings() {
        VolleyStringRequest request = new VolleyStringRequest(
                Request.Method.POST, Webservices.GET_NOTIFICATION_SETTINGS,
                getNotificationSuccessLisner(), errorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", "" + prefs.getInt("userid",-1));
                return params;
            }
        };

        showProgress();
        mQueue.add(request);
    }

    private com.android.volley.Response.Listener<String> getNotificationSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String arg0) {
                // TODO Auto-generated method stub
                Log.e("Json", "==> " + arg0);
                stopProgress();

                try {
                    JSONObject jsonObject = new JSONObject(arg0);
                    if (jsonObject != null) {
                        setSettings(jsonObject);
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

    public void showProgress() {
        mProgress = ProgressDialog.show(mContext, "", getString(R.string.loading_message));
    }

    public void stopProgress() {
        if (mProgress != null && mProgress.isShowing())
            mProgress.cancel();
    }

    private void setSettings(JSONObject jsonSettings) throws JSONException {
        if(jsonSettings!=null){

            defaultPreferredTime = jsonSettings.getString("notification_preferred_time");
            tvPreferredTime.setText(defaultPreferredTime );

            String timeToTracMojo = jsonSettings.getString("noti_trac_due");
            if (timeToTracMojo.equalsIgnoreCase("y")){
                tbItsTimeToTracMojo.setChecked(true);
                isTimeToTracMojoChecked=true;
            } else {
                tbItsTimeToTracMojo.setChecked(false);
                isTimeToTracMojoChecked=false;
            }

            String remindToTracMojo = jsonSettings.getString("noti_trac_before_hour");
            if (remindToTracMojo.equalsIgnoreCase("y")){
                tbReminderToTracMojo.setChecked(true);
                isRemindToTracMojoChecked = true;
            } else {
                tbReminderToTracMojo.setChecked(false);
                isRemindToTracMojoChecked = false;
            }

            String lastChanceToRate = jsonSettings.getString("noti_trac_after_hour");
            if (lastChanceToRate.equalsIgnoreCase("y")){
                tbLastChanceToTracMojo.setChecked(true);
                isLastChanceToTracMojoChecked = true;
            } else {
                tbLastChanceToTracMojo.setChecked(false);
                isLastChanceToTracMojoChecked = false;
            }

            String followedTracRateCompleted = jsonSettings.getString("noti_followed_trac_rate_done");
            if (followedTracRateCompleted.equalsIgnoreCase("y")){
                tbEveryTimeFollowedTracCompleted.setChecked(true);
                isFollowedTracCompletedChecked = true;
            } else {
                tbEveryTimeFollowedTracCompleted.setChecked(false);
                isFollowedTracCompletedChecked = false;
            }

            String weeklyReminder = jsonSettings.getString("noti_followed_trac_weekly_reminder");
            if (weeklyReminder.equalsIgnoreCase("y")){
                tbWeeklyReminderToTrac.setChecked(true);
                isWeeklyReminderToReviewTracChecked = true;
            } else {
                tbWeeklyReminderToTrac.setChecked(false);
                isWeeklyReminderToReviewTracChecked = false;
            }

            String participantsJoinsOrLeave = jsonSettings.getString("noti_partcipant_join_leave");
            if (participantsJoinsOrLeave.equalsIgnoreCase("y")){
                tbEveryTimeParticipantsJoinsOrLeaves.setChecked(true);
                isEveryTimeParticipantsJoinsOrLeaveChecked = true;
            } else {
                tbEveryTimeParticipantsJoinsOrLeaves.setChecked(false);
                isEveryTimeParticipantsJoinsOrLeaveChecked = false;
            }

            String followerJoinsOrLeave = jsonSettings.getString("noti_follower_join_leave");
            if (followerJoinsOrLeave.equalsIgnoreCase("y")){
                tbEveryTimeFollowerJoinsOrLeaves.setChecked(true);
                isEveryTimeFollowerJoinsOrLeaveChecked = true;
            } else {
                tbEveryTimeFollowerJoinsOrLeaves.setChecked(false);
                isEveryTimeFollowerJoinsOrLeaveChecked = false;
            }

        }
    }

    /*@Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.activity_notification_settings_tbItsTimeToTracMojo:
                if(Util.checkConnection(mContext)){
                    setNotificationSettings("noti_trac_due",isChecked);
                }
                break;

            case R.id.activity_notification_settings_tbItsRemindToTracMojo:

                break;

            case R.id.activity_notification_settings_tbLastChanceToTracMojo:

                break;

            case R.id.activity_notification_settings_tbEveryTimeFollowedTracIsCompleted:

                break;

            case R.id.activity_notification_settings_tbWeeklyReminder:

                break;

            case R.id.activity_notification_settings_tbEveryTimeParticipantsJoinsOrLeaves:

                break;

            case R.id.activity_notification_settings_tbEveryTimeFollowersJoinsOrLeaves:

                break;

            default:

                break;
        }
    }*/

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.activity_notification_settings_tbItsTimeToTracMojo:
                if(Util.checkConnection(mContext)){
                    if(isTimeToTracMojoChecked){
                        setNotificationSettings("noti_trac_due","n");
                        isTimeToTracMojoChecked = false;
                    } else {
                        setNotificationSettings("noti_trac_due","y");
                        isTimeToTracMojoChecked = true;
                    }
                }
                break;

            case R.id.activity_notification_settings_tbItsRemindToTracMojo:
                if(Util.checkConnection(mContext)){
                    if(isRemindToTracMojoChecked){
                        setNotificationSettings("noti_trac_before_hour","n");
                        isRemindToTracMojoChecked = false;
                    } else {
                        setNotificationSettings("noti_trac_before_hour","y");
                        isRemindToTracMojoChecked = true;
                    }
                }
                break;

            case R.id.activity_notification_settings_tbLastChanceToTracMojo:
                if(Util.checkConnection(mContext)){
                    if(isLastChanceToTracMojoChecked){
                        setNotificationSettings("noti_trac_after_hour","n");
                        isLastChanceToTracMojoChecked = false;
                    } else {
                        setNotificationSettings("noti_trac_after_hour","y");
                        isLastChanceToTracMojoChecked = true;
                    }
                }
                break;

            case R.id.activity_notification_settings_tbEveryTimeFollowedTracIsCompleted:
                if(Util.checkConnection(mContext)){
                    if(isFollowedTracCompletedChecked){
                        setNotificationSettings("noti_followed_trac_rate_done","n");
                        isFollowedTracCompletedChecked = false;
                    } else {
                        setNotificationSettings("noti_followed_trac_rate_done","y");
                        isFollowedTracCompletedChecked = true;
                    }
                }
                break;

            case R.id.activity_notification_settings_tbWeeklyReminder:
                if(Util.checkConnection(mContext)){
                    if(isWeeklyReminderToReviewTracChecked){
                        setNotificationSettings("noti_followed_trac_weekly_reminder","n");
                        isWeeklyReminderToReviewTracChecked = false;
                    } else {
                        setNotificationSettings("noti_followed_trac_weekly_reminder","y");
                        isWeeklyReminderToReviewTracChecked = true;
                    }
                }
                break;

            case R.id.activity_notification_settings_tbEveryTimeParticipantsJoinsOrLeaves:
                if(Util.checkConnection(mContext)){
                    if(isEveryTimeParticipantsJoinsOrLeaveChecked){
                        setNotificationSettings("noti_partcipant_join_leave","n");
                        isEveryTimeParticipantsJoinsOrLeaveChecked = false;
                    } else {
                        setNotificationSettings("noti_partcipant_join_leave","y");
                        isEveryTimeParticipantsJoinsOrLeaveChecked = true;
                    }
                }
                break;

            case R.id.activity_notification_settings_tbEveryTimeFollowersJoinsOrLeaves:
                if(Util.checkConnection(mContext)){
                    if(isEveryTimeFollowerJoinsOrLeaveChecked){
                        setNotificationSettings("noti_follower_join_leave","n");
                        isEveryTimeFollowerJoinsOrLeaveChecked = false;
                    } else {
                        setNotificationSettings("noti_follower_join_leave","y");
                        isEveryTimeFollowerJoinsOrLeaveChecked = true;
                    }
                }
                break;

            case R.id.activity_notification_settings_tvPreferredTime:
                showPrefferedTimeDialog();
                break;

            default:

                break;
        }
    }

    private void showPrefferedTimeDialog()
    {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_single_item_selection_list);

        final ArrayList<String> listParentFrequency = new ArrayList<String>();

        int defaultPosition = 0;
        for (int i = 0; i <24 ; i++) {
            listParentFrequency.add(""+i);
        }

        for (int j = 0; j < listParentFrequency.size(); j++) {
            if(listParentFrequency.get(j).equalsIgnoreCase(defaultPreferredTime))
            {
                defaultPosition=j;
            }
        }

        SingleItemSelectionAdapter adapter = new SingleItemSelectionAdapter(mContext, listParentFrequency, defaultPosition);

        ListView lvRangelist = (ListView) dialog.findViewById(R.id.custom_choose_quantity_lv_range);
        lvRangelist.setAdapter(adapter);
        lvRangelist.setSmoothScrollbarEnabled(true);
        lvRangelist.setSelection(defaultPosition);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.dialog_title);
        tvTitle.setText(getString(R.string.notification_settings_preferred_time_dialog_title));

        lvRangelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                defaultPreferredTime = listParentFrequency.get(position);
                tvPreferredTime.setText(listParentFrequency.get(position));

                dialog.dismiss();

                if(Util.checkConnection(mContext))
                    setNotificationSettings("notification_preferred_time",defaultPreferredTime);
            }
        });

        dialog.show();
    }
}
