package com.ivmall.android.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.smit.android.ivmall.stb.R;

public class FoucsImageButton extends ImageButton {

    private static String TAG = FoucsImageButton.class.getSimpleName();

    private int drawable = R.drawable.item_highlight;

    public FoucsImageButton(Context context) {
        super(context);

    }

    public FoucsImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FoucsLinearBorder);
        drawable = typedArray.getResourceId(R.styleable.FoucsLinearBorder_drawable_border, R.drawable.item_highlight);
        typedArray.recycle();
    }

    public FoucsImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isFocused()) {
            Drawable drawableWhite = getResources().getDrawable(drawable);
            drawableWhite.setBounds(0, 0, getWidth(), getHeight());
            drawableWhite.draw(canvas);
        }
    }
}
