package com.taku.safe.views;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.taku.safe.R;
import com.taku.safe.utils.TimeUtils;


public class CountTimeView extends AppCompatTextView {

    private Counter mCounter;
    private int mType;

    public CountTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCounter != null) {
            mCounter.cancel();
            mCounter = null;
        }
    }

    public void setCountTime(long time) {
        if (mCounter != null) {
            mCounter.cancel();
            mCounter = null;
        }
        mCounter = new Counter(time, 1000);
        mCounter.start();
    }

    public void setCountTime(long time, int type) {
        mType = type;
        setCountTime(time);
    }

    private class Counter extends CountDownTimer {
        private Counter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            setText("");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            String format;
            if (mType == 0) {
                format = getResources().getString(R.string.count_end);
            } else {
                format = getResources().getString(R.string.count_start);
            }

            setText(String.format(format, TimeUtils.calculateTime(millisUntilFinished)));
        }
    }


}
