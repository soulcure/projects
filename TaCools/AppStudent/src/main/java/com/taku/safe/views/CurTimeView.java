package com.taku.safe.views;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.taku.safe.utils.TimeUtils;


public class CurTimeView extends AppCompatTextView {


    public CurTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mTicker.run();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getHandler().removeCallbacks(mTicker);
    }


    private final Runnable mTicker = new Runnable() {
        public void run() {
            onTimeChanged();

            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);

            getHandler().postAtTime(mTicker, next);
        }
    };


    private void onTimeChanged() {
        setText(TimeUtils.getTime(System.currentTimeMillis(), TimeUtils.DATE_FORMAT_TEXT));
    }


}
