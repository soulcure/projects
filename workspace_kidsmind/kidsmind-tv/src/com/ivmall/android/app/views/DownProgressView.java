package com.ivmall.android.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ivmall.android.app.uitls.ScreenUtils;

/**
 * Created by Markry on 2015/11/4.
 */
public class DownProgressView extends ImageView {
    /**
     * view 的大小
     */
    private int size = 0;
    /**
     * 背景角度
     */
    private float bgCornerRadius = 20.0f;
    /**
     * 环形间距
     */
    private float rgBorderPadding = 0.2f;

    /**
     * 背景圆角矩形
     */
    private RectF mBgRect;
    /**
     * pie的矩形
     */
    private RectF mPieRect;
    /**
     *
     * */
    private float mPieRingDistance = 0.08f;
    /**
     * 背景画笔
     */
    private Paint mBgPaint;
    /**
     * 背景颜色
     */
    private String bgColor = "#000000";
    /**
     * 背景透明度
     */
    private float bgAlpha = 0.5f;
    /**
     * 环画笔
     */
    private Paint mRingPaint;
    /**
     * 环的颜色
     */
    private int mRingColor = Color.WHITE;
    /**
     * 环的透明值
     */
    private float mRingAlpha = 0.9f;
    /**
     * 环的宽度
     */
    private int mRingBorder = 3;
    /**
     * 圆画笔
     */
    private Paint mPiePaint;
    /**
     * 圆的颜色
     */
    private int mPieColor = Color.WHITE;
    /**
     * 圆的透明度
     */
    private float mPieAlpha = 0.9f;

    private int mAngle = 0;
    /**
     * 是否可以开始绘画
     */
    private boolean isDraw = false;
    private Context context;

    public DownProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public DownProgressView(Context context) {
        super(context);
        this.context = context;
    }

    public DownProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        post(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    private void init() {
        bgCornerRadius = ScreenUtils.getWidthPixels(context) / 102;
        size = getMeasuredWidth();
        mBgRect = new RectF(0, 0, size, size);
        float piePaddingValue = (rgBorderPadding + mPieRingDistance) * size;
        mPieRect = new RectF(0 + piePaddingValue / 2, 0 + piePaddingValue / 2,
                size - piePaddingValue / 2, size - piePaddingValue / 2);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(Color.parseColor(bgColor));
        mBgPaint.setAlpha((int) (bgAlpha * 255));

        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStrokeWidth(mRingBorder);
        mRingPaint.setColor(mRingColor);
        mRingPaint.setAlpha((int) (mRingAlpha * 255));
        mRingPaint.setStyle(Paint.Style.STROKE);

        mPiePaint = new Paint();
        mPiePaint.setAntiAlias(true);
        mPiePaint.setColor(mPieColor);
        mPiePaint.setAlpha((int) (mPieAlpha * 255));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isDraw) {
            canvas.drawRoundRect(mBgRect, bgCornerRadius, bgCornerRadius, mBgPaint);
            canvas.drawCircle(size / 2, size / 2, (size * (1 - rgBorderPadding)) / 2, mRingPaint);
            canvas.drawArc(mPieRect, -90, mAngle, true, mPiePaint);
        }
    }

    /**
     * 更新进度
     */
    public void updateProgress(int mAngle) {
        this.mAngle = mAngle;
        isDraw = true;
        invalidate();
    }

    /**
     * 清除进度
     */
    public void clearProgress() {
        isDraw = false;
        invalidate();
    }
}
