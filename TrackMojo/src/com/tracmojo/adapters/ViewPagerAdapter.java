package com.tracmojo.adapters;

import com.tracmojo.R;
import com.tracmojo.customwidget.CustomTextView;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ViewPagerAdapter extends PagerAdapter {
	CustomTextView[] tArray;
	Activity mActivity;
	int strIds1[] = { R.string.text1, -1, -1, R.string.text2, -1, -1, R.string.text3 };
	int strIds2[] = { R.string.text4, R.string.text5, R.string.text6, R.string.text7, R.string.text8, R.string.text9,
			R.string.text10 };
	int strIds3[] = { R.string.text11, R.string.text12, R.string.text13, R.string.text14, R.string.text15,
			R.string.text16, -1 };
	int strIds4[] = { R.string.text17, R.string.text18, R.string.text19, R.string.text20, R.string.text21,
			R.string.text22, -1 };

	public ViewPagerAdapter(Activity mActivity) {
		super();
		this.mActivity = mActivity;

	}

	@Override
	public int getCount() {
		return 4;
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

		CustomTextView t1 = (CustomTextView) itemView.findViewById(R.id.textView1);
		CustomTextView t2 = (CustomTextView) itemView.findViewById(R.id.textView2);
		CustomTextView t3 = (CustomTextView) itemView.findViewById(R.id.textView3);
		CustomTextView t4 = (CustomTextView) itemView.findViewById(R.id.textView4);
		CustomTextView t5 = (CustomTextView) itemView.findViewById(R.id.textView5);
		CustomTextView t6 = (CustomTextView) itemView.findViewById(R.id.textView6);
		CustomTextView t7 = (CustomTextView) itemView.findViewById(R.id.textView7);

		CustomTextView[] tArray = { t1, t2, t3, t4, t5, t6, t7 };

		setTextMethod(position, tArray);

		container.addView(rl);

		return itemView;
	}

	// @Override
	// public void destroyItem(ViewGroup container, int position, Object object)
	// {
	// ((ViewPager) container).removeView((RelativeLayout) object);
	// }

	private void setTextMethod(int position, CustomTextView[] tArray2) {
		
		
		if(position==0)
		{
			tArray2[0].setGravity(Gravity.CENTER);
			tArray2[6].setGravity(Gravity.RIGHT|Gravity.CENTER);
		}
		else
		{
			tArray2[0].setGravity(Gravity.LEFT);
			tArray2[6].setGravity(Gravity.LEFT);
		}
		
		
		for (int x = 0; x < tArray2.length; x++) {
			tArray2[x].setText("");
		}

		for (int x = 0; x < tArray2.length; x++) {

			if (position == 0) {

				
				if (strIds1[x] != -1)
					tArray2[x].setText(Html.fromHtml(mActivity.getResources().getString(strIds1[x])));
			} else if (position == 1) {
				if (strIds2[x] != -1)
					tArray2[x].setText(Html.fromHtml(mActivity.getResources().getString(strIds2[x])));
			}

			else if (position == 2) {
				if (strIds3[x] != -1)
					tArray2[x].setText(Html.fromHtml(mActivity.getResources().getString(strIds3[x])));
			}

			else if (position == 3) {
				if (strIds4[x] != -1)
					tArray2[x].setText(Html.fromHtml(mActivity.getResources().getString(strIds4[x])));
			}

		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((RelativeLayout) object);
	}
}