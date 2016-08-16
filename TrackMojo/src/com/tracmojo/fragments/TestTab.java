package com.tracmojo.fragments;


import com.tracmojo.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class TestTab extends BaseFragment implements OnClickListener {
	View llLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		/*mContext.tvHeader.setText(mContext.getString(R.string.company_profile_header));
		mContext.ivBack.setVisibility(View.GONE);
		mContext.ivEditProfile.setVisibility(View.GONE);*/

		if(llLayout==null){
			llLayout = inflater.inflate(R.layout.fragment_test, container, false);
			

		} else {
			((ViewGroup) llLayout.getParent()).removeView(llLayout);
		}
		
		return llLayout;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
