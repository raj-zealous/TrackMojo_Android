package com.bluewavevision.tracmojo.adapters;

import java.util.HashMap;
import java.util.List;

import com.bluewavevision.tracmojo.R;
import com.bluewavevision.tracmojo.TracMojoApplication;
import com.bluewavevision.tracmojo.fragments.EditTracFragment;
import com.bluewavevision.tracmojo.model.Trac;
import com.bluewavevision.tracmojo.ui.DashboardActivity;
import com.bluewavevision.tracmojo.ui.GroupTracAddActivity;
import com.bluewavevision.tracmojo.ui.GroupTracEditActivity;
import com.bluewavevision.tracmojo.ui.PersonalTracAddActivity;
import com.bluewavevision.tracmojo.ui.PersonalTracEditActivity;
import com.bluewavevision.tracmojo.util.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ManageTracExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Trac>> _listDataChild;
    private TracMojoApplication application;
    Animation animation;
    int parentPosition;

    public ManageTracExpandableListAdapter(Context context, List<String> listDataHeader,
                                           HashMap<String, List<Trac>> listChildData) {
        this.mContext = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.application = (TracMojoApplication) context.getApplicationContext();

        animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
        //return this._listDataChild.get(groupPosition).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public static class ViewHolder {
        TextView tvGoal, tvGroupName;
        ImageView ivEdit,ivDelete;
        ToggleButton tbFollow;
        LinearLayout linManageTrac;
        //CircularDrawable circularDrawable;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        final Trac trac = (Trac) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_edit_list_item, null);

            holder = new ViewHolder();
            holder.tvGoal = (TextView) convertView.findViewById(R.id.row_edit_list_item_tvGoal);
            holder.tvGroupName = (TextView) convertView.findViewById(R.id.row_edit_list_item_tvGroupName);
            holder.ivDelete = (ImageView) convertView.findViewById(R.id.row_edit_list_item_ivDelete);
            holder.ivEdit = (ImageView) convertView.findViewById(R.id.row_edit_list_item_ivEdit);
            holder.tbFollow = (ToggleButton) convertView.findViewById(R.id.row_edit_list_item_tbFollow);
            holder.linManageTrac = (LinearLayout) convertView.findViewById(R.id.row_edit_list_item_linManageOptions);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(groupPosition==0){
            holder.ivEdit.setVisibility(View.VISIBLE);
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.tbFollow.setVisibility(View.GONE);
            holder.tvGoal.setTextColor(mContext.getResources().getColor(R.color.list_text));
        }else if(groupPosition == 1){
            if(trac.isMyTrac()){
                holder.ivEdit.setVisibility(View.VISIBLE);
                holder.ivDelete.setVisibility(View.VISIBLE);
                holder.tbFollow.setVisibility(View.GONE);
                holder.tvGoal.setTextColor(mContext.getResources().getColor(R.color.list_text));
            }else{
                holder.ivEdit.setVisibility(View.GONE);
                holder.ivDelete.setVisibility(View.GONE);
                holder.tbFollow.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(trac.getRequestStatus())) {
                    if (trac.getRequestStatus().equalsIgnoreCase("p")) {
                        holder.tbFollow.setChecked(false);
                        holder.tvGoal.setTextColor(mContext.getResources().getColor(R.color.home_add_trac_red));
                    } else if (trac.getRequestStatus().equalsIgnoreCase("d")){
                        holder.tbFollow.setChecked(false);
                        holder.tvGoal.setTextColor(mContext.getResources().getColor(R.color.list_text));
                    }else {
                        holder.tbFollow.setChecked(true);
                        holder.tvGoal.setTextColor(mContext.getResources().getColor(R.color.list_text));
                    }
                } else {
                    holder.tbFollow.setChecked(false);
                    holder.tvGoal.setTextColor(mContext.getResources().getColor(R.color.list_text));
                }
            }
        } else if(groupPosition == 2){
            holder.ivEdit.setVisibility(View.GONE);
            holder.ivDelete.setVisibility(View.GONE);
            holder.tbFollow.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(trac.getRequestStatus())) {
                if (trac.getRequestStatus().equalsIgnoreCase("p")) {
                    holder.tbFollow.setChecked(false);
                    holder.tvGoal.setTextColor(mContext.getResources().getColor(R.color.home_add_trac_red));
                } else if (trac.getRequestStatus().equalsIgnoreCase("d")){
                    holder.tbFollow.setChecked(false);
                    holder.tvGoal.setTextColor(mContext.getResources().getColor(R.color.list_text));
                }else {
                    holder.tbFollow.setChecked(true);
                    holder.tvGoal.setTextColor(mContext.getResources().getColor(R.color.list_text));
                }
            } else {
                holder.tbFollow.setChecked(false);
                holder.tvGoal.setTextColor(mContext.getResources().getColor(R.color.list_text));
            }
        }

        if (!TextUtils.isEmpty(trac.getGoal()))
            holder.tvGoal.setText(trac.getGoal());

        if (!TextUtils.isEmpty(trac.getGroupName())) {
            holder.tvGroupName.setVisibility(View.VISIBLE);
            holder.tvGroupName.setText(trac.getGroupName());
            if(groupPosition==2){
                holder.tvGroupName.append(" - " + trac.getOwnerName());
            }
        } else {
            if(groupPosition==2){
                holder.tvGroupName.setText(trac.getOwnerName());
            }else {
                holder.tvGroupName.setVisibility(View.GONE);
            }
        }

        /*if (!TextUtils.isEmpty(trac.getGroupName()) && !TextUtils.isEmpty(trac.getGroupType())) {
            holder.tvGroupName.append(" - " + trac.getGroupType());
        }*/

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupPosition!=2)
                    showDeleteConfirmationDialog("" + trac.getId(), groupPosition, childPosition);
            }
        });

        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupPosition==0)
                    goToEditPersonalTracScreen(trac.getId());
                else if (groupPosition == 1)
                    goToEditGroupTracScreen(trac.getId());
            }
        });

        holder.tbFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTracFragment fragment = (EditTracFragment) ((DashboardActivity) mContext).mStacks.get(Util.TAB_EDIT_TRAC).get(0);
                if (groupPosition == 2) {
                    if (trac.getRequestStatus().equalsIgnoreCase("p")) {
                        fragment.respondToInvitation("" + trac.getId(), groupPosition, childPosition, "a", "follow");
                    } else if (trac.getRequestStatus().equalsIgnoreCase("d")) {
                        fragment.respondToInvitation("" + trac.getId(), groupPosition, childPosition, "a", "follow");
                    } else {
                        //fragment.respondToInvitation("" + trac.getId(), groupPosition, childPosition, "d", "follow");
                        showDeclineConfirmationDialog("" + trac.getId(), groupPosition, childPosition, "d", "follow");
                    }
                } else if (groupPosition == 1){
                    if (trac.getRequestStatus().equalsIgnoreCase("p")) {
                        fragment.respondToInvitation(""+trac.getId(),groupPosition,childPosition,"a","participate");
                    } else if (trac.getRequestStatus().equalsIgnoreCase("d")) {
                        fragment.respondToInvitation(""+trac.getId(),groupPosition,childPosition,"a","participate");
                    } else {
                        //fragment.respondToInvitation(""+trac.getId(),groupPosition,childPosition,"d","participate");
                        showDeclineConfirmationDialog("" + trac.getId(), groupPosition, childPosition, "d", "participate");
                    }
                }
            }
        });

        /*holder.tbFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditTracFragment fragment = (EditTracFragment) ((DashboardActivity)mContext).mStacks.get(Util.TAB_EDIT_TRAC).get(0);
                if(groupPosition == 2){
                    if(!isChecked){
                        fragment.respondToInvitation(""+trac.getId(),groupPosition,childPosition,"d","follow");
                    }else {
                        fragment.respondToInvitation(""+trac.getId(),groupPosition,childPosition,"a","follow");
                    }
                } else if(groupPosition == 1){
                    if(!isChecked){
                        fragment.respondToInvitation(""+trac.getId(),groupPosition,childPosition,"d","participate");
                    }else {
                        fragment.respondToInvitation(""+trac.getId(),groupPosition,childPosition,"a","participate");
                    }
                }
            }
        });*/

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
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
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        parentPosition = groupPosition;
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_edit_list_header, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.row_edit_list_header_tvHeader);
        ImageView ivPlus = (ImageView) convertView
                .findViewById(R.id.row_home_edit_header_tvPlus);

        ImageView ivAddNewTrac = (ImageView) convertView.findViewById(R.id.row_home_edit_header_ivAddNew);

        if(groupPosition==2){
            ivAddNewTrac.setVisibility(View.GONE);
        } else {
            ivAddNewTrac.setVisibility(View.VISIBLE);
        }

        ivAddNewTrac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupPosition==0){
                    goToAddPersonalTrac();
                } else if(groupPosition == 1){
                    goToAddGroupTrac();
                }
            }
        });

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

    public void showDeleteConfirmationDialog(final String tracId,final int parentPosition,final int childPosition){
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.delete_trac_dialog_title))
                .setMessage(mContext.getString(R.string.delete_trac_dialog_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        EditTracFragment fragment = (EditTracFragment) ((DashboardActivity)mContext).mStacks.get(Util.TAB_EDIT_TRAC).get(0);
                        fragment.deleteRate(tracId,parentPosition,childPosition);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void goToAddPersonalTrac(){
        Intent intent = new Intent(mContext, PersonalTracAddActivity.class);
        mContext.startActivity(intent);
    }

    private void goToEditPersonalTracScreen(int tracid){
        Intent intent = new Intent(mContext, PersonalTracEditActivity.class);
        intent.putExtra("tracid",tracid);
        mContext.startActivity(intent);
    }

    private void goToAddGroupTrac(){
        Intent intent = new Intent(mContext, GroupTracAddActivity.class);
        mContext.startActivity(intent);
    }

    private void goToEditGroupTracScreen(int tracid){
        Intent intent = new Intent(mContext, GroupTracEditActivity.class);
        intent.putExtra("tracid",tracid);
        mContext.startActivity(intent);
    }

    public void showDeclineConfirmationDialog(final String tracid, final int parentPosition, final int childPosition,
                                              final String requestStatus, final String invitationType){
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.delete_trac_dialog_title))
                .setMessage("Are you sure you want to decline?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        EditTracFragment fragment = (EditTracFragment) ((DashboardActivity)mContext).mStacks.get(Util.TAB_EDIT_TRAC).get(0);
                        //fragment.deleteRate(tracId,parentPosition,childPosition);
                        fragment.respondToInvitation("" + tracid, parentPosition, childPosition, requestStatus, invitationType);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        notifyDataSetChanged();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
