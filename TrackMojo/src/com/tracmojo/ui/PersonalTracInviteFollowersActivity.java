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
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.tracmojo.R;
import com.tracmojo.model.Contact;
import com.tracmojo.util.AppSession;
import com.tracmojo.util.Util;
import com.tracmojo.webservice.VolleySetup;
import com.tracmojo.webservice.VolleyStringRequest;
import com.tracmojo.webservice.Webservices;

public class PersonalTracInviteFollowersActivity extends BaseActivity {

    private final int REQUEST_FOR_CONTACT_SELECTION = 0x123;
    static final int PICK_CONTACT=1;

    private Context mContext;
    private EditText etTracName;
    private TextView tvInviteViaMail,tvBack,tvDone;
    private ListView lvFollowersList;
    private RelativeLayout relInviteMoreFriends;
    private ArrayList<Contact> listContact = new ArrayList<Contact>();
    private ArrayList<String> contactIdList = new ArrayList<String>();

    private String selectedContacts="",selectedContactsName="";

    private SharedPreferences preferences;
    private RequestQueue mQueue;
    private ProgressDialog mProgress;

    private String strTracIdeas,strCustomTracWordings1,strCustomTracWordings2,
            strCustomTracWordings3,strCustomTracWordings4,strCustomTracWordings5,
            strTracWordings,strParentFrequency,strChildFrequency,strFinishingDate;
    private boolean isCustomTracWordings;
    private int defaultTracIdeaId;
    private int defaultRateWordId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_trac_invite_followers);
        ivBack.setVisibility(View.INVISIBLE);
        ivInfo.setVisibility(View.VISIBLE);
        mContext = this;

        AppSession session = new AppSession(mContext);
        preferences = session.getPreferences();
        mQueue = VolleySetup.getRequestQueue();

        initializeComponents();
        setDataFromPreviousScreen();
    }

    private void initializeComponents() {
        etTracName = (EditText) findViewById(R.id.activity_invite_followers_etTracName);
        lvFollowersList = (ListView) findViewById(R.id.activity_set_frequency_lvFollowersList);
        tvInviteViaMail = (TextView) findViewById(R.id.activity_invite_followers_tvInviteViaMail);

        relInviteMoreFriends = (RelativeLayout) findViewById(R.id.activity_set_frequency_relInviteViaEmail);
        relInviteMoreFriends.setOnClickListener(this);

        tvBack = (TextView) findViewById(R.id.activity_invite_followers_tvBack);
        tvDone = (TextView) findViewById(R.id.activity_invite_followers_tvDone);
        tvBack.setOnClickListener(this);
        tvDone.setOnClickListener(this);
    }

    private void setDataFromPreviousScreen(){
        Intent intent = getIntent();
        if(intent!=null) {
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
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.activity_set_frequency_relInviteViaEmail:
                goToContactSelectionScreen();
                //launchMultiplePhonePicker();
                /*Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);*/
                /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);*/
                break;

            case R.id.activity_invite_followers_tvBack:
                onBackPressed();
                break;

            case R.id.activity_invite_followers_tvDone:
                if(Util.checkConnection(mContext))
                    addPersonalTrac();
                break;

            default:

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FOR_CONTACT_SELECTION){
            if(resultCode == RESULT_OK){
                if(data!=null){
                    listContact = (ArrayList<Contact>) data.getSerializableExtra("contact_list");
                    tvInviteViaMail.setText("invite via email (" + makeJsonArrayToSend().length() + " contacts selected)");
                    selectedContacts = makeJsonArrayToSend().toString();
                    selectedContactsName = makeJsonArrayForNames().toString();
                }
            }
        }
    }



    private void goToContactSelectionScreen() {
        Intent intent = new Intent(mContext,MyContactList.class);
        intent.putExtra("selectedContacts",selectedContacts);
        intent.putExtra("contactIdList",contactIdList);
        startActivityForResult(intent, REQUEST_FOR_CONTACT_SELECTION);
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

        return jsonContacts;
    }

    private JSONArray makeJsonArrayForNames(){
        JSONArray jsonContacts = new JSONArray();
        for (int i = 0; i < listContact.size(); i++) {
            Contact contact = listContact.get(i);
            if(contact.isSelected()){
                jsonContacts.put(contact.getName());
            }
        }

        return jsonContacts;
    }

    private void addPersonalTrac() {

        VolleyStringRequest loginRequest = new VolleyStringRequest(
                Request.Method.POST, Webservices.ADD_PERSONAL_TRAC,
                addPersonalTracSuccessLisner(), addPersonalTracErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", ""+preferences.getInt("userid", -1));
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

        showProgress();
        mQueue.add(loginRequest);
    }

    private com.android.volley.Response.Listener<String> addPersonalTracSuccessLisner() {
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

    private com.android.volley.Response.ErrorListener addPersonalTracErrorLisner() {
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
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("tabindex",3);
        startActivity(intent);
    }
}
