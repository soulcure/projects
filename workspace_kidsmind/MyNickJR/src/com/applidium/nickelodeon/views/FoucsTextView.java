package com.applidium.nickelodeon.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.applidium.nickelodeon.R;

public class FoucsTextView extends TextView {

    private static String TAG = FoucsTextView.class.getSimpleName();


    private Drawable mDrawableWhite;
    private final static int PADDING = 0;

    public FoucsTextView(Context context) {
        super(context);

        init(context);
    }

    public FoucsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public FoucsTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {
        mDrawableWhite = getResources().getDrawable(R.drawable.item_highlight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (isFocused()) {

            int width = getWidth();
            int height = getHeight();

            Rect padding = new Rect();

            mDrawableWhite.getPadding(padding);

            int left = -padding.left - PADDING;
            int top = -padding.top - PADDING;
            int right = width + padding.right + PADDING;
            int bottom = height + padding.bottom + PADDING;
            mDrawableWhite.setBounds(left, top, right, bottom);
            mDrawableWhite.draw(canvas);
            this.bringToFront();
        }

        super.onDraw(canvas);

    }


}
