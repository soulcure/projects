package com.ivmall.android.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.smit.android.ivmall.stb.R;

public class FoucsActionView extends RelativeLayout {

    private static String TAG = FoucsActionView.class.getSimpleName();


    private Drawable mDrawableWhite;
    private final static int PADDING = 2;

    public FoucsActionView(Context context) {
        super(context);

        init(context);
    }

    public FoucsActionView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public FoucsActionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {
        mDrawableWhite = getResources().getDrawable(R.drawable.item_highlight);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.dispatchDraw(canvas);

        if (isFocused()) {
            int offset = (int) ScreenUtils.dpToPx(getContext(), 5);
            int width = getWidth() - offset;
            int height = getHeight() - offset;


            Rect padding = new Rect();

            mDrawableWhite.getPadding(padding);

            int left = -padding.left - PADDING + offset;
            int top = -padding.top - PADDING + offset;
            int right = width + padding.right + PADDING;
            int bottom = height + padding.bottom + PADDING;
            mDrawableWhite.setBounds(left, top, right, bottom);
            mDrawableWhite.draw(canvas);
        }


    }


}
