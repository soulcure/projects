package com.applidium.nickelodeon.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.applidium.nickelodeon.R;

public class CartoonItemView extends RelativeLayout {
    // 用于实现得到焦点之后的白色方框。

    private static String TAG = CartoonItemView.class.getSimpleName();


    private Drawable mDrawableWhite;
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
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.dispatchDraw(canvas);

        if (isFocused()) {
            int offset = getResources().getDimensionPixelOffset(R.dimen.cartoon_item_padding);
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
