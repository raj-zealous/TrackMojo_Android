package com.bluewavevision.tracmojo.ui;

import java.util.HashMap;
import java.util.Stack;

import com.bluewavevision.tracmojo.R;
import com.bluewavevision.tracmojo.fragments.EditTracFragment;
import com.bluewavevision.tracmojo.fragments.HomeFragment;
import com.bluewavevision.tracmojo.fragments.SettingsFragment;
import com.bluewavevision.tracmojo.util.AppSession;
import com.bluewavevision.tracmojo.util.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DashboardActivity extends BaseActivity {

    private Context mContext;

    public RelativeLayout rlHome, rlSettings, rlAddTrac, rlEditTrac,relPendingCount;
    public TextView tvHome, tvSetting, tvAddTrac, tvEditTrac,tvPendingCount;
    public ImageView ivHome, ivSetting, ivAddTrac, ivEditTrac;
    public LinearLayout llAddTracDialog;
    private RelativeLayout relAddPersonalTrac, relAddGroupTrac;

    //FOR TABBING
    public HashMap<String, Stack<Fragment>> mStacks;
    private int tabindex;
    public String mCurrentTab;
    private boolean isAddTracDialogOpen;
    private SharedPreferences preferences;

    private boolean isForNotification;

    public int groupPosition = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mContext = this;

        
        
        Intent intent = getIntent();

        try{
            String action = intent.getAction().toUpperCase();

            if(action != null){
//                if(action.equalsIgnoreCase(getResources().getString(R.string.notification_action_friend))){
//                        goFrag(getResources().getInteger(R.integer.FRAG_A_INT));
//                    }
//                if(action.equalsIgnoreCase(getResources().getString(R.string.notification_action_article))){
//                        goFrag(getResources().getInteger(R.integer.FRAG_B_INT));
//                    }
//                if(action.equalsIgnoreCase(getResources().getString(R.string.notification_action_points))){
//                        goFrag(getResources().getInteger(R.integer.FRAG_C_INT));
//                    }
//                if(action.equalsIgnoreCase(getResources().getString(R.string.notification_action_redeemable))){
//                        goFrag(getResources().getInteger(R.integer.FRAG_D_INT));
//                    }
//                if(action.equalsIgnoreCase(getResources().getString(R.string.notification_action_dance))){
//                        goFrag(getResources().getInteger(R.integer.FRAG_E_INT));
//                    }
                }else{
                    Log.d("", "Intent was null");
                }
            }catch(Exception e){
                Log.e("", "Problem consuming action from intent", e);              
            }
        
        
        AppSession session = new AppSession(mContext);
        preferences = session.getPreferences();

        initializeComponents();

        mStacks = new HashMap<String, Stack<Fragment>>();
        mStacks.put(Util.TAB_HOME, new Stack<Fragment>());
        mStacks.put(Util.TAB_SETTINGS, new Stack<Fragment>());
        mStacks.put(Util.TAB_ADD_TRAC, new Stack<Fragment>());
        mStacks.put(Util.TAB_EDIT_TRAC, new Stack<Fragment>());

        tabindex = getIntent().getIntExtra("tabindex", 1);
        isForNotification = getIntent().getBooleanExtra("isForNotification",false);
        if(isForNotification)
            handleNotification(getIntent());
        else
            setInitialTab();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AccountSettingsActivity.FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION);

        registerReceiver(broadcastReceiverForLogout, intentFilter);
    }

    public BroadcastReceiver broadcastReceiverForLogout = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("onReceive", "Logout in progress");
            //At this point you should start the login activity and finish this one
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiverForLogout!=null){
            unregisterReceiver(broadcastReceiverForLogout);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        Log.e("onnewIntent","calllllllllllllllllllllllllleeeeeeeee");
        handleNotification(intent);
    }

    private void handleNotification(Intent intent){
        String type = "" + intent.getStringExtra("type");

        groupPosition = intent.getIntExtra("groupPosition",-1);

        if (type.equalsIgnoreCase("follow_invitation")) {
            refreshEditTracTab("");
        } else if (type.equalsIgnoreCase("respondtracinvitation")) {
            setHomeTab("");
        } else if (type.equalsIgnoreCase("trac_changed")) {
            setHomeTab("");
        } else if (type.equalsIgnoreCase("trac_deleted")) {
            setHomeTab("");
        }else if (type.equalsIgnoreCase("tracrated")) {
            setHomeTab("");
        } else {
            setHomeTab("");
            String message = intent.getStringExtra("message");
            if (message != null && !TextUtils.isEmpty(message)) {
                Util.showAlertDialog(mContext, getString(R.string.app_name), message);
            }
        }
    }

    private void initializeComponents() {
        relPendingCount = (RelativeLayout) findViewById(R.id.activity_dashboard_relNotificationCount);
        tvPendingCount = (TextView) findViewById(R.id.activity_dashboard_tvNotificationCount);

        rlHome = (RelativeLayout) findViewById(R.id.activity_dashboard_llHome);
        rlSettings = (RelativeLayout) findViewById(R.id.activity_dashboard_llSettings);
        rlAddTrac = (RelativeLayout) findViewById(R.id.activity_dashboard_llAddTrac);
        rlEditTrac = (RelativeLayout) findViewById(R.id.activity_dashboard_llEditTrac);
        rlHome.setOnClickListener(this);
        rlSettings.setOnClickListener(this);
        rlAddTrac.setOnClickListener(this);
        rlEditTrac.setOnClickListener(this);

        tvHome = (TextView) findViewById(R.id.activity_dashboard_tvHome);
        tvSetting = (TextView) findViewById(R.id.activity_dashboard_tvSettings);
        tvAddTrac = (TextView) findViewById(R.id.activity_dashboard_tvAddTrac);
        tvEditTrac = (TextView) findViewById(R.id.activity_dashboard_tvEdittrac);

        ivHome = (ImageView) findViewById(R.id.activity_dashboard_ivHome);
        ivSetting = (ImageView) findViewById(R.id.activity_dashboard_ivSettings);
        ivAddTrac = (ImageView) findViewById(R.id.activity_dashboard_ivAddTrac);
        ivEditTrac = (ImageView) findViewById(R.id.activity_dashboard_ivEditTrac);

        llAddTracDialog = (LinearLayout) findViewById(R.id.activity_dashboard_llCustomAddTrac);
        relAddPersonalTrac = (RelativeLayout) findViewById(R.id.custom_add_trac_dialog_relAddPersonalTrac);
        relAddGroupTrac = (RelativeLayout) findViewById(R.id.custom_add_trac_dialog_relAddGroupTrac);

        relAddPersonalTrac.setOnClickListener(this);
        relAddGroupTrac.setOnClickListener(this);
    }

    private void setInitialTab() {
        if (tabindex == 1) {
            ivHome.setImageResource(R.drawable.ic_home_blue);
            tvHome.setTextColor(getResources().getColor(R.color.blue_button_default_color));

            mCurrentTab = Util.TAB_HOME;
            pushFragments(Util.TAB_HOME, new HomeFragment(), false, true);

        } else if (tabindex == 2) {

            ivSetting.setImageResource(R.drawable.ic_home_setting_blue);
            tvSetting.setTextColor(getResources().getColor(R.color.blue_button_default_color));

            mCurrentTab = Util.TAB_SETTINGS;
            pushFragments(Util.TAB_SETTINGS, new SettingsFragment(), false, true);
        } else if (tabindex == 3) {
            ivEditTrac.setImageResource(R.drawable.ic_home_edit_blue);
            tvEditTrac.setTextColor(getResources().getColor(R.color.blue_button_default_color));

            mCurrentTab = Util.TAB_EDIT_TRAC;
            pushFragments(Util.TAB_EDIT_TRAC, new EditTracFragment(), false, true);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        try {

            if (mStacks.get(mCurrentTab).size() == 1) {
                Log.e("back--", ">" + mStacks.get(mCurrentTab).size());
                finish();
            } else {
                Log.e("pop--", ">" + mStacks.get(mCurrentTab).size());
                popFragments();
                Log.e("poped--", ">" + mStacks.get(mCurrentTab).size());
            }
        } catch (NullPointerException ne){
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void pushFragments(String tag, Fragment fragment, boolean shouldAnimate, boolean shouldAdd) {
        if (shouldAdd) {
            Log.e("Conv", "tag " + tag);
            mStacks.get(tag).push(fragment);
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if (shouldAnimate)
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        ft.replace(R.id.activity_dashboard_flContent, fragment);
        ft.commit();
    }


    public void popFragments() {
        Fragment fragment = mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - 2);
        mStacks.get(mCurrentTab).pop();
        Log.e("Size", "tag " + mCurrentTab + " " + mStacks.get(mCurrentTab).size());

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.activity_dashboard_flContent, fragment);
        ft.commit();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        String tabId = "";
        switch (v.getId()) {
            case R.id.activity_dashboard_llHome:
                setHomeTab(tabId);
                break;
            case R.id.activity_dashboard_llSettings:
                if(Util.checkConnection(mContext,getString(R.string.not_available_while_offlin)))
                    setSettingsTab(tabId);
                break;

            case R.id.activity_dashboard_llAddTrac:
                if(Util.checkConnection(mContext,getString(R.string.not_available_while_offlin))){
                    if (isAddTracDialogOpen) {
                        isAddTracDialogOpen = false;
                        llAddTracDialog.setVisibility(View.GONE);
                        ivAddTrac.setImageResource(R.drawable.ic_home_add_gray);
                        tvAddTrac.setTextColor(getResources().getColor(R.color.tab_text_gray));
                    } else {
                        isAddTracDialogOpen = true;
                        llAddTracDialog.setVisibility(View.VISIBLE);
                        ivAddTrac.setImageResource(R.drawable.ic_home_add_red);
                        tvAddTrac.setTextColor(getResources().getColor(R.color.home_add_trac_red));
                    }
                }

                break;

            case R.id.activity_dashboard_llEditTrac:
                if(Util.checkConnection(mContext,getString(R.string.not_available_while_offlin)))
                    setEditTracTab(tabId);
                break;

            case R.id.custom_add_trac_dialog_relAddPersonalTrac:
                goToAddPersonalTrac();
                break;

            case R.id.custom_add_trac_dialog_relAddGroupTrac:
                goToAddGroupTrac();
                break;

            default:

                break;
        }
    }

    private void goToAddPersonalTrac() {
        Intent intent = new Intent(mContext, PersonalTracAddActivity.class);
        startActivity(intent);
        isAddTracDialogOpen = false;
        llAddTracDialog.setVisibility(View.GONE);
        ivAddTrac.setImageResource(R.drawable.ic_home_add_gray);
        tvAddTrac.setTextColor(getResources().getColor(R.color.tab_text_gray));
    }

    private void goToAddGroupTrac() {
        Intent intent = new Intent(mContext, GroupTracAddActivity.class);
        startActivity(intent);
        isAddTracDialogOpen = false;
        llAddTracDialog.setVisibility(View.GONE);
        ivAddTrac.setImageResource(R.drawable.ic_home_add_gray);
        tvAddTrac.setTextColor(getResources().getColor(R.color.tab_text_gray));
    }

    private void setEditTracTab(String tabId) {
        llAddTracDialog.setVisibility(View.INVISIBLE);
        //setting images
        ivEditTrac.setImageResource(R.drawable.ic_home_edit_blue);
        ivSetting.setImageResource(R.drawable.ic_home_setting_gray);
        ivHome.setImageResource(R.drawable.ic_home_gray);
        ivAddTrac.setImageResource(R.drawable.ic_home_add_gray);

        //setting textcolors
        tvEditTrac.setTextColor(getResources().getColor(R.color.blue_button_default_color));
        tvSetting.setTextColor(getResources().getColor(R.color.tab_text_gray));
        tvHome.setTextColor(getResources().getColor(R.color.tab_text_gray));
        tvAddTrac.setTextColor(getResources().getColor(R.color.tab_text_gray));

        if (!mCurrentTab.equals(Util.TAB_EDIT_TRAC)) {

            mCurrentTab = Util.TAB_EDIT_TRAC;
            tabId = Util.TAB_EDIT_TRAC;
            if (mStacks.get(mCurrentTab).size() == 0) {
                pushFragments(tabId, new EditTracFragment(), false, true);
            } else {
                pushFragments(tabId, mStacks.get(tabId).lastElement(), false, false);
            }
        }
    }

    private void setHomeTab(String tabId) {
        llAddTracDialog.setVisibility(View.INVISIBLE);
        //setting images
        ivHome.setImageResource(R.drawable.ic_home_blue);
        ivSetting.setImageResource(R.drawable.ic_home_setting_gray);
        ivAddTrac.setImageResource(R.drawable.ic_home_add_gray);
        ivEditTrac.setImageResource(R.drawable.ic_home_edit_gray);

        //setting textcolors
        tvHome.setTextColor(getResources().getColor(R.color.blue_button_default_color));
        tvSetting.setTextColor(getResources().getColor(R.color.tab_text_gray));
        tvAddTrac.setTextColor(getResources().getColor(R.color.tab_text_gray));
        tvEditTrac.setTextColor(getResources().getColor(R.color.tab_text_gray));

        mCurrentTab = Util.TAB_HOME;
        tabId = Util.TAB_HOME;
        if (!mCurrentTab.equals(Util.TAB_HOME)) {
            if (mStacks.get(mCurrentTab).size() == 0) {
                HomeFragment homeFragment = new HomeFragment();
                pushFragments(tabId, homeFragment, false, true);
            } else {
                pushFragments(tabId, mStacks.get(tabId).lastElement(), false, false);
            }
        }else {
            if (mStacks.get(mCurrentTab) != null && mStacks.get(mCurrentTab).size() > 0) {
                mStacks.get(mCurrentTab).clear();
            }
            HomeFragment homeFragment = new HomeFragment();
            pushFragments(tabId, homeFragment, false, true);
        }
    }

    private void setSettingsTab(String tabId) {
        llAddTracDialog.setVisibility(View.INVISIBLE);
        //setting images
        ivSetting.setImageResource(R.drawable.ic_home_setting_blue);
        ivHome.setImageResource(R.drawable.ic_home_gray);
        ivAddTrac.setImageResource(R.drawable.ic_home_add_gray);
        ivEditTrac.setImageResource(R.drawable.ic_home_edit_gray);

        //setting textcolors
        tvSetting.setTextColor(getResources().getColor(R.color.blue_button_default_color));
        tvHome.setTextColor(getResources().getColor(R.color.tab_text_gray));
        tvAddTrac.setTextColor(getResources().getColor(R.color.tab_text_gray));
        tvEditTrac.setTextColor(getResources().getColor(R.color.tab_text_gray));

        if (!mCurrentTab.equals(Util.TAB_SETTINGS)) {

            mCurrentTab = Util.TAB_SETTINGS;
            tabId = Util.TAB_SETTINGS;
            if (mStacks.get(mCurrentTab).size() == 0) {
                pushFragments(tabId, new SettingsFragment(), false, true);
            } else {
                pushFragments(tabId, mStacks.get(tabId).lastElement(), false, false);
            }
        }
    }

    private void refreshEditTracTab(String tabId) {
        llAddTracDialog.setVisibility(View.INVISIBLE);
        //setting images
        ivEditTrac.setImageResource(R.drawable.ic_home_edit_blue);
        ivSetting.setImageResource(R.drawable.ic_home_setting_gray);
        ivHome.setImageResource(R.drawable.ic_home_gray);
        ivAddTrac.setImageResource(R.drawable.ic_home_add_gray);

        //setting textcolors
        tvEditTrac.setTextColor(getResources().getColor(R.color.blue_button_default_color));
        tvSetting.setTextColor(getResources().getColor(R.color.tab_text_gray));
        tvHome.setTextColor(getResources().getColor(R.color.tab_text_gray));
        tvAddTrac.setTextColor(getResources().getColor(R.color.tab_text_gray));

        mCurrentTab = Util.TAB_EDIT_TRAC;
        tabId = Util.TAB_EDIT_TRAC;
        if (!mCurrentTab.equals(Util.TAB_EDIT_TRAC)) {
            if (mStacks.get(mCurrentTab).size() == 0) {
                pushFragments(tabId, new EditTracFragment(), false, true);
            } else {
                pushFragments(tabId, mStacks.get(tabId).lastElement(), false, false);
            }
        } else{
            if (mStacks.get(mCurrentTab) != null && mStacks.get(mCurrentTab).size() > 0) {
                mStacks.get(mCurrentTab).clear();
            }
            pushFragments(tabId, new EditTracFragment(), false, true);
        }
    }
}
