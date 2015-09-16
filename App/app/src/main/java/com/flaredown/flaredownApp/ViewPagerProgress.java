package com.flaredown.flaredownApp;

import android.content.Context;
import android.flaredown.com.flaredown.R;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thunter on 14/09/15.
 */
public class ViewPagerProgress extends FlowLayout implements ViewPager.OnPageChangeListener {

    Context mContext;
    int numberOfPages = 0;
    int currentPage = 0;
    List<ImageButton> dots;

    public ViewPagerProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        dots = new ArrayList<ImageButton>();
        this.setGravity(Gravity.CENTER_HORIZONTAL);


        this.setOrientation(HORIZONTAL);
    }

    public ViewPagerProgress(Context context) {
        this(context, null);
    }

    public void setNumberOfPages(int number) {
        numberOfPages = number;
        updateDots();
    }

    public void updateDots() {
        this.removeAllViews();
        dots.clear();
        for(int i = 0; i < numberOfPages; i++) {
            ImageButton ib = createDot();
            if(i == currentPage) ib.setSelected(true);
            dots.add(ib);
            this.addView(ib);
            int margins = mContext.getResources().getDimensionPixelSize(R.dimen.viewpageprogress_size);
            ((MarginLayoutParams) ib.getLayoutParams()).bottomMargin = ((MarginLayoutParams) ib.getLayoutParams()).rightMargin = margins;
            ib.getLayoutParams().height = ib.getLayoutParams().width = mContext.getResources().getDimensionPixelSize(R.dimen.viewpageprogress_size);
        }
    }

    public ImageButton createDot() {
        ImageButton ib = new ImageButton(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.viewpageprogress_size), mContext.getResources().getDimensionPixelSize(R.dimen.viewpageprogress_size));
        Styling.setBackground(mContext, ib, R.drawable.viewpagerprogress_background);
        //ib.setBackground(ContextCompat.getDrawable(mContext, R.drawable.viewpagerprogress_background));
        return ib;
    }

    /**
     * OnPageChangeListener functions
     */

    @Override
    public void onPageSelected(int position) {
        if(position + 1 > dots.size())
            setNumberOfPages(position + 1);
        for(int i = 0; i < dots.size(); i++) {
            dots.get(i).setSelected(false );
        }
        try {
            currentPage = position;
            dots.get(position).setSelected(true);
        } catch (Exception e) {

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
