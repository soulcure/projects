package com.ivmall.android.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.smit.android.ivmall.stb.R;


public class FoucsRelativeLayout extends RelativeLayout {

    private static String TAG = FoucsRelativeLayout.class.getSimpleName();


    private Drawable mDrawableWhite;
    private final static int PADDING = 0;

    public FoucsRelativeLayout(Context context) {
        super(context);

        init(context);
    }

    public FoucsRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public FoucsRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
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
            //this.bringToFront();  //将该childView放在其Parent的Array数组的最后
        }

    }


}
