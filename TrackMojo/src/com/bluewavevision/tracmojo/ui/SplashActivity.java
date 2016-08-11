package com.bluewavevision.tracmojo.ui;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.bluewavevision.tracmojo.R;
import com.bluewavevision.tracmojo.pushnotification.PushUtils;
import com.bluewavevision.tracmojo.pushnotification.WakeLocker;
import com.bluewavevision.tracmojo.util.AppSession;
import com.bluewavevision.tracmojo.util.Util;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SplashActivity extends Activity {

	private Context mContext;
	private SharedPreferences preferences;
	private final int SPLASH_DISPLAY_LENGTH = 3000;

	private GoogleCloudMessaging gcm;
	private String deviceid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		mContext = this;
		AppSession session = new AppSession(mContext);
		preferences = session.getPreferences();

		registerReceiver(mHandleMessageReceiver, new IntentFilter(PushUtils.DISPLAY_MESSAGE_ACTION));

		if (Util.checkConnectionWithoutMessage(mContext))
			deviceid = registerDeviceInBackground(PushUtils.GCMSenderId);// Add
																			// your
																			// app
																			// sender
																			// id

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				Intent intent = null;
				int userid = preferences.getInt("userid", -1);
				if (userid == -1) {
					intent = new Intent(mContext, LoginActivity.class);
					startActivity(intent);
					finish();
				} else {

					if (Util.getIsFirstTimepreference(getApplicationContext()).equals("true")) {
						Intent filterIntent = new Intent(mContext, HelpSliderActivity.class);
						filterIntent.putExtra("isFrom", "splash");
						startActivity(filterIntent);
						finish();
					} else {

						intent = new Intent(mContext, DashboardActivity.class);
						startActivity(intent);
						finish();
					}
				}

			}
		}, SPLASH_DISPLAY_LENGTH);
	}

	public final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("before wakelocker--->>>", "r");
			WakeLocker.acquire(getApplicationContext());
			Log.d("after beforeGetRegid--->>>", "r");
			WakeLocker.release();
		}
	};

	public String registerDeviceInBackground(final String SenderID) {
		gcm = GoogleCloudMessaging.getInstance(this);
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setTitle("Processing...");
		pd.setMessage("Please wait.");
		pd.setCancelable(false);
		pd.setIndeterminate(true);

		@SuppressWarnings("unchecked")
		AsyncTask<String, String, String> task = new AsyncTask() {

			protected void onPostExecute(Object result) {
				if (deviceid != null && !deviceid.equals(null)) {
					storeRegistrationId(deviceid);
				}
			};

			protected void onPreExecute() {
				// pd.show();
			};

			@Override
			protected String doInBackground(Object... params) {
				// TODO Auto-generated method stub
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
					}
					Log.i("Sender ID", SenderID);
					deviceid = gcm.register(SenderID);
					Log.v("Device id", "=================>>>>>" + deviceid);
					msg = "Device registered, registration ID=" + deviceid;

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				} catch (Exception e) {
					Log.v("Device id", "=================>>>>>Not get");
				}
				return msg;
			}
		}.execute(null, null, null);
		return deviceid;
	}

	public void storeRegistrationId(String device_id) {

		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("DEVICE_ID", device_id);
		editor.commit();
	}

	public String getDeviceId() {
		String device_id = preferences.getString("DEVICE_ID", "");
		if (device_id.equalsIgnoreCase("")) {
			Log.i("Device_id_Registration", "Registration not found.");
			return "";
		} else {
			Log.i("Device_id_Registration", "Registration Found." + device_id);
		}
		return device_id;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mHandleMessageReceiver != null) {
			unregisterReceiver(mHandleMessageReceiver);
		}
	}
}
