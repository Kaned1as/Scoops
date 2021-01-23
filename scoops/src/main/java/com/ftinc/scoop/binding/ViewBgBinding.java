package com.ftinc.scoop.binding;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * @author Kanedias
 * <p>
 * Created on 28.11.20
 */
public class ViewBgBinding extends AbstractDrawableBinding {

    private final long mDurationMs;

    public ViewBgBinding(int toppingId, View view) {
        this(toppingId, view, DEFAULT_ANIMATION_DURATION);
    }

    public ViewBgBinding(int toppingId, View view, long durationMs) {
        super(toppingId, view);
        this.mDurationMs = durationMs;
    }

    public void updateDrawable(@Nullable Drawable image, boolean animate) {
        View view = mView.get();
        if (view == null || image == null) {
            return;
        }

        if (animate) {
            TransitionDrawable crossfade = new TransitionDrawable(new Drawable[]{view.getBackground(), image});
            crossfade.setCrossFadeEnabled(true);
            view.setBackground(crossfade);

            crossfade.startTransition((int) mDurationMs);
        } else {
            view.setBackground(image);
        }
    }
}
