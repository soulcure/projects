package com.ivmall.android.app.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ivmall.android.app.R;


public class MirrorItemView extends FrameLayout {
    private View mContentView;
    private boolean mHasReflection = true;
    private int REFHEIGHT;
    private Paint RefPaint = null;

    private Bitmap mReflectBitmap;
    private Canvas mReflectCanvas;

    private Drawable mDrawableWhite;
    private Drawable mDrawableShadow;
    private final static int PADDING = 2;


    public MirrorItemView(Context context) {
        super(context);

        REFHEIGHT = getResources().getDimensionPixelSize(R.dimen.mirror_ref_height);
        RefPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        RefPaint.setShader(new LinearGradient(0, 0, 0, REFHEIGHT,
                new int[]{0x77000000, 0x66AAAAAA, 0x0500000, 0x00000000},
                new float[]{0.0f, 0.1f, 0.9f, 1.0f}, Shader.TileMode.CLAMP));
        RefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));

        mDrawableWhite = getResources().getDrawable(R.drawable.item_highlight);
        mDrawableShadow = getResources().getDrawable(R.drawable.item_shadow);

        this.setClickable(true);
    }


    public void setContentView(View view, ViewGroup.LayoutParams lp) {
        mContentView = view;
        addView(view, lp);
    }

    public void setContentView(View view) {
        mContentView = view;
    }

    public View getContentView() {
        return mContentView;
    }

    @Override
    public boolean performClick() {
        return mContentView.performClick();
    }

    public void setHasReflection(boolean mHasReflection) {
        this.mHasReflection = mHasReflection;
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.dispatchDraw(canvas);
        if (isFocused()) {
            int width = mContentView.getWidth();
            int height = mContentView.getHeight();

            Rect padding = new Rect();
            mDrawableShadow.getPadding(padding);
            mDrawableShadow.setBounds(-padding.left, -padding.top, width + padding.right, height + padding.bottom);
            mDrawableShadow.draw(canvas);

            mDrawableWhite.getPadding(padding);
            mDrawableWhite.setBounds(-padding.left - PADDING, -padding.top - PADDING, width + padding.right + PADDING, height + padding.bottom + PADDING);
            mDrawableWhite.draw(canvas);
        }

        if (mHasReflection) {
            if (mReflectBitmap == null) {
                mReflectBitmap = Bitmap.createBitmap(mContentView.getWidth(), REFHEIGHT, Bitmap.Config.ARGB_8888);
                mReflectCanvas = new Canvas(mReflectBitmap);
            }

            drawReflection(mReflectCanvas, mContentView);

            canvas.save();
            int dy = mContentView.getBottom() + getResources().getDimensionPixelSize(R.dimen.ITEM_DIVIDE_SIZE);
            int dx = mContentView.getLeft();
            canvas.translate(dx, dy);
            canvas.drawBitmap(mReflectBitmap, 0, 0, null);
            canvas.restore();
        }
		requestLayout();
    }


    private void drawReflection(Canvas canvas, View view) {
        canvas.save();
        canvas.clipRect(0, 0, view.getWidth(), REFHEIGHT);
        canvas.save();
        canvas.scale(1, -1);
        canvas.translate(0, -view.getHeight());
        view.draw(canvas);
        canvas.restore();
        canvas.drawRect(0, 0, view.getWidth(), REFHEIGHT, RefPaint);
        canvas.restore();
    }


}
