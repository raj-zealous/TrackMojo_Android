package com.tracmojo.ui;

import com.tracmojo.R;
import com.tracmojo.adapters.ViewPagerAdapter;
import com.tracmojo.util.AppSession;
import com.tracmojo.util.Util;
import com.tracmojo.widget.CirclePageIndicator;
import com.tracmojo.widget.NestingViewPager;
import com.tracmojo.widget.PageIndicator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HelpSliderActivity extends Activity implements OnClickListener {

	PageIndicator mIndicator;
	NestingViewPager viewPager;
	ViewPagerAdapter mViewPagerAdapter;
	Button buttonGotIt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_help_slider);
		Util.setIsFirstTimepreference(getApplicationContext(), "false");
		InitIds();

	}

	private void InitIds() {
		// TODO Auto-generated method stub
		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		viewPager = (NestingViewPager) findViewById(R.id.view_pager);
		buttonGotIt = (Button) findViewById(R.id.buttonGotIt);
		mViewPagerAdapter = new ViewPagerAdapter(HelpSliderActivity.this);
		viewPager.setAdapter(mViewPagerAdapter);
		mIndicator.setViewPager(viewPager);

		viewPager.setVisibility(View.VISIBLE);
		buttonGotIt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == buttonGotIt) {
			SharedPreferences preferences;
			AppSession session = new AppSession(HelpSliderActivity.this);
			preferences = session.getPreferences();
			Intent intent = null;
			int userid = preferences.getInt("userid", -1);
			if (userid == -1) {
				intent = new Intent(HelpSliderActivity.this, LoginActivity.class);
			} else {
				intent = new Intent(HelpSliderActivity.this, DashboardActivity.class);
			}
			startActivity(intent);
			finish();
		}
	}

}
