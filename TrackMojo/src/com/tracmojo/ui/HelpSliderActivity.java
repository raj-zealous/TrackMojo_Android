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
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class HelpSliderActivity extends Activity {

	PageIndicator mIndicator;
	NestingViewPager viewPager;
	ViewPagerAdapter mViewPagerAdapter;

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

		mViewPagerAdapter = new ViewPagerAdapter(HelpSliderActivity.this);
		viewPager.setAdapter(mViewPagerAdapter);
		mIndicator.setViewPager(viewPager);

		viewPager.setVisibility(View.VISIBLE);

	}

}
