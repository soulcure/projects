package com.ivmall.android.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.ivmall.android.app.R;

/**
 * Created by koen on 2016/5/19.
 */
public class DeteleEditText extends EditText{

    private Drawable imgDel;
    private Context mContext;
    private Drawable leftDrawable;
    private String leftText;


    public DeteleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
        //setLeft(context, attrs);
    }

    private void init() {
        imgDel = mContext.getResources().getDrawable(R.drawable.ic_close);
        imgDel.setBounds(0, 0, imgDel.getIntrinsicWidth(), imgDel.getIntrinsicHeight());

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });
        setDrawable();
    }

    private void setDrawable() {
        if (length() > 1) {
            setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], imgDel, getCompoundDrawables()[3]);

        } else {
            setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], null, getCompoundDrawables()[3]);
        }
    }


    /*private void setLeft(Context ctx, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = ctx.obtainStyledAttributes(attrs, R.styleable.MyEditText);
            int n = array.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = array.getIndex(i);
                switch (attr) {
                    case R.styleable.MyEditText_left_drawable:
                        leftDrawable = array.getDrawable(attr);
                        break;

                    case R.styleable.MyEditText_left_text:
                        leftText = array.getText(attr).toString();
                        break;
                }
            }
            array.recycle();
        }
    }*/

   /* @Override
    protected void onDraw(Canvas canvas) {
        if (leftText != null) {
            Paint paint = new Paint();
            paint.setTextSize(20);
            paint.setColor(Color.BLACK);
            canvas.drawText(leftText,10, getHeight() / 2 + 5, paint);
        }
        super.onDraw(canvas);
    }*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (imgDel != null && event.getAction() == MotionEvent.ACTION_UP) {

            boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                    && (event.getX() < ((getWidth() - getPaddingRight())));

            if (touchable) {
                this.setText("");
            }

        }
        return super.onTouchEvent(event);
    }
}
