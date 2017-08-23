package com.thunderbird.chennai.fapapp.Utility;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.thunderbird.chennai.fapapp.R;


public class EditTextK extends android.support.v7.widget.AppCompatEditText {

	public EditTextK(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	public EditTextK(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		
	}
	
	public EditTextK(Context context) {
		super(context);
		init(null);
	}
	
	private void init(AttributeSet attrs) {
		if (attrs!=null) {
			 TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EditTextK);
			 String fontName = a.getString(R.styleable.EditTextK_fontNameET);
			 if (fontName!=null) {
				 Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), fontName);//"fonts/"+
				 setTypeface(myTypeface);
			 }
			 a.recycle();
		}
	}
	/* -----DVL-UP-er.KROID----- */
}