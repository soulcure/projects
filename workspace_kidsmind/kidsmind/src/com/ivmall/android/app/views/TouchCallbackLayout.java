package com.ivmall.android.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchCallbackLayout extends FrameLayout {

    private TouchEventListener mTouchEventListener;


    public TouchCallbackLayout(Context context) {
        super(context);
    }

    public TouchCallbackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchCallbackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTouchEventListener(TouchEventListener touchEventListener) {
        mTouchEventListener = touchEventListener;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (mTouchEventListener != null) {
            return mTouchEventListener.onLayoutInterceptTouchEvent(ev);
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mTouchEventListener != null) {
            return mTouchEventListener.onLayoutTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }


    public interface TouchEventListener {
        boolean onLayoutInterceptTouchEvent(MotionEvent ev);

        boolean onLayoutTouchEvent(MotionEvent ev);
    }
}
