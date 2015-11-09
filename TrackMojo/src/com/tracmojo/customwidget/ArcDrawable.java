package com.tracmojo.customwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.tracmojo.R;

public class ArcDrawable extends View {

	private Paint circlePaint;
	private Context mContext;
    private String cFirstColord="#ffffff",cSecondColor="#ffffff",cThirdColor="#ffffff",cFourthColor="#ffffff",cFifthColor="#ffffff";
	public ArcDrawable(Context context, AttributeSet attrs) {

		super(context, attrs);
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setColor(Color.BLACK);
		this.mContext = context;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int fifteen = (int) mContext.getResources().getDimension(R.dimen.dp15);
		int seventeen = (int) mContext.getResources().getDimension(R.dimen.dp17);
		int eighteen = (int) mContext.getResources().getDimension(R.dimen.dp18);
		int threeHundred = (int) mContext.getResources().getDimension(R.dimen.dp300);
		int threeHundredAngle = (int) mContext.getResources().getDimension(R.dimen.dpAngle300);
		int threeTwentyFive = (int) mContext.getResources().getDimension(R.dimen.dp325);
		int threeFifty = (int) mContext.getResources().getDimension(R.dimen.dp350);
		int threeSeventyFive = (int) mContext.getResources().getDimension(R.dimen.dp375);
		int fourHundred =  (int) mContext.getResources().getDimension(R.dimen.dp400);
		int oneTwenty = (int) mContext.getResources().getDimension(R.dimen.dp120);
		int fourFifty  = (int) mContext.getResources().getDimension(R.dimen.dp450);
		
		RectF oval = new RectF();
		circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		oval.set(-threeHundred, oneTwenty, threeHundred, fourFifty);
		
		circlePaint.setColor(Color.parseColor(cFirstColord));
		canvas.drawArc(oval, threeHundredAngle, fifteen, true, circlePaint);
		
		circlePaint.setColor(Color.parseColor(cSecondColor));
		canvas.drawArc(oval, threeTwentyFive, seventeen, true, circlePaint);
		
		circlePaint.setColor(Color.parseColor(cThirdColor));
		canvas.drawArc(oval, threeFifty, eighteen, true, circlePaint);
		
		circlePaint.setColor(Color.parseColor(cFourthColor));
		canvas.drawArc(oval, threeSeventyFive, seventeen, true, circlePaint);
		
		circlePaint.setColor(Color.parseColor(cFifthColor));
		canvas.drawArc(oval, fourHundred, fifteen, true, circlePaint);
	}

    public String getcFirstColord() {
        return cFirstColord;
    }

    public void setcFirstColord(String cFirstColord) {
        this.cFirstColord = cFirstColord;
    }

    public String getcSecondColor() {
        return cSecondColor;
    }

    public void setcSecondColor(String cSecondColor) {
        this.cSecondColor = cSecondColor;
    }

    public String getcThirdColor() {
        return cThirdColor;
    }

    public void setcThirdColor(String cThirdColor) {
        this.cThirdColor = cThirdColor;
    }

    public String getcFourthColor() {
        return cFourthColor;
    }

    public void setcFourthColor(String cFourthColor) {
        this.cFourthColor = cFourthColor;
    }

    public String getcFifthColor() {
        return cFifthColor;
    }

    public void setcFifthColor(String cFifthColor) {
        this.cFifthColor = cFifthColor;
    }
}