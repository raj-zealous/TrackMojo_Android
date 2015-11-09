package com.tracmojo.customwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.tracmojo.R;


public class CustomEditText extends EditText {

	public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		
	}
	
	public CustomEditText(Context context) {
		super(context);
		init(null);
	}
	
	private void init(AttributeSet attrs) {
		if (attrs!=null) {
			 TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomEditText);
			 String fontName = a.getString(R.styleable.CustomEditText_fontNameForEditText);
			 if (fontName!=null) {
				 Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), ""+fontName);
				 setTypeface(myTypeface);
			 }
			 a.recycle();
		}
	}

}