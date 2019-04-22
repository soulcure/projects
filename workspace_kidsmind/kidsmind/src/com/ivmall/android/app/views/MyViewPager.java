package com.ivmall.android.app.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by koen on 2016/3/14.
 */
public class MyViewPager extends ViewPager {

    private boolean noScroll = false;

    public void setNoScroll(boolean b) {
        this.noScroll = b;
    }


    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public MyViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (noScroll)
            return false;
        else
            return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(ev);
    }
}
