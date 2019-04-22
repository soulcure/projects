package com.taku.safe.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;

/**
 * 作者：create by YW
 * 日期：2017.03.07 09:45
 * 描述：
 */
public class ViewAnimUtils {

    /**
     * 商家详情 动画
     *
     * @param view
     * @param delay
     */
    public static void scaleAnim(View view, int delay) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 1.2f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ);
        objectAnimator.setDuration(delay);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.start();
    }

    /**
     * 聊天界面  trans、alpha、scale
     *
     * @param context
     * @param view
     * @param delay
     */
    public static void multiAnim(Context context, View view, int delay) {
        AnimatorSet set = new AnimatorSet();

        /*ObjectAnimator transXAnim = ObjectAnimator.ofFloat(view, "translationX", 0f,
                DisplayUtil.dip2px(this, context.getResources().getDimension(R.dimen.hx_anim_item_x1)),
                DisplayUtil.dip2px(this, context.getResources().getDimension(R.dimen.hx_anim_item_x2)));
        ObjectAnimator transYAnim = ObjectAnimator.ofFloat(view, "translationY", 0f,
                DisplayUtil.dip2px(this, context.getResources().getDimension(R.dimen.hx_anim_item_y1)),
                DisplayUtil.dip2px(this, context.getResources().getDimension(R.dimen.hx_anim_item_y2)));

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.75f, 0.5f, 0.25f, 0.0f);
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.25f, 0.0f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.25f, 0.0f);*/

        ObjectAnimator transXAnim = ObjectAnimator.ofFloat(view, "translationX", 0f,
                DisplayUtil.dip2px(context, 140.0f), DisplayUtil.dip2px(context, 160.0f));
        ObjectAnimator transYAnim = ObjectAnimator.ofFloat(view, "translationY", 0f,
                DisplayUtil.dip2px(context, -220.0f), DisplayUtil.dip2px(context, -240.0f));
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.75f, 0.5f, 0.25f, 0.0f);
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.15f, 0.0f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.15f, 0.0f);

        set.playTogether(transXAnim, transYAnim, alphaAnim, scaleXAnim, scaleYAnim);
        set.setDuration(delay);
        set.start();
    }

    /**
     * 弹屏IM red point tip
     *
     * @param view
     * @param delay
     */
    public static void scaleRedTip(View view, int delay) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 1.5f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.5f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.5f, 1f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ);
        objectAnimator.setDuration(delay);
        objectAnimator.setRepeatCount(2);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.start();
    }

    /**
     * 弹屏IM red point tip
     *
     * @param view
     * @param delay
     */
    public static void alphaAndScaleAnim(View view, int delay) {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 0.5f, 1.0f);
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(view, "scaleX", 0.0f, 0.5f, 1.0f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(view, "scaleY", 0.0f, 0.5f, 1.0f);
        set.playTogether(alphaAnim, scaleXAnim, scaleYAnim);
        set.setDuration(delay);
        set.start();
    }

}
