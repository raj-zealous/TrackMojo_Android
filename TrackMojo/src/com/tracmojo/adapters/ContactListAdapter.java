package com.tracmojo.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tracmojo.R;
import com.tracmojo.model.Contact;
import com.tracmojo.ui.ServiceActivity;


public class ContactListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater = null;
    public static boolean isChecked[];
    ArrayList<Contact> contacts, tempContacts;
    private boolean isForParticipants;

    public ContactListAdapter(Context con, ArrayList<Contact> contacts, boolean isForParticipants) {
        // TODO Auto-generated constructor stub

        this.contacts = contacts;
        //this.tempContacts = contacts;
        this.context = con;
        this.isForParticipants = isForParticipants;
        isChecked = new boolean[this.contacts.size()];

        this.tempContacts = new ArrayList<Contact>();
        this.tempContacts.addAll(contacts);
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
        final Contact contact = contacts.get(position);
        final int pos = position;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.row_contact_list, null);

            final View temp = convertView;
            holder = new ViewHolder();
            holder.tvContactName = (TextView) convertView.findViewById(R.id.tv_contact_name);
            holder.tvContactEmail = (TextView) convertView.findViewById(R.id.tv_contact_email);
            holder.ivTick = (ImageView) temp.findViewById(R.id.ivTick);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        if (contact.isSelected())
            holder.ivTick.setVisibility(View.VISIBLE);
        else {
            holder.ivTick.setVisibility(View.GONE);
        }
        holder.tvContactName.setText(contact.getName());
        holder.tvContactEmail.setText(contact.getEmail());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView iv = (ImageView) v.findViewById(R.id.ivTick);
                if (contact.isSelected()) {
                    contact.setSelected(false);
                    iv.setVisibility(View.GONE);
                } else {
                    contact.setSelected(true);
                    iv.setVisibility(View.VISIBLE);
                    /*if (isForParticipants) {
                        contact.setSelected(true);
                        iv.setVisibility(View.VISIBLE);
                        *//*if (Util.participantLeftCount > getSelectedContactSize()) {

						} else {
							showAlertDialog(context.getString(R.string.activity_invite_participants_max_limit_reached));
						}*//*
                    } else {
                        contact.setSelected(true);
                        iv.setVisibility(View.VISIBLE);
                    }*/
                }
            }
        });


        return convertView;

    }

    public static class ViewHolder {

        public TextView tvContactName, tvContactEmail;
        public ImageView ivTick;
    }
	
/*---------------------------Search---------------------------------------*/

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        contacts.clear();
        if (charText.length() == 0) {
            contacts.addAll(tempContacts);
        } else {
            for (Contact ct : tempContacts) {
                if (ct.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    contacts.add(ct);
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean isEmpty(int size) {
        boolean flag = false;
        for (int i = 0; i < this.contacts.size(); i++) {
            if (isChecked[i]) {
                return false;
            }
        }
        return true;
    }

    public void showAlertDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(context.getString(R.string.app_name));


        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        Intent intent = new Intent(context, ServiceActivity.class);
                        context.startActivity(intent);
                        dialog.cancel();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private int getSelectedContactSize() {
        int count = 0;
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).isSelected()) {
                count++;
            }
        }
        return count;
    }
}
