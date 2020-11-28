package com.ftinc.scoop.binding;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;

import java.lang.ref.WeakReference;

public class StatusBarBinding extends AnimatedColorBinding {

    private final WeakReference<Activity> mActivity;

    public StatusBarBinding(int toppingId, Activity activity, Interpolator interpolator) {
        super(toppingId, interpolator);
        mActivity = new WeakReference<>(activity);
    }


    public StatusBarBinding(int toppingId, Activity activity, Interpolator interpolator, long duration) {
        super(toppingId, interpolator, duration);
        mActivity = new WeakReference<>(activity);
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
        mActivity.clear();
        super.unbind();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    int getCurrentColor() {
        Activity activity = mActivity.get();
        if (activity == null) {
            return Color.TRANSPARENT;
        }

        return activity.getWindow().getStatusBarColor();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    void applyColor(@ColorInt int color) {
        Activity activity = mActivity.get();
        if (activity == null) {
            return;
        }

        activity.getWindow().setStatusBarColor(color);
    }
}
