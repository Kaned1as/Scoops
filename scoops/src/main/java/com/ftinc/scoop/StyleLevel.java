package com.ftinc.scoop;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Interpolator;
import com.ftinc.scoop.adapters.ColorAdapter;
import com.ftinc.scoop.binding.AbstractBinding;
import com.ftinc.scoop.binding.AnimatedBinding;
import com.ftinc.scoop.binding.StatusBarBinding;
import com.ftinc.scoop.binding.ViewBinding;
import com.ftinc.scoop.util.BindingUtils;

import java.util.*;

/**
 * @author Kanedias
 * <p>
 * Created on 04.08.18
 */
public class StyleLevel {

    /**
     * Mapping topping id -> topping
     */
    final SparseArray<Topping> toppings = new SparseArray<>();

    /**
     * Mapping object -> bound set
     */
    final Set<AbstractBinding> anchors = new HashSet<>();

    public StyleLevel() {
    }

    public StyleLevel(StyleLevel other) {
        for(int idx = 0; idx < other.toppings.size(); idx++) {
            Integer id = other.toppings.keyAt(idx);
            Topping clone = new Topping(other.toppings.valueAt(idx));
            toppings.put(id, clone);
        }
    }

    @Nullable
    public StatusBarBinding getStatusBarBinding() {
        for (AbstractBinding binding: anchors) {
            if (binding instanceof StatusBarBinding) {
                return (StatusBarBinding) binding;
            }
        }
        return null;
    }

    /**
     * Unbinds all collected bindings, usually done before deleting this level
     */
    public void unbind() {
        for (AbstractBinding binding: anchors) {
            binding.unbind();
        }
        anchors.clear();
    }

    /**
     * Rebinds all toppings to respective objects, restoring colors
     */
    public void rebind() {
        for (AbstractBinding binding: anchors) {
            Topping topping = toppings.get(binding.getToppingId());
            binding.update(topping.getColor());
        }
    }

    /**
     * Bind a view to a topping on a given object
     *
     * @param toppingId the id of the topping to bind to
     * @param view      the view to bind
     * @return self for chaining
     */
    public StyleLevel bind(int toppingId, View view) {
        return bind(toppingId, view, null);
    }

    /**
     * Bind a view to a topping on a given object with a specified color adapter
     *
     * @param toppingId    the id of the topping
     * @param view         the view to bind
     * @param colorAdapter the color adapter to bind with
     * @return self for chaining
     */
    public StyleLevel bind(int toppingId, View view, @Nullable ColorAdapter colorAdapter) {
        return bind(toppingId, view, colorAdapter, null);
    }

    /**
     * Bind a view to a topping on a given object with a specified color adapter and change animation
     * interpolator
     *
     * @param toppingId    the id of the topping
     * @param view         the view to bind
     * @param colorAdapter the color adapter to bind with
     * @param interpolator the interpolator to use when switching colors
     * @return self for chaining
     */
    public StyleLevel bind(int toppingId, View view, @Nullable ColorAdapter colorAdapter, @Nullable Interpolator interpolator) {
        // Get a default color adapter if not supplied
        if (colorAdapter == null) {
            colorAdapter = BindingUtils.getColorAdapter(view.getClass());
        }

        // Generate Binding
        AbstractBinding binding = new ViewBinding(toppingId, view, colorAdapter, interpolator);

        // Bind
        return bind(toppingId, binding);
    }

    /**
     * Bind the status bar of an activity to a topping so that it's color is updated when the
     * user/developer updates the color for that topping id.
     *
     * This does nothing on APIs < 21.
     *
     * @param activity  the activity whoes status bar to bind to
     * @param toppingId the id of the topping to bind with
     * @return self for chaining
     */
    public StyleLevel bindStatusBar(Activity activity, int toppingId) {
        return bindStatusBar(activity, toppingId, null);
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
    public StyleLevel bindStatusBar(Activity activity, int toppingId, @Nullable Interpolator interpolator) {
        AbstractBinding binding = new StatusBarBinding(toppingId, activity, interpolator);
        return bind(toppingId, binding);
    }

    /**
     * Provide a custom binding to a certain topping id on a given object. This allows you to
     * customize the changes between color on certain properties, i.e. Toppings, to define it
     * to your use case
     *
     * @param toppingId the topping id to bind to
     * @param binding   the binding that defines how your custom properties are updated
     * @return self for chaining
     */
    public StyleLevel bind(int toppingId, AbstractBinding binding) {

        // Find or Create Topping
        Topping topping = getOrCreateTopping(toppingId);

        // If topping has a color set, auto-apply to binding
        autoUpdateBinding(binding, topping);

        // Store binding
        this.anchors.add(binding);

        return this;
    }

    /**
     * Find the {@link Topping} object for it's given Id or create one if not found
     *
     * @param toppingId the id of the topping to get
     * @return the topping associated with the id
     */
    private Topping getOrCreateTopping(int toppingId) {
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
     * Update a topping, i.e. color property, with a new color and therefore sending it out to
     * all your bindings
     *
     * @param toppingId the id of the topping you wish to update
     * @param color     the updated color to update to
     * @return self for chaining.
     */
    public StyleLevel update(int toppingId, @ColorInt int color) {
        Topping topping = getOrCreateTopping(toppingId);
        topping.updateColor(color);

        // Update bindings
        for (AbstractBinding binding : anchors) {
            if (binding.getToppingId() == toppingId) {
                binding.update(topping.color);
            }
        }
        return this;
    }
}
