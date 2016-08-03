package com.flaredown.flaredownApp.Helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Enables the list view to "wrap_content", however it no longer scrolls without a scroll view.
 */
public class HeightContentWrapListView extends ListView {
    public HeightContentWrapListView(Context context) {
        super(context);
    }

    public HeightContentWrapListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeightContentWrapListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int updatedHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, updatedHeightMeasureSpec);
    }
}
