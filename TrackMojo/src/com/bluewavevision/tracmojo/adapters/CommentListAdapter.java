package com.bluewavevision.tracmojo.adapters;

import java.util.ArrayList;

import com.bluewavevision.tracmojo.R;
import com.bluewavevision.tracmojo.model.Comment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class CommentListAdapter extends BaseAdapter {

    String listContacts[];
    Context context;
    LayoutInflater inflater = null;
    public static boolean isChecked[];
    ArrayList<Comment> contacts;

    public CommentListAdapter(Context con, ArrayList<Comment> contacts) {
        // TODO Auto-generated constructor stub

        this.contacts = contacts;
        //this.tempContacts = contacts;
        this.context = con;
        isChecked = new boolean[this.contacts.size()];


        //this.listContacts = arr;
        inflater = (LayoutInflater) context.
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        //View vi = convertView;
        ViewHolder holder;
        final Comment comment = contacts.get(position);

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.row_comment_list, null);

            final View temp = convertView;
            holder = new ViewHolder();
            holder.tvCommentedBy = (TextView) convertView.findViewById(R.id.row_comment_list_tvCommentedBy);
            holder.tvComment = (TextView) convertView.findViewById(R.id.row_comment_list_tvComment);
            holder.tvTime = (TextView) temp.findViewById(R.id.row_comment_list_tvTime);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        if (comment.isAnonymous())
            holder.tvCommentedBy.setText("Anonymous");
        else
            holder.tvCommentedBy.setText(comment.getCommentBy());

        if (!TextUtils.isEmpty(comment.getComment()))
            holder.tvComment.setText(comment.getComment());

        if (!TextUtils.isEmpty(comment.getTime()))
            holder.tvTime.setText(comment.getTime());

        return convertView;
    }

    public static class ViewHolder {
        public TextView tvCommentedBy, tvComment, tvTime;
    }
}
