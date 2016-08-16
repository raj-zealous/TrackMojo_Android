package com.tracmojo.adapters;

import java.util.HashMap;
import java.util.List;

import com.tracmojo.R;
import com.tracmojo.TracMojoApplication;
import com.tracmojo.fragments.FollowerTracReviewFragment;
import com.tracmojo.fragments.GroupTracReviewFragment;
import com.tracmojo.fragments.HomeFragment;
import com.tracmojo.fragments.PersonalTracReviewFragment;
import com.tracmojo.fragments.TracRateFragment;
import com.tracmojo.model.Trac;
import com.tracmojo.ui.DashboardActivity;
import com.tracmojo.util.OnSwipeTouchListener;
import com.tracmojo.util.Util;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeExpandableListAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<Trac>> _listDataChild;
	private TracMojoApplication application;
	Animation animation;
	int parentPosition;

	public HomeExpandableListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<Trac>> listChildData) {
		this.mContext = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
		this.application = (TracMojoApplication) context.getApplicationContext();

		animation = new AlphaAnimation(1, 0);
		animation.setDuration(1000); // duration - half a second
		animation.setInterpolator(new LinearInterpolator()); // do not alter
																// animation
																// rate
		animation.setRepeatCount(Animation.INFINITE); // Repeat animation
														// infinitely
		animation.setRepeatMode(Animation.REVERSE);
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
		// return this._listDataChild.get(groupPosition).get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public static class ViewHolder {
		TextView tvGoal, tvGroupName;
		ImageView ivRate;
		// CircularDrawable circularDrawable;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {

		ViewHolder holder;

		final Trac trac = (Trac) getChild(groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.row_home_list_item, null);

			holder = new ViewHolder();
			holder.ivRate = (ImageView) convertView.findViewById(R.id.row_home_list_item_ivRate);
			holder.tvGoal = (TextView) convertView.findViewById(R.id.row_home_list_item_tvGoal);
			holder.tvGroupName = (TextView) convertView.findViewById(R.id.row_home_list_item_tvGroupName);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (trac.getRate().equalsIgnoreCase("-1") && groupPosition != 2) {
			holder.ivRate.startAnimation(animation);
		} else {
			holder.ivRate.clearAnimation();
		}

		int mTrac = Integer.parseInt(trac.getLastRate());
		switch (mTrac) {
		case 5:
			holder.ivRate.setImageResource(R.drawable.ic_custom_rate_one);
			break;

		case 4:
			holder.ivRate.setImageResource(R.drawable.ic_custom_rate_two);
			break;

		case 3:
			holder.ivRate.setImageResource(R.drawable.ic_custom_rate_three);
			break;

		case 2:
			holder.ivRate.setImageResource(R.drawable.ic_custom_rate_four);
			break;
		case 1:
			holder.ivRate.setImageResource(R.drawable.ic_custom_rate_five);
			break;

		case -1:
			/*
			 * if(!TextUtils.isEmpty(trac.getLastRate())){ int lastRate =
			 * Integer.parseInt(trac.getLastRate()); switch (lastRate) { case 5:
			 * holder.ivRate.setImageResource(R.drawable.ic_custom_rate_one);
			 * break;
			 * 
			 * case 4:
			 * holder.ivRate.setImageResource(R.drawable.ic_custom_rate_two);
			 * break;
			 * 
			 * case 3:
			 * holder.ivRate.setImageResource(R.drawable.ic_custom_rate_three);
			 * break;
			 * 
			 * case 2:
			 * holder.ivRate.setImageResource(R.drawable.ic_custom_rate_four);
			 * break; case 1:
			 * holder.ivRate.setImageResource(R.drawable.ic_custom_rate_five);
			 * break;
			 * 
			 * default:
			 * holder.ivRate.setImageResource(R.drawable.ic_custom_rate_zero);
			 * break; } } else {
			 * holder.ivRate.setImageResource(R.drawable.ic_custom_rate_zero); }
			 */

			break;

		default:
			holder.ivRate.setImageResource(R.drawable.ic_custom_rate_zero);
			break;
		}

		if (!TextUtils.isEmpty(trac.getGoal()))
			holder.tvGoal.setText(trac.getGoal());

		if (!TextUtils.isEmpty(trac.getGroupName())) {
			holder.tvGroupName.setVisibility(View.VISIBLE);
			holder.tvGroupName.setText(trac.getGroupName());
			if (groupPosition == 2) {
				holder.tvGroupName.append(" - " + trac.getOwnerName());
			}
		} else {
			if (groupPosition == 2) {
				holder.tvGroupName.setText(trac.getOwnerName());
			} else {
				holder.tvGroupName.setVisibility(View.GONE);
			}
		}

		/*
		 * if (!TextUtils.isEmpty(trac.getGroupName()) &&
		 * !TextUtils.isEmpty(trac.getGroupType())) { holder.tvGroupName.append(
		 * " - " + trac.getGroupType()); }
		 */
		holder.ivRate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (trac.getRate().equalsIgnoreCase("-1") && groupPosition != 2) {
					TracRateFragment tracRateFragment = new TracRateFragment();
					Bundle bundle = new Bundle();
					bundle.putSerializable("trac", trac);
					bundle.putInt("parentPosition", groupPosition);
					bundle.putInt("childPosition", childPosition);
					HomeFragment f = (HomeFragment) ((DashboardActivity) mContext).mStacks.get(Util.TAB_HOME)
							.lastElement();
					bundle.putInt("firstVisiblePosition", f.getFirstVisiblePostionOfListView());
					tracRateFragment.setArguments(bundle);
					if (Util.checkConnection(mContext, mContext.getString(R.string.not_available_while_offlin))
							&& groupPosition != 2) {
						((DashboardActivity) mContext).pushFragments(Util.TAB_HOME, tracRateFragment, false, true);
					}
				} else {
					if (groupPosition == 0) {
						PersonalTracReviewFragment personalTracReviewFragment = new PersonalTracReviewFragment();
						Bundle bundle = new Bundle();
						bundle.putInt("trac_id", trac.getId());
						personalTracReviewFragment.setArguments(bundle);
						((DashboardActivity) mContext).pushFragments(Util.TAB_HOME, personalTracReviewFragment, false,
								true);
					} else if (groupPosition == 1) {
						GroupTracReviewFragment groupTracReviewFragment = new GroupTracReviewFragment();
						Bundle bundle = new Bundle();
						bundle.putInt("trac_id", trac.getId());
						groupTracReviewFragment.setArguments(bundle);
						((DashboardActivity) mContext).pushFragments(Util.TAB_HOME, groupTracReviewFragment, false,
								true);
					} else if (groupPosition == 2) {
						if (!TextUtils.isEmpty(trac.getGroupType())) {
							GroupTracReviewFragment groupTracReviewFragment = new GroupTracReviewFragment();
							Bundle bundle = new Bundle();
							bundle.putInt("trac_id", trac.getId());
							bundle.putBoolean("isFollower", true);
							groupTracReviewFragment.setArguments(bundle);
							((DashboardActivity) mContext).pushFragments(Util.TAB_HOME, groupTracReviewFragment, false,
									true);
						} else {
							FollowerTracReviewFragment followerTracReviewFragment = new FollowerTracReviewFragment();
							Bundle bundle = new Bundle();
							bundle.putInt("trac_id", trac.getId());
							followerTracReviewFragment.setArguments(bundle);
							((DashboardActivity) mContext).pushFragments(Util.TAB_HOME, followerTracReviewFragment,
									false, true);
						}
					}
				}
			}
		});

		convertView.setOnTouchListener(new OnSwipeTouchListener(mContext) {

			public void onSwipeLeft() {
				// Toast.makeText(mContext, "left", Toast.LENGTH_SHORT).show();
				if (groupPosition == 0 && !trac.getRate().equalsIgnoreCase("-1")) {
					PersonalTracReviewFragment personalTracReviewFragment = new PersonalTracReviewFragment();
					Bundle bundle = new Bundle();
					bundle.putInt("trac_id", trac.getId());
					personalTracReviewFragment.setArguments(bundle);
					((DashboardActivity) mContext).pushFragments(Util.TAB_HOME, personalTracReviewFragment, false,
							true);
				} else if (groupPosition == 1 && !trac.getRate().equalsIgnoreCase("-1")) {
					GroupTracReviewFragment groupTracReviewFragment = new GroupTracReviewFragment();
					Bundle bundle = new Bundle();
					bundle.putInt("trac_id", trac.getId());
					bundle.putBoolean("isFollower", true);
					groupTracReviewFragment.setArguments(bundle);
					((DashboardActivity) mContext).pushFragments(Util.TAB_HOME, groupTracReviewFragment, false, true);
				} else if (groupPosition == 2 && !trac.getRate().equalsIgnoreCase("-1")) {
					FollowerTracReviewFragment followerTracReviewFragment = new FollowerTracReviewFragment();
					Bundle bundle = new Bundle();
					bundle.putInt("trac_id", trac.getId());
					followerTracReviewFragment.setArguments(bundle);
					((DashboardActivity) mContext).pushFragments(Util.TAB_HOME, followerTracReviewFragment, false,
							true);
				}
			}

			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		parentPosition = groupPosition;
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.row_home_list_header, null);
		}

		TextView lblListHeader = (TextView) convertView.findViewById(R.id.row_home_list_header_tvHeader);
		ImageView ivPlus = (ImageView) convertView.findViewById(R.id.row_home_list_header_tvPlus);

		ivPlus.setImageResource(isExpanded ? R.drawable.ic_home_list_collapse : R.drawable.ic_home_list_expand);

		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
