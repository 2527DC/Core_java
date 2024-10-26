package com.mlt.ets.rider.utills;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class MyEditText extends AppCompatEditText {
    public MyEditText(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/Muli-SemiBold.ttf", context);
        setTypeface(customFont);
    }
}
