package com.tracmojo.customwidget;

import com.tracmojo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;


public class CustomButton extends Button {

	public CustomButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		 if (!isInEditMode())
		init(attrs);
	}
	
	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		 if (!isInEditMode())
		init(attrs);
		
	}
	
	public CustomButton(Context context) {
		super(context);
		 if (!isInEditMode())
		init(null);
	}
	
	private void init(AttributeSet attrs) {
		if (attrs!=null) {
			 TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomButton);
			 String fontName = a.getString(R.styleable.CustomButton_fontNameForButton);
			 if (fontName!=null) {
				 Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), ""+fontName);
				 setTypeface(myTypeface);
			 }
			 a.recycle();
		}
	}

}
