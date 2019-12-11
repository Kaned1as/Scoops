package com.ftinc.scoop.adapters;

import android.widget.TextView;

import androidx.annotation.ColorInt;

/**
 * Created by r0adkll on 6/17/16.
 */

public class TextViewColorAdapter implements ColorAdapter<TextView> {
    @Override
    public void applyColor(TextView view, @ColorInt int color) {
        view.setTextColor(color);
    }

    @Override
    public int getColor(TextView view) {
        return view.getCurrentTextColor();
    }
}
