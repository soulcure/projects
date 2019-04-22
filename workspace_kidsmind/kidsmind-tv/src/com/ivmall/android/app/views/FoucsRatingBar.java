package com.ivmall.android.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RatingBar;

import com.ivmall.android.app.uitls.ScreenUtils;
import com.smit.android.ivmall.stb.R;

public class FoucsRatingBar extends RatingBar {

    private static String TAG = FoucsRatingBar.class.getSimpleName();


    private final static float PADDING = 0.5f;

    public FoucsRatingBar(Context context) {
        super(context);

    }

    public FoucsRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public FoucsRatingBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isFocused()) {
            int offset = (int) ScreenUtils.dpToPx(getContext(), PADDING);

            Rect padding = new Rect();
            Drawable drawableWhite = getResources().getDrawable(R.drawable.item_highlight);
            drawableWhite.getPadding(padding);

            int left = -padding.left + offset;
            int top = -padding.top + offset;
            int right = getWidth() + padding.right - offset;
            int bottom = getHeight() + padding.bottom - offset;
            drawableWhite.setBounds(left, top, right, bottom);
            drawableWhite.draw(canvas);


        }


    }


}
