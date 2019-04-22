package com.ivmall.android.app.views.seekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.ivmall.android.app.uitls.StringUtils;
import com.smit.android.ivmall.stb.R;

/**
 * seekbar background with text on it.
 *
 * @author sazonov-adm
 */
public class CustomThumbDrawable extends Drawable {
    /**
     * paints.
     */
    private Paint circlePaint;
    private Paint textPaint;
    private String mText;

    private Context mContext;
    private float mRadius;

    public CustomThumbDrawable(Context context, int color) {
        mContext = context;
        mRadius = toPix(15);
        setColor(color);
    }

    public void setColor(int color) {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor((0xA0 << 24) + (color & 0x00FFFFFF));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(mContext.getResources().getColor(R.color.white));
        invalidateSelf();
    }

    public float getRadius() {
        return mRadius;
    }


    public void setThumbText(String text) {
        mText = text;
    }

    @Override
    protected final boolean onStateChange(int[] state) {
        invalidateSelf();
        return false;
    }

    @Override
    public final boolean isStateful() {
        return true;
    }

    @Override
    public final void draw(Canvas canvas) {
        int height = this.getBounds().centerY();
        int width = this.getBounds().centerX();

        canvas.drawCircle(width + mRadius, height, mRadius, circlePaint);

        //绘制字符
        if (!StringUtils.isEmpty(mText)) {

            //设置字体大小
            int size = mContext.getResources().getDimensionPixelSize(R.dimen.comboseekbar_thumb_text_size);
            textPaint.setTextSize(size);

            Rect textBounds = new Rect();
            textPaint.getTextBounds(mText, 0, mText.length(), textBounds);

            float xres = width + mRadius - (textBounds.width() / 2);
            float yres = height + (textBounds.height() / 2);
            canvas.drawText(mText, xres, yres, textPaint);
        }
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) (mRadius * 2);
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) (mRadius * 2);
    }

    @Override
    public final int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    private float toPix(int size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size,
                mContext.getResources().getDisplayMetrics());
    }
}