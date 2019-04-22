package com.ivmall.android.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.ivmall.android.app.R;

/**
 * Created by koen on 2016/3/17.
 */
public class TopRadioButton extends RadioButton {

    private Drawable drawableTop;
    private int mTopWith, mTopHeight;

    public TopRadioButton(Context context) {
        super(context);
        initView(context, null);
    }

    public TopRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TopRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            float scale = context.getResources()
                    .getDisplayMetrics().density;
            TypedArray array = context.obtainStyledAttributes(
                    attrs, R.styleable.TopRadioButton);
            int n = array.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = array.getIndex(i);
                switch (attr) {
                    case R.styleable.TopRadioButton_top_drawable:
                        drawableTop = array.getDrawable(attr);
                        break;

                    case R.styleable.TopRadioButton_top_height:
                        mTopHeight = (int) (array.getDimension(attr, 20) * scale + 0.5f);
                        break;

                    case R.styleable.TopRadioButton_top_width:
                        mTopWith = (int) (array.getDimension(attr, 20) * scale + 0.5f);
                        break;
                }
            }

            array.recycle();
            setCompoundDrawables(null, drawableTop, null, null);
        }
    }


    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {

        if (top != null) {
            top.setBounds(0, 0, mTopWith <= 0 ? top.getIntrinsicWidth()
                    : mTopWith, mTopHeight <= 0 ? top.getIntrinsicHeight() : mTopHeight);
        }
        super.setCompoundDrawables(left, top, right, bottom);
    }
}
