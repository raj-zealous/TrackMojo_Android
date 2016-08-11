package com.bluewavevision.tracmojo.fragments;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bluewavevision.tracmojo.R;
import com.bluewavevision.tracmojo.TracMojoApplication;
import com.bluewavevision.tracmojo.model.RateOption;
import com.bluewavevision.tracmojo.model.Trac;
import com.bluewavevision.tracmojo.ui.HelpActivity;
import com.bluewavevision.tracmojo.util.Util;
import com.bluewavevision.tracmojo.webservice.VolleyStringRequest;
import com.bluewavevision.tracmojo.webservice.Webservices;

public class TracRateFragment extends BaseFragment implements OnClickListener {
    View llLayout;

    //private ImageView adCustomCanvas;
    private ImageView cdFirstTrac, cdSecondTrac, cdThirdTrac, cdFourthTrac, cdFifthTrac, cdSelectedTrac;
    private TextView tvTracOption1, tvTracOption2, tvTracOption3, tvTracOption4, tvTracOption5;
    private TextView tvGroupName,tvTracName,tvFrequency,tvLastRated,tvDone,tvComment;

    private TracMojoApplication application;
    private Trac mTrac;
    private final String TRANSPARENCY_LEVEL = "80";//50%

    private int selectedRateOption = 0;
    private int parentPosition,childPosition,firstVisiblePosition;
    private boolean isCommentCancelClicked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null){
            mTrac = (Trac) bundle.getSerializable("trac");
            parentPosition = bundle.getInt("parentPosition");
            childPosition = bundle.getInt("childPosition");
            firstVisiblePosition = bundle.getInt("firstVisiblePosition");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.application = (TracMojoApplication) mContext.getApplicationContext();

        mContext.tvHeader.setVisibility(View.GONE);
        mContext.ivInfo.setVisibility(View.GONE);
        mContext.ivBack.setVisibility(View.VISIBLE);
        mContext.ivHelp.setVisibility(View.VISIBLE);

        if (llLayout == null) {
            llLayout = inflater.inflate(R.layout.fragment_trac_rate, container, false);

            initializeComponents();
            //setCustomTracSelectionColor();
            setRateOptions();
            setOtherTracFields();

        } else {
            ((ViewGroup) llLayout.getParent()).removeView(llLayout);
        }

        mContext.ivHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	Intent intent = new Intent(getActivity(), HelpActivity.class);
        		startActivity(intent);
                //Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.help_text_for_trac_rate_screen));
            }
        });

        return llLayout;
    }

    private void initializeComponents() {
        cdFirstTrac = (ImageView) llLayout.findViewById(R.id.fragment_trac_rate_cdTracOne);
        cdSecondTrac = (ImageView) llLayout.findViewById(R.id.fragment_trac_rate_cdTracSecond);
        cdThirdTrac = (ImageView) llLayout.findViewById(R.id.fragment_trac_rate_cdTracThree);
        cdFourthTrac = (ImageView) llLayout.findViewById(R.id.fragment_trac_rate_cdTracFour);
        cdFifthTrac = (ImageView) llLayout.findViewById(R.id.fragment_trac_rate_cdTracFive);
        cdSelectedTrac = (ImageView) llLayout.findViewById(R.id.fragment_trac_rate_cdSelectedTracRate);

        cdFirstTrac.setOnClickListener(this);
        cdSecondTrac.setOnClickListener(this);
        cdThirdTrac.setOnClickListener(this);
        cdFourthTrac.setOnClickListener(this);
        cdFifthTrac.setOnClickListener(this);

        tvTracOption1 = (TextView) llLayout.findViewById(R.id.fragment_trac_rate_tvTracOption1);
        tvTracOption2 = (TextView) llLayout.findViewById(R.id.fragment_trac_rate_tvTracOption2);
        tvTracOption3 = (TextView) llLayout.findViewById(R.id.fragment_trac_rate_tvTracOption3);
        tvTracOption4 = (TextView) llLayout.findViewById(R.id.fragment_trac_rate_tvTracOption4);
        tvTracOption5 = (TextView) llLayout.findViewById(R.id.fragment_trac_rate_tvTracOption5);

        tvGroupName = (TextView) llLayout.findViewById(R.id.fragment_trac_rate_tvGroupName);
        tvTracName = (TextView) llLayout.findViewById(R.id.fragment_trac_rate_tvTracName);
        tvFrequency = (TextView) llLayout.findViewById(R.id.fragment_trac_rate_tvFrequency);
        tvLastRated = (TextView) llLayout.findViewById(R.id.fragment_trac_rate_tvLastRated);
        tvDone = (TextView) llLayout.findViewById(R.id.fragment_trac_rate_tvDone);
        tvComment = (TextView) llLayout.findViewById(R.id.fragment_trac_rate_tvComment);

        //adCustomCanvas = (ImageView) llLayout.findViewById(R.id.fragment_trac_rate_adCustomCanvas);

        tvDone.setOnClickListener(this);
        tvComment.setOnClickListener(this);
    }

    private void setRateOptions(){
        if(mTrac!=null){
            RateOption rateOption = mTrac.getRateOption();
            if(rateOption!=null){
                if(!TextUtils.isEmpty(rateOption.getOption1())){
                    tvTracOption1.setText(rateOption.getOption1());
                }
                if(!TextUtils.isEmpty(rateOption.getOption2())){
                    tvTracOption2.setText(rateOption.getOption2());
                }
                if(!TextUtils.isEmpty(rateOption.getOption3())){
                    tvTracOption3.setText(rateOption.getOption3());
                }
                if(!TextUtils.isEmpty(rateOption.getOption4())){
                    tvTracOption4.setText(rateOption.getOption4());
                }
                if(!TextUtils.isEmpty(rateOption.getOption5())){
                    tvTracOption5.setText(rateOption.getOption5());
                }
            }
        }
    }

    private void setOtherTracFields(){
        if(mTrac!=null){
            if(!TextUtils.isEmpty(mTrac.getGroupName())){
                tvGroupName.setText(mTrac.getGroupName());
            }else {
                tvGroupName.setVisibility(View.GONE);
            }

            if(!TextUtils.isEmpty(mTrac.getGoal())){
                tvTracName.setText(mTrac.getGoal());
            }

            if(!TextUtils.isEmpty(mTrac.getRateFrequency())){
                tvFrequency.setText("rating is "+mTrac.getRateFrequency());
            }

            if(!TextUtils.isEmpty(mTrac.getLastRated())){
                if(TextUtils.isEmpty(mTrac.getRateFrequency())){
                    tvLastRated.setText("last rated "+mTrac.getLastRated());
                }else{
                    tvLastRated.setText(", last rated "+mTrac.getLastRated());
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.fragment_trac_rate_cdTracOne:
                tvComment.setEnabled(true);
                if (application.getListRateColor() != null) {
                    selectedRateOption=5;
                    cdSelectedTrac.setImageResource(R.drawable.ic_custom_rate_one);
                }
                break;

            case R.id.fragment_trac_rate_cdTracSecond:
                tvComment.setEnabled(true);
                if (application.getListRateColor() != null) {
                    selectedRateOption=4;
                    cdSelectedTrac.setImageResource(R.drawable.ic_custom_rate_two);

                }
                break;

            case R.id.fragment_trac_rate_cdTracThree:
                tvComment.setEnabled(true);
                if (application.getListRateColor() != null) {
                    selectedRateOption=3;
                    cdSelectedTrac.setImageResource(R.drawable.ic_custom_rate_three);

                }
                break;

            case R.id.fragment_trac_rate_cdTracFour:
                tvComment.setEnabled(true);
                if (application.getListRateColor() != null) {
                    selectedRateOption=2;
                    cdSelectedTrac.setImageResource(R.drawable.ic_custom_rate_four);
                }
                break;

            case R.id.fragment_trac_rate_cdTracFive:
                tvComment.setEnabled(true);
                if (application.getListRateColor() != null) {
                    selectedRateOption=1;
                    cdSelectedTrac.setImageResource(R.drawable.ic_custom_rate_five);
                }
                break;

            case R.id.fragment_trac_rate_tvComment:
                if(Util.checkConnection(mContext)){
                    showCommentDialog();
                }
                break;

            case R.id.fragment_trac_rate_tvDone:
                 if(selectedRateOption==0){
                     Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.fragment_trac_rate_please_select_one_option));
                 }else {
                    if(Util.checkConnection(mContext)){
                        //showCommentDialog();
                        addRate();
                        if(parentPosition == 0){
                        } else {
                        }
                    }
                 }
                break;
            default:
                break;
        }
    }

    private void addRate() {
        VolleyStringRequest loginRequest = new VolleyStringRequest(
                Request.Method.POST, Webservices.ADD_RATE,
                addRateSuccessLisner(), addRateErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", ""+prefs.getInt("userid", -1));
                params.put("trac_id", ""+mTrac.getId());
                params.put("rate", ""+selectedRateOption);
                return params;
            }
        };

        // ***************Requesting Queue


        showProgress();
        mQueue.add(loginRequest);
    }

    private com.android.volley.Response.Listener<String> addRateSuccessLisner() {
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

                        String msg =""+ jsonObject.getString("message");
                        Util.showMessage(mContext,msg);

                        if(((HomeFragment)mContext.mStacks.get(Util.TAB_HOME).get(0))!=null)
                            ((HomeFragment)mContext.mStacks.get(Util.TAB_HOME).get(0)).updateRow(parentPosition,childPosition,firstVisiblePosition,""+selectedRateOption);

                        mContext.popFragments();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
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
                params.put("trac_id", ""+mTrac.getId());
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
                    tvComment.setTextColor(getResources().getColor(R.color.blue_button_default_color));
                    JSONObject jsonObject = new JSONObject(arg0);
                    String message = "" + jsonObject.getString("message");
                    Util.showMessage(mContext, message);
                    /*if (jsonObject != null) {
                        if(Util.checkConnection(mContext))
                            addRate();
                    }*/
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

    public interface Updateable {
        public void updateRow(int parentPostion ,int childPosition,int firstPosition,String rate);
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
}
