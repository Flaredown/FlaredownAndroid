package com.flaredown.flaredownApp.Helpers;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.widget.Adapter;

import org.apmem.tools.layouts.FlowLayout;

import java.util.HashMap;
import java.util.List;

/**
 * Currently the FlowLayout library does not support adapters yet, this is a helper class to allow
 * the adapter behaviour
 */
public class FlowLayoutHelper<T> {
    private Adapter<T> adapter;
    private FlowLayout flowLayout;
    private HashMap<T, View> items;

    public FlowLayoutHelper(FlowLayout flowLayout, Adapter<T> adapter) {
        this.flowLayout = flowLayout;
        this.adapter = adapter;
        this.items = new HashMap<>();
    }

    /**
     * Add multiple items.
     * @param items
     */
    public void addItems(List<T> items) {
        for (T item : items) {
            addItem(item);
        }
    }

    /**
     * Add an item to the flow layout.
     * @param item the item to add.
     */
    public void addItem(T item) {
        if(!items.containsKey(item)) {
            View v = adapter.viewCreation(item);
            items.put(item, v);
            flowLayout.addView(v);
        }
    }

    /**
     * Remove an item from the flow layout.
     * @param item the item to remove.
     */
    public void removeItem(T item) {
        flowLayout.removeView(items.get(item));
        items.remove(item);
    }

    /**
     * Remove all objects from the array.
     */
    public void clear() {
        flowLayout.removeAllViews();
        items.clear();
    }


    public interface Adapter <T> {
        View viewCreation(T item);
    }
}
