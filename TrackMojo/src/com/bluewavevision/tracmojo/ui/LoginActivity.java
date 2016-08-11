package com.bluewavevision.tracmojo.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.User;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.bluewavevision.tracmojo.R;
import com.bluewavevision.tracmojo.model.SocialUser;
import com.bluewavevision.tracmojo.twitter.Twitt_Sharing;
import com.bluewavevision.tracmojo.twitter.TwitterSession;
import com.bluewavevision.tracmojo.util.AppSession;
import com.bluewavevision.tracmojo.util.Util;
import com.bluewavevision.tracmojo.webservice.VolleySetup;
import com.bluewavevision.tracmojo.webservice.VolleyStringRequest;
import com.bluewavevision.tracmojo.webservice.Webservices;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class LoginActivity extends Activity
		implements OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	public static final int CODE_FOR_REGISTER = 0x143;

	// WEB-SERVICES LOGIN PARAMETERS
	private static final String EMAIL_ID = "email_id";
	private static final String PASSWORD = "password";
	private static final String DEVICE_TYPE = "device_type";
	private static final String DEVICE_ID = "device_id";
	public static final String TIMEZONE = "timezone";
	public static final String TIMEZONESTRING = "timezone_string";

	// WEB-SERVICES LOGIN PARAMETERS
	private static final String FORGOT_EMAIL_ID = "email_id";

	// WEB-SERVICES CHECK SOCIAL USER
	private static final String SOCIAL_MEDIA_TYPE = "social_media_type";
	private static final String SOCIAL_USER_ID = "id";

	// WEB-SERVICES SOCIAL LOGIN
	private static final String FIRST_NAME = "first_name";
	private static final String LAST_NAME = "last_name";

	private Context mContext;
	private EditText etEmail, etPassword;
	private TextView tvForgotPassword;
	private Button btnLogin, btnCreateNewAccount;
	private ImageView ivFacebook, ivGooglePlus, ivTwitter;

	private SharedPreferences preferences;
	private RequestQueue mQueue;
	private ProgressDialog mProgress;

	// facebook variables
	private static final List<String> PERMISSIONS = new ArrayList<String>() {
		{
			add("user_friends");
			add("public_profile");
			add("email");
			add("user_birthday");
		}
	};
	private static final int FACEBOOK_SIGN_IN = 64206;
	private LoginManager loginManager;
	CallbackManager callbackManager;
	ProfileTracker profileTracker;
	AccessTokenTracker accessTokenTracker;
	String facebook_profile_data = "", Batch_Requests = "";

	// Google plus variables
	private static final int RC_SIGN_IN = 0;
	private static final int PROFILE_PIC_SIZE = 400;
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	private ConnectionResult mConnectionResult;
	private boolean isGoogleConnected = false;
	private Person googleUser;
	private String googleEmail;

	// twitter login variable
	SocialAuthAdapter adapter;
	public String providerName;
	Profile profile;
	static String TWITTER_CONSUMER_KEY = "emp9JMh1dMrF6060c1muLfiYk";
	static String TWITTER_CONSUMER_SECRET = "pSMctANAdVqTpgnn1M1dLrXRkWXKG8q551UgR812yoA9kx8xyE";
	Twitt_Sharing twitter;
	private ProgressDialog mSpinner;

	private String socialMediaType = "";
	private SocialUser socialUser;
	private boolean isFacebookClicked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		FacebookSdk.sdkInitialize(getApplicationContext());
		callbackManager = CallbackManager.Factory.create();

		setContentView(R.layout.activity_login);
		mContext = this;

		twitter = new Twitt_Sharing(LoginActivity.this, TwitterSession.TWEET_AUTH_KEY,
				TwitterSession.TWEET_AUTH_SECRET_KEY, TwitterSession.TWITTER_CONSUMER_CALLBACK);

		mSpinner = new ProgressDialog(mContext);
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading...");

		AppSession session = new AppSession(mContext);
		preferences = session.getPreferences();

		mQueue = VolleySetup.getRequestQueue();

		initializeComponents();

		// facebook initailizing some fields....

		AccessToken.getCurrentAccessToken();
		FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
		profileTracker = new ProfileTracker() {
			@Override
			protected void onCurrentProfileChanged(com.facebook.Profile oldProfile,
					com.facebook.Profile currentProfile) {
				// App code
				Log.e("Old ProfileTracker", "" + oldProfile);
				Log.e("Current ProfileTracker", "" + currentProfile);

				if (currentProfile != null) {
					facebook_profile_data = "Id: " + currentProfile.getId() + "\nFirstName: "
							+ currentProfile.getFirstName() + "\nMiddleName: " + currentProfile.getMiddleName()
							+ "\nLastName: " + currentProfile.getLastName() + "\nName: " + currentProfile.getName()
							+ "\nLinkUri: " + currentProfile.getLinkUri() + "\nImage: "
							+ currentProfile.getProfilePictureUri(800, 600);

					String fbId = "" + currentProfile.getId();
					String firstName = "" + currentProfile.getFirstName();
					String lastName = "" + currentProfile.getLastName();
					String email = "";

					socialUser = new SocialUser(fbId, firstName, lastName, email);
					if (Util.checkConnection(mContext)) {
						isFacebookClicked = false;
						checkSocialUser();
					}

					Log.e("Profile Detail", "profiletracker=>" + facebook_profile_data);
				} else {
					Log.e("profileData", "profiletracker Not Data found");
				}

				Log.e("Profile Detail", "profiletracker =>" + facebook_profile_data);
			}
		};

		AccessToken.getCurrentAccessToken();
		FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
		LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

			@Override
			public void onSuccess(LoginResult result) {
				// TODO Auto-generated method stub
				/*
				 * Toast.makeText(LoginActivity.this,
				 * "You are successfully login with Facebook",
				 * Toast.LENGTH_SHORT).show();
				 */

				Log.e("A-Token", "=>" + result.getAccessToken());

				AccessToken.setCurrentAccessToken(result.getAccessToken());

				// tvFacebookLogin.setText(getResources().getString(R.string.logout));

				com.facebook.Profile currentProfile = com.facebook.Profile.getCurrentProfile();
				Log.e("Current Profile: ", "" + currentProfile);
				if (currentProfile != null) {
					facebook_profile_data = "Id: " + currentProfile.getId() + "\nFirstName: "
							+ currentProfile.getFirstName() + "\nMiddleName: " + currentProfile.getMiddleName()
							+ "\nLastName: " + currentProfile.getLastName() + "\nName: " + currentProfile.getName()
							+ "\nLinkUri: " + currentProfile.getLinkUri() + "\nImage: "
							+ currentProfile.getProfilePictureUri(800, 600);

					Log.e("profileData", "" + facebook_profile_data);
					// tvFacebookProfile.setText(facebook_profile_data);
				} else {
					Log.e("profileData", "Not Data found");
				}

				Log.e("Profile Detail", "=>" + facebook_profile_data);
				Log.e("Access Token", "=>" + AccessToken.getCurrentAccessToken());
			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				/*
				 * Toast.makeText(LoginActivity.this,
				 * "You cancel login with Facebook", Toast.LENGTH_SHORT).show();
				 */
			}

			@Override
			public void onError(FacebookException error) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, "Error while login with Facebook", Toast.LENGTH_SHORT).show();
			}
		});

		// google plus initialization...
		mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN).build();

		// twitter integration code...
		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(SocialAuthAdapter.Provider.TWITTER, R.drawable.twitter);
		adapter.addCallBack(SocialAuthAdapter.Provider.TWITTER,
				"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");

		try {
			adapter.addConfig(SocialAuthAdapter.Provider.TWITTER, TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// adapter.enable(ivTwitter, SocialAuthAdapter.Provider.TWITTER);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		profileTracker.stopTracking();
	}

	private void initializeComponents() {
		etEmail = (EditText) findViewById(R.id.activity_login_etEmailAddress);
		etPassword = (EditText) findViewById(R.id.activity_login_etPassword);

		tvForgotPassword = (TextView) findViewById(R.id.activity_login_tvForgotPassword);
		tvForgotPassword.setOnClickListener(this);

		btnLogin = (Button) findViewById(R.id.activity_login_btnSubmit);
		btnCreateNewAccount = (Button) findViewById(R.id.activity_login_btnCreateNewAccount);
		btnLogin.setOnClickListener(this);
		btnCreateNewAccount.setOnClickListener(this);

		ivFacebook = (ImageView) findViewById(R.id.activity_login_ivFacebook);
		ivGooglePlus = (ImageView) findViewById(R.id.activity_login_ivGooglePlus);
		ivTwitter = (ImageView) findViewById(R.id.activity_login_ivTwitter);
		ivTwitter.setOnClickListener(this);
		ivFacebook.setOnClickListener(this);
		ivGooglePlus.setOnClickListener(this);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		boolean ret = false;
		try {
			View view = getCurrentFocus();
			ret = super.dispatchTouchEvent(event);

			if (view instanceof EditText) {
				View w = getCurrentFocus();
				int scrcoords[] = new int[2];
				w.getLocationOnScreen(scrcoords);
				float x = event.getRawX() + w.getLeft() - scrcoords[0];
				float y = event.getRawY() + w.getTop() - scrcoords[1];

				if (event.getAction() == MotionEvent.ACTION_UP
						&& (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
				}
			}
			return ret;
		} catch (Exception e) {
			Log.e("dispatchevent", e.toString());
			return ret;
		}
	}

	private void showForgotPasswordDialog() {
		final Dialog dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_forgot_password);

		Button btnSend = (Button) dialog.findViewById(R.id.fogot_pass_btn_send);
		Button btnCancel = (Button) dialog.findViewById(R.id.fogot_pass_btn_cancel);

		final EditText etForgotEmail = (EditText) dialog.findViewById(R.id.fogot_pass_et_email);

		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				if (Util.isEditTextEmpty(etForgotEmail)) {
					imm.hideSoftInputFromWindow(etForgotEmail.getWindowToken(), 0);
					Util.showMessage(mContext, getString(R.string.activity_login_enter_email));
				} else if (!Util.emailValidator(etForgotEmail.getText().toString().trim())) {
					imm.hideSoftInputFromWindow(etForgotEmail.getWindowToken(), 0);
					Util.showMessage(mContext, getString(R.string.activity_login_enter_valid_email));
				} else {
					imm.hideSoftInputFromWindow(etForgotEmail.getWindowToken(), 0);
					dialog.dismiss();

					if (Util.checkConnection(mContext)) {
						forgotPassword(etForgotEmail.getText().toString().trim());
					}
				}

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		});

		dialog.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.activity_login_tvForgotPassword:
			showForgotPasswordDialog();
			break;

		case R.id.activity_login_btnCreateNewAccount:
			startRegistrationActivity();
			break;

		case R.id.activity_login_btnSubmit:
			if (Util.isEditTextEmpty(etEmail)) {
				Util.showAlertDialog(mContext, getString(R.string.app_name),
						getString(R.string.activity_login_enter_email));
			} else if (!Util.emailValidator(etEmail.getText().toString().trim())) {
				Util.showAlertDialog(mContext, getString(R.string.app_name),
						getString(R.string.activity_login_enter_valid_email));
			} else if (TextUtils.isEmpty(etPassword.getText().toString())) {
				Util.showAlertDialog(mContext, getString(R.string.app_name),
						getString(R.string.activity_login_enter_password));
			} else {
				if (Util.checkConnection(mContext))
					login();
			}
			break;

		case R.id.activity_login_ivFacebook:
			socialMediaType = "f";
			// if(!isFacebookClicked) {
			isFacebookClicked = true;
			if (Util.checkConnection(mContext)) {
				if (AccessToken.getCurrentAccessToken() != null) {
					LoginManager.getInstance().logOut();
					// tvFacebookLogin.setText(getResources().getString(R.string.facebook_login));
				} else {
				}
				LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
						Arrays.asList("public_profile", "user_friends"));

			} else {
				isFacebookClicked = false;
			}
			// }
			break;

		case R.id.activity_login_ivGooglePlus:
			socialMediaType = "g";
			if (Util.checkConnection(mContext)) {
				if (mGoogleApiClient.isConnected())
					getGooglePlusProfileInformation();
				else
					signInWithGplus();
			}
			break;

		case R.id.activity_login_ivTwitter:
			socialMediaType = "t";
			if (Util.checkConnection(mContext)) {
				twitter.Login(new Twitt_Sharing.LoginListner() {
					@Override
					public void OnSuccessfulLogin(User mUser) {
						Log.e("Twitter", "" + mUser.getName());
						String twitterId = "" + mUser.getId();
						String firstName = "" + mUser.getName();

						socialUser = new SocialUser(twitterId, firstName, "", "");
						if (Util.checkConnection(mContext)) {
							checkSocialUser();
						}
					}

					@Override
					public void OnError(Exception e) {

					}

					@Override
					public void OnPreLogin() {

					}
				});
			}
			break;
		default:
			break;
		}
	}

	private void setUserCredential(String email, String password) {
		etEmail.setText("" + email);
		etPassword.setText("" + password);
	}

	private void startRegistrationActivity() {
		Intent intent = new Intent(mContext, RegistrationActivity.class);
		startActivityForResult(intent, CODE_FOR_REGISTER);
	}
	// --- API CALLING

	private void checkSocialUser() {

		VolleyStringRequest checkSocialUserRequest = new VolleyStringRequest(Method.POST, Webservices.CHECK_SOCIAL_USER,
				checkSocialUserSuccessLisner(), loginErrorLisner()) {

			@Override
			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put(SOCIAL_MEDIA_TYPE, socialMediaType);
				params.put(SOCIAL_USER_ID, socialUser.getSocialId());
				params.put(DEVICE_TYPE, "a");
				params.put(DEVICE_ID, preferences.getString("DEVICE_ID", ""));
				return params;
			}

			;
		};

		// ***************Requesting Queue

		showProgress();
		mQueue.add(checkSocialUserRequest);
	}

	private void socialLogin() {

		VolleyStringRequest socialLoginRequest = new VolleyStringRequest(Method.POST, Webservices.SOCIAL_LOGIN,
				socialLoginSuccessLisner(), loginErrorLisner()) {

			@Override
			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put(SOCIAL_MEDIA_TYPE, socialMediaType);
				params.put(SOCIAL_USER_ID, socialUser.getSocialId());
				params.put(EMAIL_ID, socialUser.getEmail());
				params.put(FIRST_NAME, socialUser.getFirstName());
				params.put(LAST_NAME, socialUser.getLastName());
				params.put(DEVICE_TYPE, "a");
				params.put(DEVICE_ID, preferences.getString("DEVICE_ID", ""));
				params.put(LoginActivity.TIMEZONE, Util.getTimeZoneOffSet());
				params.put(LoginActivity.TIMEZONESTRING, Util.getCurrentTimeZone());
				return params;
			}

		};

		// ***************Requesting Queue

		showProgress();
		mQueue.add(socialLoginRequest);
	}

	private void login() {

		VolleyStringRequest loginRequest = new VolleyStringRequest(Method.POST, Webservices.LOGIN, loginSuccessLisner(),
				loginErrorLisner()) {

			@Override
			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put(EMAIL_ID, etEmail.getText().toString().trim());
				params.put(PASSWORD, etPassword.getText().toString());
				params.put(DEVICE_TYPE, "a");
				params.put(DEVICE_ID, preferences.getString("DEVICE_ID", ""));
				params.put(DEVICE_TYPE, "a");
				params.put(TIMEZONE, Util.getTimeZoneOffSet());
				params.put(TIMEZONESTRING, Util.getCurrentTimeZone());
				return params;
			}

			;
		};

		// ***************Requesting Queue

		showProgress();
		mQueue.add(loginRequest);
	}

	private void forgotPassword(final String email) {
		VolleyStringRequest forgotPasswordRequest = new VolleyStringRequest(Method.POST, Webservices.FORGOT_PASSWORD,
				forgotPasswordSuccessLisner(), loginErrorLisner()) {

			@Override
			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();

				params.put(FORGOT_EMAIL_ID, "" + email);
				return params;
			}

			;
		};

		// ***************Requesting Queue

		showProgress();
		mQueue.add(forgotPasswordRequest);
	}

	private com.android.volley.Response.Listener<String> forgotPasswordSuccessLisner() {
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
						responseMessage = jsonObject.optString("message");
						Util.showMessage(mContext, responseMessage);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	private com.android.volley.Response.Listener<String> checkSocialUserSuccessLisner() {
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
						boolean isExist = jsonObject.getBoolean("is_exist");
						if (isExist) {
							Log.e("Login", "Success");
							JSONObject jsonUser = jsonObject.getJSONObject("User");
							int userid = jsonUser.getInt("user_id");
							preferences.edit().putInt("userid", userid).commit();
							goToDashboard();
							String msg = "" + jsonObject.getString("message");
							Util.showMessage(mContext, msg);
						} else {
							if (TextUtils.isEmpty(socialUser.getEmail()) || TextUtils.isEmpty(socialUser.getFirstName())
									|| TextUtils.isEmpty(socialUser.getLastName())
									|| socialUser.getEmail().equalsIgnoreCase("null")
									|| socialUser.getFirstName().equalsIgnoreCase("null")
									|| socialUser.getLastName().equalsIgnoreCase("null")) {
								// redirect to register screen.
								Log.e("Email", "Need to register");
								Intent intent = new Intent(mContext, RegistrationActivity.class);
								intent.putExtra("isForSocialLogin", true);
								intent.putExtra("email", socialUser.getEmail());
								intent.putExtra("firstName", socialUser.getFirstName());
								intent.putExtra("lastName", socialUser.getLastName());
								intent.putExtra("socialUserId", socialUser.getSocialId());
								intent.putExtra("socialMediaType", socialMediaType);
								startActivity(intent);
							} else {
								// call the social login api
								if (Util.checkConnection(mContext)) {
									socialLogin();
								}
							}
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	private com.android.volley.Response.Listener<String> socialLoginSuccessLisner() {
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
						JSONObject jsonUser = jsonObject.getJSONObject("User");
						int userid = jsonUser.getInt("user_id");
						String email = "" + jsonUser.optString("email_id");
						preferences.edit().putString("useremail", email).commit();
						preferences.edit().putInt("userid", userid).commit();
						goToDashboard();

						String msg = "" + jsonObject.getString("message");
						Util.showMessage(mContext, msg);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	private com.android.volley.Response.Listener<String> loginSuccessLisner() {
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
						JSONObject jsonUser = jsonObject.getJSONObject("User");
						int userid = jsonUser.getInt("user_id");
						String email = "" + jsonUser.optString("email_id");
						preferences.edit().putString("useremail", email).commit();
						preferences.edit().putInt("userid", userid).commit();
						goToDashboard();

						String msg = "" + jsonObject.getString("message");
						// Util.showMessage(mContext,msg);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	private com.android.volley.Response.ErrorListener loginErrorLisner() {
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

	// ---------facebook login part----------

	// google plus part

	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		mSignInClicked = false;
		if (isGoogleConnected)
			getGooglePlusProfileInformation();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				resolveSignInError();
			}
		}
	}

	private void getGooglePlusProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
				googleUser = currentPerson;
				googleEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
				String googleId = "" + googleUser.getId();
				String firstName = "" + googleUser.getName().getGivenName();
				String lastName = "" + googleUser.getName().getFamilyName();
				socialUser = new SocialUser(googleId, firstName, lastName, googleEmail);
				if (Util.checkConnection(mContext)) {
					checkSocialUser();
				}

			} else {
				Toast.makeText(getApplicationContext(), "Person information is null", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to resolve any signin errors
	 */
	private void resolveSignInError() {
		if (mConnectionResult != null) {
			if (mConnectionResult.hasResolution()) {
				try {
					mIntentInProgress = true;
					mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
				} catch (IntentSender.SendIntentException e) {
					mIntentInProgress = false;
					mGoogleApiClient.connect();
				}
			}
		}
	}

	private void signInWithGplus() {
		if (!mGoogleApiClient.isConnected()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
		if (requestCode == FACEBOOK_SIGN_IN) {

			Log.d("token request code ", "" + requestCode);
		} else if (requestCode == RC_SIGN_IN) {
			if (resultCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				isGoogleConnected = true;
				mGoogleApiClient.connect();
			} else {
				getGooglePlusProfileInformation();
			}
		} else if (requestCode == CODE_FOR_REGISTER) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					String email = "" + data.getStringExtra("email");
					String password = "" + data.getStringExtra("password");
					setUserCredential(email, password);
				}
			}
		}
	}

	// twitter login part
	private final class ResponseListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {

			socialMediaType = "t";
			// Variable to receive message status
			Log.d("twt Demo", "Authentication Successful");

			// Get name of provider after authentication
			providerName = values.getString(SocialAuthAdapter.PROVIDER);
			Log.d("twt Demo", "Provider Name = " + providerName);
			// Toast.makeText(LoginActivity.this, providerName + " connected",
			// Toast.LENGTH_SHORT).show();

			if (Util.checkConnection(mContext)) {
				profile = adapter.getUserProfile();
				Log.e("name", ":" + profile.getValidatedId());

				String twitterId = "" + profile.getValidatedId();
				String firstName = "" + profile.getFirstName();
				String lastName = "" + profile.getLastName();
				String email = "" + profile.getEmail();
				socialUser = new SocialUser(twitterId, firstName, lastName, email);
				if (Util.checkConnection(mContext)) {
					checkSocialUser();
				}
			}

			boolean status = adapter.signOut(LoginActivity.this, providerName);
		}

		@Override
		public void onError(SocialAuthError error) {
			error.printStackTrace();
			Log.d("Share-Bar", error.getMessage());
			adapter.setClicked(false);
		}

		@Override
		public void onCancel() {
			Log.d("Share-Bar", "Authentication Cancelled");
			adapter.setClicked(false);
		}

		@Override
		public void onBack() {
			Log.d("Share-Bar", "Dialog Closed by pressing Back Key");
			adapter.setClicked(false);
		}
	}

	private void goToDashboard() {

		if (Util.getIsFirstTimepreference(getApplicationContext()).equals("true")) {
			Intent filterIntent = new Intent(mContext, HelpSliderActivity.class);
			filterIntent.putExtra("isFrom", "splash");
			startActivity(filterIntent);
			finish();
		} else {

			Intent intent = new Intent(mContext, DashboardActivity.class);
			startActivity(intent);
			finish();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		int userid = preferences.getInt("userid", -1);
		if (userid != -1) {
			finish();
		}
	}
}
