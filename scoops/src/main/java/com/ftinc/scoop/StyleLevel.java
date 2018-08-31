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
    final SparseArray<Topping> toppings;

    /**
     * Mapping object -> bound set
     */
    final Map<Object, Set<AbstractBinding>> anchors = new HashMap<>();

    public StyleLevel() {
        this.toppings = new SparseArray<>();
    }

    public StyleLevel(StyleLevel other) {
        toppings = other.toppings.clone();
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

    public void unbind() {
        for (Set<AbstractBinding> bindings: anchors.values()) {
            for (AbstractBinding binding: bindings) {
                binding.unbind();
            }
        }
        anchors.clear();
    }
}
