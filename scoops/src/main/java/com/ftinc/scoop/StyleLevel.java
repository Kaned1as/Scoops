package com.ftinc.scoop;

import android.support.annotation.Nullable;
import android.util.SparseArray;
import com.ftinc.scoop.binding.AbstractBinding;
import com.ftinc.scoop.binding.StatusBarBinding;

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
    final Map<Object, Set<AbstractBinding>> anchors = new HashMap<>();

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
        for (Set<AbstractBinding> bindings: anchors.values()) {
            for (AbstractBinding binding: bindings) {
                if (binding instanceof StatusBarBinding) {
                    return (StatusBarBinding) binding;
                }
            }
        }
        return null;
    }

    /**
     * Unbinds all collected bindings, usually done before deleting this level
     */
    public void unbind() {
        for (Set<AbstractBinding> bindings: anchors.values()) {
            for (AbstractBinding binding: bindings) {
                binding.unbind();
            }
        }
        anchors.clear();
    }

    /**
     * Rebinds all toppings to respective objects, restoring colors
     */
    public void rebind() {
        for (Set<AbstractBinding> bindings: anchors.values()) {
            for (AbstractBinding binding: bindings) {
                Topping topping = toppings.get(binding.getToppingId());
                binding.update(topping.getColor());
            }
        }
    }
}
