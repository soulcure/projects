package com.ivmall.android.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.smit.android.ivmall.stb.R;

public class FoucsLinearLayout extends LinearLayout {

    private static String TAG = FoucsLinearLayout.class.getSimpleName();

    private int drawable = R.drawable.item_highlight;

    public FoucsLinearLayout(Context context) {
        super(context);

    }

    public FoucsLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FoucsLinearBorder);
        drawable = typedArray.getResourceId(R.styleable.FoucsLinearBorder_drawable_border, R.drawable.item_highlight);
        typedArray.recycle();
    }

    public FoucsLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isFocused()) {
            Drawable drawableWhite = getResources().getDrawable(drawable);
            drawableWhite.setBounds(0, 0, getWidth(), getHeight());
            drawableWhite.draw(canvas);
        }
    }
}
