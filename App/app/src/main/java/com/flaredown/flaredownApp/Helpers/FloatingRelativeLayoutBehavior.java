package com.flaredown.flaredownApp.Helpers;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by thunter on 23/04/16.
 */
public class FloatingRelativeLayoutBehavior extends CoordinatorLayout.Behavior<RelativeLayout> {
    public FloatingRelativeLayoutBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, RelativeLayout child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, RelativeLayout child, View dependency) {
        int translationY = (int) Math.min(0, dependency.getTranslationY() - dependency.getHeight()) * -1;
        child.setPadding(child.getPaddingLeft(), child.getPaddingTop(), child.getPaddingRight(), translationY);
        return true;
    }
}
