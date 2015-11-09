package com.tracmojo.fragments;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.tracmojo.R;
import com.tracmojo.database.RemDBAdapter;
import com.tracmojo.model.GraphModel;
import com.tracmojo.ui.DashboardActivity;
import com.tracmojo.util.OnSwipeTouchListener;
import com.tracmojo.util.Util;
import com.tracmojo.webservice.VolleyStringRequest;
import com.tracmojo.webservice.Webservices;


public class FollowerTracReviewFragment extends BaseFragment implements OnClickListener ,OnChartGestureListener {

    private static final String DATE_FORMAT_FOR_FINISHING_DATE = "dd-MM-yyyy";

    private View llLayout;
    private int tracId;

    private RelativeLayout relCommentCircle;
    private TextView tvCommentCount;

    private RelativeLayout relShare;
    private LinearLayout relCaptureGraph,linTracToDate;
    private TextView tvTracName, tvTracFrequency, tvPersonalFollowers, tvStartedDate, tvFinishDate,
            tvTracToDate, tvNextTracDay,tvNextTracDayLabel, tvViewComments, tvSelectedDate,tvCommunicationLable,tvPersonalTracLabel,
            tvFollowerCounts;

    private ImageView ivComment, ivMessage, ivShareFacebook, ivShareTwitter, ivShareMessage, ivSelectDate, ivCallOrText, ivAddFollower;
    private RelativeLayout relGraph;
    private int noOfdays;
    String horLabel[];

    private ArrayList<GraphModel> listGraphData = new ArrayList<GraphModel>();
    private GestureDetectorCompat gestureDetectorCompat;

    private LineChart mChart;
    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private int mYear,mMonth,mDay;
    private String dob="";
    private String frequency="";


    RemDBAdapter remDBAdapter;


    //twitter login variable
    SocialAuthAdapter adapter;
    public String providerName;
    Profile profile;
    static String TWITTER_CONSUMER_KEY = "emp9JMh1dMrF6060c1muLfiYk";
    static String TWITTER_CONSUMER_SECRET = "pSMctANAdVqTpgnn1M1dLrXRkWXKG8q551UgR812yoA9kx8xyE";
    private File pictureFile;

    private String followersIds="",phoneNumberOwner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tracId = bundle.getInt("trac_id");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext.tvHeader.setVisibility(View.GONE);
        mContext.ivInfo.setVisibility(View.GONE);
        mContext.ivBack.setVisibility(View.VISIBLE);
        mContext.ivHelp.setVisibility(View.VISIBLE);
        mContext.ivEmailCommentList.setVisibility(View.GONE);


