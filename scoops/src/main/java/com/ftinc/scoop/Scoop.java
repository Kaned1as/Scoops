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

    static final Map<Class<?>, ToppingBinder<Object>> BINDERS = new LinkedHashMap<>();
    static final ToppingBinder<Object> NOP_VIEW_BINDER = new ToppingBinder<Object>() {
        @Override
        public List<AbstractBinding> bind(Object target) {
            return new ArrayList<>();
        }
    };

    private static final String TAG = "Scoop";

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
     * Debug flag for logging
     */
    private static boolean debug = false;

    /**
     * Private constructor to prevent initialization
     */
    private Scoop() {
        addStyleLevel(); // first and default style level
    }

    public static void initialize(Map<Integer, Integer> defaultColors) {
        // initialize default colors for new level
        Scoop.defaultColors.putAll(defaultColors);
    }

    @NonNull
    @UiThread
    private ToppingBinder<Object> getViewBinder(@NonNull Object target) {
        Class<?> targetClass = target.getClass();
        if (debug) Log.d(TAG, "Looking up topping binder for " + targetClass.getName());
        return findViewBinderForClass(targetClass);
    }

    @NonNull
    @UiThread
    private ToppingBinder<Object> findViewBinderForClass(Class<?> cls) {
        ToppingBinder<Object> viewBinder = BINDERS.get(cls);
        if (viewBinder != null) {
            if (debug) Log.d(TAG, "HIT: Cached in topping binder map.");
            return viewBinder;
        }
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            return NOP_VIEW_BINDER;
        }

        try {
            Class<?> viewBindingClass = Class.forName(clsName + "_ToppingBinder");
            //noinspection unchecked
            viewBinder = (ToppingBinder<Object>) viewBindingClass.newInstance();
            if (debug) Log.d(TAG, "HIT: Loaded topping binder class.");
        } catch (ClassNotFoundException e) {
            if (debug) Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
            viewBinder = findViewBinderForClass(cls.getSuperclass());
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to create topping binder for " + clsName, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to create topping binder for " + clsName, e);
        }
        BINDERS.put(cls, viewBinder);
        return viewBinder;
    }

    @UiThread
    public void addStyleLevel() {
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
    }

    @UiThread
    public void popStyleLevel() {
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
        curLevel.rebind();
    }

    /**
     * Get the set of bindings for a given class
     *
     * @param obj the object key for the bindings to look up
     * @return the set of bindings for the class
     */
    private Set<AbstractBinding> getBindings(Object obj) {
        Map<Object, Set<AbstractBinding>> anchors = mLevels.peek().anchors;
        Set<AbstractBinding> bindings = anchors.get(obj);
        if (bindings == null) {
            bindings = new HashSet<>();
            anchors.put(obj, bindings);
        }
        return bindings;
    }

    /**
     * Find the {@link Topping} object for it's given Id or create one if not found
     *
     * @param toppingId the id of the topping to get
     * @return the topping associated with the id
     */
    private Topping getOrCreateTopping(int toppingId) {
        SparseArray<Topping> toppings = mLevels.peek().toppings;
        Topping topping = toppings.get(toppingId);
        if (topping == null) {
            topping = new Topping(toppingId);
            toppings.put(toppingId, topping);
        }

        return topping;
    }

    private void autoUpdateBinding(AbstractBinding binding, Topping topping) {
        if (topping.getColor() != 0) {
            if (binding instanceof AnimatedBinding) {
                ((AnimatedBinding) binding).update(topping.color, false);
            } else {
                binding.update(topping.color);
            }
        }
    }

    /**
     * Enable debug logging
     */
    public static void setDebug(boolean flag) {
        debug = flag;
    }
    /**
     * Apply the desired theme to an activity and it's window
     *
     * @param activity the activity to apply to
     * @param theme    the theme to apply
     */
    private void apply(Activity activity, @StyleRes int theme) {
        // Apply theme
        activity.setTheme(theme);

        // Ensure window background get's properly set
        int color = AttrUtils.getColorAttr(activity, android.R.attr.colorBackground);
        activity.getWindow().setBackgroundDrawable(new ColorDrawable(color));
    }

    /**
     * Bind all the annotated elements to a given activity
     *
     * @param activity the activity to bind to
     * @see BindTopping
     * @see BindToppingStatus
     */
    public void bind(Activity activity) {
        // Get the pre-generated bindings
        List<AbstractBinding> bindings = getViewBinder(activity).bind(activity);

        // Iterate and verify topping creation and auto-applying
        for (AbstractBinding binding : bindings) {
            Topping topping = getOrCreateTopping(binding.getToppingId());
            autoUpdateBinding(binding, topping);
        }

        // add to system
        Set<AbstractBinding> allBindings = getBindings(activity);
        allBindings.addAll(bindings);
    }

    /**
     * Bind a view to a topping on a given object
     *
     * @param obj       the class the view belongs to
     * @param toppingId the id of the topping to bind to
     * @param view      the view to bind
     * @return self for chaining
     */
    public Scoop bind(Object obj, int toppingId, View view) {
        return bind(obj, toppingId, view, null);
    }

    /**
     * Bind a view to a topping on a given object with a specified color adapter
     *
     * @param obj          the classs the view belongs to
     * @param toppingId    the id of the topping
     * @param view         the view to bind
     * @param colorAdapter the color adapter to bind with
     * @return self for chaining
     */
    public Scoop bind(Object obj, int toppingId, View view, @Nullable ColorAdapter colorAdapter) {
        return bind(obj, toppingId, view, colorAdapter, null);
    }

    /**
     * Bind a view to a topping on a given object with a specified color adapter and change animation
     * interpolator
     *
     * @param obj          the class the view belongs to
     * @param toppingId    the id of the topping
     * @param view         the view to bind
     * @param colorAdapter the color adapter to bind with
     * @param interpolator the interpolator to use when switching colors
     * @return self for chaining
     */
    public Scoop bind(Object obj, int toppingId, View view, @Nullable ColorAdapter colorAdapter, @Nullable Interpolator interpolator) {

        // Get a default color adapter if not supplied
        if (colorAdapter == null) {
            colorAdapter = BindingUtils.getColorAdapter(view.getClass());
        }

        // Generate Binding
        AbstractBinding binding = new ViewBinding(toppingId, view, colorAdapter, interpolator);

        // Bind
        return bind(obj, toppingId, binding);
    }

    /**
     * Bind the status bar of an activity to a topping so that it's color is updated when the
     * user/developer updates the color for that topping id.
     *
     * This does nothing on APIs < 21.
     *
     * @param obj object in scope of which this binding is created
     * @param activity  the activity whoes status bar to bind to
     * @param toppingId the id of the topping to bind with
     * @return self for chaining
     */
    public Scoop bindStatusBar(Object obj, Activity activity, int toppingId) {
        return bindStatusBar(obj, activity, toppingId, null);
    }

    /**
     * Bind the status bar of an activity to a topping so that it's color is updated when the
     * user/developer updates the color for that topping id and animation it's color change using
     * the provided interpolator
     *
     * This does nothing on APIs < 21.
     *
     * @param activity     the activity whoes status bar to bind to
     * @param toppingId    the id of the topping to bind with
     * @param interpolator the interpolator that defines how the animation for the color change will run
     * @return self for chaining
     */
    public Scoop bindStatusBar(Object obj, Activity activity, int toppingId, @Nullable Interpolator interpolator) {
        AbstractBinding binding = new StatusBarBinding(toppingId, activity, interpolator);
        return bind(obj, toppingId, binding);
    }

    /**
     * Provide a custom binding to a certain topping id on a given object. This allows you to
     * customize the changes between color on certain properties, i.e. Toppings, to define it
     * to your use case
     *
     * @param obj       the object to bind on
     * @param toppingId the topping id to bind to
     * @param binding   the binding that defines how your custom properties are updated
     * @return self for chaining
     */
    public Scoop bind(Object obj, int toppingId, AbstractBinding binding) {

        // Find or Create Topping
        Topping topping = getOrCreateTopping(toppingId);

        // If topping has a color set, auto-apply to binding
        autoUpdateBinding(binding, topping);

        // Store binding
        Set<AbstractBinding> bindings = getBindings(obj);
        bindings.add(binding);

        return this;
    }

    /**
     * Unbind all bindings on a certain class
     *
     * @param obj the class/object that you previously made bindings to (i.e. an Activity, or Fragment)
     */
    public void unbind(Object obj) {
        Set<AbstractBinding> bindings = getBindings(obj);
        for (AbstractBinding binding : bindings) {
            binding.unbind();
        }

        // Clear the bindings out of the map
        Map<Object, Set<AbstractBinding>> anchors = mLevels.peek().anchors;
        anchors.remove(obj);
    }

    /**
     * Update a topping, i.e. color property, with a new color and therefore sending it out to
     * all your bindings
     *
     * @param toppingId the id of the topping you wish to update
     * @param color     the updated color to update to
     * @return self for chaining.
     */
    public Scoop update(int toppingId, @ColorInt int color) {
        Map<Object, Set<AbstractBinding>> anchors = mLevels.peek().anchors;

        Topping topping = getOrCreateTopping(toppingId);
        if (topping != null) {
            topping.updateColor(color);

            // Update bindings
            Collection<Set<AbstractBinding>> bindings = anchors.values();
            for (Set<AbstractBinding> bindingSet : bindings) {
                for (AbstractBinding binding : bindingSet) {
                    if (binding.getToppingId() == toppingId) {
                        binding.update(topping.color);
                    }
                }
            }
        }
        return this;
    }

    /**
     * Update a topping, i.e. color property, with a new color and therefore sending it out to
     * bindings of object obj
     *
     * @param obj object to update toppings for.
     * @param toppingId the id of the topping you wish to update
     * @param color     the updated color to update to
     * @return self for chaining.
     */
    public Scoop update(Object obj, int toppingId, @ColorInt int color) {
        Map<Object, Set<AbstractBinding>> anchors = mLevels.peek().anchors;

        // Update bindings
        Set<AbstractBinding> bindings = anchors.get(obj);
        if (bindings == null)
            return this;

        for (AbstractBinding binding : bindings) {
            if (binding.getToppingId() == toppingId) {
                binding.update(color);
            }
        }
        return this;
    }
}
