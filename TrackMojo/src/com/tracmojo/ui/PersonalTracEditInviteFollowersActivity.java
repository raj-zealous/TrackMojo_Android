package com.tracmojo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.tracmojo.R;
import com.tracmojo.TracMojoApplication;
import com.tracmojo.adapters.FollowersListAdapter;
import com.tracmojo.model.Contact;
import com.tracmojo.model.Follower;
import com.tracmojo.util.AppSession;
import com.tracmojo.util.Util;
import com.tracmojo.webservice.VolleySetup;
import com.tracmojo.webservice.VolleyStringRequest;
import com.tracmojo.webservice.Webservices;

public class PersonalTracEditInviteFollowersActivity extends BaseActivity {

    private final int REQUEST_FOR_CONTACT_SELECTION = 0x123;

    private Context mContext;
    private EditText etTracName;
    private TextView tvInviteViaMail,tvBack,tvDone,tvNoFollwerFound;
    private ListView lvFollowersList;
    private RelativeLayout relInviteMoreFriends;

    private FollowersListAdapter adapter;
    private ArrayList<Follower> listFollowers = new ArrayList<Follower>();
    private ArrayList<Contact> listContact = new ArrayList<Contact>();
    private ArrayList<String> contactIdList = new ArrayList<String>();

    private String selectedContacts="",selectedContactsName="";
    private String oldFollowersEmails = "";

    private SharedPreferences preferences;
    private RequestQueue mQueue;

