package com.bluewavevision.tracmojo.fragments;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bluewavevision.tracmojo.R;
import com.bluewavevision.tracmojo.TracMojoApplication;
import com.bluewavevision.tracmojo.adapters.HomeExpandableListAdapter;
import com.bluewavevision.tracmojo.backgroundservices.DownloadResultReceiver;
import com.bluewavevision.tracmojo.backgroundservices.DownloadService;
import com.bluewavevision.tracmojo.database.RemDBAdapter;
import com.bluewavevision.tracmojo.database.RemDBHelper;
import com.bluewavevision.tracmojo.model.RateColor;
import com.bluewavevision.tracmojo.model.RateOption;
import com.bluewavevision.tracmojo.model.Trac;
import com.bluewavevision.tracmojo.ui.PersonalTracAddActivity;
import com.bluewavevision.tracmojo.util.Util;
import com.bluewavevision.tracmojo.webservice.VolleyStringRequest;
import com.bluewavevision.tracmojo.webservice.Webservices;

public class HomeFragment extends BaseFragment implements OnClickListener, TracRateFragment.Updateable, DownloadResultReceiver.Receiver, EditTracFragment.RefreshTracList {

    //WEB-SERVICES GET TRAC LIST
    private static final String USER_ID = "user_id";
    private static final String TRAC_TYPE = "load_more";
    private static final String START = "page";

    private View llLayout;
    private RelativeLayout relAddPersonalTrac;
    private LinearLayout llCustomAddTracDialog;
    private TextView tvNoTracFound;
    private HomeExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private TracMojoApplication application;

    private List<String> listDataHeader = new ArrayList<String>();
    private HashMap<String, List<Trac>> listDataChild = new HashMap<String, List<Trac>>();
    private ArrayList<Trac> listPersonalTrac = new ArrayList<Trac>();
    private ArrayList<Trac> listGroupTrac = new ArrayList<Trac>();
    private ArrayList<Trac> listFollowersTrac = new ArrayList<Trac>();

    private int startForPersonal = 0, startForGroup = 0, startforFollowers = 0;

    public boolean isLastPersonalTrac, isLastGroupTrac, isLastFollowersTrac;
    public boolean flag_loading;
    private int expandedGroupPosition;

    private boolean isAddTracDialogOpen;

    //for background download
    private DownloadResultReceiver mReceiver;
    RemDBAdapter remDBAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub


        application = (TracMojoApplication) mContext.getApplicationContext();
        mContext.tvHeader.setVisibility(View.GONE);
        mContext.ivBack.setVisibility(View.GONE);
        mContext.ivInfo.setVisibility(View.VISIBLE);
        mContext.ivHelp.setVisibility(View.GONE);
        mContext.ivEmailCommentList.setVisibility(View.GONE);

