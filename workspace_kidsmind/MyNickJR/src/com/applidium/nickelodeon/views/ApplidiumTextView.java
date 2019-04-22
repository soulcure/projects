package com.applidium.nickelodeon.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.applidium.nickelodeon.R;

/*
 * Copy this file and the attrs.xml file to your project In your xml layouts,
 * declare the xmlns:applidium="http://schemas.android.com/apk/res-auto"
 * namespace You can now use the applidium:minShrinkSize dimension and the
 * applidium:customFont string addtributes to set the minimum text size and the
 * font of your ApplidiumTextView
 */
public class ApplidiumTextView extends TextView {

    private final static String LOG_TAG = ApplidiumTextView.class.getSimpleName();

    private final static float NO_SHRINKING_VALUE = Float.NaN;
    private final static int MIN_TEXT_SIZE_SP = 12;
    private float mMinTextSize;
    private int mMinLines, mMaxLines;

    private TextPaint mPaint;

    public ApplidiumTextView(Context context) {
        super(context);
        init(context, null);
    }

    public ApplidiumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ApplidiumTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!Float.isNaN(mMinTextSize)) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
            int heightSize = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
            autoShrink(getText().toString(), getTextSize(), widthSize, heightSize);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init(Context ctx, AttributeSet attrs) {
        mPaint = new TextPaint();
        mPaint.set(getPaint());
        if (attrs != null) {
            TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.ApplidiumTextView);
            setMinTextSize(attrArray.getDimension(R.styleable.ApplidiumTextView_minShrinkSize, NO_SHRINKING_VALUE));
            setCustomFont(ctx, attrArray.getString(R.styleable.ApplidiumTextView_customFont));
            setMinLines(attrArray.getInt(R.styleable.ApplidiumTextView_android_minLines, 0));
            setMaxLines(attrArray.getInt(R.styleable.ApplidiumTextView_android_maxLines, 0));
            attrArray.recycle();
        }
    }

    private void autoShrink(String text, float originalSize, int desiredWidth, int desiredHeight) {

        if (originalSize <= mMinTextSize) {
            return;
        }

        StaticLayout layout = getLayout(originalSize, text, desiredWidth);
        int desiredArea = desiredWidth * desiredHeight;
        int layoutArea = layout.getWidth() * layout.getHeight();

        float size = originalSize;
        if (layout.getWidth() > desiredWidth || layout.getHeight() > desiredHeight) {
//            if (layout.getWidth() > desiredWidth) {
//            } else {
//            }
            size = (float) Math.max(mMinTextSize, (originalSize * desiredArea) / layoutArea);
            layout = getLayout(size, text, desiredWidth);
        }

        // TODO: Handle maxLines and minLines

        if (size != originalSize) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            if (size == mMinTextSize && layout.getWidth() > desiredWidth) {
                setEllipsize(TruncateAt.END);
            }
//            if (size == mMinTextSize) {
//                setEllipsize(TruncateAt.END);
//            }
        }
    }

    private StaticLayout getLayout(float size, String text, int maxWidth) {
        mPaint.setTextSize(size);
        return new StaticLayout(text, mPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    public boolean setCustomFont(Context ctx, String asset) {
        if (asset == null) {
            return true;
        }
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + asset);
        } catch (Exception e) {
            return false;
        }
        setTypeface(tf);
        return true;
    }

    public double getMinTextSize() {
        return mMinTextSize;
    }

    public void setMinTextSize(float minTextSize) {
        float minAcceptableValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, MIN_TEXT_SIZE_SP, getContext().getResources()
                .getDisplayMetrics());
        mMinTextSize = minTextSize < minAcceptableValue ? NO_SHRINKING_VALUE : minTextSize;
//        if (Float.isNaN(mMinTextSize)) {
//        }
    }

    public int getMinLines() {
        return mMinLines;
    }

    public int getMaxLines() {
        return mMaxLines;
    }

    public void setMinLines(int minLines) {
        mMinLines = minLines;
    }

    public void setMaxLines(int maxLines) {
        mMaxLines = maxLines;
    }

}
