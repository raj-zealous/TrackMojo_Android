package com.bluewavevision.tracmojo.twitter;



import twitter4j.auth.AccessToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TwitterSession {
    private final SharedPreferences Pref;
    private final Editor editor;

    public static final String TWEET_AUTH_KEY = "emp9JMh1dMrF6060c1muLfiYk";
	public static final String TWEET_AUTH_SECRET_KEY = "pSMctANAdVqTpgnn1M1dLrXRkWXKG8q551UgR812yoA9kx8xyE";
    private static final String TWEET_USER_NAME = "user_name";
	public static final String TWITTER_CONSUMER_CALLBACK = "https://www.google.co.in/";

    public TwitterSession(Context context, String PreferenceName) {
	Pref = context.getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);

	editor = Pref.edit();
    }

    public void storeAccessToken(AccessToken accessToken, String username) {
	editor.putString(TWEET_AUTH_KEY, accessToken.getToken());
	editor.putString(TWEET_AUTH_SECRET_KEY, accessToken.getTokenSecret());
	editor.putString(TWEET_USER_NAME, username);

	editor.commit();
    }

    public void resetAccessToken() {
	editor.putString(TWEET_AUTH_KEY, null);
	editor.putString(TWEET_AUTH_SECRET_KEY, null);
	editor.putString(TWEET_USER_NAME, null);

	editor.commit();
    }

    public String getUsername() {
	return Pref.getString(TWEET_USER_NAME, "");
    }

    public AccessToken getAccessToken() {
	String token = Pref.getString(TWEET_AUTH_KEY, null);
	String tokenSecret = Pref.getString(TWEET_AUTH_SECRET_KEY, null);

	if (token != null && tokenSecret != null)
	    return new AccessToken(token, tokenSecret);
	else
	    return null;
    }
}