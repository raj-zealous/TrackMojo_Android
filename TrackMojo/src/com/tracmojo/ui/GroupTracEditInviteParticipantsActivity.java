package com.tracmojo.ui;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.tracmojo.R;
import com.tracmojo.TracMojoApplication;
import com.tracmojo.adapters.ParticipantsListAdapter;
import com.tracmojo.model.Contact;
import com.tracmojo.model.Follower;
import com.tracmojo.util.AppSession;
import com.tracmojo.webservice.VolleySetup;

public class GroupTracEditInviteParticipantsActivity extends BaseActivity {

    private final int REQUEST_FOR_CONTACT_SELECTION = 0x123;

    private Context mContext;
    private EditText etTracName;
    private TextView tvInviteViaMail,tvBack,tvDone,tvGroupName,tvNoParticipantsFound;
    private ListView lvParticipantsList;
    private RelativeLayout relInviteMoreFriends;

    private ArrayList<Contact> listContact = new ArrayList<Contact>();
    private ArrayList<Follower> listParticipants = new ArrayList<Follower>();
    //private ArrayList<Follower> listFollowers = new ArrayList<Follower>();
    private ArrayList<String> contactIdList = new ArrayList<String>();

    private String selectedContacts="", selectedContactsName = "";

    private SharedPreferences preferences;
    private RequestQueue mQueue;
    private ProgressDialog mProgress;

    private String strTracIdeas,strCustomTracWordings1,strCustomTracWordings2,
            strCustomTracWordings3,strCustomTracWordings4,strCustomTracWordings5,
            strTracWordings,strParentFrequency,strChildFrequency,strFinishingDate,strGroupName;
    private boolean isCustomTracWordings;
    private int defaultGroupId;
    private int defaultRateWordId;
    private int tracId;

    private TracMojoApplication application;
    private ParticipantsListAdapter adapter;

