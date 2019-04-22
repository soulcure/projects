package com.ivmall.android.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.R;

public class SerieItemView extends RelativeLayout {

    private static String TAG = SerieItemView.class.getSimpleName();


    private Drawable mDrawableWhite;
    private final static int PADDING = 3;

    public SerieItemView(Context context) {
        super(context);

        init(context);
    }

    public SerieItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public SerieItemView(Context context, AttributeSet attrs, int defStyle) {
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
            int offsetLeft = (int) ScreenUtils.dpToPx(getContext(), 20);
            int offsetTop = (int) ScreenUtils.dpToPx(getContext(), 10);
            int width = getWidth() - offsetLeft;
            int height = getHeight() - offsetTop;


            Rect padding = new Rect();

            mDrawableWhite.getPadding(padding);

            int left = -padding.left - PADDING + offsetLeft;
            int top = -padding.top - PADDING + offsetTop;
            int right = width + padding.right + PADDING;
            int bottom = height + padding.bottom + PADDING;
            mDrawableWhite.setBounds(left, top, right, bottom);
            mDrawableWhite.draw(canvas);
        }


    }


}
