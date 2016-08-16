package com.tracmojo.customwidget;

import com.tracmojo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CircularDrawable extends View {

    private Paint circlePaint;
    private Paint circleStrokePaint;
    private RectF circleArc;

    // Attrs
    private int circleRadius;
    private int circleFillColor;
    private int circleStrokeColor;
    private int circleStartAngle;
    private int circleEndAngle;
    private boolean isShadow;


    public void setCircleEndAngle(int circleEndAngle) {
        this.circleEndAngle = circleEndAngle;
    }

    public CircularDrawable(Context context, AttributeSet attrs) {

        super(context, attrs);
        init(attrs); // Read all attributes

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        circleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleStrokePaint.setStyle(Paint.Style.STROKE);
        circleStrokePaint.setStrokeWidth(2);
        circleStrokePaint.setColor(circleStrokeColor);
    }

    public void init(AttributeSet attrs) {
        // Go through all custom attrs.
        TypedArray attrsArray = getContext().obtainStyledAttributes(attrs, R.styleable.circleview);
        circleRadius = attrsArray.getInteger(R.styleable.circleview_cRadius, 0);
        circleFillColor = attrsArray.getColor(R.styleable.circleview_cFillColor, 16777215);
        circleStrokeColor = attrsArray.getColor(R.styleable.circleview_cStrokeColor, -1);
        circleStartAngle = attrsArray.getInteger(R.styleable.circleview_cAngleStart, 0);
        circleEndAngle = attrsArray.getInteger(R.styleable.circleview_cAngleEnd, 360);
        isShadow = attrsArray.getBoolean(R.styleable.circleview_cIsShadow,true);
        // Google tells us to call recycle.
        attrsArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Move canvas down and right 1 pixel.
        // Otherwise the stroke gets cut off.
        //canvas.translate(1, 1);
        this.setLayerType(LAYER_TYPE_SOFTWARE, circlePaint);
        if(isShadow)
            circlePaint.setShadowLayer(4.0f, 0.0f, 2.0f, circleFillColor);
        circlePaint.setColor(circleFillColor);
        RectF oval1=null;
        if(isShadow){
            oval1 = new RectF(10, 0, getWidth()-10, getHeight()-10);
        }else {
            oval1 = new RectF(0, 0, getWidth(), getHeight());
        }
        canvas.drawOval(oval1, circlePaint);
        //canvas.drawArc(circleArc, circleStartAngle, circleEndAngle, true, circlePaint);
        //canvas.drawArc(circleArc, circleStartAngle, circleEndAngle, true, circleStrokePaint);



        /*Paint paint = new Paint();
        //this.setLayerType(LAYER_TYPE_SOFTWARE, paint);
        //paint.setShadowLayer(5.0f, 0.0f, 5.0f, circleFillColor);
        *//*paint.setColor(getResources().getColor(R.color.list_oval_shadow));
        paint.setStyle(Paint.Style.STROKE);*//*
        paint.setColor(circleFillColor);
        paint.setStyle(Paint.Style.FILL);
        *//*paint.setShader(new RadialGradient(getWidth() / 2, getHeight() / 2,
                getHeight() / 3, circleFillColor, circleFillColor, Shader.TileMode.MIRROR));*//*
        RectF oval1 = new RectF(10, 0, getWidth()-10, getHeight()-10);
        canvas.drawOval(oval1, paint);*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int measuredWidth = measureWidth(widthMeasureSpec);
        if (circleRadius == 0) // No radius specified.
        {                     // Lets see what we can make.
            // Check width size. Make radius half of available.
            circleRadius = measuredWidth / 2;
            int tempRadiusHeight = measureHeight(heightMeasureSpec) / 2;
            if (tempRadiusHeight < circleRadius)
                // Check height, if height is smaller than
                // width, then go half height as radius.
                circleRadius = tempRadiusHeight;
        }
        // Remove 2 pixels for the stroke.
        int circleDiameter = circleRadius * 2 - 2;
        // RectF(float left, float top, float right, float bottom)
        circleArc = new RectF(0, 0, circleDiameter, circleDiameter);
        int measuredHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
        Log.d("onMeasure() ::", "measuredHeight =>" + String.valueOf(measuredHeight) + "px measuredWidth => " + String.valueOf(measuredWidth) + "px");
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        if (specMode == MeasureSpec.AT_MOST) {
            result = circleRadius * 2;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    public int getCircleFillColor() {
        return circleFillColor;
    }

    public void setCircleFillColor(int circleFillColor) {
        this.circleFillColor = circleFillColor;
        this.circlePaint.setColor(circleFillColor);
    }

    public int getCircleStrokeColor() {
        return circleStrokeColor;
    }

    public void setCircleStrokeColor(int circleStrokeColor) {
        this.circleStrokeColor = circleStrokeColor;
    }

    public int getCircleStartAngle() {
        return circleStartAngle;
    }

    public void setCircleStartAngle(int circleStartAngle) {
        this.circleStartAngle = circleStartAngle;
    }

    public int getCircleEndAngle() {
        return circleEndAngle;
    }
}