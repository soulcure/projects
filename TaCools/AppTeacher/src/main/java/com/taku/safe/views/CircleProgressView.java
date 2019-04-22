package com.taku.safe.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.taku.safe.R;

/**
 * Created by colin on 2017/7/5.
 */

public class CircleProgressView extends DonutProgress {

    public CircleProgressView(Context context) {
        super(context);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        // do what you want
        return true;
    }
}