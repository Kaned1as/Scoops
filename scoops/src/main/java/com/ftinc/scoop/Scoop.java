package com.ftinc.scoop;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

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

    private StyleLevel lastAddedLevel = null;

    /**
     * Private constructor to prevent initialization
     */
    private Scoop() {
    }

    public static void initialize(Map<Integer, Integer> defaultColors) {
        // initialize default colors for new level
        Scoop.defaultColors.putAll(defaultColors);
    }

    /**
     * Sets main level to derive from. If you have it set, all new levels will
     * be inherited from this one.
     */
    public void setLastLevel(@Nullable StyleLevel level) {
        lastAddedLevel = level;
    }

    @UiThread
    public StyleLevel addStyleLevel() {
        StyleLevel level;

        if (lastAddedLevel != null) {
            level = new StyleLevel(lastAddedLevel);
        } else {
            level = new StyleLevel();

            // populate level toppings with default colors
            if (!defaultColors.isEmpty()) {
                for (Map.Entry<Integer, Integer> entry : defaultColors.entrySet()) {
                    // key is ToppingId, value is Color
                    Topping top = level.getOrCreateTopping(entry.getKey());
                    top.color = entry.getValue();
                }
            }
        }

        lastAddedLevel = level;
        return level;
    }
}
