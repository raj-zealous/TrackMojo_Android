package com.tracmojo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.tracmojo.R;
import com.tracmojo.TracMojoApplication;
import com.tracmojo.adapters.TracIdeaAdapter;
import com.tracmojo.adapters.TracRateWordingAdapter;
import com.tracmojo.customwidget.CircularDrawable;
import com.tracmojo.model.Follower;
import com.tracmojo.model.RateIdea;
import com.tracmojo.model.RateWord;
import com.tracmojo.util.AppSession;
import com.tracmojo.util.Util;
import com.tracmojo.webservice.VolleySetup;
import com.tracmojo.webservice.VolleyStringRequest;
import com.tracmojo.webservice.Webservices;

public class PersonalTracEditActivity extends BaseActivity implements View.OnFocusChangeListener{

    private Context mContext;
    private CircularDrawable cdFirstTrac,cdSecondTrac,cdThirdTrac,cdFourthTrac, cdFifthTrac;
    private EditText etCustomTracWording1,etCustomTracWording2,etCustomTracWording3,etCustomTracWording4,etCustomTracWording5;
    private EditText etTracName;
    private TextView tvChooseTracIdeas,tvChooseTracWordings,tvBack,tvNext;
    private TracMojoApplication application;

    private SharedPreferences preferences;
    private RequestQueue mQueue;
    private ProgressDialog mProgress;

    private ArrayList<Follower> listFollowers = new ArrayList<Follower>();
    private ArrayList<RateIdea> listRateIdea = new ArrayList<RateIdea>();
    private ArrayList<RateWord> listRateWord = new ArrayList<RateWord>();
    private HashMap<String,ArrayList<String>> rateFrequencyList = new HashMap<String,ArrayList<String>>();
    private int defaultTracIdeaId;
    private int defaultRateWordId;

