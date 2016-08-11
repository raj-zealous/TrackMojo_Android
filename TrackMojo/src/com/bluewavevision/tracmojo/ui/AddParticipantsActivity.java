package com.bluewavevision.tracmojo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.bluewavevision.tracmojo.R;
import com.bluewavevision.tracmojo.TracMojoApplication;
import com.bluewavevision.tracmojo.adapters.ParticipantsListForAddAdapter;
import com.bluewavevision.tracmojo.model.Contact;
import com.bluewavevision.tracmojo.model.Follower;
import com.bluewavevision.tracmojo.util.AppSession;
import com.bluewavevision.tracmojo.util.Util;
import com.bluewavevision.tracmojo.webservice.VolleySetup;
import com.bluewavevision.tracmojo.webservice.VolleyStringRequest;
import com.bluewavevision.tracmojo.webservice.Webservices;

public class AddParticipantsActivity extends BaseActivity {

	private final int REQUEST_FOR_CONTACT_SELECTION = 0x123;

	private Context mContext;
	private EditText etTracName;
	private TextView tvInviteViaMail, tvBack, tvDone, tvGroupName, tvNoParticipantsFound;
	private ListView lvParticipantsList;
	private LinearLayout linAsParticipants;
	private RelativeLayout relInviteMoreFriends;

	private ArrayList<Contact> listContact = new ArrayList<Contact>();
	private ArrayList<Follower> listParticipants = new ArrayList<Follower>();
	private ArrayList<Follower> listFollowers = new ArrayList<Follower>();
	private ArrayList<String> contactIdList = new ArrayList<String>();

	private String selectedContacts = "", selectedContactsName = "";

	private SharedPreferences preferences;
	private RequestQueue mQueue;
	private ProgressDialog mProgress;

	private String strTracIdeas, strTracWordings, strGroupName;
	private int tracId;

