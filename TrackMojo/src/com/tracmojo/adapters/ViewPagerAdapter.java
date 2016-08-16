package com.tracmojo.adapters;

import com.tracmojo.R;
import com.tracmojo.customwidget.CustomTextView;
import com.tracmojo.ui.DashboardActivity;
import com.tracmojo.ui.HelpSliderActivity;
import com.tracmojo.ui.LoginActivity;
import com.tracmojo.util.AppSession;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ViewPagerAdapter extends PagerAdapter {
	CustomTextView[] tArray;
	Activity mActivity;
	int imgId[] = { R.drawable.h1, R.drawable.h2, R.drawable.h3, R.drawable.h4, R.drawable.h5, R.drawable.h6 };

	public ViewPagerAdapter(Activity mActivity) {
		super();
		this.mActivity = mActivity;

	}

	@Override
	public int getCount() {
		return imgId.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		// Context context = MainActivity.this;

		LayoutInflater inflator = mActivity.getLayoutInflater();
		View itemView = inflator.inflate(R.layout.page1, container, false);

		RelativeLayout rl = (RelativeLayout) itemView.findViewById(R.id.rl);

		ImageView imageView1 = (ImageView) itemView.findViewById(R.id.imageView1);
		ImageView buttonGotIt = (ImageView) itemView.findViewById(R.id.buttonGotIt);
		ImageView imageViewClose = (ImageView) itemView.findViewById(R.id.imageViewClose);

		imageView1.setImageResource(imgId[position]);

		if (position == 5) {
			buttonGotIt.setVisibility(View.VISIBLE);
			imageViewClose.setVisibility(View.GONE);
		} else {
			buttonGotIt.setVisibility(View.GONE);
			imageViewClose.setVisibility(View.VISIBLE);
		}

		imageViewClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				SharedPreferences preferences;
				AppSession session = new AppSession(mActivity);
				preferences = session.getPreferences();
				Intent intent = null;
				int userid = preferences.getInt("userid", -1);
				if (userid == -1) {
					intent = new Intent(mActivity, LoginActivity.class);
				} else {
					intent = new Intent(mActivity, DashboardActivity.class);
				}
				mActivity.startActivity(intent);
				mActivity.finish();
			}
		});

		buttonGotIt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				SharedPreferences preferences;
				AppSession session = new AppSession(mActivity);
				preferences = session.getPreferences();
				Intent intent = null;
				int userid = preferences.getInt("userid", -1);
				if (userid == -1) {
					intent = new Intent(mActivity, LoginActivity.class);
				} else {
					intent = new Intent(mActivity, DashboardActivity.class);
				}
				mActivity.startActivity(intent);
				mActivity.finish();
			}
		});

		container.addView(rl);

		return itemView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((RelativeLayout) object);
	}
}