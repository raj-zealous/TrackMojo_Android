package com.tracmojo.fragments;


import com.tracmojo.R;
import com.tracmojo.database.RemDBAdapter;
import com.tracmojo.ui.AccountSettingsActivity;
import com.tracmojo.ui.LoginActivity;
import com.tracmojo.ui.NotificationSettingsActivity;
import com.tracmojo.ui.ServiceActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingsFragment extends BaseFragment implements OnClickListener {
	View llLayout;

    private TextView tvLogout;
    private RelativeLayout relNotificationAndPrompts,relAccount,relService;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

        mContext.tvHeader.setVisibility(View.GONE);
        mContext.ivBack.setVisibility(View.GONE);
        mContext.ivInfo.setVisibility(View.VISIBLE);
        mContext.ivHelp.setVisibility(View.GONE);
        mContext.ivEmailCommentList.setVisibility(View.GONE);

		if(llLayout==null){
			llLayout = inflater.inflate(R.layout.fragment_settings, container, false);
            initializeComponents();

		} else {
			((ViewGroup) llLayout.getParent()).removeView(llLayout);
		}
		
		return llLayout;
	}

    private void initializeComponents(){

        relNotificationAndPrompts = (RelativeLayout) llLayout.findViewById(R.id.fragment_settings_relNotificationsAndPrompts);
        relAccount = (RelativeLayout) llLayout.findViewById(R.id.fragment_settings_relAccount);
        relService = (RelativeLayout) llLayout.findViewById(R.id.fragment_settings_relService);

        relNotificationAndPrompts.setOnClickListener(this);
        relAccount.setOnClickListener(this);
        relService.setOnClickListener(this);

       /* tvLogout = (TextView) llLayout.findViewById(R.id.fragment_settings_tvLogout);
        tvLogout.setOnClickListener(this);*/
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
         switch (v.getId()){
             /*case R.id.fragment_settings_tvLogout:
                 showLogoutDialog();
                 break;*/

             case R.id.fragment_settings_relNotificationsAndPrompts:
                 goToNotificationSettingsScreen();
                 break;
             case R.id.fragment_settings_relAccount:
                 goToAccountSettingsScreen();
                 break;
             case R.id.fragment_settings_relService:
                 goToServiceScreen();
                 break;
             default:

                 break;
         }
	}

    private void showLogoutDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);

        // set title
        alertDialogBuilder.setTitle(getString(R.string.logout_title));

        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.logout_text))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        /*if(Util.checkConnection(mContext)){
                            logout();
                        }*/
                        prefs.edit().remove("userid").commit();
                        prefs.edit().remove("timestamp").commit();
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        mContext.finish();

                        RemDBAdapter remDBAdapter = new RemDBAdapter(mContext);
                        remDBAdapter.open();
                        remDBAdapter.deleteAll();
                        remDBAdapter.close();

                    }
                }).setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                dialog.cancel();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void goToAccountSettingsScreen(){
        Intent intent = new Intent(mContext, AccountSettingsActivity.class);
        startActivity(intent);
    }

    private void goToNotificationSettingsScreen(){
        Intent intent = new Intent(mContext, NotificationSettingsActivity.class);
        startActivity(intent);
    }

    private void goToServiceScreen(){
        Intent intent = new Intent(mContext, ServiceActivity.class);
        startActivity(intent);
    }
}
