package com.applidium.nickelodeon.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class VideoViewCustom extends VideoView {

    private int mForceHeight = 0;
    private int mForceWidth = 0;

    public VideoViewCustom(Context context) {
        super(context);
    }

    public VideoViewCustom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoViewCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDimensions(int w, int h) {
        mForceHeight = h;
        mForceWidth = w;

        getHolder().setFixedSize(w, h);

        //requestLayout();

        forceLayout();
        //invalidate();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mForceWidth, widthMeasureSpec);
        int height = getDefaultSize(mForceHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
