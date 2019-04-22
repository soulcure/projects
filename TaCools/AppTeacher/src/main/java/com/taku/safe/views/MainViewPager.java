package com.taku.safe.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class MainViewPager extends ViewPager {

    public MainViewPager(Context context) {
        this(context, null);
    }

    public MainViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, false);
    }
}