	private TracMojoApplication application;
	private ParticipantsListForAddAdapter adapter;
	private boolean isParticipantsAddSuccess;
	private boolean is_owner_participant;
	private CheckBox cbAsParticipant;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_trac_edit_invite_participants);
		ivBack.setVisibility(View.VISIBLE);
		ivInfo.setVisibility(View.INVISIBLE);

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
		linAsParticipants = (LinearLayout) findViewById(R.id.activity_add_participants_linAsParticipants);
		// linAsParticipants.setVisibility(View.GONE);

		tvNoParticipantsFound = (TextView) findViewById(
				R.id.activity_group_trac_edit_invite_participants_tvNoFollowerFound);

		etTracName = (EditText) findViewById(R.id.activity_group_trac_edit_invite_participants_etTracName);
		lvParticipantsList = (ListView) findViewById(R.id.activity_group_trac_edit_invite_participants_lvFollowersList);
		tvInviteViaMail = (TextView) findViewById(R.id.activity_group_trac_edit_invite_participants_tvInviteViaMail);
		tvGroupName = (TextView) findViewById(R.id.activity_group_trac_edit_invite_participants_tvGroupName);

		relInviteMoreFriends = (RelativeLayout) findViewById(
				R.id.activity_group_trac_edit_invite_participants_relInviteViaEmail);
		relInviteMoreFriends.setOnClickListener(this);

		tvBack = (TextView) findViewById(R.id.activity_group_trac_edit_invite_participants_tvBack);
		tvDone = (TextView) findViewById(R.id.activity_group_trac_edit_invite_participants_tvDone);
		tvDone.setText("Done>");
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

	private void setDataFromPreviousScreen() {
		Intent intent = getIntent();
		if (intent != null) {
			tracId = intent.getIntExtra("tracid", -1);

			strTracIdeas = intent.getStringExtra("etTracName");
			etTracName.setText(strTracIdeas);
			etTracName.setKeyListener(null);

			strGroupName = intent.getStringExtra("strGroupName");
			tvGroupName.setText(strGroupName);

			is_owner_participant = intent.getBooleanExtra("is_owner_participant", false);
			cbAsParticipant.setChecked(is_owner_participant);

			listFollowers = (ArrayList<Follower>) intent.getSerializableExtra("followersList");

			listParticipants = (ArrayList<Follower>) intent.getSerializableExtra("listParticipants");
			if (listParticipants != null) {
				adapter = new ParticipantsListForAddAdapter(mContext, listParticipants);
				lvParticipantsList.setAdapter(adapter);
			}

			if (listParticipants != null && listParticipants.size() == 0) {
				tvNoParticipantsFound.setVisibility(View.VISIBLE);
				lvParticipantsList.setVisibility(View.GONE);
			} else {
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
			selectedContacts = makeJsonArrayToSend().toString();
			selectedContactsName = makeJsonArrayForNames().toString();
			if (Util.checkConnection(mContext))
				addParticipants();
			break;

		default:

			break;
		}
	}

	private void addParticipants() {

		VolleyStringRequest request = new VolleyStringRequest(Request.Method.POST, Webservices.ADD_PARTICIPANTS,
				addParticipantsSuccessListener(), errorListener()) {

			@Override
			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put("user_id", "" + preferences.getInt("userid", -1));
				params.put("trac_id", "" + tracId);
				params.put("participated_emails", "" + selectedContacts);
				params.put("participated_names", "" + selectedContactsName);

				if (cbAsParticipant.isChecked())
					params.put("is_owner_participant", "y");
				else
					params.put("is_owner_participant", "n");

				Log.i("", "ADD_PARTICIPANTS user_id=" + preferences.getInt("userid", -1));
				Log.i("", "ADD_PARTICIPANTS trac_id=" + tracId);
				Log.i("", "ADD_PARTICIPANTS participated_emails=" + selectedContacts);
				Log.i("", "ADD_PARTICIPANTS participated_names=" + selectedContactsName);

				return params;
			}
		};

		showProgress();
		mQueue.add(request);
	}

	private void showProgress() {
		mProgress = ProgressDialog.show(mContext, "", getString(R.string.loading_message));
	}

	private void stopProgress() {
		if (mProgress != null && mProgress.isShowing())
			mProgress.cancel();
	}

	private com.android.volley.Response.Listener<String> addParticipantsSuccessListener() {
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

						// goToHomePage();

						isParticipantsAddSuccess = true;

						onBackPressed();

						/*
						 * String msg = "" + jsonObject.getString("message");
						 * Util.showMessage(mContext, msg);
						 */

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	private com.android.volley.Response.ErrorListener errorListener() {
		return new com.android.volley.Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("Json Error", "==> " + error.getMessage());
				stopProgress();
				Util.showMessage(mContext, error.getMessage());
			}
		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_FOR_CONTACT_SELECTION) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					listContact = (ArrayList<Contact>) data.getSerializableExtra("contact_list");
					tvInviteViaMail.setText("invite via email (" + getSelectedContactSize() + " contacts selected)");
					makeJsonArrayToSend();
				}
			}
		}
	}

	private int getSelectedContactSize() {
		int count = 0;
		for (int i = 0; i < listContact.size(); i++) {
			Contact contact = listContact.get(i);
			if (contact.isSelected()) {
				count++;
			}
		}
		return count;

	}

	private void goToContactSelectionScreen() {
		// if (contactIdList.size() <= Util.participantLeftCount) {
		Intent intent = new Intent(mContext, MyContactList.class);
		intent.putExtra("isFromEdit", true);
		ArrayList<Follower> tempList = new ArrayList<Follower>();
		tempList.addAll(listParticipants);
		tempList.addAll(listFollowers);
		intent.putExtra("listFollowers", tempList);
		intent.putExtra("contactIdList", contactIdList);
		intent.putExtra("isForParticipants", true);
		startActivityForResult(intent, REQUEST_FOR_CONTACT_SELECTION);
		/*
		 * } else { showAlertDialog(getString(R.string.
		 * activity_invite_participants_max_limit_reached)); }
		 */
	}

	private JSONArray makeJsonArrayForNames() {
		JSONArray jsonContacts = new JSONArray();
		for (int i = 0; i < listContact.size(); i++) {
			Contact contact = listContact.get(i);
			if (contact.isSelected()) {
				jsonContacts.put(contact.getName());
			}
		}

		if (listParticipants != null) {
			for (int i = 0; i < listParticipants.size(); i++) {
				jsonContacts.put("");
			}
		}

		return jsonContacts;
	}

	private JSONArray makeJsonArrayToSend() {
		contactIdList.clear();
		JSONArray jsonContacts = new JSONArray();
		for (int i = 0; i < listContact.size(); i++) {
			Contact contact = listContact.get(i);
			if (contact.isSelected()) {
				jsonContacts.put(contact.getEmail());
				contactIdList.add(contact.getId());
			}
		}

		if (listParticipants != null) {
			for (int i = 0; i < listParticipants.size(); i++) {
				jsonContacts.put(listParticipants.get(i).getEmailId());
			}
		}
		return jsonContacts;
	}

	public void deleteParticipants(int position) {
		String email = listParticipants.get(position).getEmailId();
		if (preferences.getString("useremail", "").equalsIgnoreCase(email)) {
			cbAsParticipant.setChecked(false);
		}
		listParticipants.remove(position);
		adapter.notifyDataSetChanged();

		if (listParticipants != null && listParticipants.size() == 0) {
			tvNoParticipantsFound.setVisibility(View.VISIBLE);
			lvParticipantsList.setVisibility(View.GONE);
		} else {
			lvParticipantsList.setVisibility(View.VISIBLE);
			tvNoParticipantsFound.setVisibility(View.GONE);
		}
	}

	public void showAlertDialog(String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

		// set title
		alertDialogBuilder.setTitle(getString(R.string.app_name));

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
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

	@Override
	public void onBackPressed() {
		if (isParticipantsAddSuccess)
			setResult(RESULT_OK);
		finish();
	}
}
