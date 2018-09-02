package com.ftinc.scoop;

import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewParent;
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

    private View root;

    public StyleLevel() {
    }

    public StyleLevel(StyleLevel other) {
        for(int idx = 0; idx < other.toppings.size(); idx++) {
            Integer id = other.toppings.keyAt(idx);
            Topping clone = new Topping(other.toppings.valueAt(idx));
            toppings.put(id, clone);
        }
    }

    /**
     * Restrict scope of this style level. When views try to bind and they are not descendants of the root view,
     * try to bind them to the level above.
     * @param root view
     */
    public void restrictToDescendantsOf(View root) {
        this.root = root;
    }


    public boolean canBind(View view) {
        if (root == null || view == root)
            return true;

        // traverse parents
        View parent = view;
        while (parent.getParent() != null && parent.getParent() instanceof View) {
            if (parent.getParent() == root)
                return true;

            parent = (View) parent.getParent();
        }
        return false;
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
