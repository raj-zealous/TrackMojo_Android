package com.bluewavevision.tracmojo.adapters;

import java.util.ArrayList;

import com.bluewavevision.tracmojo.R;
import com.bluewavevision.tracmojo.model.Follower;
import com.bluewavevision.tracmojo.ui.AddFollowersActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class FollowersListForAddAdapter extends BaseAdapter {

	String listContacts[];
	Context mContext;
	LayoutInflater inflater=null;
	public static boolean isChecked[];
	ArrayList<Follower> contacts;
    boolean isGroup;

	public FollowersListForAddAdapter(Context con, ArrayList<Follower> contacts, boolean isGroup) {
		// TODO Auto-generated constructor stub

		this.contacts = contacts;
		//this.tempContacts = contacts;
		this.mContext =con;
        this.isGroup = isGroup;
		isChecked = new boolean[this.contacts.size()];
		
		//this.listContacts = arr;
		 inflater = ( LayoutInflater ) mContext.
                 getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contacts.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(final int position,  View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		//View vi = convertView;
		ViewHolder holder;
		final Follower follower = contacts.get(position);
		final int pos =position;
		
		if(convertView==null){

            convertView = inflater.inflate(R.layout.row_invitees_list, null);

			final View temp=convertView;
			holder = new ViewHolder();
			holder.tvEmail = (TextView) convertView.findViewById(R.id.row_invitees_list_tvEmail);
            holder.tvRequestStatus = (TextView) convertView.findViewById(R.id.row_invitees_list_tvRequestStatus);
			holder.ivRequestStatus = (ImageView) temp.findViewById(R.id.row_invitees_list_ivRequestStatus);
            holder.ivDelete = (ImageView) temp.findViewById(R.id.row_invitees_list_ivDelete);
            convertView.setTag( holder );
		}
		else
			holder=(ViewHolder)convertView.getTag();

        if(!TextUtils.isEmpty(follower.getEmailId())){
            holder.tvEmail.setText(follower.getEmailId());
        }
        if(!TextUtils.isEmpty(follower.getRequestStatus())){
            if(follower.getRequestStatus().equalsIgnoreCase(Follower.ACCEPTED)){
                holder.tvRequestStatus.setText(mContext.getString(R.string.invitees_list_accepted));
                holder.ivRequestStatus.setImageResource(R.drawable.ic_invitation_accepted);
            }else if(follower.getRequestStatus().equalsIgnoreCase(Follower.DECLINED)){
                holder.tvRequestStatus.setText(mContext.getString(R.string.invitees_list_declined));
                holder.ivRequestStatus.setImageResource(R.drawable.ic_invitation_decline);
            }else if(follower.getRequestStatus().equalsIgnoreCase(Follower.NO_RESPONSE)){
                holder.tvRequestStatus.setText(mContext.getString(R.string.invitees_list_no_response));
                holder.ivRequestStatus.setImageResource(R.drawable.ic_invitation_pending);
            }
        }

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(position);
            }
        });

		return convertView;
	}

	public static class ViewHolder{

		public TextView tvEmail, tvRequestStatus;
		public ImageView ivRequestStatus,ivDelete;
	}

    public void showDeleteConfirmationDialog(final int position){
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.delete_followers_dialog_title))
                .setMessage(mContext.getString(R.string.delete_followers_dialog_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        /*if(isGroup)
                            ((AddFollowersActivity)mContext).deleteFollowers(position);
                        else*/
                            ((AddFollowersActivity)mContext).deleteFollowers(position);
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
}