    private ProgressDialog mProgress;
    private String strTracIdeas,strCustomTracWordings1,strCustomTracWordings2,
    strCustomTracWordings3,strCustomTracWordings4,strCustomTracWordings5,
    strTracWordings,strParentFrequency,strChildFrequency,strFinishingDate;
    private boolean isCustomTracWordings;
    private int defaultTracIdeaId;
    private int defaultRateWordId;
    private int tracId;
    private int originalFollowersSize;
    private TracMojoApplication application;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_trac_edit_invite_followers);
        ivBack.setVisibility(View.INVISIBLE);
        ivInfo.setVisibility(View.VISIBLE);
        mContext = this;

        AppSession session = new AppSession(mContext);
        preferences = session.getPreferences();
        mQueue = VolleySetup.getRequestQueue();
        this.application = (TracMojoApplication) getApplicationContext();

        initializeComponents();
        setDataFromPreviousScreen();
    }

    private void initializeComponents() {

        tvNoFollwerFound = (TextView) findViewById(R.id.activity_personal_trac_edit_invite_followers_tvNoFollowerFound);

        etTracName = (EditText) findViewById(R.id.activity_personal_trac_edit_invite_followers_etTracName);
        lvFollowersList = (ListView) findViewById(R.id.activity_set_frequency_lvFollowersList);
        tvInviteViaMail = (TextView) findViewById(R.id.activity_personal_trac_edit_invite_followers_tvInviteViaMail);

        relInviteMoreFriends = (RelativeLayout) findViewById(R.id.activity_personal_trac_edit_invite_followers_relInviteViaEmail);
        relInviteMoreFriends.setOnClickListener(this);

        tvBack = (TextView) findViewById(R.id.activity_personal_trac_edit_invite_followers_tvBack);
        tvDone = (TextView) findViewById(R.id.activity_personal_trac_edit_invite_followers_tvDone);
        tvBack.setOnClickListener(this);
        tvDone.setOnClickListener(this);

        lvFollowersList = (ListView) findViewById(R.id.activity_personal_trac_edit_invite_followers_lvFollowersList);

        lvFollowersList.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });


    }

    private void setDataFromPreviousScreen(){
        Intent intent = getIntent();
        if(intent!=null) {
            tracId = intent.getIntExtra("tracid", -1);
            isCustomTracWordings = intent.getBooleanExtra("isCustomTracWordings",false);
            if (isCustomTracWordings) {
                strCustomTracWordings1 = intent.getStringExtra("etCustomTracWording1");
                strCustomTracWordings2 = intent.getStringExtra("etCustomTracWording2");
                strCustomTracWordings3 = intent.getStringExtra("etCustomTracWording3");
                strCustomTracWordings4 = intent.getStringExtra("etCustomTracWording4");
                strCustomTracWordings5 = intent.getStringExtra("etCustomTracWording5");
            }else{
                strTracWordings = intent.getStringExtra("tvChooseTracWordings");
                defaultRateWordId = intent.getIntExtra("defaultRateWordId",0);
            }

            strTracIdeas = intent.getStringExtra("etTracName");
            defaultTracIdeaId = intent.getIntExtra("defaultTracIdeaId",0);
            etTracName.setText(strTracIdeas);
            if(defaultTracIdeaId > 0){
                etTracName.setKeyListener(null);
            }

            strParentFrequency = intent.getStringExtra("parent_frequency");
            strChildFrequency = intent.getStringExtra("child_frequency");
            strFinishingDate = intent.getStringExtra("finishing_date");

            listFollowers = application.getListFollowers();
            //listFollowers = (ArrayList<Follower>) intent.getSerializableExtra("followersList");
            if(listFollowers!=null) {
                originalFollowersSize = listFollowers.size();
                adapter = new FollowersListAdapter(mContext, listFollowers,false);
                lvFollowersList.setAdapter(adapter);
            }

            if(listFollowers!=null && listFollowers.size()==0){
                tvNoFollwerFound.setVisibility(View.VISIBLE);
                lvFollowersList.setVisibility(View.GONE);
            }else {
                lvFollowersList.setVisibility(View.VISIBLE);
                tvNoFollwerFound.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.activity_personal_trac_edit_invite_followers_relInviteViaEmail:
                goToContactSelectionScreen();
                break;

            case R.id.activity_personal_trac_edit_invite_followers_tvBack:
                onBackPressed();
                break;

            case R.id.activity_personal_trac_edit_invite_followers_tvDone:
                selectedContacts = makeJsonArrayToSend().toString();
                selectedContactsName = makeJsonArrayForNames().toString();
                if(Util.checkConnection(mContext))
                    editPersonalTrac();
                break;

            default:

                break;
        }
    }

    /*@Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(listFollowers!=null && originalFollowersSize != listFollowers.size()){
           Intent intent = new Intent();
           intent.putExtra("listFollowers",listFollowers);
           setResult(RESULT_OK,intent);
        }
        finish();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FOR_CONTACT_SELECTION){
            if(resultCode == RESULT_OK){
                if(data!=null){
                    listContact = (ArrayList<Contact>) data.getSerializableExtra("contact_list");
                    tvInviteViaMail.setText("invite via email (" + getSelectedContactSize() + " contacts selected)");
                    makeJsonArrayToSend();
                }
            }
        }
    }

    public void deleteFollowers(int position){
        listFollowers.remove(position);
        adapter.notifyDataSetChanged();
        if(listFollowers!=null && listFollowers.size()==0){
            tvNoFollwerFound.setVisibility(View.VISIBLE);
            lvFollowersList.setVisibility(View.GONE);
        }else {
            lvFollowersList.setVisibility(View.VISIBLE);
            tvNoFollwerFound.setVisibility(View.GONE);
        }
    }

    private void goToContactSelectionScreen() {
        Intent intent = new Intent(mContext,MyContactList.class);
        intent.putExtra("isFromEdit",true);
        intent.putExtra("listFollowers",listFollowers);
        intent.putExtra("contactIdList", contactIdList);
        startActivityForResult(intent, REQUEST_FOR_CONTACT_SELECTION);
    }

    private int getSelectedContactSize(){
       int count = 0;
        for (int i = 0; i < listContact.size(); i++) {
            Contact contact = listContact.get(i);
            if(contact.isSelected()){
                count++;
            }
        }
       return count;

    }

    private JSONArray makeJsonArrayForNames(){
        JSONArray jsonContacts = new JSONArray();
        for (int i = 0; i < listContact.size(); i++) {
            Contact contact = listContact.get(i);
            if(contact.isSelected()){
                jsonContacts.put(contact.getName());
            }
        }

        if(listFollowers!=null){
            for (int i = 0; i < listFollowers.size() ; i++) {
                jsonContacts.put("");
            }
        }

        return jsonContacts;
    }

    private JSONArray makeJsonArrayToSend(){
        contactIdList.clear();
        JSONArray jsonContacts = new JSONArray();
        for (int i = 0; i < listContact.size(); i++) {
            Contact contact = listContact.get(i);
            if(contact.isSelected()){
                jsonContacts.put(contact.getEmail());
                contactIdList.add(contact.getId());
            }
        }

        if(listFollowers!=null){
            for (int i = 0; i < listFollowers.size() ; i++) {
                jsonContacts.put(listFollowers.get(i).getEmailId());
            }
        }

        return jsonContacts;
    }

    private void editPersonalTrac() {

        VolleyStringRequest editPersonalTracRequest = new VolleyStringRequest(
                Request.Method.POST, Webservices.EDIT_PERSONAL_TRAC,
                editPersonalTracSuccessLisner(), editPersonalTracErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", ""+preferences.getInt("userid", -1));
                params.put("trac_id",""+tracId);
                if(defaultTracIdeaId > 0){
                    params.put("idea_id",""+defaultTracIdeaId);
                }else {
                    params.put("goal",""+strTracIdeas);
                }

                if(defaultRateWordId > 0){
                    params.put("rate_word_id",""+defaultRateWordId);
                }else {
                    params.put("cust_rate_word1",""+strCustomTracWordings1);
                    params.put("cust_rate_word2",""+strCustomTracWordings2);
                    params.put("cust_rate_word3",""+strCustomTracWordings3);
                    params.put("cust_rate_word4",""+strCustomTracWordings4);
                    params.put("cust_rate_word5",""+strCustomTracWordings5);
                }

                params.put("rating_frequency",""+strParentFrequency);
                params.put("rating_day",""+strChildFrequency);
                params.put("finish_date",""+strFinishingDate);
                params.put("invited_emails",""+selectedContacts);
                params.put("invited_names",""+selectedContactsName);

                return params;
            }
        };

        // ***************Requesting Queue

        showProgress();
        mQueue.add(editPersonalTracRequest);
    }

    private com.android.volley.Response.Listener<String> editPersonalTracSuccessLisner() {
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

                        goToHomePage();

                        String msg =""+ jsonObject.getString("message");
                        Util.showMessage(mContext, msg);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener editPersonalTracErrorLisner() {
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

    private void goToHomePage(){
        Intent intent = new Intent(mContext,DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
