package com.tracmojo.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tracmojo.R;
import com.tracmojo.model.RateWord;


public class TracRateWordingAdapter extends BaseAdapter  {

	Context con;
	List<RateWord> itemList;
	public LayoutInflater layoutInflater;
	int defaultPos;
	public static class ViewHolder{
		TextView tvName;
		ImageView ivRadio;

		//public TextView tvName;
	}

	public TracRateWordingAdapter(Context c, ArrayList<RateWord> list, int defaultPosition) {
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
	
	public RateWord getItemFromList(int position)
	{
		return itemList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		 View vi=convertView;
	     ViewHolder holder;

        RateWord rateIdea = itemList.get(position);
	     
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
	     
	     if(!TextUtils.isEmpty(rateIdea.getName()))
	    	 holder.tvName.setText(rateIdea.getName());
	     
	     if(position==defaultPos)
	    	 holder.ivRadio.setImageResource(R.drawable.ic_radio_check);
	     else
	    	 holder.ivRadio.setImageResource(R.drawable.ic_radio_uncheck);
	     
		return vi;
	}

}
