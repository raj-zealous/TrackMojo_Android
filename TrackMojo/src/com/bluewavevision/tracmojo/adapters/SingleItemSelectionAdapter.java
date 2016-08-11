package com.bluewavevision.tracmojo.adapters;

import java.util.ArrayList;
import java.util.List;

import com.bluewavevision.tracmojo.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SingleItemSelectionAdapter extends BaseAdapter  {

	Context con;
	List<String> itemList;
	public LayoutInflater layoutInflater;
	int defaultPos;
	public static class ViewHolder{
		TextView tvName;
		ImageView ivRadio;

		//public TextView tvName;
	}

	public SingleItemSelectionAdapter(Context c, ArrayList<String> list, int defaultPosition) {
		// TODO Auto-generated constructor stub
		this.con = c;
		this.itemList = list;
		this.defaultPos = defaultPosition;
		layoutInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itemList.size();
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
	
	public String getItemFromList(int position)
	{
		return itemList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		 View vi=convertView;
	     ViewHolder holder;

        String name = itemList.get(position);
	     
	     if(convertView==null){
	    	 
	    	 vi = layoutInflater.inflate(R.layout.row_single_item_selection, null);

	    	 holder = new ViewHolder();

	    	 holder.tvName = (TextView) vi.findViewById(R.id.list_row_single_item_selection_tvQuatntity);
	    	 holder.ivRadio = (ImageView) vi.findViewById(R.id.list_row_single_item_selection_iv);
	    	 
	    	 vi.setTag( holder );
	    	 
	     }
	     else
	     {
	    	 holder = (ViewHolder)vi.getTag();
	     }
	     
	     if(!TextUtils.isEmpty(name))
	    	 holder.tvName.setText(name);
	     
	     if(position==defaultPos)
	    	 holder.ivRadio.setImageResource(R.drawable.ic_radio_check);
	     else
	    	 holder.ivRadio.setImageResource(R.drawable.ic_radio_uncheck);
	     
		return vi;
	}

}
