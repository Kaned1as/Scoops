package com.ftinc.scoop.binding;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Build;
import android.view.animation.Interpolator;

import androidx.annotation.ColorInt;

/**
 * Project: ThemeEngineTest
 * Package: com.ftinc.scoop.model
 * Created by drew.heavner on 6/21/16.
 */

public abstract class AnimatedColorBinding extends AbstractBinding {

    private ValueAnimator mAnimator;
    private Interpolator mInterpolator;
    private final long mDurationMs;

    public AnimatedColorBinding(int toppingId, Interpolator interpolator) {
        this(toppingId, interpolator, DEFAULT_ANIMATION_DURATION);
    }

    public AnimatedColorBinding(int toppingId, Interpolator mInterpolator, long durationMs) {
        super(toppingId);
        this.mInterpolator = mInterpolator;
        this.mDurationMs = durationMs;
    }

    public void update(@ColorInt Integer toColor, boolean animate) {
        if (isPaused()) {
            return;
        }

        int fromColor = getCurrentColor() != 0 ? getCurrentColor() : Color.TRANSPARENT;

        if (fromColor != toColor && animate) {

            if (mAnimator != null) {
                mAnimator.cancel();
                mAnimator = null;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mAnimator = ValueAnimator.ofArgb(fromColor, toColor).setDuration(mDurationMs);
            } else {
                mAnimator = ValueAnimator.ofInt(fromColor, toColor).setDuration(mDurationMs);
                mAnimator.setEvaluator(new ArgbEvaluator());
            }

            if (mInterpolator != null) mAnimator.setInterpolator(mInterpolator);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int colorValue = (int) valueAnimator.getAnimatedValue();
                    applyColor(colorValue);
                }
            });

            mAnimator.start();

        } else {
            applyColor(toColor);
        }
    }

    @Override
    public void update(@ColorInt Integer color) {
        update(color, true);
    }

    @Override
    public void unbind() {
        mInterpolator = null;
        if(mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    @ColorInt
    abstract int getCurrentColor();

    abstract void applyColor(@ColorInt int color);

}
