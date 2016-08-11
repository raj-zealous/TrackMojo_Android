package com.bluewavevision.tracmojo.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.bluewavevision.tracmojo.R;
import com.bluewavevision.tracmojo.adapters.SingleItemSelectionAdapter;
import com.bluewavevision.tracmojo.util.Util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class PersonalTracSetFrequencyActivity extends BaseActivity {

    private static final String DATE_FORMAT_FOR_FINISHING_DATE = "dd-MM-yyyy";

    private Context mContext;
    private TextView tvGroupName,tvParentFrequency,tvChildFrequency,tvChildFrequencyLabel,tvFinishDate,tvBack,tvNext;
    private EditText etTracName;


    private HashMap<String,ArrayList<String>> rateFrequencyList = new HashMap<String,ArrayList<String>>();
    private String defaultParentFrequency = "",defaultChildFrequency = "";

    private int mYear,mMonth,mDay;
    private String dob="";

    private String strTracIdeas,strCustomTracWordings1,strCustomTracWordings2,
                        strCustomTracWordings3,strCustomTracWordings4,strCustomTracWordings5,
                            strTracWordings;
    private boolean isCustomTracWordings;
    private int defaultTracIdeaId;
    private int defaultRateWordId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_trac_set_frequency);
        ivBack.setVisibility(View.INVISIBLE);
        ivInfo.setVisibility(View.VISIBLE);
        mContext = this;

        initializeComponents();
        setDataFromPreviousScreen();

        Intent intent = getIntent();

    }

    private void setDataFromPreviousScreen(){
        Intent intent = getIntent();
        if(intent!=null) {
            rateFrequencyList = (HashMap<String, ArrayList<String>>) intent.getSerializableExtra("rateFrequencyList");
            isCustomTracWordings = intent.getBooleanExtra("isCustomTracWordings",false);
            if (isCustomTracWordings) {
                strCustomTracWordings1 = intent.getStringExtra("etCustomTracWording1");
                strCustomTracWordings2 = intent.getStringExtra("etCustomTracWording2");
                strCustomTracWordings3 = intent.getStringExtra("etCustomTracWording3");
                strCustomTracWordings4 = intent.getStringExtra("etCustomTracWording4");
                strCustomTracWordings5 = intent.getStringExtra("etCustomTracWording5");
            }else{
                strTracWordings = intent.getStringExtra("tvChooseTracWordings");
                defaultRateWordId = intent.getIntExtra("defaultRateWordId",0);
            }

            strTracIdeas = intent.getStringExtra("etTracName");
            defaultTracIdeaId = intent.getIntExtra("defaultTracIdeaId",0);
            if(TextUtils.isEmpty(strTracIdeas)){
                strTracIdeas = intent.getStringExtra("tvTracName");
            }
            etTracName.setText(strTracIdeas);
            if(defaultTracIdeaId > 0){
                etTracName.setKeyListener(null);
            }
        }
    }


    private void initializeComponents(){

        etTracName = (EditText) findViewById(R.id.activity_set_frequency_etTracName);

        tvGroupName = (TextView) findViewById(R.id.activity_set_frequency_tvGroupName);
        tvParentFrequency = (TextView) findViewById(R.id.activity_set_frequency_tvParentFrequency);
        tvChildFrequency = (TextView) findViewById(R.id.activity_set_frequency_tvChildFrequency);
        tvChildFrequencyLabel = (TextView) findViewById(R.id.activity_set_frequency_tvChildFrequencyLabel);
        tvFinishDate = (TextView) findViewById(R.id.activity_set_frequency_tvFinishingDate);
        tvBack = (TextView) findViewById(R.id.activity_set_frequency_tvBack);
        tvNext = (TextView) findViewById(R.id.activity_set_frequency_tvNext);

        tvParentFrequency.setOnClickListener(this);
        tvChildFrequency.setOnClickListener(this);
        tvFinishDate.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        tvNext.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.activity_set_frequency_tvParentFrequency:
                showParentFrequencyDialog();
                break;

            case R.id.activity_set_frequency_tvChildFrequency:
                if(!TextUtils.isEmpty(defaultParentFrequency)){
                    showChildFrequencyDialog();
                }
                break;

            case R.id.activity_set_frequency_tvFinishingDate:
                showCustomDatePickerDialog();
                break;

            case R.id.activity_set_frequency_tvBack:
                onBackPressed();
                break;

            case R.id.activity_set_frequency_tvNext:
                if(Util.isEditTextEmpty(etTracName)){
                    Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.activity_set_frequncy_enter_trac_ideas));
                }else if(TextUtils.isEmpty(tvParentFrequency.getText().toString().trim())){
                    Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.activity_set_frequncy_select_frequency_as));
                }else if(rateFrequencyList.get(defaultParentFrequency)!=null && TextUtils.isEmpty(tvChildFrequency.getText().toString().trim())){
                    Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.activity_set_frequncy_select_frequency_on));
                }else if(TextUtils.isEmpty(tvFinishDate.getText().toString().trim())){
                    Util.showAlertDialog(mContext,getString(R.string.app_name),getString(R.string.activity_set_frequncy_select_finishing_date));
                }else {
                    goToInviteFollowersScreen();
                }
                break;
        }
    }

    private void goToInviteFollowersScreen(){
        Intent intent = new Intent(mContext,PersonalTracInviteFollowersActivity.class);
        intent.putExtra("rateFrequencyList",rateFrequencyList);
        intent.putExtra("etTracName",""+etTracName.getText().toString().trim());
        intent.putExtra("defaultTracIdeaId",defaultTracIdeaId);
        intent.putExtra("isCustomTracWordings",isCustomTracWordings);
        if(isCustomTracWordings){
            intent.putExtra("etCustomTracWording1",""+strCustomTracWordings1);
            intent.putExtra("etCustomTracWording2",""+strCustomTracWordings2);
            intent.putExtra("etCustomTracWording3",""+strCustomTracWordings3);
            intent.putExtra("etCustomTracWording4",""+strCustomTracWordings4);
            intent.putExtra("etCustomTracWording5",""+strCustomTracWordings5);
        }else{
            intent.putExtra("tvChooseTracWordings",""+strTracWordings);
            intent.putExtra("defaultRateWordId",defaultRateWordId);
        }

        intent.putExtra("parent_frequency",""+tvParentFrequency.getText().toString().trim());
        intent.putExtra("child_frequency",""+tvChildFrequency.getText().toString().trim());
        intent.putExtra("finishing_date",""+tvFinishDate.getText().toString().trim());
        startActivity(intent);
    }

    private void showParentFrequencyDialog()
    {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_single_item_selection_list);

        final ArrayList<String> listParentFrequency = new ArrayList<String>();

        String arry[] = getResources().getStringArray(R.array.array_category);
        for (String str : arry) {
            listParentFrequency.add(str);
        }

        int defaultPosition = 0;
        /*for(String key  : rateFrequencyList.keySet()){
            listParentFrequency.add(key);
        }*/

        /*try {
            String strName = listParentFrequency.remove(2);
            listParentFrequency.add(strName);
        }catch (Exception e){

        }*/

        for (int j = 0; j < listParentFrequency.size(); j++) {
            if(listParentFrequency.get(j).equalsIgnoreCase(defaultParentFrequency))
            {
                defaultPosition=j;
            }
        }

        SingleItemSelectionAdapter adapter = new SingleItemSelectionAdapter(mContext, listParentFrequency, defaultPosition);

        ListView lvRangelist = (ListView) dialog.findViewById(R.id.custom_choose_quantity_lv_range);
        lvRangelist.setAdapter(adapter);
        lvRangelist.setSmoothScrollbarEnabled(true);
        lvRangelist.setSelection(defaultPosition);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.dialog_title);
        tvTitle.setText(getString(R.string.activity_set_frequncy_select_trac_frequency_as));

        lvRangelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!defaultParentFrequency.equals(listParentFrequency.get(position))) {
                    tvChildFrequency.setText("");
                    tvFinishDate.setText("");
                    mDay = 0;
                    mMonth = 0;
                    mYear = 0;
                }

                defaultParentFrequency = listParentFrequency.get(position);
                tvParentFrequency.setText(listParentFrequency.get(position));
                if (rateFrequencyList.get(defaultParentFrequency) == null) {
                    hideChildFrequency();
                    tvChildFrequency.setOnClickListener(null);
                } else {
                    visibleChildFrequency();
                    tvChildFrequency.setOnClickListener(PersonalTracSetFrequencyActivity.this);
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showChildFrequencyDialog()
    {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_single_item_selection_list);

        int defaultPosition = 0;
        final ArrayList<String> listChildFrequency  = rateFrequencyList.get(defaultParentFrequency);

        for (int j = 0; j < listChildFrequency.size(); j++) {
            if(listChildFrequency.get(j).equalsIgnoreCase(defaultChildFrequency))
            {
                defaultPosition=j;
            }
        }

        SingleItemSelectionAdapter adapter = new SingleItemSelectionAdapter(mContext, listChildFrequency, defaultPosition);

        ListView lvRangelist = (ListView) dialog.findViewById(R.id.custom_choose_quantity_lv_range);
        lvRangelist.setAdapter(adapter);
        lvRangelist.setSmoothScrollbarEnabled(true);
        lvRangelist.setSelection(defaultPosition);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.dialog_title);
        tvTitle.setText(getString(R.string.activity_set_frequncy_select_trac_frequency_on));

        lvRangelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                defaultChildFrequency = listChildFrequency.get(position);
                tvChildFrequency.setText(listChildFrequency.get(position));

                dialog.dismiss();
            }
        });

        dialog.show();
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

        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

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
                tvFinishDate.setText(dob);
            }
        }, mYear, mMonth, mDay);
        //Calendar currentDateCalendar = Calendar.getInstance();
        //dpd.getDatePicker().setMinDate(currentDateCalendar.getTimeInMillis()-50000);
        if(!TextUtils.isEmpty(defaultParentFrequency)){
            Calendar currentDateCalendar = Calendar.getInstance();
            if(defaultParentFrequency.equalsIgnoreCase("Monthly")){
                currentDateCalendar.add(Calendar.MONTH, 1);
                dpd.getDatePicker().setMinDate(currentDateCalendar.getTimeInMillis()-50000);
                //dpd.getDatePicker().setMaxDate(currentDateCalendar.getTimeInMillis());
            }else if(defaultParentFrequency.equalsIgnoreCase("Weekly")){
                currentDateCalendar.add(Calendar.DAY_OF_MONTH, 7);
                dpd.getDatePicker().setMinDate(currentDateCalendar.getTimeInMillis()-50000);
            }else if(defaultParentFrequency.equalsIgnoreCase("Fortnightly")){
                currentDateCalendar.add(Calendar.DAY_OF_MONTH, 14);
                dpd.getDatePicker().setMinDate(currentDateCalendar.getTimeInMillis()-50000);
            }else{
                dpd.getDatePicker().setMinDate(currentDateCalendar.getTimeInMillis()-50000);
            }
        }
        dpd.show();

    }

    private void hideChildFrequency(){
        tvChildFrequencyLabel.setVisibility(View.GONE);
        tvChildFrequency.setVisibility(View.GONE);
    }

    private void visibleChildFrequency(){
        tvChildFrequencyLabel.setVisibility(View.VISIBLE);
        tvChildFrequency.setVisibility(View.VISIBLE);
    }
}
