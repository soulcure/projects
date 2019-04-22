package com.ivmall.android.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.ivmall.android.app.R;

public class FoucsRadioButton extends RadioButton {

    private static String TAG = FoucsRadioButton.class.getSimpleName();


    //private final static float PADDING = 0.5f;

    public FoucsRadioButton(Context context) {
        super(context);
    }

    public FoucsRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FoucsRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isFocused()) {

            Rect padding = new Rect();
            Drawable drawableWhite = getResources().getDrawable(R.drawable.red_highlight);
            drawableWhite.getPadding(padding);

            int left = 0;
            int top = 0;
            int right = getWidth();
            int bottom = getHeight();
            drawableWhite.setBounds(left, top, right, bottom);
            drawableWhite.draw(canvas);

        }


    }


}
