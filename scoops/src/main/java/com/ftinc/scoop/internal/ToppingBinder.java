package com.ftinc.scoop.internal;

import com.ftinc.scoop.binding.AbstractBinding;

import java.util.List;

/**
 * Created by r0adkll on 6/25/16.
 */

public interface ToppingBinder<T> {
    List<AbstractBinding> bind(T target);
}
