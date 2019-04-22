package com.applidium.nickelodeon.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.applidium.nickelodeon.uitls.AppUtils;
import com.applidium.nickelodeon.uitls.ScreenUtils;

/**
 * Created by colin on 2015/6/9.
 */
public class TextProgressBar extends ProgressBar {

    private String mTitle;
    private Paint mPaint;

    public TextProgressBar(Context context) {
        super(context);
        initText(context);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initText(context);
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initText(context);
    }

    @Override
    public void setProgress(int progress) {
        setText(progress);
        super.setProgress(progress);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        this.mPaint.getTextBounds(mTitle, 0, mTitle.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();// 让现实的字体处于中心位置;;
        int y = (getHeight() / 2) - rect.centerY();// 让显示的字体处于中心位置;;
        canvas.drawText(mTitle, x, y, this.mPaint);
    }

    // 初始化，画笔
    private void initText(Context context) {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);// 设置抗锯齿
        mPaint.setTextSize(ScreenUtils.dpToPx(context, 18));
        this.mPaint.setColor(Color.BLUE);
    }

    // 设置文字内容
    private void setText(int progress) {
        int i = (int) ((progress * 1.0f / this.getMax()) * 100);
        mTitle = String.valueOf(i) + "%";
    }
}