package com.ivmall.android.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.smit.android.ivmall.stb.R;

public class CartoonItemView extends RelativeLayout {
    // 用于实现得到焦点之后的白色方框。

    private static String TAG = CartoonItemView.class.getSimpleName();


    private Drawable mDrawableWhite;
    private Drawable mDrawableShadow;

    private final static int PADDING = 2;

    public CartoonItemView(Context context) {
        super(context);
        init(context);
    }

    public CartoonItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CartoonItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.setClipChildren(true);
        mDrawableWhite = getResources().getDrawable(R.drawable.item_highlight);
        mDrawableShadow = getResources().getDrawable(R.drawable.item_shadow);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.dispatchDraw(canvas);

        if (isFocused()) {
            int offset = 0;
            int width = getWidth() - offset;
            int height = getHeight() - offset;

            Rect padding = new Rect();

            mDrawableShadow.getPadding(padding);
            mDrawableShadow.setBounds(-padding.left, -padding.top, width + padding.right, height + padding.bottom);
            mDrawableShadow.draw(canvas);

            mDrawableWhite.getPadding(padding);

            int left = -padding.left - PADDING + offset;
            int top = -padding.top - PADDING + offset;
            int right = width + padding.right + PADDING;
            int bottom = height + padding.bottom + PADDING;

            mDrawableWhite.setBounds(left, top, right, bottom);
            mDrawableWhite.draw(canvas);

        }
        requestLayout();
    }


}
