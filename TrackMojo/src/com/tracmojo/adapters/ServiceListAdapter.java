package com.tracmojo.adapters;

import java.util.ArrayList;

import com.tracmojo.R;
import com.tracmojo.model.Service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ServiceListAdapter extends BaseAdapter {

	Context context;
	LayoutInflater inflater=null;
	public static boolean isChecked[];
	ArrayList<Service> contacts;

	public ServiceListAdapter(Context con, ArrayList<Service> contacts) {
		// TODO Auto-generated constructor stub

		this.contacts = contacts;
		//this.tempContacts = contacts;
		this.context =con;
		isChecked = new boolean[this.contacts.size()];
		
		//this.listContacts = arr;
		 inflater = ( LayoutInflater )context.
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
	public View getView(int position,  View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		//View vi = convertView;
		ViewHolder holder;
		final Service service = contacts.get(position);
		final int pos =position;
		
		if(convertView==null){

            convertView = inflater.inflate(R.layout.row_service_list, null);

			final View temp=convertView;
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.row_service_list_tvName);
            holder.tvDescription = (TextView) convertView.findViewById(R.id.row_service_list_tvDescription);
			holder.ivTick = (ImageView) temp.findViewById(R.id.ivTick);
            convertView.setTag( holder );
		}
		else
			holder=(ViewHolder)convertView.getTag();

		if(service.isActive())
			holder.ivTick.setVisibility(View.VISIBLE);
		else {
			holder.ivTick.setVisibility(View.GONE);
		}
		holder.tvName.setText(service.getName());
        holder.tvDescription.setText(service.getDescription());

        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView iv =(ImageView) v.findViewById(R.id.ivTick);
                if(contact.isSelected()){
                    contact.setSelected(false);
                    iv.setVisibility(View.GONE);
                }else{
                    contact.setSelected(true);
                    iv.setVisibility(View.VISIBLE);
                }
            }
        });*/


		return convertView;

	}

	public static class ViewHolder{

		public TextView tvName, tvDescription;
		public ImageView ivTick;
	}

}
