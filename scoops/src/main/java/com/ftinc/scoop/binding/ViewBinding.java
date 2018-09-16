package com.ftinc.scoop.binding;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Interpolator;

import com.ftinc.scoop.adapters.ColorAdapter;

import java.lang.ref.WeakReference;

/**
 * Created by r0adkll on 6/17/16.
 */

public class ViewBinding extends AnimatedBinding {

    private WeakReference<View> mView;
    private ColorAdapter<View> mColorAdapter;

    public ViewBinding(int toppingId,
                       @NonNull View view,
                       @NonNull ColorAdapter<View> adapter,
                       @Nullable Interpolator interpolator){
        super(toppingId, interpolator);
        mView = new WeakReference<>(view);
        mColorAdapter = adapter;
    }

    public ViewBinding(int toppingId,
                       @NonNull View view,
                       @NonNull ColorAdapter adapter,
                       @Nullable Interpolator interpolator,
                       long duration){
        super(toppingId, interpolator, duration);
        mView = new WeakReference<>(view);
        mColorAdapter = adapter;
    }

    @Override
    public void unbind() {
        mView = null;
        super.unbind();
    }

    @Override
    int getCurrentColor() {
        View view = mView.get();
        if (view != null) {
            return mColorAdapter.getColor(view);
        }

        return Color.TRANSPARENT;
    }

    @Override
    void applyColor(@ColorInt int color) {
        View view = mView.get();
        if (view != null) {
            mColorAdapter.applyColor(view, color);
        }
    }
}
