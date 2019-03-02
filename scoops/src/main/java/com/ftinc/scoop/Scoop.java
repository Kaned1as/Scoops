package com.ftinc.scoop;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.annotation.UiThread;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Interpolator;

import com.ftinc.scoop.adapters.ColorAdapter;
import com.ftinc.scoop.binding.AnimatedBinding;
import com.ftinc.scoop.binding.AbstractBinding;
import com.ftinc.scoop.binding.StatusBarBinding;
import com.ftinc.scoop.binding.ViewBinding;
import com.ftinc.scoop.internal.ToppingBinder;
import com.ftinc.scoop.util.AttrUtils;
import com.ftinc.scoop.util.BindingUtils;

import java.util.*;


/**
 * Main singleton instance to control colors of toppings
 *
 * @author drew.heavner, created on 6/7/16
 * @author Kanedias, altered on 03 Aug 2018
 */
@SuppressWarnings("WeakerAccess") // this is public API
public class Scoop {

    private static class Holder {
        private static Scoop sInstance = new Scoop();
    }


    public static Scoop getInstance() {
        return Holder.sInstance;
    }

    private static Map<Integer, Integer> defaultColors = new HashMap<>();

    /**
     * Stack of style levels
     */
    private Deque<StyleLevel> mLevels = new LinkedList<>();

    /**
     * Private constructor to prevent initialization
     */
    private Scoop() {
        addStyleLevel(); // first and default style level, with no restrictions for views
    }

    public static void initialize(Map<Integer, Integer> defaultColors) {
        // initialize default colors for new level
        Scoop.defaultColors.putAll(defaultColors);
    }

    @UiThread
    public StyleLevel addStyleLevel() {
        // clone previous level colors if we can
        StyleLevel level;
        if (!mLevels.isEmpty()) {
            level = new StyleLevel(mLevels.peek());
        } else {
            level = new StyleLevel();

            // populate level toppings with default colors
            if (!defaultColors.isEmpty()) {
                for (Map.Entry<Integer, Integer> entry : defaultColors.entrySet()) {
                    // key is ToppingId, value is Color
                    level.toppings.put(entry.getKey(), new Topping(entry.getKey(), entry.getValue()));
                }
            }
        }

        mLevels.push(level);

        return level;
    }

    /**
     * Removes one style layer from the style stack, rebinding the underlying layer back to its views
     */
    public void popStyleLevel() {
        popStyleLevel(true);
    }

    /**
     * Removes one style layer from the style stack
     *
     * @param rebind if true, restores colors of all views in current style level to their respective
     *               values of this level. This is useful if you have bound views in both level layers
     */
    @UiThread
    public void popStyleLevel(Boolean rebind) {
        if (mLevels.size() == 1) {
            // we can't get rid of top level
            throw new IllegalStateException("Requested to pop style level but only top level remains!");
        }

        StyleLevel oldLevel = mLevels.pop();
        StyleLevel curLevel = mLevels.peek();

        // if we have status bar binding in both levels, should reset to the previous level
        // as status bar remains between fragment transitions
        StatusBarBinding oldSb =  oldLevel.getStatusBarBinding();
        StatusBarBinding curSb =  curLevel.getStatusBarBinding();
        if (curSb != null) {
            // return to previous status bar value
            curSb.update(curLevel.toppings.get(curSb.getToppingId()).color);
        } else if (oldSb != null && defaultColors.containsKey(oldSb.getToppingId())) {
            // we don't have binding on lower level, back to default color before we unbind
            oldSb.update(defaultColors.get(oldSb.getToppingId()), false);
        }

        // unbind everything from the old level
        oldLevel.unbind();

        if (rebind) {
            curLevel.rebind();
        }
    }
}
