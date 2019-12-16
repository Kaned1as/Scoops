package com.ftinc.scoop.adapters;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.ColorInt;

import com.ftinc.scoop.util.Utils;

/**
 * Created by r0adkll on 6/17/16.
 */

public class ImageViewColorAdapter implements ColorAdapter<ImageView> {

    @ColorInt private int savedColor = Color.TRANSPARENT;

    @Override
    public void applyColor(ImageView view, @ColorInt int color) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setImageTintList(Utils.colorToStateList(color));
        } else {
            view.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
        this.savedColor = color;
    }

    @Override
    public int getColor(ImageView view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(view.getImageTintList() != null) {
                return view.getImageTintList().getDefaultColor();
            }
        }
        return savedColor;
    }
}