    private boolean is_owner_participant;
    private CheckBox cbAsParticipant;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_trac_edit_invite_participants);
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

        cbAsParticipant = (CheckBox) findViewById(R.id.activity_group_trac_invite_participants_cbAsParticipant);

        tvNoParticipantsFound = (TextView) findViewById(R.id.activity_group_trac_edit_invite_participants_tvNoFollowerFound);

        etTracName = (EditText) findViewById(R.id.activity_group_trac_edit_invite_participants_etTracName);
        lvParticipantsList = (ListView) findViewById(R.id.activity_group_trac_edit_invite_participants_lvFollowersList);
        tvInviteViaMail = (TextView) findViewById(R.id.activity_group_trac_edit_invite_participants_tvInviteViaMail);
        tvGroupName = (TextView) findViewById(R.id.activity_group_trac_edit_invite_participants_tvGroupName);

        relInviteMoreFriends = (RelativeLayout) findViewById(R.id.activity_group_trac_edit_invite_participants_relInviteViaEmail);
        relInviteMoreFriends.setOnClickListener(this);

        tvBack = (TextView) findViewById(R.id.activity_group_trac_edit_invite_participants_tvBack);
        tvDone = (TextView) findViewById(R.id.activity_group_trac_edit_invite_participants_tvDone);
        tvBack.setOnClickListener(this);
        tvDone.setOnClickListener(this);

        lvParticipantsList.setOnTouchListener(new ListView.OnTouchListener() {
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
            isCustomTracWordings = intent.getBooleanExtra("isCustomTracWordings", false);
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
            etTracName.setText(strTracIdeas);
            defaultGroupId = intent.getIntExtra("defaultGroupTypeId", 0);

            strGroupName = intent.getStringExtra("strGroupName");
            tvGroupName.setText(strGroupName);

            strParentFrequency = intent.getStringExtra("parent_frequency");
            strChildFrequency = intent.getStringExtra("child_frequency");
            strFinishingDate = intent.getStringExtra("finishing_date");

            is_owner_participant = intent.getBooleanExtra("is_owner_participant", false);
            cbAsParticipant.setChecked(is_owner_participant);

            listParticipants = application.getListParticipants();
            //listFollowers = (ArrayList<Follower>) intent.getSerializableExtra("followersList");
            if(listParticipants!=null) {
                adapter = new ParticipantsListAdapter(mContext, listParticipants);
                lvParticipantsList.setAdapter(adapter);
            }

            if(listParticipants!=null && listParticipants.size()==0){
                tvNoParticipantsFound.setVisibility(View.VISIBLE);
                lvParticipantsList.setVisibility(View.GONE);
            }else {
                lvParticipantsList.setVisibility(View.VISIBLE);
                tvNoParticipantsFound.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.activity_group_trac_edit_invite_participants_relInviteViaEmail:
                goToContactSelectionScreen();
                break;

            case R.id.activity_group_trac_edit_invite_participants_tvBack:
                onBackPressed();
                break;

            case R.id.activity_group_trac_edit_invite_participants_tvDone:
                goToInviteFollowersScreen();
                /*if(Util.checkConnection(mContext))
                    addGroupTrac();*/
                break;

            default:

                break;
        }
    }

    private void goToInviteFollowersScreen(){
        Intent intent = new Intent(mContext,GroupTracEditInviteFollowersActivity.class);
        intent.putExtra("etTracName",""+etTracName.getText().toString().trim());
        intent.putExtra("defaultGroupTypeId", defaultGroupId);
        intent.putExtra("isCustomTracWordings",isCustomTracWordings);
        if(isCustomTracWordings){
            intent.putExtra("etCustomTracWording1",""+strCustomTracWordings1);
            intent.putExtra("etCustomTracWording2",""+strCustomTracWordings2);
            intent.putExtra("etCustomTracWording3",""+strCustomTracWordings3);
            intent.putExtra("etCustomTracWording4",""+strCustomTracWordings4);
            intent.putExtra("etCustomTracWording5",""+strCustomTracWordings5);
        }else{
            intent.putExtra("tvChooseTracWordings",""+strTracWordings);
            intent.putExtra("defaultRateWordId",defaultRateWordId);
        }

        intent.putExtra("strGroupName",""+strGroupName);
        intent.putExtra("parent_frequency",""+strParentFrequency);
        intent.putExtra("child_frequency",""+strChildFrequency);
        intent.putExtra("finishing_date",""+strFinishingDate);

        selectedContacts = makeJsonArrayToSend().toString();
        intent.putExtra("strSelectedParticipantsEmails",selectedContacts);

        selectedContactsName = makeJsonArrayForNames().toString();
        intent.putExtra("strSelectedParticipantsNames", selectedContactsName);

        intent.putExtra("tracid",tracId);

        intent.putExtra("is_owner_participant",cbAsParticipant.isChecked());

        startActivity(intent);
    }

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

    private void goToContactSelectionScreen() {
        //if (contactIdList.size() <= Util.participantLeftCount) {
            Intent intent = new Intent(mContext,MyContactList.class);
            intent.putExtra("isFromEdit", true);
            ArrayList<Follower> tempList = new ArrayList<Follower>();
            tempList.addAll(listParticipants);
            tempList.addAll(application.getListFollowers());
            intent.putExtra("listFollowers",tempList);
            intent.putExtra("contactIdList",contactIdList);
            intent.putExtra("isForParticipants",true);
            startActivityForResult(intent, REQUEST_FOR_CONTACT_SELECTION);
        /*} else {
            showAlertDialog(getString(R.string.activity_invite_participants_max_limit_reached));
        }*/
    }

    private JSONArray makeJsonArrayForNames(){
        JSONArray jsonContacts = new JSONArray();
        for (int i = 0; i < listContact.size(); i++) {
            Contact contact = listContact.get(i);
            if(contact.isSelected()) {
                jsonContacts.put(contact.getName());
            }
        }

        if(listParticipants!=null){
            for (int i = 0; i < listParticipants.size() ; i++) {
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

        if(listParticipants!=null){
            for (int i = 0; i < listParticipants.size() ; i++) {
                jsonContacts.put(listParticipants.get(i).getEmailId());
            }
        }
        return jsonContacts;
    }

    public void deleteParticipants(int position){
        String email = listParticipants.get(position).getEmailId();
        if(preferences.getString("useremail", "").equalsIgnoreCase(email)){
            cbAsParticipant.setChecked(false);
        }
        listParticipants.remove(position);
        adapter.notifyDataSetChanged();

        if(listParticipants!=null && listParticipants.size()==0){
            tvNoParticipantsFound.setVisibility(View.VISIBLE);
            lvParticipantsList.setVisibility(View.GONE);
        }else {
            lvParticipantsList.setVisibility(View.VISIBLE);
            tvNoParticipantsFound.setVisibility(View.GONE);
        }
    }

    public void showAlertDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);

        // set title
        alertDialogBuilder.setTitle(getString(R.string.app_name));


        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        Intent intent = new Intent(mContext, ServiceActivity.class);
                        startActivity(intent);
                        dialog.cancel();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