    private boolean isCustomTracWordings;
    private int tracId;
    private String frequency;
    private String finishDate;
    private String ratingDay;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_trac_edit);
        ivBack.setVisibility(View.INVISIBLE);
        ivInfo.setVisibility(View.VISIBLE);
        mContext = this;
        this.application = (TracMojoApplication) getApplicationContext();
        AppSession session = new AppSession(mContext);
        preferences = session.getPreferences();

        mQueue = VolleySetup.getRequestQueue();

        initializeComponents();
        //setCustomTracSelectionColor();

        tracId = getIntent().getIntExtra("tracid",-1);
        if(Util.checkConnection(mContext))
            getTracDetail();

    }

    private void initializeComponents(){
        cdFirstTrac = (CircularDrawable) findViewById(R.id.activity_personal_trac_edit_cdTracOne);
        cdSecondTrac = (CircularDrawable) findViewById(R.id.activity_personal_trac_edit_cdTracTwo);
        cdThirdTrac = (CircularDrawable) findViewById(R.id.activity_personal_trac_edit_cdTracThree);
        cdFourthTrac = (CircularDrawable) findViewById(R.id.activity_personal_trac_edit_cdTracFour);
        cdFifthTrac = (CircularDrawable) findViewById(R.id.activity_personal_trac_edit_cdTracFive);

        etCustomTracWording1 = (EditText) findViewById(R.id.activity_personal_trac_edit_etCustomTracWord1);
        etCustomTracWording2 = (EditText) findViewById(R.id.activity_personal_trac_edit_etCustomTracWord2);
        etCustomTracWording3 = (EditText) findViewById(R.id.activity_personal_trac_edit_etCustomTracWord3);
        etCustomTracWording4 = (EditText) findViewById(R.id.activity_personal_trac_edit_etCustomTracWord4);
        etCustomTracWording5 = (EditText) findViewById(R.id.activity_personal_trac_edit_etCustomTracWord5);

        etTracName = (EditText) findViewById(R.id.activity_personal_trac_edit_etTracName);

        tvBack = (TextView) findViewById(R.id.activity_personal_trac_edit_tvBack);
        tvNext = (TextView) findViewById(R.id.activity_personal_trac_edit_tvNext);
        tvChooseTracIdeas = (TextView) findViewById(R.id.activity_personal_trac_edit_tvChooseTracIdeas);
        tvChooseTracWordings = (TextView) findViewById(R.id.activity_personal_trac_edit_tvChooseTracWordings);

        tvBack.setOnClickListener(this);
        tvNext.setOnClickListener(this);
        tvChooseTracIdeas.setOnClickListener(this);
        tvChooseTracWordings.setOnClickListener(this);

        etTracName.setOnClickListener(this);
        etCustomTracWording1.setOnClickListener(this);
        etCustomTracWording2.setOnClickListener(this);
        etCustomTracWording3.setOnClickListener(this);
        etCustomTracWording4.setOnClickListener(this);
        etCustomTracWording5.setOnClickListener(this);

        etTracName.setOnFocusChangeListener(this);
        etCustomTracWording1.setOnFocusChangeListener(this);
        etCustomTracWording2.setOnFocusChangeListener(this);
        etCustomTracWording3.setOnFocusChangeListener(this);
        etCustomTracWording4.setOnFocusChangeListener(this);
        etCustomTracWording5.setOnFocusChangeListener(this);

    }
    private void getTracDetail() {

        VolleyStringRequest loginRequest = new VolleyStringRequest(
                Request.Method.POST, Webservices.GET_TRAC_DETAIL,
                getTracDetailSuccessLisner(), ErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", ""+preferences.getInt("userid", -1));
                params.put("trac_id", ""+tracId);
                return params;
            }

            ;
        };

        // ***************Requesting Queue

        showProgress();
        mQueue.add(loginRequest);
    }

    private com.android.volley.Response.Listener<String> getTracDetailSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String arg0) {
                // TODO Auto-generated method stub
                Log.e("Json", "==> " + arg0);
                stopProgress();

                try {
                    JSONObject json = new JSONObject(arg0);
                    JSONObject jsonObject = json.getJSONObject("general_details");
                    if (jsonObject != null) {

                        JSONArray jsonArrayRateWord = jsonObject.getJSONArray("rate_word");
                        if(jsonArrayRateWord!=null){
                            for (int i = 0; i < jsonArrayRateWord.length(); i++) {
                                JSONObject jsonRateWord = jsonArrayRateWord.getJSONObject(i);
                                int id = jsonRateWord.getInt("id");
                                String name = jsonRateWord.getString("name");
                                RateWord rateWord = new RateWord(id,name);
                                listRateWord.add(rateWord);
                            }
                        }

                        JSONArray jsonArrayRateIdeas = jsonObject.getJSONArray("rate_idea");
                        if(jsonArrayRateIdeas!=null){
                            for (int i = 0; i < jsonArrayRateIdeas.length(); i++) {
                                JSONObject jsonRateIdea = jsonArrayRateIdeas.getJSONObject(i);
                                int id = jsonRateIdea.getInt("id");
                                String name = jsonRateIdea.getString("name");
                                RateIdea rateIdea = new RateIdea(id,name);
                                listRateIdea.add(rateIdea);
                            }
                        }

                        JSONObject jsonRateFrequency = jsonObject.getJSONObject("rate_frequency");
                        if(jsonRateFrequency!=null){
                            rateFrequencyList.put("Daily",null);
                            rateFrequencyList.put("All Weekdays",null);

                            JSONArray jsonArrayWeeklyFrequency = jsonRateFrequency.getJSONArray("Weekly");
                            ArrayList<String> listWeeklyFrequency = new ArrayList<String>();
                            if(jsonArrayWeeklyFrequency!=null){
                                for (int i = 0; i < jsonArrayWeeklyFrequency.length(); i++) {
                                    String str = jsonArrayWeeklyFrequency.getString(i);
                                    listWeeklyFrequency.add(str);
                                }
                                rateFrequencyList.put("Weekly",listWeeklyFrequency);
                            }

                            JSONArray jsonArrayFortNightly = jsonRateFrequency.getJSONArray("Fortnightly");
                            ArrayList<String> listFortnightlyFrequency = new ArrayList<String>();
                            if(jsonArrayFortNightly!=null){
                                for (int i = 0; i < jsonArrayFortNightly.length(); i++) {
                                    String str = jsonArrayFortNightly.getString(i);
                                    listFortnightlyFrequency.add(str);
                                }
                                rateFrequencyList.put("Fortnightly",listFortnightlyFrequency);
                            }

                            JSONArray jsonArrayMonthly = jsonRateFrequency.getJSONArray("Monthly");
                            ArrayList<String> listMonthly = new ArrayList<String >();
                            if(jsonArrayMonthly!=null){
                                for (int i = 0; i < jsonArrayMonthly.length(); i++) {
                                    String str = jsonArrayMonthly.getString(i);
                                    listMonthly.add(str);
                                }
                                rateFrequencyList.put("Monthly",listMonthly);
                            }
                        }

                        JSONObject jsonObj = json.getJSONObject("Trac");
                        setData(jsonObj);
                        /*String msg =""+ jsonObject.getString("message");
                        Util.showMessage(mContext, msg);*/


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
                Util.showMessage(mContext, error.getMessage());
            }
        };
    }

    private void showProgress() {
        mProgress = ProgressDialog.show(mContext, "", getString(R.string.loading_message));
    }

    private void stopProgress() {
        if (mProgress != null && mProgress.isShowing())
            mProgress.cancel();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.activity_personal_trac_edit_tvChooseTracIdeas:
                if(listRateIdea!=null && listRateIdea.size()>0)
                    showTracIdeaDialog();
                break;

            case R.id.activity_personal_trac_edit_tvChooseTracWordings:
                if(listRateWord!=null && listRateWord.size()>0)
                    showTracRateWordingsDialog();
                break;

            case R.id.activity_personal_trac_edit_tvBack:
                onBackPressed();
                break;

            case R.id.activity_personal_trac_edit_tvNext:
                if(Util.isEditTextEmpty(etTracName) && TextUtils.isEmpty(tvChooseTracIdeas.getText().toString().trim())){
                    Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.activity_add_personal_trac_enter_trac_idea));
                }else if(isEmptyCustomWordings() && TextUtils.isEmpty(tvChooseTracWordings.getText().toString().trim())){
                    Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.activity_add_personal_trac_select_trac_wordings));
                } else {
                    if(TextUtils.isEmpty(tvChooseTracWordings.getText().toString().trim())){
                        if(!hasFilledAllCustomWordings()){
                            Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.activity_add_personal_trac_enter_all_wordings));
                        }else {
                            isCustomTracWordings = true;
                            goToSetFrequencyScreen();
                        }
                    }else {
                        isCustomTracWordings = false;
                        goToSetFrequencyScreen();
                    }
                }
                break;

            case R.id.activity_personal_trac_edit_etTracName:
                defaultTracIdeaId = 0;
                tvChooseTracIdeas.setText("");

                break;

            case R.id.activity_personal_trac_edit_etCustomTracWord1:
                defaultRateWordId = 0;
                tvChooseTracWordings.setText("");
                break;

            case R.id.activity_personal_trac_edit_etCustomTracWord2:
                defaultRateWordId = 0;
                tvChooseTracWordings.setText("");
                break;

            case R.id.activity_personal_trac_edit_etCustomTracWord3:
                defaultRateWordId = 0;
                tvChooseTracWordings.setText("");
                break;

            case R.id.activity_personal_trac_edit_etCustomTracWord4:
                defaultRateWordId = 0;
                tvChooseTracWordings.setText("");
                break;

            case R.id.activity_personal_trac_edit_etCustomTracWord5:
                defaultRateWordId = 0;
                tvChooseTracWordings.setText("");
                break;
            default:
                break;
        }

    }

    private void goToSetFrequencyScreen(){
        Intent intent = new Intent(mContext,PersonalTracEditSetFrequencyActivity.class);
        intent.putExtra("rateFrequencyList",rateFrequencyList);
        intent.putExtra("etTracName",""+etTracName.getText().toString().trim());
        intent.putExtra("tvTracName",""+tvChooseTracIdeas.getText().toString().trim());
        intent.putExtra("defaultTracIdeaId",defaultTracIdeaId);
        intent.putExtra("isCustomTracWordings",isCustomTracWordings);
        if(isCustomTracWordings){
            intent.putExtra("etCustomTracWording1",""+etCustomTracWording1.getText().toString().trim());
            intent.putExtra("etCustomTracWording2",""+etCustomTracWording2.getText().toString().trim());
            intent.putExtra("etCustomTracWording3",""+etCustomTracWording3.getText().toString().trim());
            intent.putExtra("etCustomTracWording4",""+etCustomTracWording4.getText().toString().trim());
            intent.putExtra("etCustomTracWording5",""+etCustomTracWording5.getText().toString().trim());
        }else{
            intent.putExtra("tvChooseTracWordings",""+tvChooseTracWordings.getText().toString().trim());
            intent.putExtra("defaultRateWordId",defaultRateWordId);
        }

        intent.putExtra("frequency",frequency);
        intent.putExtra("ratingDay",ratingDay);
        intent.putExtra("finishDate",finishDate);
        intent.putExtra("tracid",tracId);
        //intent.putExtra("followersList",listFollowers);
        startActivity(intent);
    }

    private void showTracIdeaDialog()
    {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_single_item_selection_list);


        int defaultPosition = 0;
        for(int i=0;i<listRateIdea.size();i++)
        {
            if(listRateIdea.get(i).getId()== defaultTracIdeaId)
            {
                defaultPosition=i;
            }
        }

        TracIdeaAdapter adapter = new TracIdeaAdapter(mContext, listRateIdea, defaultPosition);

        ListView lvRangelist = (ListView) dialog.findViewById(R.id.custom_choose_quantity_lv_range);
        lvRangelist.setAdapter(adapter);
        lvRangelist.setSmoothScrollbarEnabled(true);
        lvRangelist.setSelection(defaultPosition);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.dialog_title);
        tvTitle.setText(getString(R.string.activity_add_personal_trac_custom_trac_ideas_title));

        lvRangelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                defaultTracIdeaId = listRateIdea.get(position).getId();

                tvChooseTracIdeas.setText(listRateIdea.get(position).getName());

                etTracName.setText("");

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showTracRateWordingsDialog()
    {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_single_item_selection_list);


        int defaultPosition = 0;
        for(int i=0;i<listRateWord.size();i++)
        {
            if(listRateWord.get(i).getId()==defaultRateWordId)
            {
                defaultPosition=i;
            }
        }

        TracRateWordingAdapter adapter = new TracRateWordingAdapter(mContext,listRateWord,defaultPosition);

        ListView lvRangelist = (ListView) dialog.findViewById(R.id.custom_choose_quantity_lv_range);
        lvRangelist.setAdapter(adapter);
        lvRangelist.setSmoothScrollbarEnabled(true);
        lvRangelist.setSelection(defaultPosition);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.dialog_title);
        tvTitle.setText(getString(R.string.activity_add_personal_trac_custom_trac_wording_title));

        lvRangelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                defaultRateWordId  = listRateWord.get(position).getId();

                tvChooseTracWordings.setText(listRateWord.get(position).getName());

                resetAllCustomWordings();

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean isEmptyCustomWordings(){
        if(Util.isEditTextEmpty(etCustomTracWording1) && Util.isEditTextEmpty(etCustomTracWording2) &&Util.isEditTextEmpty(etCustomTracWording3) &&
                Util.isEditTextEmpty(etCustomTracWording4) &&Util.isEditTextEmpty(etCustomTracWording5) ){
            return  true;
        }else{
            return  false;
        }
    }

    private void resetAllCustomWordings(){
        etCustomTracWording1.setText("");
        etCustomTracWording2.setText("");
        etCustomTracWording3.setText("");
        etCustomTracWording4.setText("");
        etCustomTracWording5.setText("");
    }

    private boolean hasFilledAllCustomWordings(){
        if(Util.isEditTextEmpty(etCustomTracWording1) || Util.isEditTextEmpty(etCustomTracWording2) ||
                Util.isEditTextEmpty(etCustomTracWording3) || Util.isEditTextEmpty(etCustomTracWording4) ||
                        Util.isEditTextEmpty(etCustomTracWording5) ){
            return  false;
        }else{
            return  true;
        }
    }

    private void setData(JSONObject jsonTrac) throws JSONException {
        if (jsonTrac != null) {

            String goal = "" + jsonTrac.getString("goal");
            if(!TextUtils.isEmpty(goal)){
                etTracName.setText(goal);
            }else{
                String ideaName = "" + jsonTrac.getString("idea_id_name");
                tvChooseTracIdeas.setText(ideaName);
                defaultTracIdeaId = jsonTrac.getInt("idea_id");
            }

            String rateWordings = ""+jsonTrac.getString("rate_wording");
            if(!TextUtils.isEmpty(rateWordings)){
                tvChooseTracWordings.setText(rateWordings);
                defaultRateWordId = jsonTrac.getInt("rate_wording_id");
            } else {
                String customWording1 = "" + jsonTrac.getString("cust_rate_word1");
                String customWording2 = "" + jsonTrac.getString("cust_rate_word2");
                String customWording3 = "" + jsonTrac.getString("cust_rate_word3");
                String customWording4 = "" + jsonTrac.getString("cust_rate_word4");
                String customWording5 = "" + jsonTrac.getString("cust_rate_word5");

                etCustomTracWording1.setText(customWording1);
                etCustomTracWording2.setText(customWording2);
                etCustomTracWording3.setText(customWording3);
                etCustomTracWording4.setText(customWording4);
                etCustomTracWording5.setText(customWording5);
            }

            frequency = "" + jsonTrac.getString("rating_frequency");
            ratingDay = "" + jsonTrac.getString("rating_day");
            finishDate = "" + jsonTrac.getString("finish_date_edit");

            JSONArray jsonArrayFollowers = jsonTrac.getJSONArray("Followers");
            if(jsonArrayFollowers!=null){
                for (int i = 0; i < jsonArrayFollowers.length(); i++) {
                    JSONObject jsonObject = jsonArrayFollowers.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String userid = jsonObject.getString("user_id");
                    String email = jsonObject.getString("email_id");
                    String requestStatus = jsonObject.getString("request_status");

                    Follower follower = new Follower(id,userid,email,requestStatus);
                    listFollowers.add(follower);
                }
                application.setListFollowers(listFollowers);
            }

        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            switch (v.getId()){
                case R.id.activity_personal_trac_edit_etTracName:
                    defaultTracIdeaId = 0;
                    tvChooseTracIdeas.setText("");

                    break;

                case R.id.activity_personal_trac_edit_etCustomTracWord1:
                    defaultRateWordId = 0;
                    tvChooseTracWordings.setText("");
                    break;

                case R.id.activity_personal_trac_edit_etCustomTracWord2:
                    defaultRateWordId = 0;
                    tvChooseTracWordings.setText("");
                    break;

                case R.id.activity_personal_trac_edit_etCustomTracWord3:
                    defaultRateWordId = 0;
                    tvChooseTracWordings.setText("");
                    break;

                case R.id.activity_personal_trac_edit_etCustomTracWord4:
                    defaultRateWordId = 0;
                    tvChooseTracWordings.setText("");
                    break;

                case R.id.activity_personal_trac_edit_etCustomTracWord5:
                    defaultRateWordId = 0;
                    tvChooseTracWordings.setText("");
                    break;
                default:
                    break;
            }
        }
    }
}
