package com.taku.safe.views;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


public class DotTextView extends AppCompatTextView {


    public DotTextView(Context context, AttributeSet attrs) {
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
            long next = now + 1000;

            getHandler().postAtTime(mTicker, next);
        }
    };


    private void onTimeChanged() {
        String content = getText().toString();

        if (!content.endsWith(".")) {
            content = content + ".";
        } else if (content.endsWith("...")) {
            content = content.replace("...", "");
        } else if (content.endsWith("..")) {
            content = content.replace("..", "...");
        } else if (content.endsWith(".")) {
            content = content.replace(".", "..");
        }
        setText(content);
    }


}
