package com.taku.safe.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.taku.safe.R;

/**
 * Created by colin on 2017/7/5.
 */

public class ArcView extends View {


    private final Paint paintNormal;
    private final Paint paintUnusual;
    private final Paint paintNoSign;
    private final Paint paintNoRegedit;

    private float normalWidth;
    private float unusualWidth;
    private float noSignWidth;
    private float noRegeditWidth;

    private static final int strokeWidth = 20;

    private static final float dashWidth = 10f;

    public ArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintNormal = new Paint();
        paintNormal.setAntiAlias(true);
        paintNormal.setColor(ContextCompat.getColor(getContext(), R.color.color_normal));
        paintNormal.setStyle(Paint.Style.STROKE);
        paintNormal.setStrokeWidth(strokeWidth);

        paintUnusual = new Paint();
        paintUnusual.setAntiAlias(true);
        paintUnusual.setColor(ContextCompat.getColor(getContext(), R.color.color_unusual));
        paintUnusual.setStyle(Paint.Style.STROKE);
        paintUnusual.setStrokeWidth(strokeWidth);

        paintNoSign = new Paint();
        paintNoSign.setAntiAlias(true);
        paintNoSign.setColor(ContextCompat.getColor(getContext(), R.color.color_nosign));
        paintNoSign.setStyle(Paint.Style.STROKE);
        paintNoSign.setStrokeWidth(strokeWidth);

        paintNoRegedit = new Paint();
        paintNoRegedit.setAntiAlias(true);
        paintNoRegedit.setColor(ContextCompat.getColor(getContext(), R.color.color_noregedit));
        paintNoRegedit.setStyle(Paint.Style.STROKE);
        paintNoRegedit.setStrokeWidth(strokeWidth);
    }


    public void setNumber(int normal, int unusual, int noSign, int noRegedit) {
        float contentWidth = 360f - dashWidth * 4;
        float total = normal + unusual + noSign + noRegedit;

        unusualWidth = (unusual * contentWidth) / total;
        noSignWidth = (noSign * contentWidth) / total;
        noRegeditWidth = (noRegedit * contentWidth) / total;
        normalWidth = (normal * contentWidth) / total;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int left = strokeWidth;
        int top = strokeWidth;
        int right = getWidth() - strokeWidth;
        int bottom = getHeight() - strokeWidth;

        RectF rectangle = new RectF(left, top, right, bottom);

        canvas.drawArc(rectangle, dashWidth, unusualWidth, false, paintUnusual);
        canvas.drawArc(rectangle, dashWidth * 2 + unusualWidth, noSignWidth, false, paintNoSign);
        canvas.drawArc(rectangle, dashWidth * 3 + unusualWidth + noSignWidth, noRegeditWidth, false, paintNoRegedit);
        canvas.drawArc(rectangle, dashWidth * 4 + unusualWidth + noSignWidth + noRegeditWidth, normalWidth, false, paintNormal);

        super.onDraw(canvas);
    }

}