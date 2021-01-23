package com.ftinc.scoop.binding;

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

    public void updateDrawable(@Nullable Drawable image, boolean animate) {
        ImageView view = (ImageView) mView.get();
        if (view == null || image == null) {
            return;
        }

        if (animate) {
            TransitionDrawable crossfade = new TransitionDrawable(new Drawable[]{view.getBackground(), image});
            crossfade.setCrossFadeEnabled(true);
            view.setImageDrawable(crossfade);

            crossfade.startTransition((int) mDurationMs);
        } else {
            view.setImageDrawable(image);
        }
    }
}
