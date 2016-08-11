package com.bluewavevision.tracmojo.ui;

import com.bluewavevision.tracmojo.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class HelpActivity extends BaseActivity {

    private Context mContext;

    private RelativeLayout relWhatIsIt,relWhy,relHow;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        mContext = this;
        ivLogo.setVisibility(View.GONE);
        tvHeader.setVisibility(View.VISIBLE);
        tvHeader.setText(getString(R.string.help_activity_header));

        initializeComponents();
    }

    private void initializeComponents(){
        relWhatIsIt = (RelativeLayout) findViewById(R.id.activity_help_relWhatIs);
        relWhy  = (RelativeLayout) findViewById(R.id.activity_help_relWhy);
        relHow  = (RelativeLayout) findViewById(R.id.activity_help_relHow);

        relWhatIsIt.setOnClickListener(this);
        relWhy.setOnClickListener(this);
        relHow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.activity_help_relWhatIs:
                goToCmsScreen("whatisit",getString(R.string.help_activity_what_is_it));
                break;

            case R.id.activity_help_relWhy:
                goToCmsScreen("why",getString(R.string.help_activity_why));
                break;
            case R.id.activity_help_relHow:
                goToCmsScreen("how",getString(R.string.help_activity_how));
                break;
            default:

                break;
        }

    }

    private void goToCmsScreen(String page,String header){
        Intent intent = new Intent(mContext,CmsActivity.class);
        intent.putExtra("page",page);
        intent.putExtra("header",header);
        startActivity(intent);
    }
}
