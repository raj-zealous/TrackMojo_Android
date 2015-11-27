package com.tracmojo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
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
import com.tracmojo.R;
import com.tracmojo.model.SocialUser;
import com.tracmojo.util.AppSession;
import com.tracmojo.util.Util;
import com.tracmojo.webservice.VolleySetup;
import com.tracmojo.webservice.VolleyStringRequest;
import com.tracmojo.webservice.Webservices;

public class RegistrationActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int PHONE_NUMBER_MIN_LIMIT = 10;

    //WEB-SERVICES CHECK SOCIAL USER
    private static final String SOCIAL_MEDIA_TYPE = "social_media_type";
    private static final String SOCIAL_USER_ID = "id";

    //WEB-SERVICES REGISTRATION PARAMETERS
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String EMAIL_ID = "email_id";
    private static final String PASSWORD = "password";
    private static final String PHONE_NUMBER = "mobile";
    private static final String DEVICE_TYPE = "device_type";
    private static final String DEVICE_ID = "device_id";

    private Context mContext;
    private TextView tvPrivacyPolicy;
    private EditText etFirstName, etLastName, etEmail, etPassword, etPhoneNumber, etCountryCode;
    private ImageView ivFacebook, ivGooglePlus, ivTwitter;
    private Button btnRegister;

    private SharedPreferences preferences;

    private RequestQueue mQueue;
    private ProgressDialog mProgress;
    private boolean isForSocialLogin;
    private String email = "", firstName = "", lastName = "", socialUserId = "";

    //facebook variables
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
    String facebook_profile_data = "", Batch_Requests="";

    //Google plus variables
    private static final int RC_SIGN_IN = 0;
    private static final int PROFILE_PIC_SIZE = 400;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private boolean isGoogleConnected = false;
    private Person googleUser;
    private String googleEmail;

    //twitter login variable
    SocialAuthAdapter adapter;
    public String providerName;
    Profile profile;
    static String TWITTER_CONSUMER_KEY = "emp9JMh1dMrF6060c1muLfiYk";
    static String TWITTER_CONSUMER_SECRET = "pSMctANAdVqTpgnn1M1dLrXRkWXKG8q551UgR812yoA9kx8xyE";

    private String socialMediaType = "";
    private SocialUser socialUser;
    private boolean isFacebookClicked;

    private boolean isSetUserCredential;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_registration);
        tvHeader.setVisibility(View.VISIBLE);
        ivLogo.setVisibility(View.INVISIBLE);
        ivHelp.setVisibility(View.GONE);
        setHeaderText(getString(R.string.activity_registration_header));
        mContext = this;
        AppSession session = new AppSession(mContext);
        preferences = session.getPreferences();

        Intent intent = getIntent();

        isForSocialLogin = intent.getBooleanExtra("isForSocialLogin", false);
        if (isForSocialLogin) {
            email = intent.getStringExtra("email");
            firstName = intent.getStringExtra("firstName");
            lastName = intent.getStringExtra("lastName");
            socialMediaType = intent.getStringExtra("socialMediaType");
            socialUserId = intent.getStringExtra("socialUserId");

        }

        mQueue = VolleySetup.getRequestQueue();

        initializeComponents();

        //facebook initailizing some fields....
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(com.facebook.Profile oldProfile,
                                                   com.facebook.Profile currentProfile) {
                // App code
                Log.e("Old ProfileTracker", "" + oldProfile);
                Log.e("Current ProfileTracker", "" + currentProfile);

                if (currentProfile != null) {
                    facebook_profile_data = "Id: " + currentProfile.getId()
                            + "\nFirstName: " + currentProfile.getFirstName()
                            + "\nMiddleName: " + currentProfile.getMiddleName()
                            + "\nLastName: " + currentProfile.getLastName()
                            + "\nName: " + currentProfile.getName()
                            + "\nLinkUri: " + currentProfile.getLinkUri()
                            + "\nImage: "
                            + currentProfile.getProfilePictureUri(800, 600);


                    String fbId = ""+currentProfile.getId();
                    String firstName = "" + currentProfile.getFirstName();
                    String lastName = "" + currentProfile.getLastName();
                    String email="";

                    socialUser = new SocialUser(fbId,firstName,lastName,email);
                    if(Util.checkConnection(mContext)) {
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
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult result) {
                        // TODO Auto-generated method stub
                        /*Toast.makeText(LoginActivity.this,
                                "You are successfully login with Facebook",
                                Toast.LENGTH_SHORT).show();*/

                        Log.e("A-Token", "=>"+result.getAccessToken());

                        AccessToken.setCurrentAccessToken(result.getAccessToken());

                        //tvFacebookLogin.setText(getResources().getString(R.string.logout));

                        com.facebook.Profile currentProfile = com.facebook.Profile.getCurrentProfile();
                        Log.e("Current Profile: ", ""+currentProfile);
                        if (currentProfile != null) {
                            facebook_profile_data = "Id: " + currentProfile.getId()
                                    + "\nFirstName: " + currentProfile.getFirstName()
                                    + "\nMiddleName: " + currentProfile.getMiddleName()
                                    + "\nLastName: " + currentProfile.getLastName()
                                    + "\nName: " + currentProfile.getName()
                                    + "\nLinkUri: " + currentProfile.getLinkUri()
                                    + "\nImage: "
                                    + currentProfile.getProfilePictureUri(800, 600);

                            Log.e("profileData",""+facebook_profile_data);
                            //tvFacebookProfile.setText(facebook_profile_data);
                        } else {
                            Log.e("profileData", "Not Data found");
                        }

                        Log.e("Profile Detail", "=>" + facebook_profile_data);
                        Log.e("Access Token","=>"+AccessToken.getCurrentAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // TODO Auto-generated method stub
                        /*Toast.makeText(LoginActivity.this,
                                "You cancel login with Facebook",
                                Toast.LENGTH_SHORT).show();*/
                    }

                    @Override
                    public void onError(FacebookException error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(mContext,
                                "Error while login with Facebook",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        //google plus initialization...
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        //twitter integration code...
        adapter = new SocialAuthAdapter(new ResponseListener());

        adapter.addProvider(SocialAuthAdapter.Provider.TWITTER, R.drawable.twitter);
        adapter.addCallBack(SocialAuthAdapter.Provider.TWITTER, "http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");

        try {
            adapter.addConfig(SocialAuthAdapter.Provider.TWITTER, TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.enable(ivTwitter, SocialAuthAdapter.Provider.TWITTER);

        if (isForSocialLogin) {
            Util.showAlertDialog(mContext, getString(R.string.app_name), getString(R.string.activity_registration_please_fill_required_detail));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    private void initializeComponents() {

        etFirstName = (EditText) findViewById(R.id.activity_registration_etFirstName);
        etLastName = (EditText) findViewById(R.id.activity_registration_etLastName);
        etEmail = (EditText) findViewById(R.id.activity_registration_etEmailAddress);
        etPassword = (EditText) findViewById(R.id.activity_registration_etPassword);
        etPhoneNumber = (EditText) findViewById(R.id.activity_registration_etPhoneNumber);
        etCountryCode = (EditText) findViewById(R.id.activity_registration_etCountryCode);
        if (!TextUtils.isEmpty(GetCountryZipCodeTelephony())) {
            etCountryCode.setText("+" + GetCountryZipCodeTelephony());
        }else if (!TextUtils.isEmpty(GetCountryZipCode())) {
            etCountryCode.setText("+" + GetCountryZipCode());
        }

        tvPrivacyPolicy = (TextView) findViewById(R.id.activity_registration_tvViewPrivacyPolicy);
        tvPrivacyPolicy.setOnClickListener(this);

        ivFacebook = (ImageView) findViewById(R.id.activity_registration_ivFacebook);
        ivGooglePlus = (ImageView) findViewById(R.id.activity_registration_ivGooglePlus);
        ivTwitter = (ImageView) findViewById(R.id.activity_registration_ivTwitter);
        ivFacebook.setOnClickListener(this);
        ivGooglePlus.setOnClickListener(this);

        btnRegister = (Button) findViewById(R.id.activity_registration__btnSubmit);
        btnRegister.setOnClickListener(this);

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

        if (isForSocialLogin) {
            etEmail.setText(email.replace("null", ""));
            etFirstName.setText(firstName.replace("null", ""));
            etLastName.setText(lastName.replace("null", ""));
            etPassword.setVisibility(View.GONE);
        }
    }

    public String GetCountryZipCode() {
        //String CountryID="";
        String CountryZipCode = "";

        //getNetworkCountryIso
        //CountryID= manager.getSimCountryIso().toUpperCase();
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

    public String GetCountryZipCodeTelephony(){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
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

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {
            case R.id.activity_registration_tvViewPrivacyPolicy:
                goToPrivacyPolicy();
                break;

            case R.id.activity_registration__btnSubmit:
                if (Util.isEditTextEmpty(etFirstName)) {
                    Util.showAlertDialog(mContext, getString(R.string.app_name), getString(R.string.activity_registration_enter_user_name));
                } else if (!Util.isAlpha(etFirstName.getText().toString().trim())) {
                    Util.showAlertDialog(mContext, getString(R.string.app_name), getString(R.string.activity_registration_enter_first_name_contains_invalid_chars));
                } else if (Util.isEditTextEmpty(etLastName)) {
                    Util.showAlertDialog(mContext, getString(R.string.app_name), getString(R.string.activity_registration_enter_last_name));
                } else if (!Util.isAlpha(etLastName.getText().toString().trim())) {
                    Util.showAlertDialog(mContext, getString(R.string.app_name), getString(R.string.activity_registration_enter_last_name_contains_invalid_chars));
                } else if (Util.isEditTextEmpty(etEmail)) {
                    Util.showAlertDialog(mContext, getString(R.string.app_name), getString(R.string.activity_login_enter_email));
                } else if (!Util.emailValidator(etEmail.getText().toString().trim())) {
                    Util.showAlertDialog(mContext, getString(R.string.app_name), getString(R.string.activity_login_enter_valid_email));
                } else if (TextUtils.isEmpty(etPassword.getText().toString()) && !isForSocialLogin) {
                    Util.showAlertDialog(mContext, getString(R.string.app_name), getString(R.string.activity_login_enter_password));
                } else if (etCountryCode.getText().toString().trim().length() == 1 && !isForSocialLogin) {
                    Util.showAlertDialog(mContext, getString(R.string.app_name), getString(R.string.activity_registration_enter_country_code));
                } else if (Util.isEditTextEmpty(etPhoneNumber) && !isForSocialLogin) {
                    Util.showAlertDialog(mContext, getString(R.string.app_name), getString(R.string.activity_registration_enter_phone_number));
                } else if (etPhoneNumber.getText().toString().length() < PHONE_NUMBER_MIN_LIMIT && !isForSocialLogin) {
                    Util.showAlertDialog(mContext, getString(R.string.app_name), getString(R.string.activity_registration_invalid_phone_number));
                } else {
                    if (isForSocialLogin) {
                        if(!Util.isEditTextEmpty(etPhoneNumber)){
                            if (etCountryCode.getText().toString().trim().length() == 1) {
                                Util.showAlertDialog(mContext, getString(R.string.app_name), getString(R.string.activity_registration_enter_country_code));
                            } else if(etPhoneNumber.getText().toString().length() < PHONE_NUMBER_MIN_LIMIT){
                                Util.showAlertDialog(mContext, getString(R.string.app_name), getString(R.string.activity_registration_invalid_phone_number));
                            } else{
                                if (Util.checkConnection(mContext))
                                    socialLogin();
                            }
                        }else {
                            if (Util.checkConnection(mContext))
                                socialLogin();
                        }
                    } else {
                        if (Util.checkConnection(mContext))
                            registerUser();
                    }

                }
                break;

            case R.id.activity_registration_ivFacebook:
                socialMediaType = "f";
                if (!isFacebookClicked) {
                    isFacebookClicked = true;
                    if (Util.checkConnection(mContext)) {

                    } else {
                        isFacebookClicked = false;
                    }
                }
                break;

            case R.id.activity_registration_ivGooglePlus:
                socialMediaType = "g";
                if (Util.checkConnection(mContext)) {
                    if (mGoogleApiClient.isConnected())
                        getGooglePlusProfileInformation();
                    else
                        signInWithGplus();
                }
                break;

            default:
                break;
        }
    }

    private void goToPrivacyPolicy() {
        Intent intent = new Intent(mContext, PrivacyPolicy.class);
        startActivity(intent);
    }

    //--- API CALLING

    private void registerUser() {

        VolleyStringRequest registerRequest = new VolleyStringRequest(
                Method.POST, Webservices.REGISTER,
                registerSuccessLisner(), registerErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(FIRST_NAME, etFirstName.getText().toString().trim());
                params.put(LAST_NAME, etLastName.getText().toString().trim());
                params.put(EMAIL_ID, etEmail.getText().toString().trim());
                params.put(PASSWORD, etPassword.getText().toString());
                params.put(PHONE_NUMBER, etCountryCode.getText().toString().trim().replace("+", "") + "-" + etPhoneNumber.getText().toString().trim());
                params.put(DEVICE_TYPE, "a");
                params.put(DEVICE_ID, preferences.getString("DEVICE_ID", ""));
                params.put(LoginActivity.TIMEZONE, Util.getTimeZoneOffSet());
                params.put(LoginActivity.TIMEZONESTRING, Util.getCurrentTimeZone());
                return params;
            }

            ;
        };

        // ***************Requesting Queue

        showProgress();
        mQueue.add(registerRequest);
    }

    private com.android.volley.Response.Listener<String> registerSuccessLisner() {
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
                        /*int userid = jsonUser.getInt("user_id");
                        preferences.edit().putInt("userid", userid).commit();*/
                        //goToDashboard();


                        String msg = "" + jsonObject.getString("message");
                        showAlertDialog(mContext,getString(R.string.app_name),msg);
                        //Util.showMessage(mContext, msg);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener registerErrorLisner() {
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

    private void socialLogin() {

        VolleyStringRequest socialLoginRequest = new VolleyStringRequest(
                Method.POST, Webservices.SOCIAL_LOGIN,
                socialLoginSuccessLisner(), registerErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(SOCIAL_MEDIA_TYPE, socialMediaType);
                params.put(SOCIAL_USER_ID, socialUserId);
                params.put(EMAIL_ID, etEmail.getText().toString().trim());
                params.put(FIRST_NAME, etFirstName.getText().toString().trim());
                params.put(LAST_NAME, etLastName.getText().toString().trim());
                params.put(PHONE_NUMBER, etCountryCode.getText().toString().trim().replace("+", "") + "-" + etPhoneNumber.getText().toString().trim());
                //params.put(PHONE_NUMBER, etCountryCode.getText().toString().trim()+etPhoneNumber.getText().toString().trim());
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
                        String email = ""+jsonUser.optString("email_id");
                        preferences.edit().putString("useremail",email).commit();
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
    	
//        Intent intent = new Intent(mContext, DashboardActivity.class);
//        startActivity(intent);
//        finish();
    }

    //---------facebook login part----------


    //google plus part

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
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
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
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                googleUser = currentPerson;
                googleEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                String googleId = "" + googleUser.getId();
                String firstName = "" + googleUser.getName().getFormatted();
                String lastName = "" + googleUser.getName().getMiddleName();
                socialUser = new SocialUser(googleId, firstName, lastName, googleEmail);
                if (Util.checkConnection(mContext)) {
                    checkSocialUser();
                }

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
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
        }
    }

    //twitter login part
    private final class ResponseListener implements DialogListener {
        @Override
        public void onComplete(Bundle values) {

            socialMediaType = "t";
            // Variable to receive message status
            Log.d("twt Demo", "Authentication Successful");

            // Get name of provider after authentication
            providerName = values.getString(SocialAuthAdapter.PROVIDER);
            Log.d("twt Demo", "Provider Name = " + providerName);
            //Toast.makeText(LoginActivity.this, providerName + " connected", Toast.LENGTH_SHORT).show();

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

                /*if(!TextUtils.isEmpty(profile.getValidatedId()))
                    tvId.setText("Twitter Id : " + profile.getValidatedId());
                if(!TextUtils.isEmpty(profile.getFirstName()))
                    tvFirstName.setText("First Name : "+profile.getFirstName());
                if(!TextUtils.isEmpty(profile.getLastName()))
                    tvLastName.setText("Last Name : "+profile.getLastName());
                if(!TextUtils.isEmpty(profile.getDisplayName()))
                    tvDisplayName.setText("DisplayName : "+profile.getDisplayName() );
                if(!TextUtils.isEmpty(profile.getEmail()))
                    tvEmail.setText("Email : "+profile.getEmail());
                else
                    tvEmail.setText("Email Not Found");

                if(profile.getDob()!=null){
                    tvBirthday.setText("BirthDay : "+profile.getDob());
                }
                if(!TextUtils.isEmpty(profile.getGender()))
                    tvGender.setText("Gender : "+profile.getGender());

                if(!TextUtils.isEmpty(profile.getProfileImageURL())){
                    tvProfileLink.setText("User Profile Link : "+profile.getProfileImageURL());
                    Log.e("url	", ":"+profile.getProfileImageURL().replace("_normal", ""));
                    Map<String, String> contactInfo = profile.getContactInfo();
                    if(contactInfo!=null){
                        Log.e("contact info size	", ":"+contactInfo.size());
                    }
                }*/
            }

            boolean status = adapter.signOut(RegistrationActivity.this, providerName);
            Log.e("status", ":" + status);

            //btnLogout.setVisibility(View.VISIBLE);
            //btnUserInfo.setVisibility(View.VISIBLE);
            //btnLogin.setVisibility(View.GONE);
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

    private void checkSocialUser() {

        VolleyStringRequest checkSocialUserRequest = new VolleyStringRequest(
                Method.POST, Webservices.CHECK_SOCIAL_USER,
                checkSocialUserSuccessLisner(), registerErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
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
                            if (TextUtils.isEmpty(socialUser.getEmail()) || TextUtils.isEmpty(socialUser.getFirstName()) || TextUtils.isEmpty(socialUser.getLastName())
                                    || socialUser.getEmail().equalsIgnoreCase("null") || socialUser.getFirstName().equalsIgnoreCase("null") || socialUser.getLastName().equalsIgnoreCase("null")) {
                                //redirect to register screen.
                                Log.e("Email", "Need to register");
                                isForSocialLogin = true;
                                etEmail.setText(socialUser.getEmail().replace("null", ""));
                                etFirstName.setText(socialUser.getFirstName().replace("null", ""));
                                etLastName.setText(socialUser.getLastName().replace("null", ""));
                                socialUserId = socialUser.getSocialId();
                                etPassword.setEnabled(false);

                            } else {
                                //call the social login api
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

    public void showAlertDialog(Context context,String title,String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(title);


        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        Intent intent = new Intent();
                        intent.putExtra("email",etEmail.getText().toString().trim());
                        intent.putExtra("password",etPassword.getText().toString().trim());
                        setResult(RESULT_OK,intent);
                        finish();
                        dialog.cancel();
                    }
                })/*.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				})*/;

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