        if (llLayout == null) {
            llLayout = inflater.inflate(R.layout.fragment_personal_trac_review, container, false);
            initializeComponents();
            //twitter integration code...
            adapter = new SocialAuthAdapter(new ResponseListener());
            adapter.addProvider(SocialAuthAdapter.Provider.TWITTER, R.drawable.twitter);
            adapter.addCallBack(SocialAuthAdapter.Provider.TWITTER, "http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");

            try {
                adapter.addConfig(SocialAuthAdapter.Provider.TWITTER, TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (Util.checkConnectionWithoutMessage(mContext)) {
                getTracDetail();
            } else {
                remDBAdapter = new RemDBAdapter(mContext);
                remDBAdapter.open();
                try {
                    setData(remDBAdapter.getJsonTrac(tracId));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            gestureDetectorCompat = new GestureDetectorCompat(mContext, new My2ndGestureListener());

            adapter.enable(ivShareTwitter, SocialAuthAdapter.Provider.TWITTER);



        } else {
            ((ViewGroup) llLayout.getParent()).removeView(llLayout);
        }

        llLayout.setOnTouchListener(new OnSwipeTouchListener(mContext) {

            @Override
            public void onSwipeRight() {
                //super.onSwipeRight();
                mContext.popFragments();
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        mContext.ivHelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.help_text_for_trac_review_screen));
            }
        });

        return llLayout;
    }


    //twitter login part
    private final class ResponseListener implements DialogListener {

        @Override
        public void onComplete(Bundle values) {

            // Variable to receive message status
            Log.d("twt Demo", "Authentication Successful");

            // Get name of provider after authentication
            providerName = values.getString(SocialAuthAdapter.PROVIDER);
            Log.d("twt Demo", "Provider Name = " + providerName);
            //Toast.makeText(LoginActivity.this, providerName + " connected", Toast.LENGTH_SHORT).show();

            if(Util.checkConnection(mContext)){
                try {
                    if (saveToPath(relCaptureGraph,"title", "")) {
                    /*Toast.makeText(mContext, "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();*/
                        pictureFile = new File(Environment.getExternalStorageDirectory().getPath()
                                + "/TracMojo/title.png");
                        Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getPath());
                        //adapter.uploadImage("","title",bitmap,1);
                        adapter.uploadImageAsync("Shared tracmojo trac","title.png",bitmap,0,new SocialAuthListener<Integer>() {
                            @Override
                            public void onExecute(String provider, Integer integer) {
                                Log.e("OnExecute","sdf");
                            }

                            @Override
                            public void onError(SocialAuthError e) {

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onError(SocialAuthError error) {
            error.printStackTrace();
            Log.d("Share-Bar", error.getMessage());
            adapter.setClicked(false);
        }

        @Override
        public void onCancel() {
            Log.d("Share-Bar", "Authentication Cancelled");
            adapter.setClicked(false);
        }

        @Override
        public void onBack() {
            Log.d("Share-Bar", "Dialog Closed by pressing Back Key");
            adapter.setClicked(false);
        }
    }

    private void initializeComponents() {
        tvPersonalTracLabel = (TextView) llLayout.findViewById(R.id.fragment_trac_review_tvPersonalTrac);
        linTracToDate = (LinearLayout) llLayout.findViewById(R.id.fragment_trac_review_linTracToDateLabel);
        linTracToDate.setVisibility(View.GONE);

        ivAddFollower = (ImageView) llLayout.findViewById(R.id.fragment_trac_review_tvAddFollowers);
        ivAddFollower.setVisibility(View.GONE);

        tvFollowerCounts = (TextView) llLayout.findViewById(R.id.fragment_trac_review_tvPersonalFollowers);

        relCommentCircle = (RelativeLayout) llLayout.findViewById(R.id.fragment_trac_review_relNotificationCount);
        tvCommentCount = (TextView) llLayout.findViewById(R.id.fragment_trac_review_tvNotificationCount);

        relShare = (RelativeLayout) llLayout.findViewById(R.id.fragment_trac_review_relShare);
        relShare.setVisibility(View.GONE);
        relCaptureGraph = (LinearLayout) llLayout.findViewById(R.id.fragment_trac_review_relCaptureGraph);

        tvTracName = (TextView) llLayout.findViewById(R.id.fragment_trac_review_tvTracName);
        tvTracFrequency = (TextView) llLayout.findViewById(R.id.fragment_trac_review_tvFrequency);
        tvPersonalFollowers = (TextView) llLayout.findViewById(R.id.fragment_trac_review_tvOwnerName);
        tvStartedDate = (TextView) llLayout.findViewById(R.id.fragment_trac_review_tvStartedDate);
        tvFinishDate = (TextView) llLayout.findViewById(R.id.fragment_trac_review_tvFinishDate);
        tvTracToDate = (TextView) llLayout.findViewById(R.id.fragment_trac_review_tvTracToDate);
        tvNextTracDay = (TextView) llLayout.findViewById(R.id.fragment_trac_review_tvNext);
        tvNextTracDayLabel = (TextView) llLayout.findViewById(R.id.fragment_trac_review_tvNextLabel);
        tvSelectedDate = (TextView) llLayout.findViewById(R.id.fragment_trac_review_tvDate);
        tvViewComments = (TextView) llLayout.findViewById(R.id.fragment_trac_review_tvViewComments);
        tvViewComments.setText("View Comments");
        tvViewComments.setOnClickListener(this);

        tvCommunicationLable = (TextView) llLayout.findViewById(R.id.fragment_trac_review_tvCommunicationLabel);
        tvCommunicationLable.setText(getString(R.string.fragment_trac_review_communicate_with_owner));


        ivCallOrText  = (ImageView) llLayout.findViewById(R.id.fragment_trac_review_ivCallOrText);
        ivComment = (ImageView) llLayout.findViewById(R.id.fragment_trac_review_ivComment);
        ivMessage = (ImageView) llLayout.findViewById(R.id.fragment_trac_review_ivMessage);
        ivShareFacebook = (ImageView) llLayout.findViewById(R.id.fragment_trac_review_ivShareFaceBook);
        ivShareTwitter = (ImageView) llLayout.findViewById(R.id.fragment_trac_review_ivShareTwitter);
        ivShareMessage = (ImageView) llLayout.findViewById(R.id.fragment_trac_review_ivShareMessage);
        ivSelectDate  = (ImageView) llLayout.findViewById(R.id.fragment_trac_review_ivCalender);

        ivCallOrText.setVisibility(View.VISIBLE);
        ivCallOrText.setOnClickListener(this);
        ivComment.setOnClickListener(this);
        ivMessage.setOnClickListener(this);
        ivShareFacebook.setOnClickListener(this);
        ivShareTwitter.setOnClickListener(this);
        ivShareMessage.setOnClickListener(this);
        ivSelectDate.setOnClickListener(this);

        relGraph = (RelativeLayout) llLayout.findViewById(R.id.fragment_trac_review_relGraph);

        mChart = (LineChart) llLayout.findViewById(R.id.chart);

        Calendar currentDateCalendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_FOR_FINISHING_DATE);
        dob = formatter.format(currentDateCalendar.getTime());
        tvSelectedDate.setText(dob);

    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.fragment_trac_review_tvViewComments:
                CommentListFragment commentListFragment = new CommentListFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("tracId",tracId);
                bundle.putBoolean("isOwner",false);
                bundle.putBoolean("isFollower",true);
                commentListFragment.setArguments(bundle);
                ((DashboardActivity) mContext).pushFragments(Util.TAB_HOME, commentListFragment, false, true);
                break;

            case R.id.fragment_trac_review_ivCalender:
                showCustomDatePickerDialog();
                break;

            case R.id.fragment_trac_review_ivShareFaceBook:
                if (saveToPath(relCaptureGraph, "title", "")) {
                    /*Toast.makeText(mContext, "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();*/
                    pictureFile = new File(Environment.getExternalStorageDirectory().getPath()
                            +"/TracMojo/title.png");
                    boolean isInstalled = appInstalledOrNot("com.facebook.katana");
                    ArrayList<Uri> files = new ArrayList<Uri>();
                    Uri fileUri = Uri.fromFile(pictureFile);
                    if (fileUri != null)
                        files.add(fileUri);
                    if (isInstalled) {
                        Intent fbIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        fbIntent.setPackage("com.facebook.katana");
                        fbIntent.setType("image/*");
                        fbIntent.putExtra(Intent.EXTRA_TEXT, "Shared tracmojo trac");
                        fbIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        fbIntent.putExtra(Intent.EXTRA_STREAM, files);
                        startActivityForResult(fbIntent, 666);
                    } else if(!isInstalled) {
                        Toast.makeText(mContext, "Facebook App isn't installed!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    /*Toast.makeText(mContext, "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show();*/

                     /*
                    Going to facebook
                     */

                }

                break;

            case R.id.fragment_trac_review_ivShareMessage:
                if (saveToPath(relCaptureGraph, "title", "")) {
                    /*Toast.makeText(mContext, "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();*/
                    pictureFile = new File(Environment.getExternalStorageDirectory().getPath()
                            + "/TracMojo/title.png");

                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("application/image");
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Shared tracmojo trac");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                    emailIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse("file://" + pictureFile.getAbsolutePath()));
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
                break;

            case R.id.fragment_trac_review_ivMessage:
                sendPushNotificationDialog(true);
                break;

            case R.id.fragment_trac_review_ivComment:
                sendPushNotificationDialog(false);
                break;

            case R.id.fragment_trac_review_ivCallOrText:
                if(!TextUtils.isEmpty(phoneNumberOwner))
                    showCallOrTextDialog(phoneNumberOwner);
                break;

            default:

                break;
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = mContext.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    private void getTracDetail() {

        VolleyStringRequest getTracDetailRequest = new VolleyStringRequest(
                Request.Method.POST, Webservices.GET_TRAC_DETAIL,
                getTracDetailSuccessLisner(), getTracDetailErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", ""+prefs.getInt("userid", -1));
                params.put("trac_id", ""+tracId);
                if(prefs.getInt("userid", -1)==3){
                    params.put("israndom", "y");
                }
                /*params.put("user_id", "3");
                params.put("trac_id", "19");*/
                return params;
            }

        };

        showProgress();
        mQueue.add(getTracDetailRequest);
    }

    private Response.Listener<String> getTracDetailSuccessLisner() {
        return new Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String arg0) {
                // TODO Auto-generated method stub
                Log.e("Json", "==> " + arg0);
                stopProgress();

                try {
                    JSONObject jsonObject = new JSONObject(arg0);
                    if (jsonObject != null) {

                        JSONObject jsonTrac = jsonObject.getJSONObject("Trac");
                        setData(jsonTrac);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    private void setData(JSONObject jsonTrac) throws JSONException {
        if (jsonTrac != null) {

            String goal = "" + jsonTrac.getString("goal");
            if(!TextUtils.isEmpty(goal)){
                tvTracName.setText(goal);
            }else{
                String ideaName = "" + jsonTrac.getString("idea_id_name");
                tvTracName.setText(ideaName);
            }

            frequency = "" + jsonTrac.getString("rating_frequency");
            tvTracFrequency.setText(frequency);
            tvPersonalTracLabel.setText("Personal trac - " + frequency);

            String personalFollowersCount = "" + jsonTrac.getString("follower_count");
            tvFollowerCounts.setText(personalFollowersCount + " followers");

            String owner = "" + jsonTrac.getString("i_by_name");
            tvPersonalFollowers.setText("Owner : "+owner);

            String startDate = "" + jsonTrac.getString("start_date");
            tvStartedDate.setText(startDate);

            String finishDate = "" + jsonTrac.getString("finish_date");
            tvFinishDate.setText(finishDate);

            String tracToDate = "" + jsonTrac.getString("trac_to_date");
            tvTracToDate.setText(tracToDate);

            String nextDate = "" + jsonTrac.optString("next_rate_date");
            tvNextTracDay.setText(nextDate);
            if(TextUtils.isEmpty(nextDate)){
                tvNextTracDayLabel.setVisibility(View.INVISIBLE);
            }

            phoneNumberOwner = "" + jsonTrac.optString("phone");
            if(!TextUtils.isEmpty(phoneNumberOwner) && phoneNumberOwner.contains("-"))
                phoneNumberOwner.replace("-","");

            String commentCount = "" + jsonTrac.getString("comment_count");
            tvCommentCount.setText(commentCount);
            if(!TextUtils.isEmpty(commentCount)){
                if(!commentCount.equalsIgnoreCase("0")){
                    relCommentCircle.setBackgroundResource(R.drawable.home_blue_circular_background);
                }else {
                    relCommentCircle.setBackgroundResource(R.drawable.home_gray_circular_background);
                }
            }

            JSONObject jsonGraphData = jsonTrac.getJSONObject("graph_data");
            if (jsonGraphData != null) {
                JSONArray jsonArrayOwnerRates = jsonGraphData.getJSONArray("owner_rates");
                if (jsonArrayOwnerRates != null) {
                    for (int i = 0; i < jsonArrayOwnerRates.length(); i++) {
                        JSONObject jsonObjectRate = jsonArrayOwnerRates.getJSONObject(i);
                        int rate = jsonObjectRate.getInt("rate");
                        String date = jsonObjectRate.getString("date");
                        long dateStamp = jsonObjectRate.getLong("date_timestamp");

                        GraphModel graphModel = new GraphModel(rate,date,dateStamp);
                        listGraphData.add(graphModel);
                    }
                }
            }

            Calendar calendar = Calendar.getInstance();
            //calendar.set(Calendar.MONTH,2);
            //setDailyGraph(calendar);
            createGraph(calendar,frequency);
        }
    }

    private Response.ErrorListener getTracDetailErrorLisner() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Json Error", "==> " + error.getMessage());
                stopProgress();

                Util.showMessage(mContext, error.getMessage());
            }
        };
    }

    private Calendar getDateFromTimeStamp(long timeStamp){
        Calendar calendar = Calendar.getInstance();
        timeStamp = timeStamp * 1000l;
        calendar.setTimeInMillis(timeStamp);
        return calendar;
    }

    private ArrayList<GraphModel> getTempList(){
        ArrayList<GraphModel> listTemp = new ArrayList<GraphModel>();
        int j = 0;
        for (int i = listGraphData.size() - 1; i >= 0; i--) {
            Calendar graphCalender = getDateFromTimeStamp(listGraphData.get(i).getDateTimeStamp());
            Log.e("date", ":" + graphCalender.getTime().toString() + ":" + listGraphData.get(i).getRate());
            listTemp.add(listGraphData.get(i));
            /*if (j < 30) {

            } else
                break;*/

            j++;
        }

        Log.e("Size", ":" + listTemp.size());
        Collections.reverse(listTemp);
        return listTemp;
    }

    private void createGraph(Calendar calendar,String frequency){
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");
        mChart.setHighlightEnabled(true);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
        //mChart.setHighlightIndicatorEnabled(false);
        mChart.getLegend().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawGridLines(false);
       // xAxis.setAdjustXLabels(true);
        xAxis.setDrawAxisLine(false);
        //xAxis.setAxisLineColor(getResources().getColor(android.R.color.transparent));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(5);
        leftAxis.setAxisMinValue(1);
        leftAxis.setStartAtZero(false);
        leftAxis.setDrawGridLines(false);
        //leftAxis.setLabelCount(5);
        leftAxis.setEnabled(false);
        //leftAxis.setDrawLimitLinesBehindData(false);

        mChart.setDoubleTapToZoomEnabled(true);
        mChart.setVerticalScrollBarEnabled(false);
        mChart.setScaleYEnabled(false);
        mChart.getAxisRight().setEnabled(false);

        setData(calendar,frequency);

        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    private void setData(Calendar calendar,String frequency) {

        //calendar.set(Calendar.MONTH,3);
        if (!TextUtils.isEmpty(frequency)) {

            ArrayList<String> xVals = new ArrayList<String>();

            ArrayList<GraphModel> tempList = getTempList();
            for (int i = 0; i < tempList.size(); i++) {
                if(i == 0){
                    xVals.add("   " + Util.getDateInDdMMFormat(tempList.get(i).getDate()));
                }else if(i == tempList.size()-1){
                    xVals.add("" + Util.getDateInDdMMFormat(tempList.get(i).getDate()) + "    ");
                }else {
                    xVals.add("" + Util.getDateInDdMMFormat(tempList.get(i).getDate()));
                }

            }

            ArrayList<ArrayList<Entry>> yValues = new ArrayList<ArrayList<Entry>>();

            ArrayList<Entry> yVals = new ArrayList<Entry>();
            boolean isDashedLine = false;
            for (int i = 0; i < tempList.size(); i++) {
                int day = i;
                if (tempList.get(i).getRate() == 0) {
                    isDashedLine = true;
                } else {
                    if (isDashedLine) {
                        isDashedLine = false;
                        Entry mEntrySet;
                        if (yVals != null && yVals.size() > 0) {
                            yValues.add(yVals);
                            mEntrySet = yVals.get(yVals.size() - 1);
                           // mEntrySet.setMisDashedLine(true);
                            yVals = new ArrayList<Entry>();
                            yVals.add(mEntrySet);
                        }
                        yVals.add(new Entry(tempList.get(i).getRate(), day));
                        yValues.add(yVals);
                        yVals = new ArrayList<Entry>();
                    }
                    yVals.add(new Entry(tempList.get(i).getRate(), day));
                }
            }
            yValues.add(yVals);
            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
            for (int i = 0; i < yValues.size(); i++) {
                set1 = new LineDataSet(yValues.get(i), "DataSet 1");
                set1.setDrawValues(false);
                set1.setColor(Color.BLACK);
                set1.setCircleColor(Color.BLACK);
                set1.setLineWidth(1f);
                set1.setCircleSize(3f);
                set1.setDrawCircleHole(false);
                set1.setValueTextSize(9f);
                set1.setFillAlpha(65);
                set1.setFillColor(Color.BLACK);

                ArrayList<Entry> ySubValues = new ArrayList<Entry>();
                ySubValues = yValues.get(i);
                /*if (ySubValues != null && ySubValues.size() > 0 && ySubValues.get(0).isDashedLine()) {*/
                	   if (ySubValues != null && ySubValues.size() > 0 ) {
                    set1.enableDashedLine(10f, 5f, 0f);
                }
                dataSets.add(set1); // add the datasets
            }

            //mChart.setScaleMinima(5f,1f);
            /*if(frequency.equalsIgnoreCase("Daily") || frequency.equalsIgnoreCase("All Weekdays")){
                if(tempList!=null && tempList.size()>0)
                    mChart.centerViewTo(calendar.get(Calendar.DAY_OF_MONTH),set1.getYValForXIndex(calendar.get(Calendar.DAY_OF_MONTH)),null);
            }*/

            LineData data = new LineData(xVals, dataSets);

            mChart.invalidate();
            mChart.refreshDrawableState();
            mChart.setData(data);
        }
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        int action = me.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Disallow ScrollView to intercept touch events.
                mChart.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                // Allow ScrollView to intercept touch events.
                mChart.getParent().getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    public void showCustomDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        if (mYear == 0) {
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        } else {
            c.set(Calendar.YEAR, mYear);
            c.set(Calendar.MONTH, mMonth);
            c.set(Calendar.DAY_OF_MONTH, mDay);
        }

        DatePickerDialog dpd = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //tvBirthday.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_FOR_FINISHING_DATE);
                dob = formatter.format(calendar.getTime());
                tvSelectedDate.setText(dob);

                setData(calendar, frequency);
            }
        }, mYear, mMonth, mDay);

        Calendar currentDateCalendar = Calendar.getInstance();
        dpd.getDatePicker().setMaxDate(currentDateCalendar.getTimeInMillis());

        dpd.show();

    }


    //--sending push notification dialog
    private void sendPushNotificationDialog(final boolean isEmail){
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_send_push_dialog);

        Button btnSend = (Button) dialog.findViewById(R.id.fogot_pass_btn_send);
        Button btnCancel = (Button) dialog.findViewById(R.id.fogot_pass_btn_cancel);

        final EditText etMessage = (EditText) dialog.findViewById(R.id.fogot_pass_et_email);

        TextView textView = (TextView) dialog.findViewById(R.id.dialog_title);
        if(isEmail){
            textView.setText(getString(R.string.custom_send_email_dialog_title));
        }

        btnSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(
                        Context.INPUT_METHOD_SERVICE);

                if(Util.isEditTextEmpty(etMessage))
                {
                    imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
                    Util.showMessage(mContext, getString(R.string.custom_push_notification_dialog_enter_message));
                }
                else
                {
                    imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
                    dialog.dismiss();

                    if(Util.checkConnection(mContext)){
                        if(isEmail)
                            sendEmail(etMessage.getText().toString().trim());
                        else
                            sendNotification(etMessage.getText().toString().trim());
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

    private void sendEmail(final String message) {
        VolleyStringRequest sendPushRequest = new VolleyStringRequest(
                Request.Method.POST, Webservices.SEND_EMAIL,
                sendPushSuccessLisner(), ErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", "" + prefs.getInt("userid",-1));
                params.put("trac_id", "" + tracId);
                params.put("message", "" + message);
                params.put("send_to", "owner");
                return params;
            }
        };

        showProgress();
        mQueue.add(sendPushRequest);
    }

    private void sendNotification(final String message) {
        VolleyStringRequest sendPushRequest = new VolleyStringRequest(
                Request.Method.POST, Webservices.SEND_NOTIFICATION,
                sendPushSuccessLisner(), ErrorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", "" + prefs.getInt("userid",-1));
                params.put("trac_id", "" + tracId);
                params.put("message", "" + message);
                params.put("send_to", "owner");
                return params;
            }
        };

        showProgress();
        mQueue.add(sendPushRequest);
    }

    private Response.Listener<String> sendPushSuccessLisner() {
        return new Response.Listener<String>() {

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

    private Response.ErrorListener ErrorLisner() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Json Error", "==> " + error.getMessage());
                stopProgress();
                Util.showMessage(mContext, error.getMessage());
            }
        };
    }


    class My2ndGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe right' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

         /*
         Toast.makeText(getBaseContext(),
          event1.toString() + "\n\n" +event2.toString(),
          Toast.LENGTH_SHORT).show();
         */

            if(event2.getX() > event1.getX()){
                Toast.makeText(mContext,
                        "Swipe right - finish()",
                        Toast.LENGTH_SHORT).show();

                mContext.popFragments();
            }

            return true;
        }
    }

    public Bitmap getChartBitmap(View view) {
        // Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        // Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        // Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            // has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            // does not have background drawable, then draw white background on
            // the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        // return the bitmap
        return returnedBitmap;
    }

    public boolean saveToPath(View view, String title, String pathOnSD) {

        Bitmap b = getChartBitmap(view);

        OutputStream stream = null;
        try {
            File file =  new File(Environment.getExternalStorageDirectory().getPath()+"/TracMojo");
            file.mkdir();
            stream = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()
                    + pathOnSD +"/TracMojo" + "/" + title
                    + ".png");

            /*
             * Write bitmap to file using JPEG or PNG and 40% quality hint for
             * JPEG.
             */
            b.compress(Bitmap.CompressFormat.PNG, 40, stream);

            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void showCallOrTextDialog(final String phone){
        try {
            final CharSequence[] items = mContext.getResources().getStringArray(R.array.PhotoSelectionOptions);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.photoselection_dialog_header));
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    //mDoneButton.setText(items[item]);
                    if (item == 0) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+phone));
                        startActivity(intent);
                    } else if (item == 1) {
                        Uri uri = Uri.parse("smsto:"+phone);
                        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                        it.putExtra("sms_body", "");
                        startActivity(it);
                    } else if (item == 2) {
                        dialog.dismiss();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


