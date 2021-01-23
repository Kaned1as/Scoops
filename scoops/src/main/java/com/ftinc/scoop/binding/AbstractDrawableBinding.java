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
public abstract class AbstractDrawableBinding extends AbstractBinding {

    protected final WeakReference<? extends View> mView;

    public AbstractDrawableBinding(int toppingId, View view) {
        super(toppingId);
        mView = new WeakReference<>(view);
    }

    @Override
    public void update(Integer color) {
        // NOP
    }

    public void updateDrawable(@Nullable Drawable image) {
        updateDrawable(image, true);
    }

    public abstract void updateDrawable(@Nullable Drawable image, boolean animate);

    @Override
    public void unbind() {
        mView.clear();
    }
}
