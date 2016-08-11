package com.bluewavevision.tracmojo.ui;

import com.bluewavevision.tracmojo.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseActivity extends ActionBarActivity implements OnClickListener {

	boolean isForHomeScreen;
	public ImageView ivBack, ivInfo, ivHelp, ivEmailCommentList;
	public TextView tvHeader, ivLogo, tvLogout;

	public BaseActivity() {
	}

	public BaseActivity(boolean isForHomeScreen) {
		this.isForHomeScreen = isForHomeScreen;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = null;

		mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
		tvHeader = (TextView) mCustomView
				.findViewById(R.id.custom_actionbar_tvHeader);
		ivBack = (ImageView) mCustomView
				.findViewById(R.id.custom_actionbar_ivBack);
		ivInfo = (ImageView) mCustomView
				.findViewById(R.id.custom_actionbar_ivInfo);
		ivHelp = (ImageView) mCustomView
				.findViewById(R.id.custom_actionbar_ivHelp);
		ivLogo = (TextView) mCustomView
				.findViewById(R.id.custom_actionbar_ivLogo);
		ivEmailCommentList = (ImageView) mCustomView
				.findViewById(R.id.custom_actionbar_ivEmailCommentList);
		tvLogout = (TextView) mCustomView
				.findViewById(R.id.custom_actionbar_tvLogout);
		ivBack.setOnClickListener(this);
		ivInfo.setOnClickListener(this);

		SpannableString spText = new SpannableString("tracmojo");
		spText.setSpan(
				new ForegroundColorSpan(getResources().getColor(
						R.color.trac_color)), 0, 3,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spText.setSpan(
				new ForegroundColorSpan(getResources().getColor(
						R.color.mojo_color)), 4, 8,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ivLogo.setText(spText);
		ActionBar actionBar = getSupportActionBar();
		System.out.println("================================================"+actionBar);
		if (actionBar != null) {
			try {
			actionBar.setDisplayHomeAsUpEnabled(false);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayUseLogoEnabled(false);
			actionBar.setDisplayShowTitleEnabled(false);

			actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_CUSTOM);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setCustomView(mCustomView, new ActionBar.LayoutParams(
					ActionBar.LayoutParams.MATCH_PARENT,
					ActionBar.LayoutParams.MATCH_PARENT));
			/*
			 * Toolbar parent = (Toolbar) mCustomView.getParent();
			 * parent.setContentInsetsAbsolute(0, 0);
			 */
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void setHeaderText(String text) {
		tvHeader.setText(text);
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
						&& (x < w.getLeft() || x >= w.getRight()
								|| y < w.getTop() || y > w.getBottom())) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
							.getWindowToken(), 0);
				}
			}
			return ret;
		} catch (Exception e) {
			return ret;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.custom_actionbar_ivBack:
			onBackPressed();
			break;
		case R.id.custom_actionbar_ivInfo:
			goToHelpScreen();
			break;

		default:
			break;
		}
	}

	private void goToHelpScreen() {
		Intent intent = new Intent(BaseActivity.this, HelpActivity.class);
		startActivity(intent);
	}

}
