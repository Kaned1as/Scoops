package com.ftinc.scoop.binding;

import android.support.annotation.ColorInt;


/**
 * Project: ThemeEngineTest
 * Package: com.ftinc.scoop.model
 * Created by drew.heavner on 6/21/16.
 */

public abstract class AbstractBinding {

    public static final long DEFAULT_ANIMATION_DURATION = 600L;

    protected int toppingId;

    public AbstractBinding(int toppingId){
        this.toppingId = toppingId;
    }

    public int getToppingId(){
        return toppingId;
    }

    public abstract void update(@ColorInt Integer color);

    public abstract void unbind();

}
