package com.ftinc.scoop.binding;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ftinc.scoop.adapters.ColorAdapter;

import java.lang.ref.WeakReference;

/**
 * @author Kanedias
 * <p>
 * Created on 28.11.20
 */
public class ViewDrawableBinding extends AbstractBinding {

    private final WeakReference<View> mView;
    private final long mDurationMs;

    public ViewDrawableBinding(int toppingId, View view) {
        this(toppingId, view, DEFAULT_ANIMATION_DURATION);
    }

    public ViewDrawableBinding(int toppingId, View view, long durationMs) {
        super(toppingId);
        mView = new WeakReference<>(view);
        this.mDurationMs = durationMs;
    }

    @Override
    public void update(Integer color) {
        // NOP
    }

    public void updateDrawable(@Nullable Drawable image) {
        updateDrawable(image, true);
    }

    public void updateDrawable(@Nullable Drawable image, boolean animate) {
        View view = mView.get();
        if (view == null || image == null) {
            return;
        }

        if (animate) {
            TransitionDrawable crossfade = new TransitionDrawable(new Drawable[]{view.getBackground(), image});
            view.setBackground(crossfade);

            crossfade.startTransition((int) mDurationMs);
        } else {
            view.setBackground(image);
        }
    }

    @Override
    public void unbind() {
        mView.clear();
    }
}
