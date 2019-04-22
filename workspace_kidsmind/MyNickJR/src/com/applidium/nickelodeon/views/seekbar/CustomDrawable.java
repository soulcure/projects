package com.applidium.nickelodeon.views.seekbar;

import java.util.List;

import com.applidium.nickelodeon.views.seekbar.ComboSeekBar.Dot;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

/**
 * seekbar background with text on it.
 *
 * @author sazonov-adm
 */
public class CustomDrawable extends Drawable {
    private final ComboSeekBar mySlider;
    private final Drawable myBase;
    private final Paint textUnselected;
    private float mThumbRadius;
    /**
     * paints.
     */
    private final Paint unselectLinePaint;
    private List<Dot> mDots;
    private Paint selectLinePaint;
    private Paint circleLinePaint;
    private float mDotRadius;
    private Paint textSelected;
    private int mTextSize;
    private float mTextMargin;
    private int mTextHeight;
    private boolean mIsMultiline;

    private int mCurIndex = 0;

    public CustomDrawable(Drawable base, ComboSeekBar slider, float thumbRadius, List<Dot> dots, int color, int textSize, boolean isMultiline) {
        mIsMultiline = isMultiline;
        mySlider = slider;
        myBase = base;
        mDots = dots;
        mTextSize = textSize;
        textUnselected = new Paint(Paint.ANTI_ALIAS_FLAG);
        textUnselected.setColor(Color.YELLOW);
        textUnselected.setAlpha(255);

        textSelected = new Paint(Paint.ANTI_ALIAS_FLAG);
        textSelected.setTypeface(Typeface.DEFAULT_BOLD);
        textSelected.setColor(Color.YELLOW);
        textSelected.setAlpha(255);

        mThumbRadius = thumbRadius;

        unselectLinePaint = new Paint();
        unselectLinePaint.setColor(0xffeefc12);

        unselectLinePaint.setStrokeWidth(toPix(3));

        selectLinePaint = new Paint();
        selectLinePaint.setColor(color);
        selectLinePaint.setStrokeWidth(toPix(3));

        circleLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleLinePaint.setColor(color);


        Rect textBounds = new Rect();
        textSelected.setTextSize((int) (mTextSize * 2));
        textSelected.getTextBounds("M", 0, 1, textBounds);

        textUnselected.setTextSize(mTextSize);
        textSelected.setTextSize(mTextSize);

        mTextHeight = textBounds.height();
        mDotRadius = toPix(5);
        mTextMargin = toPix(16);
    }

    private float toPix(int size) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, mySlider.getContext().getResources().getDisplayMetrics());
    }

    @Override
    protected final void onBoundsChange(Rect bounds) {
        myBase.setBounds(bounds);
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
        // Log.d("--- draw:" + (getBounds().right - getBounds().left));
        int height = this.getIntrinsicHeight() / 2;
        if (mDots.size() == 0) {
            canvas.drawLine(0, height, getBounds().right, height, unselectLinePaint);
            return;
        }
        //for (Dot dot : mDots) {
        for (int i = 0; i < mDots.size(); i++) {
            //for(int i=mDots.size()-1; i>=0; i--){
            Dot dot = mDots.get(i);


            if (dot.isSelected) {
                mCurIndex = i;
                //circlePaint=unselectLinePaint;
                canvas.drawLine(mDots.get(0).mX, height, dot.mX, height, selectLinePaint);
                canvas.drawLine(dot.mX, height, mDots.get(mDots.size() - 1).mX, height, unselectLinePaint);
            }


            float radius = mDotRadius;
            if (i % 2 == 0) {
                radius *= 2;
                drawText(canvas, dot, dot.mX, height);
            }
            if (i > mCurIndex) {
                canvas.drawCircle(dot.mX, height, radius, unselectLinePaint);
            } else {
                canvas.drawCircle(dot.mX, height, radius, circleLinePaint);
            }


        }
    }


    /**
     * @param canvas canvas.
     * @param dot    current dot.
     * @param x      x cor.
     * @param y      y cor.
     */
    private void drawText(Canvas canvas, Dot dot, float x, float y) {
        final Rect textBounds = new Rect();
        textSelected.getTextBounds(dot.text, 0, dot.text.length(), textBounds);
       /* float xres;
        if (dot.id == (mDots.size() - 1)) {
            xres = getBounds().width() - textBounds.width() - 20;
        } else if (dot.id == 0) {
            xres = 20;
        } else {
            xres = x - (textBounds.width() / 2);
        }*/
        float xres= x - (textBounds.width() / 2);

        float yres;

        if (mIsMultiline) {

            if ((dot.id % 2) == 0) {
                yres = y - mTextMargin - mDotRadius;
            } else {
                yres = y + mTextHeight;
            }
        } else {
            yres = y + (mDotRadius * 4) + mTextMargin;
        }

        if (dot.isSelected) {
            canvas.drawText(dot.text, xres, yres, textSelected);
        } else {
            canvas.drawText(dot.text, xres, yres, textUnselected);
        }
    }

    @Override
    public final int getIntrinsicHeight() {
        if (mIsMultiline) {
            return (int) (selectLinePaint.getStrokeWidth() + mDotRadius + (mTextHeight) * 2 + mTextMargin);
        } else {
            return (int) (mThumbRadius + mTextMargin + mTextHeight + mDotRadius);
        }
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


    public void setCurIndex(int index) {
        mCurIndex = index;
    }


}