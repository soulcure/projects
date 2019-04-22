package com.applidium.nickelodeon.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.applidium.nickelodeon.R;
import com.applidium.nickelodeon.uitls.ScreenUtils;

public class RecordItemView extends RelativeLayout {

    private static String TAG = RecordItemView.class.getSimpleName();


    private Drawable mDrawableWhite;
    private final static int PADDING = 2;

    public RecordItemView(Context context) {
        super(context);

        init(context);
    }

    public RecordItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public RecordItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {
        this.setClipChildren(true);
        mDrawableWhite = getResources().getDrawable(R.drawable.item_highlight);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (isFocused()) {
            int offsetLeft = getResources().getDimensionPixelOffset(R.dimen.record_item_padding);
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
