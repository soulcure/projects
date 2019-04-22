package com.ivmall.android.app.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.smit.android.ivmall.stb.R;


/**
 * 此控件只支持2行
 */
public class MetroLayout extends FrameLayout implements View.OnFocusChangeListener {

    /**
     * 是否开启控件倒影
     */
    private static final boolean mMirror = true;

    public static final int Vertical = 0; //occupy two vertical cells
    public static final int Horizontal = 1; //occupy two horizontal cells
    public static final int Normal = 2; //square rectangle


    public static final int ROW_FIRST = 0; //第一行
    public static final int ROW_SECOND = 1; //第二行

    private Context mContext;
    private int[] rowOffset = new int[2];
    private static int DIVIDE_SIZE = 6;


    private int ITEM_V_WIDTH;
    private int ITEM_V_HEIGHT;
    private int ITEM_H_WIDTH;
    private int ITEM_H_HEIGHT;
    private int ITEM_NORMAL_SIZE;
    private static int mirror_ref_height;

    public MetroLayout(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MetroLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


    private void init() {

        DIVIDE_SIZE = getResources().getDimensionPixelSize(R.dimen.ITEM_DIVIDE_SIZE);
        ITEM_V_WIDTH = getResources().getDimensionPixelSize(R.dimen.ITEM_V_WIDTH);
        ITEM_V_HEIGHT = getResources().getDimensionPixelSize(R.dimen.ITEM_V_HEIGHT);
        ITEM_H_WIDTH = getResources().getDimensionPixelSize(R.dimen.ITEM_H_WIDTH);
        ITEM_H_HEIGHT = getResources().getDimensionPixelSize(R.dimen.ITEM_H_HEIGHT);
        ITEM_NORMAL_SIZE = getResources().getDimensionPixelSize(R.dimen.ITEM_NORMAL_SIZE);

        mirror_ref_height = getResources().getDimensionPixelSize(R.dimen.mirror_ref_height);
        setClipChildren(false);
        setClipToPadding(false);
    }


    public View addItemView(RecommendCardView child) {
        return addItemView(child, child.getViewType(), child.getRow(), DIVIDE_SIZE);
    }

    /**
     * 添加子控件
     *
     * @param child
     * @param celltype
     * @param row
     * @return
     */
    public View addItemView(View child, int celltype, int row) {
        return addItemView(child, celltype, row, DIVIDE_SIZE);
    }


    /**
     * 添加子控件实现
     *
     * @param child
     * @param celltype
     * @param row
     * @param padding
     * @return
     */
    private View addItemView(View child, int celltype, int row, int padding) {
        LayoutParams flp;
        View result = child;
        switch (celltype) {
            /**竖着摆放的子控件 直接占用两行*/
            case Vertical:
                flp = new LayoutParams(ITEM_V_WIDTH, ITEM_V_HEIGHT);

                if (mMirror) {
                    MirrorItemView mirror = new MirrorItemView(mContext);
                    mirror.setContentView(child, flp);
                    flp.bottomMargin = mirror_ref_height;
                    flp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    flp.leftMargin = rowOffset[0];
                    flp.topMargin = getPaddingTop();
                    flp.rightMargin = getPaddingRight();
                    addView(mirror, flp);
                    result = mirror;
                } else {
                    flp.leftMargin = rowOffset[0];
                    flp.topMargin = getPaddingTop();
                    flp.rightMargin = getPaddingRight();
                    addView(child, flp);

                }
                rowOffset[0] += ITEM_V_WIDTH + padding;
                rowOffset[1] = rowOffset[0];
                break;
            case Horizontal:
                flp = new LayoutParams(ITEM_H_WIDTH, ITEM_H_HEIGHT);
                switch (row) {
                    case ROW_FIRST:
                        flp.leftMargin = rowOffset[0];
                        flp.topMargin = getPaddingTop();
                        flp.rightMargin = getPaddingRight();
                        addView(child, flp);
                        rowOffset[0] += ITEM_H_WIDTH + padding;
                        break;
                    case ROW_SECOND:
                        if (mMirror) {
                            MirrorItemView mirror = new MirrorItemView(mContext);
                            mirror.setContentView(child, flp);
                            flp.bottomMargin = mirror_ref_height;
                            flp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                            flp.leftMargin = rowOffset[1];
                            flp.topMargin = getPaddingTop();
                            flp.rightMargin = getPaddingRight();
                            flp.topMargin += ITEM_NORMAL_SIZE + padding;
                            addView(mirror, flp);
                            result = mirror;
                        } else {
                            flp.leftMargin = rowOffset[1];
                            flp.topMargin = getPaddingTop();
                            flp.rightMargin = getPaddingRight();
                            flp.topMargin += ITEM_NORMAL_SIZE + padding;
                            addView(child, flp);
                        }
                        rowOffset[1] += ITEM_H_WIDTH + padding;
                        break;
                }
                break;
            case Normal:
                flp = new LayoutParams(ITEM_NORMAL_SIZE, ITEM_NORMAL_SIZE);
                switch (row) {
                    case ROW_FIRST:
                        flp.leftMargin = rowOffset[0];
                        flp.topMargin = getPaddingTop();
                        flp.rightMargin = getPaddingRight();
                        addView(child, flp);
                        rowOffset[0] += ITEM_NORMAL_SIZE + padding;
                        break;
                    case ROW_SECOND:
                        if (mMirror) {
                            MirrorItemView mirror = new MirrorItemView(mContext);
                            mirror.setContentView(child, flp);
                            flp.bottomMargin = mirror_ref_height;
                            flp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                            flp.leftMargin = rowOffset[1];
                            flp.topMargin = getPaddingTop();
                            flp.rightMargin = getPaddingRight();
                            flp.topMargin += ITEM_NORMAL_SIZE + padding;
                            addView(mirror, flp);
                            result = mirror;
                        } else {
                            flp.leftMargin = rowOffset[1];
                            flp.topMargin = getPaddingTop();
                            flp.rightMargin = getPaddingRight();
                            flp.topMargin += ITEM_NORMAL_SIZE + padding;
                            addView(child, flp);
                        }
                        rowOffset[1] += ITEM_NORMAL_SIZE + padding;
                        break;
                }
                break;
        }

        result.setFocusable(true);
        result.setOnFocusChangeListener(this);

        return result;
    }

    @Override
    public void onFocusChange(final View v, boolean hasFocus) {

        if (hasFocus) {
            bringChildToFront(v); //将该v置于其他所有子视图之上
            invalidate();
            ObjectAnimator animX = ObjectAnimator.ofFloat(v, "ScaleX",
                    new float[]{1.0F, 1.1F}).setDuration(200);
            ObjectAnimator animY = ObjectAnimator.ofFloat(v, "ScaleY",
                    new float[]{1.0F, 1.1F}).setDuration(200);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{animX, animY}); //设置两个动画一起执行
            animatorSet.start();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    invalidate();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        } else {

            ObjectAnimator animX = ObjectAnimator.ofFloat(v, "ScaleX",
                    new float[]{1.1F, 1.0F}).setDuration(200);
            ObjectAnimator animY = ObjectAnimator.ofFloat(v, "ScaleY",
                    new float[]{1.1F, 1.0F}).setDuration(200);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{animX, animY}); //设置两个动画一起执行
            animatorSet.start();

        }


    }


}
