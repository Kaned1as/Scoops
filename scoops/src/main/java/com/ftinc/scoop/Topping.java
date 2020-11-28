package com.ftinc.scoop;

import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * Model object that represents a Color Property that the developer can subscribe to changes for
 * to update specified views.
 *
 * Example:
 *
 *  Say you have a {@link Topping} that represents a PRIMARY color that you want to use for toolbars, fabs, and the like.
 *  and you want to update this color using Palette based on primary images loading (such as a banner) and you want bound
 *  views to automatically react to these changes. The user would then bind the associated views to this property
 *  and Scoops will automatically take care of it
 *
 * Package: com.ftinc.scoop.model
 * Created by drew.heavner on 6/17/16.
 */
public final class Topping {

    final int id;

    @ColorInt
    int color = 0;

    @Nullable
    Drawable drawable = null;

    Topping(Topping other) {
        id = other.id;
        color = other.color;
        drawable = other.drawable;
    }

    Topping(int id) {
        this.id = id;
    }

    Topping(int id, @ColorInt int color) {
        this.id = id;
        this.color = color;
    }

    public int getId(){
        return id;
    }

    public int getColor() {
        return color;
    }

    void updateColor(@ColorInt int color) {
        this.color = color;
    }

    void updateDrawable(@Nullable Drawable image) {
        this.drawable = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Topping topping = (Topping) o;

        if (id != topping.id) return false;
        if (color != topping.color) return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + color;
        return result;
    }

    @Override
    public String toString() {
        return "Topping{" +
                "id=" + id +
                ", color=" + color +
                '}';
    }
}
