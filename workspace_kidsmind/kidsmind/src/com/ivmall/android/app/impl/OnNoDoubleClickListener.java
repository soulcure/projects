package com.ivmall.android.app.impl;

import android.view.View;

import java.util.Calendar;

/**
 * Created by koen on 2015/11/11.
 */
public abstract class OnNoDoubleClickListener implements View.OnClickListener{

    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    public abstract void onNoDoubleClick(View v);
    @Override
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(view);
        }
    }
}
