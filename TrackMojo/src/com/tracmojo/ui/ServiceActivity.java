package com.tracmojo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.tracmojo.R;
import com.tracmojo.adapters.ServiceListAdapter;
import com.tracmojo.model.Service;
import com.tracmojo.util.AppSession;
import com.tracmojo.util.Util;
import com.tracmojo.webservice.VolleySetup;
import com.tracmojo.webservice.VolleyStringRequest;
import com.tracmojo.webservice.Webservices;

public class ServiceActivity extends BaseActivity {

    // SAMPLE APP CONSTANTS
    private static final String ACTIVITY_NUMBER = "activity_num";
    private static final String LOG_TAG = "iabv3";

    // PRODUCT & SUBSCRIPTION IDS
    private static final String PRODUCT_ID = "com.anjlab.test.iab.s2.p5";
    private static final String SUBSCRIPTION_ID = "com.anjlab.test.iab.subs1";
    private static final String LICENSE_KEY = null; // PUT YOUR MERCHANT KEY HERE;

    private boolean readyToPurchase = false;

    private BillingProcessor bp;


    private Context mContext;
    private ProgressDialog mProgress;
    public RequestQueue mQueue;
    private SharedPreferences prefs;

    private ListView lvServiceList;
    private ArrayList<Service> listServices = new ArrayList<Service>();
    private ServiceListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        mContext = this;
        mQueue = VolleySetup.getRequestQueue();
        AppSession session = new AppSession(mContext);
        prefs = session.getPreferences();

        ivLogo.setVisibility(View.GONE);
        tvHeader.setVisibility(View.VISIBLE);
        ivHelp.setVisibility(View.VISIBLE);
        tvHeader.setText(getString(R.string.service_activity_header));

        lvServiceList = (ListView) findViewById(R.id.activity_service_lvServiceList);

        if(Util.checkConnection(mContext)){
            getServicePlansDetail();
        }

        lvServiceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Service service = listServices.get(position);
                if(service!=null){
                    if (!readyToPurchase) {
                        Util.showMessage(mContext,"Billing not initialized.");
                        return;
                    }else{
                        bp.subscribe(ServiceActivity.this,SUBSCRIPTION_ID);
                    }
                }
            }
        });

        bp = new BillingProcessor(this, LICENSE_KEY, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
                /*showToast("onProductPurchased: " + productId);
                updateTextViews();*/
            }
            @Override
            public void onBillingError(int errorCode, Throwable error) {
                /*showToast("onBillingError: " + Integer.toString(errorCode));*/
            }
            @Override
            public void onBillingInitialized() {
                /*showToast("onBillingInitialized");
                updateTextViews();*/
                readyToPurchase = true;
            }
            @Override
            public void onPurchaseHistoryRestored() {
                //showToast("onPurchaseHistoryRestored");
                for(String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                //updateTextViews();
            }
        });

        ivHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	Intent intent = new Intent(ServiceActivity.this, HelpActivity.class);
        		startActivity(intent);
               // Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.help_text_for_service_screen));
            }
        });
    }

    private void getServicePlansDetail() {
        VolleyStringRequest request = new VolleyStringRequest(
                Request.Method.POST, Webservices.GET_USER_PLANS,
                getServicePlansSuccessLisner(), errorLisner()) {

            @Override
            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", ""+prefs.getInt("userid",-1));
                return params;
            }
        };

        // ***************Requesting Queue

        showProgress();
        mQueue.add(request);
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private Response.Listener<String> getServicePlansSuccessLisner() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String arg0) {
                // TODO Auto-generated method stub
                Log.e("Json", "==> " + arg0);
                stopProgress();

                try {
                    JSONObject jsonObject = new JSONObject(arg0);
                    if (jsonObject != null) {
                        JSONArray jsonServiceArray = jsonObject.getJSONArray("plan");
                        if (jsonServiceArray != null) {
                            for (int i = 0; i < jsonServiceArray.length(); i++) {
                                JSONObject jsonService = jsonServiceArray.getJSONObject(i);
                                int id = jsonService.getInt("id");
                                String name = jsonService.getString("name");
                                String description = jsonService.getString("description");
                                String amount = jsonService.getString("amount");
                                String period = jsonService.getString("period");
                                String strIsPlanActive = jsonService.getString("my_active_plan");
                                boolean isActive;
                                if(strIsPlanActive.equalsIgnoreCase("y")){
                                    isActive = true;
                                }else {
                                    isActive = false;
                                }

                                int personalTrac = jsonService.getInt("personal_trac");
                                int groupTrac = jsonService.getInt("group_trac");
                                int participantsCount = jsonService.getInt("participant_count");

                                Service service = new Service();
                                service.setId(id);
                                service.setName(name);
                                service.setDescription(description);
                                service.setActive(isActive);
                                service.setAmount(Double.parseDouble(amount));
                                service.setGroupTrac(groupTrac);
                                service.setPeriod(period);
                                service.setPersonalTrac(personalTrac);
                                service.setParticipantsCount(participantsCount);
                                listServices.add(service);
                            }
                        }
                    }

                    adapter = new ServiceListAdapter(mContext,listServices);
                    lvServiceList.setAdapter(adapter);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener errorLisner() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Json Error", "==> " + error.getMessage());
                stopProgress();
                Util.showMessage(mContext, error.getMessage());
            }
        };
    }

    public void showProgress() {
        mProgress = ProgressDialog.show(mContext, "", getString(R.string.loading_message));
    }

    public void stopProgress() {
        if (mProgress != null && mProgress.isShowing())
            mProgress.cancel();
    }
}