        if (llLayout == null) {
            llLayout = inflater.inflate(R.layout.fragment_home, container, false);
            // get the listview
            tvNoTracFound = (TextView) llLayout.findViewById(R.id.fragment_home_tvNoTracFound);
            expListView = (ExpandableListView) llLayout.findViewById(R.id.lvExp);
            expListView.setGroupIndicator(null);

            llCustomAddTracDialog = (LinearLayout) llLayout.findViewById(R.id.fragment_home_llCustomAddDialog);
            relAddPersonalTrac = (RelativeLayout) llCustomAddTracDialog.findViewById(R.id.custom_add_trac_dialog_relAddPersonalTrac);
            relAddPersonalTrac.setOnClickListener(this);

            listDataHeader.add(getString(R.string.activity_dashboard_my_tracs));
            listDataHeader.add(getString(R.string.activity_dashboard_group_tracs));
            listDataHeader.add(getString(R.string.activity_dashboard_followed_tracs));

            expListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem + visibleItemCount == totalItemCount
                            && totalItemCount != 0 && expListView.isGroupExpanded(expandedGroupPosition)) {

                        switch (expandedGroupPosition + 1) {
                            case Trac.PERSONAL_TRAC:
                                if (flag_loading == false && !isLastPersonalTrac) {
                                    flag_loading = true;
                                    startForPersonal++;
                                    if (Util.checkConnection(mContext)) {
                                        getTracListForLoadMore();
                                        Log.e("LoadMore", "......Personal");
                                    }
                                }
                                break;
                            case Trac.GROUP_TRAC:
                                if (flag_loading == false && !isLastGroupTrac) {
                                    flag_loading = true;
                                    startForGroup++;
                                    if (Util.checkConnection(mContext)) {
                                        getTracListForLoadMore();
                                        Log.e("LoadMore", "......Group");
                                    }
                                }
                                break;
                            case Trac.FOLLOWING_TRAC:
                                if (flag_loading == false && !isLastFollowersTrac) {
                                    flag_loading = true;

                                    startforFollowers++;
                                    if (Util.checkConnection(mContext)) {
                                        getTracListForLoadMore();
                                        Log.e("LoadMore", "......Following");
                                    }
                                }
                                break;

                            default:
                                break;
                        }


                    }
                }
            });

            //Listview Group Header item Click
            expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                    return false;
                }
            });


            // Listview Group expanded listener
            expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int previousGroup = -1;

                @Override
                public void onGroupExpand(int groupPosition) {
                    /*Toast.makeText(getApplicationContext(),
                            listDataHeader.get(groupPosition) + " Expanded",
                            Toast.LENGTH_SHORT).show();*/
                    expandedGroupPosition = groupPosition;
                    prefs.edit().putInt("groupPosition",expandedGroupPosition).apply();
                    if (groupPosition != previousGroup)
                        expListView.collapseGroup(previousGroup);
                    previousGroup = groupPosition;
                    flag_loading = false;

                    if (groupPosition == Trac.PERSONAL_TRAC - 1) {
                        if (listPersonalTrac.size() == 0) {
                            //Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.activity_dashboard_home_no_personal_trac_found));
                            //Util.showMessage(mContext,getString(R.string.activity_dashboard_home_no_personal_trac_found));
                        }
                    } else if (groupPosition == Trac.GROUP_TRAC - 1) {
                        if (listGroupTrac.size() == 0) {
                            //Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.activity_dashboard_home_no_group_trac_found));
                            // Util.showMessage(mContext,getString(R.string.activity_dashboard_home_no_group_trac_found));
                        }
                    } else if (groupPosition == Trac.FOLLOWING_TRAC - 1) {
                        if (listFollowersTrac.size() == 0) {
                            //Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.activity_dashboard_home_not_following_any_tracs));
                            //Util.showMessage(mContext,getString(R.string.activity_dashboard_home_not_following_any_tracs));
                        }
                    }
                }
            });

            // Listview on child click listener
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    // TODO Auto-generated method stub
                   /* Toast.makeText(
                            mContext,
                            listDataHeader.get(groupPosition)
                                    + " : "
                                    + listDataChild.get(
                                    listDataHeader.get(groupPosition)).get(
                                    childPosition), Toast.LENGTH_SHORT)
                            .show();*/
                    return false;
                }
            });

        } else {
            ((ViewGroup) llLayout.getParent()).removeView(llLayout);
        }

        //For offline data
        /* Starting Download Service */
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, mContext, DownloadService.class);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("requestId", 101);

        mContext.startService(intent);

