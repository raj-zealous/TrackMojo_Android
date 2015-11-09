package com.tracmojo.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.tracmojo.R;
import com.tracmojo.database.RemDBAdapter;
import com.tracmojo.util.AppSession;
import com.tracmojo.util.Util;
import com.tracmojo.webservice.VolleySetup;
import com.tracmojo.webservice.VolleyStringRequest;
import com.tracmojo.webservice.Webservices;

public class AccountSettingsActivity extends BaseActivity {
	public static final String FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION = "com.hrupin.FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION";
	private static final int PHONE_NUMBER_MIN_LIMIT = 10;

	private Context mContext;
	private ProgressDialog mProgress;
	public RequestQueue mQueue;

	private SharedPreferences prefs;
	private TextView tvPrimaryEmail;
	private EditText etSecondaryEmail, etFirstName, etLastName, etCurrentPassword, etCountryCode, etNewPassword,
			etPhoneNumber;

	private LinearLayout linPassWordPart;

	private Button btnSave;

	private boolean isSocialUser;

	private BaseActivityReceiver baseActivityReceiver = new BaseActivityReceiver();
	public static final IntentFilter INTENT_FILTER = createIntentFilter();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_settings);

		mContext = this;
		mQueue = VolleySetup.getRequestQueue();
		AppSession session = new AppSession(mContext);
		prefs = session.getPreferences();

		ivLogo.setVisibility(View.GONE);
		tvHeader.setVisibility(View.VISIBLE);
		tvLogout.setVisibility(View.VISIBLE);
		tvHeader.setText(getString(R.string.account_settings_header));

		initializeComponents();

		if (Util.checkConnection(mContext))
			getUserDetail();

		tvLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showLogoutDialog();
			}
		});

		registerBaseActivityReceiver();
	}

	private void initializeComponents() {

		linPassWordPart = (LinearLayout) findViewById(R.id.activity_account_settings_linPasswordPart);
		tvPrimaryEmail = (TextView) findViewById(R.id.activity_account_settings_etPrimaryEmail);

		etSecondaryEmail = (EditText) findViewById(R.id.activity_account_settings_etSecondaryEmail);
		etFirstName = (EditText) findViewById(R.id.activity_account_settings_etFirstName);
		etLastName = (EditText) findViewById(R.id.activity_account_settings_etLastName);
		etCurrentPassword = (EditText) findViewById(R.id.activity_account_settings_etCurrentPassword);
		etNewPassword = (EditText) findViewById(R.id.activity_account_settings_etNewPassword);
		etPhoneNumber = (EditText) findViewById(R.id.activity_account_settings_etPhoneNumber);
		etCountryCode = (EditText) findViewById(R.id.activity_account_settings_etCountryCode);
		if (!TextUtils.isEmpty(GetCountryZipCode())) {
			etCountryCode.setText("+" + GetCountryZipCode());
		}

		btnSave = (Button) findViewById(R.id.activity_account_settings_btnSave);
		btnSave.setOnClickListener(this);

		etCountryCode.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (!s.toString().contains("+")) {
					etCountryCode.setText("+");
					Selection.setSelection(etCountryCode.getText(), etCountryCode.getText().length());
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.activity_account_settings_btnSave:
			if (!Util.isEditTextEmpty(etSecondaryEmail)
					&& !Util.emailValidator(etSecondaryEmail.getText().toString().trim())) {
				Util.showAlertDialog(mContext, getString(R.string.app_name),
						getString(R.string.activity_login_enter_valid_email));
			} else if (etSecondaryEmail.getText().toString().equals(tvPrimaryEmail.getText().toString().trim())) {
				Util.showAlertDialog(mContext, getString(R.string.app_name),
						getString(R.string.activity_registration_primary_and_sec_not_same));
			} else if (Util.isEditTextEmpty(etFirstName)) {
				Util.showAlertDialog(mContext, getString(R.string.app_name),
						getString(R.string.activity_registration_enter_user_name));
			} else if (!Util.isAlpha(etFirstName.getText().toString().trim())) {
				Util.showAlertDialog(mContext, getString(R.string.app_name),
						getString(R.string.activity_registration_enter_first_name_contains_invalid_chars));
			} else if (Util.isEditTextEmpty(etLastName)) {
				Util.showAlertDialog(mContext, getString(R.string.app_name),
						getString(R.string.activity_registration_enter_last_name));
			} else if (!Util.isAlpha(etLastName.getText().toString().trim())) {
				Util.showAlertDialog(mContext, getString(R.string.app_name),
						getString(R.string.activity_registration_enter_last_name_contains_invalid_chars));
			} else if (!Util.isEditTextEmpty(etCurrentPassword) && Util.isEditTextEmpty(etNewPassword)) {
				Util.showAlertDialog(mContext, getString(R.string.app_name),
						getString(R.string.account_settings_enter_new_password));
			} else if (etCountryCode.getText().toString().trim().length() == 1) {
				Util.showAlertDialog(mContext, getString(R.string.app_name),
						getString(R.string.activity_registration_enter_country_code));
			} else if (Util.isEditTextEmpty(etPhoneNumber)) {
				Util.showAlertDialog(mContext, getString(R.string.app_name),
						getString(R.string.activity_registration_enter_phone_number));
			} else if (etPhoneNumber.getText().toString().length() < PHONE_NUMBER_MIN_LIMIT) {
				Util.showAlertDialog(mContext, getString(R.string.app_name),
						getString(R.string.activity_registration_invalid_phone_number));
			} else {
				if (Util.checkConnection(mContext)) {
					updateUserDetail();
				}
			}
			break;

		default:

			break;
		}
	}

	private void getUserDetail() {
		VolleyStringRequest request = new VolleyStringRequest(Request.Method.POST, Webservices.GET_USER_DETAIL,
				getUserDetailSuccessLisner(), errorLisner()) {

			@Override
			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put("user_id", "" + prefs.getInt("userid", -1));
				return params;
			}
		};

		// ***************Requesting Queue

		showProgress();
		mQueue.add(request);
	}

	private com.android.volley.Response.Listener<String> getUserDetailSuccessLisner() {
		return new com.android.volley.Response.Listener<String>() {
			@Override
			public void onResponse(String arg0) {
				// TODO Auto-generated method stub
				Log.e("Json", "==> " + arg0);
				stopProgress();

				try {
					JSONObject jsonObject = new JSONObject(arg0);
					if (jsonObject != null) {
						JSONObject jsonUser = jsonObject.getJSONObject("User");
						setUserDetail(jsonUser);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	private void updateUserDetail() {
		VolleyStringRequest request = new VolleyStringRequest(Request.Method.POST, Webservices.UPDATE_USER_DETAIL,
				updateUserDetailSuccessLisner(), errorLisner()) {

			@Override
			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put("user_id", "" + prefs.getInt("userid", -1));
				params.put("email_id", "" + tvPrimaryEmail.getText().toString().trim());
				params.put("current_password", "" + etCurrentPassword.getText().toString().trim());
				params.put("password", "" + etNewPassword.getText().toString().trim());
				params.put("secondary_email_id", "" + etSecondaryEmail.getText().toString().trim());
				params.put("first_name", "" + etFirstName.getText().toString().trim());
				params.put("last_name", "" + etLastName.getText().toString().trim());
				params.put("mobile", etCountryCode.getText().toString().trim().replace("+", "") + "-"
						+ etPhoneNumber.getText().toString().trim());
				// params.put("mobile", "" +
				// etPhoneNumber.getText().toString().trim());

				return params;
			}
		};

		// ***************Requesting Queue

		showProgress();
		mQueue.add(request);
	}

	private com.android.volley.Response.Listener<String> updateUserDetailSuccessLisner() {
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
						Util.showMessage(mContext, message);
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

	private void setUserDetail(JSONObject jsonUser) throws JSONException {
		if (jsonUser != null) {
			String primaryEmail = "" + jsonUser.getString("email_id");
			String secondaryEmail = "" + jsonUser.getString("secondary_email");
			String firstName = "" + jsonUser.getString("first_name");
			String lastName = "" + jsonUser.getString("last_name");
			String phoneNumber = "" + jsonUser.getString("mobile");

			tvPrimaryEmail.setText(primaryEmail);
			etSecondaryEmail.setText(secondaryEmail);
			etFirstName.setText(firstName);
			etLastName.setText(lastName);

			if (!TextUtils.isEmpty(phoneNumber)) {
				Log.i("", "phoneNumber==" + phoneNumber);
				if (phoneNumber.contains("-")) {
					String[] parts = phoneNumber.split("-");

					if (parts.length > 0)
						etCountryCode.setText("+" + parts[0]);
					if (parts.length > 1)
						etPhoneNumber.setText("" + parts[1]);
				} else {
					etPhoneNumber.setText(phoneNumber);
				}
			}

			String fbId = "" + jsonUser.getString("fb_id");
			String twitId = "" + jsonUser.getString("twitter_id");
			String googleId = "" + jsonUser.getString("gplus_id");
			if (TextUtils.isEmpty(fbId) && TextUtils.isEmpty(twitId) && TextUtils.isEmpty(googleId)) {
				isSocialUser = false;
				linPassWordPart.setVisibility(View.VISIBLE);
			} else {
				isSocialUser = true;
				linPassWordPart.setVisibility(View.GONE);
				etCurrentPassword.setEnabled(false);
				etCurrentPassword.setOnKeyListener(null);
				etNewPassword.setEnabled(false);
				etNewPassword.setOnKeyListener(null);
			}
		}
	}

	private void showLogoutDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

		// set title
		alertDialogBuilder.setTitle(getString(R.string.logout_title));

		// set dialog message
		alertDialogBuilder.setMessage(getString(R.string.logout_text)).setCancelable(false)
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						if (Util.checkConnection(mContext))
							logoutApi();

					}
				}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	private void logout() {
		prefs.edit().remove("userid").commit();
		prefs.edit().remove("timestamp").commit();
		prefs.edit().putBoolean("isFromLogout", true).commit();
		closeAllActivities();
		Intent intent = new Intent(mContext, LoginActivity.class);
		intent.setFlags(
				Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();

		RemDBAdapter remDBAdapter = new RemDBAdapter(mContext);
		remDBAdapter.open();
		remDBAdapter.deleteAll();
		remDBAdapter.close();
	}

	public class BaseActivityReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION)) {
				finish();
			}
		}
	}

	public void closeAllActivities() {
		sendBroadcast(new Intent(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION));
	}

	protected void registerBaseActivityReceiver() {
		registerReceiver(baseActivityReceiver, INTENT_FILTER);
	}

	protected void unRegisterBaseActivityReceiver() {
		unregisterReceiver(baseActivityReceiver);
	}

	private static IntentFilter createIntentFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION);
		return filter;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterBaseActivityReceiver();
	}

	private void logoutApi() {
		VolleyStringRequest request = new VolleyStringRequest(Request.Method.POST, Webservices.LOGOUT,
				logoutSuccessLisner(), errorLisner()) {

			@Override
			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put("user_id", "" + prefs.getInt("userid", -1));

				return params;
			}
		};

		// ***************Requesting Queue

		showProgress();
		mQueue.add(request);
	}

	private com.android.volley.Response.Listener<String> logoutSuccessLisner() {
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
						Util.showMessage(mContext, message);
						logout();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	public String GetCountryZipCode() {
		// String CountryID="";
		String CountryZipCode = "";

		// getNetworkCountryIso
		// CountryID= manager.getSimCountryIso().toUpperCase();
		String locale = getResources().getConfiguration().locale.getCountry();
		String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < rl.length; i++) {
			String[] g = rl[i].split(",");
			if (g[1].trim().equals(locale.trim())) {
				CountryZipCode = g[0];
				break;
			}
		}
		return CountryZipCode;
	}
}
