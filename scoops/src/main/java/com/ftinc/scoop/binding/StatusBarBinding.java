package com.ftinc.scoop.binding;

import android.app.Activity;
import android.os.Build;
import android.view.animation.Interpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;

public class StatusBarBinding extends AnimatedBinding {

    // Dragons Beware! This will memory leak if not properly unbound
    private Activity mActivity;

    public StatusBarBinding(int toppingId, Activity activity, Interpolator interpolator) {
        super(toppingId, interpolator);
        mActivity = activity;
    }


    public StatusBarBinding(int toppingId, Activity activity, Interpolator interpolator, long duration) {
        super(toppingId, interpolator, duration);
        mActivity = activity;
    }

    @Override
    public void update(@ColorInt Integer color) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.update(color);
        }
    }

    @Override
    public void update(Integer toColor, boolean animate) {
        if (isPaused()) {
            return;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.update(toColor, animate);
        }
    }

    @Override
    public void unbind() {
        super.unbind();
        mActivity = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    int getCurrentColor() {
        return mActivity.getWindow().getStatusBarColor();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    void applyColor(@ColorInt int color) {
        mActivity.getWindow().setStatusBarColor(color);
    }
}
