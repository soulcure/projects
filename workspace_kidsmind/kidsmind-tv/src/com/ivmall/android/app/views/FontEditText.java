package com.ivmall.android.app.views;

import com.smit.android.ivmall.stb.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 程序中统一使用的输入框  自动获取焦点  有橘色边框
 *
 * @author Administrator
 */
public class FontEditText extends EditText {
    private static final String TAG = FontEditText.class.getSimpleName();

    public FontEditText(Context context) {
        super(context);
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.FontEditText);
        String customFont = a.getString(R.styleable.FontEditText_customEditFont);
        setCustomFont(ctx, customFont);
        a.recycle();            //回调，否则这次的设定会影响下次的使用。
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            //调用字库  低于1M,超过1M的显示默认字体   设置字体的样式
            tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + asset);
        } catch (Exception e) {
            return false;
        }

        setTypeface(tf);
        return true;
    }
}
