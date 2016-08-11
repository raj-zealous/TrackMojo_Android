package com.bluewavevision.tracmojo.fragments;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bluewavevision.tracmojo.R;
import com.bluewavevision.tracmojo.adapters.CommentListAdapter;
import com.bluewavevision.tracmojo.model.Comment;
import com.bluewavevision.tracmojo.util.Util;
import com.bluewavevision.tracmojo.webservice.VolleyStringRequest;
import com.bluewavevision.tracmojo.webservice.Webservices;

public class CommentListFragment extends BaseFragment implements OnClickListener {
	View llLayout;

    private ListView lvCommentList;
    private static final String FORGOT_EMAIL_ID = "email_id";
    private ArrayList<Comment> listComment = new ArrayList<Comment>();
    private CommentListAdapter commentListAdapter;

    private int tracId;
    public boolean isLastEvent;
    public boolean flag_loading;
    protected int start = 0;

    private TextView tvAddComment;
    private boolean isOwner;
    private boolean isFollower;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null){
            tracId = bundle.getInt("tracId");
            isOwner = bundle.getBoolean("isOwner",false);
            isFollower = bundle.getBoolean("isFollower",false);
        }

    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

        mContext.tvHeader.setVisibility(View.GONE);
        mContext.ivInfo.setVisibility(View.GONE);
        mContext.ivBack.setVisibility(View.VISIBLE);
        mContext.ivHelp.setVisibility(View.GONE);
        if(isOwner)
            mContext.ivEmailCommentList.setVisibility(View.VISIBLE);
        else
            mContext.ivEmailCommentList.setVisibility(View.GONE);

		if(llLayout==null){
			llLayout = inflater.inflate(R.layout.fragment_comment_list, container, false);
            tvAddComment = (TextView) llLayout.findViewById(R.id.fragment_comment_list_tvAddComment);
            tvAddComment.setOnClickListener(this);

            if(isFollower)
                tvAddComment.setVisibility(View.GONE);

            lvCommentList = (ListView) llLayout.findViewById(R.id.fragment_comment_list_lvCommentList);
            commentListAdapter = new CommentListAdapter(mContext,listComment);
            lvCommentList.setAdapter(commentListAdapter);

            if(Util.checkConnection(mContext))
                getCommentList();

            lvCommentList.setOnScrollListener(new AbsListView.OnScrollListener() {

                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {

                    if (firstVisibleItem + visibleItemCount == totalItemCount
                            && totalItemCount != 0) {
                        if (flag_loading == false && !isLastEvent) {

                            flag_loading = true;
                            start ++;

                            if(Util.checkConnection(mContext)) {
                                getCommentList();
                            }
                        }
                    }
                }
            });
        } else {
			((ViewGroup) llLayout.getParent()).removeView(llLayout);
		}

        mContext.ivEmailCommentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Util.showAlertDialog(mContext, getString(R.string.app_name), getString(R.string.help_text_for_trac_review_screen));
                showEmailCommentListDialog();
            }
        });
		return llLayout;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
        switch (v.getId()){
            case R.id.fragment_comment_list_tvAddComment:
                if(Util.checkConnection(mContext)){
                    showCommentDialog();
                }
            default:

                break;
        }
	}

    private void getCommentList() {
        VolleyStringRequest request = new VolleyStringRequest(
                Request.Method.POST, Webservices.GET_COMMENT_LIST,
                getCommentListSuccessLisner(), ErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", ""+prefs.getInt("userid", -1));
                params.put("trac_id", ""+tracId);
                params.put("page", ""+start);
                return params;
            }
        };

        // ***************Requesting Queue

        showProgress();
        mQueue.add(request);
    }

    private com.android.volley.Response.Listener<String> getCommentListSuccessLisner() {
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
                        String isLast = jsonObject.getString("is_last");
                        if (isLast.equalsIgnoreCase("y"))
                            isLastEvent = true;
                        else{
                            isLastEvent = false;
                            flag_loading = false;
                        }

                        JSONArray jsonCommentArray = jsonObject.getJSONArray("comments");
                        if(jsonCommentArray!=null){
                            for (int i = 0; i < jsonCommentArray.length(); i++) {
                                JSONObject object = jsonCommentArray.getJSONObject(i);
                                int id = object.getInt("id");
                                String comment = object.getString("comment");
                                String commentedBy = object.getString("comment_by_name");
                                String date = object.getString("i_date");
                                String time = object.getString("time");
                                String strAnonymous = "" + object.getString("is_anonymous");

                                boolean isAnonymous = false;
                                if(strAnonymous.equalsIgnoreCase("y")){
                                    isAnonymous = true;
                                } else
                                    isAnonymous = false;

                                Comment commentModel = new Comment(id,comment,date,time,commentedBy);
                                commentModel.setIsAnonymous(isAnonymous);
                                listComment.add(commentModel);
                            }
                        }

                        if(listComment.size()<0){
                            mContext.ivEmailCommentList.setVisibility(View.GONE);
                        }

                        commentListAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener ErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Json Error", "==> " + error.getMessage());
                stopProgress();
                if(listComment.size()==0){
                    mContext.ivEmailCommentList.setVisibility(View.GONE);
                }
                Util.showMessage(mContext, error.getMessage());
            }
        };
    }

    private void showEmailCommentListDialog(){
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_forgot_password);

        Button btnSend = (Button) dialog.findViewById(R.id.fogot_pass_btn_send);
        Button btnCancel = (Button) dialog.findViewById(R.id.fogot_pass_btn_cancel);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.dialog_title);
        tvTitle.setText(getString(R.string.custom_send_email_dialog_title));
        final EditText etForgotEmail = (EditText) dialog.findViewById(R.id.fogot_pass_et_email);

        btnSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(
                        Context.INPUT_METHOD_SERVICE);

                if(Util.isEditTextEmpty(etForgotEmail))
                {
                    imm.hideSoftInputFromWindow(etForgotEmail.getWindowToken(), 0);
                    Util.showMessage(mContext, getString(R.string.activity_login_enter_email));
                }
                else if(!Util.emailValidator(etForgotEmail.getText().toString().trim()))
                {
                    imm.hideSoftInputFromWindow(etForgotEmail.getWindowToken(), 0);
                    Util.showMessage(mContext, getString(R.string.activity_login_enter_valid_email));
                }else
                {
                    imm.hideSoftInputFromWindow(etForgotEmail.getWindowToken(), 0);
                    dialog.dismiss();

                    if(Util.checkConnection(mContext)){
                        emailCommentList(etForgotEmail.getText().toString().trim());
                    }
                }

            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private void emailCommentList(final String email) {
        VolleyStringRequest request = new VolleyStringRequest(
                Request.Method.POST, Webservices.EMAIL_COMMENT_LIST,
                emailCommentListSuccessListener(), ErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", ""+prefs.getInt("userid", -1));
                params.put("trac_id", ""+tracId);
                params.put(FORGOT_EMAIL_ID, "" + email);
                return params;
            }
        };

        showProgress();
        mQueue.add(request);
    }

    private com.android.volley.Response.Listener<String> emailCommentListSuccessListener() {
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
                        responseMessage = jsonObject.optString("message");
                        Util.showMessage(mContext, responseMessage);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    private void showCommentDialog(){
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_send_comment);

        Button btnSend = (Button) dialog.findViewById(R.id.fogot_pass_btn_send);
        Button btnCancel = (Button) dialog.findViewById(R.id.fogot_pass_btn_cancel);

        final EditText etMessage = (EditText) dialog.findViewById(R.id.fogot_pass_et_email);
        etMessage.setHint(getString(R.string.add_comment_dialog_hint));
        TextView textView = (TextView) dialog.findViewById(R.id.dialog_title);
        textView.setText(getString(R.string.add_comment_dialog_title));
        final CheckBox cbAnonymous = (CheckBox) dialog.findViewById(R.id.add_comment_dialog_cbAnonymous);
        final CheckBox cbForOwnerOnly = (CheckBox) dialog.findViewById(R.id.add_comment_dialog_cbForOwnerOnly);


        btnSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(
                        Context.INPUT_METHOD_SERVICE);

                if(Util.isEditTextEmpty(etMessage))
                {
                    imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
                    Util.showMessage(mContext, getString(R.string.add_comment_dialog_enter_comment));
                }
                else
                {
                    imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
                    dialog.dismiss();

                    if(Util.checkConnection(mContext)){
                        String strAnonymous = "";
                        if(cbAnonymous.isChecked())
                            strAnonymous = "y";
                        else
                            strAnonymous = "n";

                        String strForOwnerOnly = "";
                        if(cbForOwnerOnly.isChecked())
                            strForOwnerOnly = "y";
                        else
                            strForOwnerOnly = "n";

                        addComment(etMessage.getText().toString().trim(),strAnonymous,strForOwnerOnly);
                    }
                }

            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private void addComment(final String comment, final String isAnonymous, final String isOwnerOnly) {
        VolleyStringRequest request = new VolleyStringRequest(
                Request.Method.POST, Webservices.ADD_COMMENT,
                addCommentSuccessLisner(), addRateErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", ""+prefs.getInt("userid", -1));
                params.put("trac_id", ""+tracId);
                params.put("comment", ""+comment);
                params.put("is_anonymous", ""+isAnonymous);
                params.put("for_admin_only", ""+isOwnerOnly);
                return params;
            }
        };

        // ***************Requesting Queue

        showProgress();
        mQueue.add(request);
    }

    private com.android.volley.Response.Listener<String> addCommentSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String arg0) {
                // TODO Auto-generated method stub
                Log.e("Json", "==> " + arg0);
                stopProgress();

                try {
                    JSONObject jsonObject = new JSONObject(arg0);
                    String message = "" + jsonObject.getString("message");
                    Util.showMessage(mContext, message);

                    if(Util.checkConnection(mContext)){
                        start = 0;
                        listComment.clear();
                        getCommentList();
                    }

                    prefs.edit().putBoolean("isCommentCountUpdated",true).commit();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener addRateErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Json Error", "==> " + error.getMessage());
                stopProgress();
                Util.showMessage(mContext, error.getMessage());
            }
        };
    }
}
