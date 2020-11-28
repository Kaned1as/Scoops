package com.ftinc.scoop.binding;

import android.graphics.Color;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ftinc.scoop.adapters.ColorAdapter;

import java.lang.ref.WeakReference;

/**
 * Created by r0adkll on 6/17/16.
 */

public class ViewBinding extends AnimatedColorBinding {

    private final WeakReference<View> mView;
    private final ColorAdapter<View> mColorAdapter;

    public ViewBinding(int toppingId,
                       @NonNull View view,
                       @NonNull ColorAdapter<View> adapter,
                       @Nullable Interpolator interpolator) {
        super(toppingId, interpolator);
        mView = new WeakReference<>(view);
        mColorAdapter = adapter;
    }

    public ViewBinding(int toppingId,
                       @NonNull View view,
                       @NonNull ColorAdapter adapter,
                       @Nullable Interpolator interpolator,
                       long durationMs) {
        super(toppingId, interpolator, durationMs);
        mView = new WeakReference<>(view);
        mColorAdapter = adapter;
    }

    @Override
    public void unbind() {
        mView.clear();
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
