package com.bluewavevision.tracmojo.twitter;

import com.bluewavevision.tracmojo.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


public class TwitterDialog extends Dialog {

	static final float[] DIMENSIONS_LANDSCAPE = { 460, 260 };
	static final float[] DIMENSIONS_PORTRAIT = { 280, 420 };
	static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);
	static final int MARGIN = 4;
	static final int PADDING = 2;
	private final String mUrl;
	private final String mCallBackUrl;
	private final Twitter_Handler.TwDialogListener mListener;
	private ProgressDialog mSpinner;
	private WebView mWebView;
	private LinearLayout mContent;
	private TextView mTitle;
	private boolean progressDialogRunning = false,isSuccess = false;
	
	String value="", Description="";

	public TwitterDialog(Context context, String url,String callBackUrl, Twitter_Handler.TwDialogListener listener) {
		super(context);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE); // the results will
															// be higher than
															// using the
															// activity context
															// object or t									// getWindowManager()
															// shortcut
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;

		DIMENSIONS_LANDSCAPE[0] = screenWidth - 20;
		DIMENSIONS_LANDSCAPE[1] = screenHeight - 20;

		DIMENSIONS_PORTRAIT[0] = screenWidth - 20;
		DIMENSIONS_PORTRAIT[1] = screenHeight - 20;
		mUrl = url;
		mCallBackUrl = callBackUrl;
		mListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSpinner = new ProgressDialog(getContext());
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.twitter_auth_dialog);

		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if(Twitt_Sharing.strLoadingMessage!=null && Twitt_Sharing.strLoadingMessage.length()>0){
			mSpinner.setMessage(Twitt_Sharing.strLoadingMessage);
		}
		
		mContent = new LinearLayout(getContext());

		mContent.setOrientation(LinearLayout.VERTICAL);

		setUpWebView();

	}
	
	
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		if(isSuccess){
			mListener.onComplete(value);
		}else{
			mListener.onError(new Exception(Description));
		}
		super.dismiss();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setUpWebView() {
		mWebView = (WebView) findViewById(R.id.webv);
		// mWebView.setLayoutParams(FILL);
		mWebView.setVerticalScrollBarEnabled(true);
		mWebView.setHorizontalScrollBarEnabled(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.setWebViewClient(new TwitterWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(mUrl);

		// mContent.addView(mWebView);
	}

	private class TwitterWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			Log.e("url on loading", "=>" + url);

			Uri uri = Uri.parse(url);

			if (uri != null
					&& (uri.toString().startsWith(mCallBackUrl) || (uri.toString().contains("twitter.com/oauth/authorize?oauth_token")))) {

				mListener.onComplete(url);

				TwitterDialog.this.dismiss();

				isSuccess=true;
				return true;
			} else if (url.startsWith("authorize")) {
				isSuccess = false;
				return false;
			}
			return false;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(new Exception(description));
			isSuccess = false;
			TwitterDialog.this.dismiss();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			mSpinner.show();
			progressDialogRunning = true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			progressDialogRunning = false;
			mSpinner.dismiss();
		}

	}

	@Override
	protected void onStop() {
		progressDialogRunning = false;
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		if (!progressDialogRunning) {
			if(mWebView.canGoBack()){
				mWebView.goBack();
			}else{
				TwitterDialog.this.dismiss();
			}
			
		}
	}
}
