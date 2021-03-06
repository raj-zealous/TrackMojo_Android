package com.tracmojo.customwidget;

import com.tracmojo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class CustomTextView extends TextView {

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		  if (!isInEditMode())
		init(attrs);
	}
	
	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		  if (!isInEditMode())
		init(attrs);
		
	}
	
	public CustomTextView(Context context) {
		super(context);
		  if (!isInEditMode())
		init(null);
	}
	
	private void init(AttributeSet attrs) {
		if (attrs!=null) {
			 TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);
			 String fontName = a.getString(R.styleable.CustomTextView_fontName);
			 if (fontName!=null) {
				 Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), ""+fontName);
				 setTypeface(myTypeface);
			 }
			 else
			 {
				 Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Regular.ttf");
				 setTypeface(myTypeface);
			 }
			 a.recycle();
		}
	}

}