//        if (prefs.getBoolean("isFirstTimmer", true)) {
//            showAlertDialog(getString(R.string.app_name), getString(R.string.activity_dashboard_home_first_timer_message));
//        }

        if (Util.checkConnectionWithoutMessage(mContext)) {
            startforFollowers = 0;
            startForGroup = 0;
            startForPersonal = 0;

            //listDataHeader.clear();
            flag_loading = false;
            isLastPersonalTrac = true;
            isLastGroupTrac = true;
            isLastFollowersTrac = true;
            listPersonalTrac.clear();
            listGroupTrac.clear();
            listFollowersTrac.clear();
            //listDataChild.clear();
            //listAdapter = null;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            showProgress();
            getTracList();
        } else {
            flag_loading = false;
            isLastPersonalTrac = true;
            isLastGroupTrac = true;
            isLastFollowersTrac = true;
            listPersonalTrac.clear();
            listGroupTrac.clear();
            listFollowersTrac.clear();
            getTracListInOffline();
        }

        return llLayout;
    }

    public void hideAddTracDialog() {
        llCustomAddTracDialog.setVisibility(View.GONE);
        isAddTracDialogOpen = false;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.custom_add_trac_dialog_relAddPersonalTrac:
                goToAddPersonalTrac();
                break;
            default:
                break;
        }
    }

    private void goToAddPersonalTrac() {
        Intent intent = new Intent(mContext, PersonalTracAddActivity.class);
        startActivity(intent);
    }

    private void getTracListForLoadMore() {

        VolleyStringRequest getTracListLoadMoreRequest = new VolleyStringRequest(
                Request.Method.POST, Webservices.GET_TRAC_LIST,
                getTracListForLoadMoreSuccessLisner(), getTracListErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(USER_ID, "" + prefs.getInt("userid", -1));
                switch (expandedGroupPosition + 1) {
                    case Trac.PERSONAL_TRAC:
                        params.put(TRAC_TYPE, "" + Trac.PERSONAL_TRAC);
                        params.put(START, "" + startForPersonal);
                        break;

                    case Trac.GROUP_TRAC:
                        params.put(TRAC_TYPE, "" + Trac.GROUP_TRAC);
                        params.put(START, "" + startForGroup);
                        break;

                    case Trac.FOLLOWING_TRAC:
                        params.put(TRAC_TYPE, "" + Trac.FOLLOWING_TRAC);
                        params.put(START, "" + startforFollowers);
                        break;

                    default:
                        break;
                }
                params.put("is_home", "y");
                return params;
            }

            ;
        };

        showProgress();
        mQueue.add(getTracListLoadMoreRequest);
    }

    private void getTracListInOffline() {
        remDBAdapter = new RemDBAdapter(mContext);
        remDBAdapter.open();

        try {
            listPersonalTrac.addAll(remDBAdapter.getTracList(RemDBHelper.PERSONAL_TRAC));
            listDataChild.put(listDataHeader.get(0), listPersonalTrac);

            listGroupTrac.addAll(remDBAdapter.getTracList(RemDBHelper.GROUP_TRAC));
            listDataChild.put(listDataHeader.get(1), listGroupTrac);

            listFollowersTrac.addAll(remDBAdapter.getTracList(RemDBHelper.FOLLOWING_TRAC));
            listDataChild.put(listDataHeader.get(2), listFollowersTrac);

            listAdapter = new HomeExpandableListAdapter(mContext, listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
            if(prefs.getInt("groupPosition",-1)!=-1){
                expListView.expandGroup(prefs.getInt("groupPosition",0));
            } else {
                if (listDataHeader.size() != 0) {
                    if (listPersonalTrac != null && listPersonalTrac.size() > 0)
                        expListView.expandGroup(0);
                    else if (listGroupTrac != null && listGroupTrac.size() > 0)
                        expListView.expandGroup(1);
                    else if (listFollowersTrac != null && listFollowersTrac.size() > 0)
                        expListView.expandGroup(2);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getTracList() {
        VolleyStringRequest getTracListRequest = new VolleyStringRequest(
                Request.Method.POST, Webservices.GET_TRAC_LIST,
                getTracListSuccessLisner(), getTracListErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put(USER_ID, "" + prefs.getInt("userid", -1));
                params.put(TRAC_TYPE, "" + Trac.ALL_TRACS);
                params.put(START, "0");
                params.put("is_home", "y");
                return params;
            }

            ;
        };



        mQueue.add(getTracListRequest);
    }

    private com.android.volley.Response.Listener<String> getTracListSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String arg0) {
                // TODO Auto-generated method stub
                Log.e("Json", "==> " + arg0);
                stopProgress();

                try {
                    JSONObject jsonObject = new JSONObject(arg0);
                    if (jsonObject != null) {

                        String pendingCount = "" + jsonObject.optString("pending_request_count");
                        mContext.tvPendingCount.setText(pendingCount);
                        if(!TextUtils.isEmpty(pendingCount)){
                            if(!pendingCount.equalsIgnoreCase("0")){
                                mContext.relPendingCount.setBackgroundResource(R.drawable.home_blue_circular_background);
                            }else {
                                mContext.relPendingCount.setBackgroundResource(R.drawable.home_gray_circular_background);
                            }
                        }

                        //PARSING COLOR FOR THE RATTING
                        ArrayList<RateColor> listRateColor = new ArrayList<RateColor>();
                        JSONArray jsonArrayRateColor = jsonObject.getJSONArray("rate_color");
                        if (jsonArrayRateColor != null) {
                            for (int i = 0; i < jsonArrayRateColor.length(); i++) {
                                JSONObject jsonRateColor = jsonArrayRateColor.getJSONObject(i);
                                String rate = jsonRateColor.getString("rate");
                                String rgb = jsonRateColor.getString("rgb");
                                String hash = jsonRateColor.getString("hex");

                                RateColor rateColor = new RateColor(rate, rgb, hash);
                                listRateColor.add(rateColor);
                            }
                            application.setListRateColor(listRateColor);
                        }

                        //PARSING PERSONAL TRAC
                        JSONObject jsonObjectPersonalTrac = jsonObject.getJSONObject("personal_trac");
                        if (jsonObjectPersonalTrac != null) {

                            String isLast = jsonObjectPersonalTrac.getString("is_last");
                            if (isLast.equalsIgnoreCase("Y"))
                                isLastPersonalTrac = true;
                            else {
                                isLastPersonalTrac = false;
                                flag_loading = false;
                            }

                            JSONArray jsonArrayPersonalTrac = jsonObjectPersonalTrac.getJSONArray("trac");
                            listPersonalTrac = getTracList(jsonArrayPersonalTrac);
                        }

                        //PARSING GROUP TRACS
                        JSONObject jsonObjectGroupTrac = jsonObject.getJSONObject("group_trac");
                        if (jsonObjectGroupTrac != null) {

                            String isLast = jsonObjectGroupTrac.getString("is_last");
                            if (isLast.equalsIgnoreCase("Y"))
                                isLastGroupTrac = true;
                            else {
                                isLastGroupTrac = false;
                                flag_loading = false;
                            }

                            JSONArray jsonArrayGroupTrac = jsonObjectGroupTrac.getJSONArray("trac");
                            listGroupTrac = getTracList(jsonArrayGroupTrac);
                        }

                        //PARSING FOLLOWING TRACS
                        JSONObject jsonObjectFollowingTracs = jsonObject.getJSONObject("following_trac");
                        if (jsonObjectFollowingTracs != null) {
                            String isLast = jsonObjectFollowingTracs.getString("is_last");
                            if (isLast.equalsIgnoreCase("Y"))
                                isLastFollowersTrac = true;
                            else {
                                isLastFollowersTrac = false;
                                flag_loading = false;
                            }
                            JSONArray jsonArrayFollowingTracs = jsonObjectFollowingTracs.getJSONArray("trac");
                            listFollowersTrac = getTracList(jsonArrayFollowingTracs);
                        }


                        listDataChild.put(listDataHeader.get(0), listPersonalTrac);
                        listDataChild.put(listDataHeader.get(1), listGroupTrac);
                        listDataChild.put(listDataHeader.get(2), listFollowersTrac);

                        listAdapter = new HomeExpandableListAdapter(mContext, listDataHeader, listDataChild);
                        expListView.setAdapter(listAdapter);

                        if(prefs.getInt("groupPosition",-1)!=-1){
                            expListView.expandGroup(prefs.getInt("groupPosition",0));
                        } else {
                            if(mContext.groupPosition !=-1){
                                expListView.expandGroup(mContext.groupPosition);
                            }else {
                                if (listDataHeader.size() != 0) {
                                    if (listPersonalTrac != null && listPersonalTrac.size() > 0)
                                        expListView.expandGroup(0);
                                    else if (listGroupTrac != null && listGroupTrac.size() > 0)
                                        expListView.expandGroup(1);
                                    else if (listFollowersTrac != null && listFollowersTrac.size() > 0)
                                        expListView.expandGroup(2);
                                }
                            }
                        }

                        mContext.groupPosition = -1;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener getTracListErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Json Error", "==> " + error.getMessage());
                stopProgress();
                Util.showMessage(mContext, error.getMessage());
            }
        };
    }

    private com.android.volley.Response.Listener<String> getTracListForLoadMoreSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String arg0) {
                // TODO Auto-generated method stub
                Log.e("Json", "==> " + arg0);
                stopProgress();

                try {
                    JSONObject jsonObject = new JSONObject(arg0);
                    if (jsonObject != null) {

                        switch (expandedGroupPosition + 1) {
                            case Trac.PERSONAL_TRAC:
                                //PARSING PERSONAL TRAC
                                JSONObject jsonObjectPersonalTrac = jsonObject.getJSONObject("personal_trac");
                                if (jsonObjectPersonalTrac != null) {

                                    String isLast = jsonObjectPersonalTrac.getString("is_last");
                                    if (isLast.equalsIgnoreCase("Y"))
                                        isLastPersonalTrac = true;
                                    else {
                                        isLastPersonalTrac = false;
                                        flag_loading = false;
                                    }

                                    JSONArray jsonArrayPersonalTrac = jsonObjectPersonalTrac.getJSONArray("trac");
                                    listPersonalTrac.addAll(getTracList(jsonArrayPersonalTrac));
                                }
                                break;

                            case Trac.GROUP_TRAC:
                                //PARSING GROUP TRACS
                                JSONObject jsonObjectGroupTrac = jsonObject.getJSONObject("group_trac");
                                if (jsonObjectGroupTrac != null) {

                                    String isLast = jsonObjectGroupTrac.getString("is_last");
                                    if (isLast.equalsIgnoreCase("Y"))
                                        isLastGroupTrac = true;
                                    else {
                                        isLastGroupTrac = false;
                                        flag_loading = false;
                                    }

                                    JSONArray jsonArrayGroupTrac = jsonObjectGroupTrac.getJSONArray("trac");
                                    listGroupTrac.addAll(getTracList(jsonArrayGroupTrac));
                                }
                                break;

                            case Trac.FOLLOWING_TRAC:
                                //PARSING FOLLOWING TRACS
                                JSONObject jsonObjectFollowingTracs = jsonObject.getJSONObject("following_trac");
                                if (jsonObjectFollowingTracs != null) {
                                    String isLast = jsonObjectFollowingTracs.getString("is_last");
                                    if (isLast.equalsIgnoreCase("Y"))
                                        isLastFollowersTrac = true;
                                    else {
                                        isLastFollowersTrac = false;
                                        flag_loading = false;
                                    }
                                    JSONArray jsonArrayFollowingTracs = jsonObjectFollowingTracs.getJSONArray("trac");
                                    listFollowersTrac.addAll(getTracList(jsonArrayFollowingTracs));
                                }
                                break;

                            default:
                                break;
                        }

                        if (listPersonalTrac.size() != 0) {
                            listDataChild.put(listDataHeader.get(0), listPersonalTrac);
                        }

                        if (listGroupTrac.size() != 0) {
                            listDataChild.put(listDataHeader.get(1), listGroupTrac);
                        }

                        if (listFollowersTrac.size() != 0) {
                            listDataChild.put(listDataHeader.get(2), listFollowersTrac);
                        }

                        listAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    private ArrayList<Trac> getTracList(JSONArray jsonArray) {
        ArrayList<Trac> tempList = new ArrayList<Trac>();
        try {
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectChildTrac = jsonArray.getJSONObject(i);
                    int id = jsonObjectChildTrac.getInt("id");
                    String goal = jsonObjectChildTrac.getString("goal");
                    String groupName = "" + jsonObjectChildTrac.optString("name");
                    String myTrac = jsonObjectChildTrac.getString("my_trac");
                    String groupType = jsonObjectChildTrac.optString("group_type");
                    boolean isMyTrac;
                    if (!TextUtils.isEmpty(myTrac) && myTrac.equalsIgnoreCase("y")) {
                        isMyTrac = true;
                    } else {
                        isMyTrac = false;
                    }
                    String rate = jsonObjectChildTrac.getString("rate");
                    boolean isNotiOn = false;
                    /*String notiOn = jsonObjectChildTrac.getString("noti_on");
                    if (!TextUtils.isEmpty(notiOn) && notiOn.equalsIgnoreCase("y")) {
                        isNotiOn = true;
                    } else {
                        isNotiOn = false;
                    }*/

                    JSONObject jsonRateOptions = jsonObjectChildTrac.optJSONObject("rate_option");
                    RateOption rateOption = null;
                    if (jsonRateOptions != null) {
                        rateOption = new RateOption();
                        rateOption.setOption1("" + jsonRateOptions.getString("option1"));
                        rateOption.setOption2("" + jsonRateOptions.getString("option2"));
                        rateOption.setOption3("" + jsonRateOptions.getString("option3"));
                        rateOption.setOption4("" + jsonRateOptions.getString("option4"));
                        rateOption.setOption5("" + jsonRateOptions.getString("option5"));
                    }

                    String rateFrequency = "" + jsonObjectChildTrac.optString("rating_frequency");
                    Log.e("rateFrequency", ":" + rateFrequency);
                    String lastRated = "" + jsonObjectChildTrac.optString("last_rated");
                    String lastRate = "" + jsonObjectChildTrac.optString("last_rate");

                    String requestStatus = "" + jsonObjectChildTrac.optString("request_status");

                    String ownerName = "" + jsonObjectChildTrac.optString("owner_name");

                    Trac trac = new Trac();
                    trac.setId(id);
                    trac.setGoal(goal);
                    trac.setGroupName(groupName);
                    trac.setGroupType(groupType);
                    trac.setMyTrac(isMyTrac);
                    trac.setRate(rate);
                    trac.setNotificationOn(isNotiOn);
                    trac.setRateOption(rateOption);
                    trac.setRateFrequency(rateFrequency);
                    trac.setLastRated(lastRated);
                    trac.setRequestStatus(requestStatus);
                    trac.setLastRate(lastRate);
                    trac.setOwnerName(ownerName);
                    tempList.add(trac);
                }
            }
        } catch (JSONException e) {

        }

        return tempList;
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);

        // set title
        alertDialogBuilder.setTitle(title);


        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        prefs.edit().putBoolean("isFirstTimmer", false).commit();
                        dialog.cancel();
                    }
                })/*.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				})*/;

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public int getFirstVisiblePostionOfListView() {
        return expListView.getFirstVisiblePosition();
    }

    @Override
    public void updateRow(int parentPostion, int childPosition, int fisrtVisiblePostion, String rate) {
        Log.e("ParentPosition", "" + parentPostion);
        Trac mTrac = listDataChild.get(listDataHeader.get(parentPostion)).get(childPosition);
        mTrac.setRate(rate);
        listDataChild.get(listDataHeader.get(parentPostion)).set(childPosition, mTrac);
        listAdapter.notifyDataSetChanged();
        expListView.setSelection(fisrtVisiblePostion);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadService.STATUS_RUNNING:
                Toast.makeText(mContext, "StartRunning", Toast.LENGTH_LONG).show();

                //setProgressBarIndeterminateVisibility(true);
                break;
            case DownloadService.STATUS_FINISHED:

                //Toast.makeText(this, "FinishRunning", Toast.LENGTH_LONG).show();
                /* Hide progress & extract result from bundle */
                //setProgressBarIndeterminateVisibility(false);

                /* Update ListView with result */
                /*arrayAdapter = new ArrayAdapter(MyActivity.this, android.R.layout.simple_list_item_2, results);
                listView.setAdapter(arrayAdapter);*/

                break;
            case DownloadService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.e("DownloadError", ":" + error);
                Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void refreshTracList(int parentPosition, int childPosition) {
        switch (parentPosition) {
            case 0:
                listPersonalTrac.remove(childPosition);
                break;
            case 1:
                listGroupTrac.remove(childPosition);
                break;
            case 2:
                listFollowersTrac.remove(childPosition);
                break;
            default:

                break;
        }
        listAdapter.notifyDataSetChanged();
    }
}
