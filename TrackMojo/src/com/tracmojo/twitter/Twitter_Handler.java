package com.tracmojo.twitter;

import java.net.MalformedURLException;
import java.net.URL;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class Twitter_Handler {
	public static Twitter twitterObj;
	private final TwitterSession mSession;
	private AccessToken mAccessToken;
	private static String mConsumerKey;
	private static String mSecretKey;
	private RequestToken requestToken = null;
	private final Activity context;
	private String Preferencename = "TwitterIntegration";
	private TwitterAuthListner authlistner;

	public static  String mCallback_url = "";
	public Twitter_Handler(Activity context, String consumerKey,
			String secretKey, String callbackurl) {
		this.context = context;

		twitterObj = new TwitterFactory().getInstance();
		mSession = new TwitterSession(context, Preferencename);

		mConsumerKey = consumerKey;
		mSecretKey = secretKey;
		mCallback_url =  callbackurl;
		mAccessToken = mSession.getAccessToken();
		configureToken();
	}
	
	
	public void setAuthListner(TwitterAuthListner listner){
		this.authlistner = listner;
	}


	private void configureToken() {
		if (mAccessToken != null) {
			twitterObj.setOAuthConsumer(mConsumerKey, mSecretKey);
			twitterObj.setOAuthAccessToken(mAccessToken);
		}else{
			twitterObj.setOAuthConsumer(mConsumerKey, mSecretKey);
		}
	}

	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}
	public AccessToken  getAccessToken() {
		return mAccessToken; 
	}

	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();
			mAccessToken = null;
		}
	}

	public String getUsername() {
		return mSession.getUsername();
	}

	public void updateStatus(String status) throws Exception {
		try {
			twitterObj.updateStatus(status);
		} catch (TwitterException e) {
			throw e;
		}
	}

	public Twitter getTwitterObject(){
		return this.twitterObj;
	}
	
	
	public void authorize(){
		new GetTwitterTocken().execute();
	}
		private class GetTwitterTocken extends AsyncTask<String, String, String> {

			TwitterException exeption;
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}

			@Override
			protected String doInBackground(String... args) {
				String authUrl = null;
				try {	
					requestToken = twitterObj.getOAuthRequestToken();
					authUrl = requestToken.getAuthorizationURL();
			
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					exeption = e; 
				}
				return authUrl;
			}

			@Override
			protected void onPostExecute(String oauth_url) {
				if (oauth_url != null) {
					Log.e("URL", oauth_url);
					showLoginDialog(oauth_url);		
				} else {
					if(authlistner!=null){
						authlistner.OnErrorListener(exeption);
					}
				}
			}
		}


		private void showLoginDialog(String url) {
			TwDialogListener listener = new TwDialogListener() {
				
				@Override
				public void onComplete(String value) {
					new AccessTokenGet(value).execute();
				}

				@Override
				public void onError(Exception e) {
					if(authlistner!=null){
						authlistner.OnErrorListener(e);
					}
				}
			};

			new TwitterDialog(context, url,mCallback_url, listener).show();
		}

		
		private class AccessTokenGet extends AsyncTask<String, String, Boolean> {

			String callback=null,varifier =null;
			User user;
			TwitterException exception;
			public AccessTokenGet(String url) {
				// TODO Auto-generated constructor stub
				callback = url;
			}
			
			@Override
			protected void onPreExecute() {
				varifier = getVerifier(callback);
				super.onPreExecute();

			}

			@Override
			protected Boolean doInBackground(String... args) {

				boolean isGetData = false;

				try {

					mAccessToken = twitterObj.getOAuthAccessToken(requestToken,
							varifier);
					twitterObj.setOAuthAccessToken(mAccessToken);
					user = twitterObj.verifyCredentials();
					mSession.storeAccessToken(mAccessToken, user.getName());
					isGetData = true;
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					exception = e;
					e.printStackTrace();
					isGetData = false;
				}

				return isGetData;
			}

			@Override
			protected void onPostExecute(Boolean response) {
				if(response){
					if(authlistner!=null){
						authlistner.OnAuthenticated(user);
					}
				}else{
					if(authlistner!=null){
						authlistner.OnErrorListener(exception);
					}
				}
			}

		}	
		

		

	private String getVerifier(String callbackUrl) {
		String verifier = "";

		try {
			callbackUrl = callbackUrl.replace("twitterapp", "http");

			URL url = new URL(callbackUrl);
			String query = url.getQuery();

			String array[] = query.split("&");

			for (String parameter : array) {
				String v[] = parameter.split("=");

				/*if (URLDecoder.decode(v[0]).equals(
						oauth.signpost.OAuth.OAUTH_VERIFIER)) {
					verifier = URLDecoder.decode(v[1]);
					break;
				}*/
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return verifier;
	}

	
	
	public interface TwitterAuthListner{
		public void OnAuthenticated(User mUser);
		public void OnErrorListener(Exception e);
	}
	


	public interface TwDialogListener {
		public void onComplete(String value);

		public void onError(Exception e);
	}

}