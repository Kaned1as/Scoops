package com.ftinc.scoop.binding;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

/**
 * @author Kanedias
 * <p>
 * Created on 28.11.20
 */
public class ViewImageBinding extends AbstractDrawableBinding {

    private final long mDurationMs;

    public ViewImageBinding(int toppingId, ImageView view) {
        this(toppingId, view, DEFAULT_ANIMATION_DURATION);
    }

    public ViewImageBinding(int toppingId, ImageView view, long durationMs) {
        super(toppingId, view);
        this.mDurationMs = durationMs;
    }

    public void updateDrawable(@Nullable Drawable next, boolean animate) {
        ImageView view = (ImageView) mView.get();
        if (view == null) {
            // nothing to update
            return;
        }

        if (next == null) {
            next = new ColorDrawable(Color.TRANSPARENT);
        } else {
            next = next.mutate();
        }

        Drawable prev = view.getDrawable();
        if (prev == null) {
            prev = new ColorDrawable(Color.TRANSPARENT);
        }

        if (animate) {
            TransitionDrawable crossfade = new TransitionDrawable(new Drawable[]{prev, next});
            crossfade.setCrossFadeEnabled(true);
            view.setImageDrawable(crossfade);

            crossfade.startTransition((int) mDurationMs);
        } else {
            view.setImageDrawable(next);
        }
    }
}
