package com.bluewavevision.tracmojo.twitter;

import java.io.File;

import twitter4j.PagableResponseList;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.User;
import android.app.Activity;
import android.os.AsyncTask;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;


public class Twitt_Sharing {

	private Twitter_Handler mTwitter;
	private final Activity activity;
	private File ImageFile;
	private LoginListner mLoginListner;
	private PostTwittListner mPostTwittListner;
	private PostImageListener mPostImageListener;
	private FollowerListListener mFollowerListListener;
	private SendDirectMessageListener mSendDirectMessageListener;

	public static String CONSUMER_SECRET="";
	public static String CONSUMER_KEY="";
	public static String CALLBACK_URL="";
	public static String strLoadingMessage= null;
	
	public Twitt_Sharing(Activity act, String consumer_key,
			String consumer_secret, String Callback_url) {
		this.activity = act;
		CONSUMER_KEY = consumer_key;
		CONSUMER_SECRET =  consumer_secret;
		CALLBACK_URL = Callback_url;
		
		mTwitter = new Twitter_Handler(activity, consumer_key, consumer_secret, Callback_url);
	}

	
	public void setLoadingMessage(String Text){
		strLoadingMessage =  Text;
	}
	
	
	
	//**************Login Code************//
	
	public void Login(LoginListner listner){
		
		this.mLoginListner = listner;
		
		if(mLoginListner!=null){
			mLoginListner.OnPreLogin();
		}
		
		//Checking for AccessTocken
		if (mTwitter.hasAccessToken()) {
			
			//Has Access Tocken and Perform Login Process
			PerformLogin();
		} else {
			//Authorize Twitter object for Getting Access Tocken
			mTwitter = new Twitter_Handler(activity, CONSUMER_KEY, CONSUMER_SECRET, CALLBACK_URL);
			mTwitter.authorize();
			mTwitter.setAuthListner(new Twitter_Handler.TwitterAuthListner() {
				
				@Override
				public void OnErrorListener(Exception e) {
					// TODO Auto-generated method stub
					if(mLoginListner!=null){
						mLoginListner.OnError(e);
					}
				}
				
				@Override
				public void OnAuthenticated(User mUser) {
					// TODO Auto-generated method stub
					PerformLogin();
				}
			});
		}
	}
	
	
	//Logging in User
	private void PerformLogin(){
		new AsyncTask<Void, Void, Void>() {
			User user;
			TwitterException exception;
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					user = mTwitter.getTwitterObject().showUser(mTwitter.getAccessToken().getUserId());
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					exception = e;
				}
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				if(user!=null){
					if(mLoginListner!=null){
						mLoginListner.OnSuccessfulLogin(user);
					}
				}else{
					if(mLoginListner!=null){
						mLoginListner.OnError(exception);
					}
				}
				super.onPostExecute(result);
			}
		}.execute();
	}
	
	//************************************ Login Code Ends Here***************************//
	
	
	
	//************************************Posting Image Code Starts Here************//
	
	public void PostImageOnTwitter(final File image, final String msg, PostImageListener listener) {
		
		this.mPostImageListener = listener;
		this.ImageFile =  image;
		if(mPostImageListener!=null){
			mPostImageListener.OnPreExecute();
		}
		
		//Checking for AccessTocken
		if (mTwitter.hasAccessToken()) {
			
			//Has Access Tocken and Perform Posting
			new PostImageTwittTask().execute(msg);
		} else {
			//Authorize Twitter object for Getting Access Tocken
			mTwitter = new Twitter_Handler(activity, CONSUMER_KEY, CONSUMER_SECRET, CALLBACK_URL);
			mTwitter.authorize();
			mTwitter.setAuthListner(new Twitter_Handler.TwitterAuthListner() {
				
				@Override
				public void OnErrorListener(Exception e) {
					// TODO Auto-generated method stub
					if(mPostImageListener!=null){
						mPostImageListener.OnError(e);
					}
				}
				
				@Override
				public void OnAuthenticated(User mUser) {
					// TODO Auto-generated method stub
					new PostTwittTask().execute(msg);
				}
			});
		}
	}
	
	class PostImageTwittTask extends AsyncTask<String, Void, Boolean> {
		
		TwitterException exception;
	
		@Override
		protected Boolean doInBackground(String... twitt) {
			
			try {
				StatusUpdate st = new StatusUpdate(twitt[0]);
				//st.setMedia(twitt[1], ImageStream);
				st.setMedia(ImageFile);
				mTwitter.twitterObj.updateStatus(st);				
				return true;
			} catch (TwitterException e) {
				exception = e;
				return false;
			} catch (Exception e) {
				// TODO: handle exception
				exception = (TwitterException)e;
				return false;
			}
			
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				if(mPostImageListener!=null){
					mPostImageListener.OnSuccessfulPost();
				}
			}else{
				if(mPostImageListener!=null){
					mPostImageListener.OnError(exception);
				}
			}
			ImageFile = null;
			
			super.onPostExecute(result);
		}
	}

	//************************************Posting Image Code EndsHere************//
	

	
	//************************************Posting Tweet Code Starts Here************//
	
		public void PostTwittOnTwitter(final String msg, PostTwittListner listener) {
			
			this.mPostTwittListner = listener;
			
			if(mPostTwittListner!=null){
				mPostTwittListner.OnPreExecute();
			}
			
			//Checking for AccessTocken
			if (mTwitter.hasAccessToken()) {
				
				//Has Access Tocken and Perform Posting
				new PostTwittTask().execute(msg);
			} else {
				//Authorize Twitter object for Getting Access Tocken
				
				mTwitter.authorize();
				mTwitter.setAuthListner(new Twitter_Handler.TwitterAuthListner() {
					
					@Override
					public void OnErrorListener(Exception e) {
						// TODO Auto-generated method stub
						if(mPostTwittListner!=null){
							mPostTwittListner.OnError(e);
						}
					}
					
					@Override
					public void OnAuthenticated(User mUser) {
						// TODO Auto-generated method stub
						new PostTwittTask().execute(msg);
					}
				});
			}
		}
		
		class PostTwittTask extends AsyncTask<String, Void, Boolean> {
			
			TwitterException exception;
		
			@Override
			protected Boolean doInBackground(String... twitt) {
				
				try{
					
					StatusUpdate st = new StatusUpdate(twitt[0]);
					mTwitter.twitterObj.updateStatus(st);
					return true;
					
				} catch (TwitterException e) {
					exception = e;
					return false;
				}
				
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if(result){
					if(mPostTwittListner!=null){
						mPostTwittListner.OnSuccessfulShare();
					}
				}else{
					if(mPostTwittListner!=null){
						mPostTwittListner.OnError(exception);
					}
				}
				
				super.onPostExecute(result);
			}
		}

		//************************************Posting Tweet Code EndsHere************//
		
	
		
		
		//************************************Get FollowerList Code Starts Here************//
		
		public void GetFollowerList(final long Cursor, FollowerListListener listener) {
			
			this.mFollowerListListener = listener;
			if(mFollowerListListener!=null){
				mFollowerListListener.OnPreExecute();
			}
			
			//Checking for AccessTocken
			if (mTwitter.hasAccessToken()) {
				
				//Has Access Tocken and Perform Posting
				new GetFollowerListTask(Cursor).execute();
			} else {
				//Authorize Twitter object for Getting Access Tocken
				mTwitter = new Twitter_Handler(activity, CONSUMER_KEY, CONSUMER_SECRET, CALLBACK_URL);
				mTwitter.authorize();
				mTwitter.setAuthListner(new Twitter_Handler.TwitterAuthListner() {
					
					@Override
					public void OnErrorListener(Exception e) {
						// TODO Auto-generated method stub
						if(mFollowerListListener!=null){
							mFollowerListListener.OnError(e);
						}
					}
					
					@Override
					public void OnAuthenticated(User mUser) {
						// TODO Auto-generated method stub
						new GetFollowerListTask(Cursor).execute();
					}
				});
			}
		}
		
		
		public void GetFollowerList(final long Cursor,final int retrivecount, FollowerListListener listener) {
			
			this.mFollowerListListener = listener;
			if(mFollowerListListener!=null){
				mFollowerListListener.OnPreExecute();
			}
			
			//Checking for AccessTocken
			if (mTwitter.hasAccessToken()) {
				
				//Has Access Tocken and Perform Posting
				new GetFollowerListTask(Cursor,retrivecount).execute();
			} else {
				//Authorize Twitter object for Getting Access Tocken
				mTwitter = new Twitter_Handler(activity, CONSUMER_KEY, CONSUMER_SECRET, CALLBACK_URL);
				mTwitter.authorize();
				mTwitter.setAuthListner(new Twitter_Handler.TwitterAuthListner() {
					
					@Override
					public void OnErrorListener(Exception e) {
						// TODO Auto-generated method stub
						if(mFollowerListListener!=null){
							mFollowerListListener.OnError(e);
						}
					}
					
					@Override
					public void OnAuthenticated(User mUser) {
						// TODO Auto-generated method stub
						new GetFollowerListTask(Cursor,retrivecount).execute();
					}
				});
			}
		}
		
		class GetFollowerListTask extends AsyncTask<String, Void, Boolean> {
			
			TwitterException exception;
			PagableResponseList<User> followersList;
			long mCursor = -1;
			int mRetrivecount = -1;
			
			public GetFollowerListTask(long Cursor) {
				// TODO Auto-generated constructor stub
				mCursor =  Cursor;
			}
			
			public GetFollowerListTask(long Cursor, int RetriveCount) {
				// TODO Auto-generated constructor stub
				mCursor =  Cursor;
				mRetrivecount = RetriveCount;
			}
			
			@Override
			protected Boolean doInBackground(String... twitt) {
				try {
					
					if(mRetrivecount>0){
						followersList = mTwitter.twitterObj
								.getFollowersList(mTwitter.getAccessToken().getUserId(),-1,mRetrivecount);
							
					}else{
						followersList = mTwitter.twitterObj
								.getFollowersList(mTwitter.getAccessToken().getUserId(),mCursor);
						
					}
					 return true;
				} catch (TwitterException e) {
					exception = e;
					return false;
				} catch (Exception e) {
					// TODO: handle exception
					exception = (TwitterException)e;
					return false;
				}
				
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if(result){
					if(mFollowerListListener!=null){
						mFollowerListListener.OnSuccessful(followersList);
					}
				}else{
					if(mFollowerListListener!=null){
						mFollowerListListener.OnError(exception);
					}
				}
				super.onPostExecute(result);
			}
		}

		//************************************Get FollowerList Code EndsHere************//

	
		
	//************************************Get FollowerList Code Starts Here************//
		
		public void SendDirectMessage(final String ScreenName,final String Message, SendDirectMessageListener listener) {
			
			this.mSendDirectMessageListener = listener;
			if(mSendDirectMessageListener!=null){
				mSendDirectMessageListener.OnPreExecute();
			}
			
			//Checking for AccessTocken
			if (mTwitter.hasAccessToken()) {
				
				//Has Access Tocken and Perform Task
				new SendDirectMessageTask().execute(ScreenName,Message);
			} else {
				//Authorize Twitter object for Getting Access Tocken
				mTwitter = new Twitter_Handler(activity, CONSUMER_KEY, CONSUMER_SECRET, CALLBACK_URL);
				mTwitter.authorize();
				mTwitter.setAuthListner(new Twitter_Handler.TwitterAuthListner() {
					
					@Override
					public void OnErrorListener(Exception e) {
						// TODO Auto-generated method stub
						if(mSendDirectMessageListener!=null){
							mSendDirectMessageListener.OnError(e);
						}
					}
					
					@Override
					public void OnAuthenticated(User mUser) {
						// TODO Auto-generated method stub
						new SendDirectMessageTask().execute(ScreenName,Message);
					}
				});
			}
		}
		
		class SendDirectMessageTask extends AsyncTask<String, Void, Boolean> {
			TwitterException exception;
			@Override
			protected Boolean doInBackground(String... twitt) {
				try {
					mTwitter.twitterObj.sendDirectMessage(twitt[0],twitt[1]);
					return true;
				} catch (TwitterException e) {
					exception = e;
					return false;
				} catch (Exception e) {
					// TODO: handle exception
					exception = (TwitterException)e;
					return false;
				}
				
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if(result){
					if(mSendDirectMessageListener!=null){
						mSendDirectMessageListener.OnSuccessful();
					}
				}else{
					if(mSendDirectMessageListener!=null){
						mSendDirectMessageListener.OnError(exception);
					}
				}
				super.onPostExecute(result);
			}
		}

		//************************************SendDirectMessage Code EndsHere************//
	public void LogOut(){
		mTwitter.resetAccessToken();
		CookieSyncManager.createInstance(activity);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}
		
	
	
	void showToast(final String msg) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();

			}
		});

	}
	
	
	
	//*******Defining of Lisners************//
	
	public interface PostTwittListner{
		void OnSuccessfulShare();
		void OnError(Exception e);
		void OnPreExecute();
	}//Posting Twitt Listenrer
	
	public interface LoginListner{
		void OnSuccessfulLogin(User mUser);
		void OnError(Exception e);
		void OnPreLogin();
	}//Login Listner
	
	public interface PostImageListener{
		void OnSuccessfulPost();
		void OnError(Exception e);
		void OnPreExecute();
	}//PostImage Listner
	
	public interface FollowerListListener{
		void OnSuccessful(PagableResponseList<User> mUserList);
		void OnError(Exception e);
		void OnPreExecute();
	}//GetFollowerList Listner
	
	public interface SendDirectMessageListener{
		void OnSuccessful();
		void OnError(Exception e);
		void OnPreExecute();
	}//SendDirectMessage Listner
	
}
