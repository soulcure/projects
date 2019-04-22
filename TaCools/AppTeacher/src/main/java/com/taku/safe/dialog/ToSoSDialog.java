package com.taku.safe.dialog;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.taku.safe.R;


/**
 * Created by Administrator on 2017/5/27.
 * 进入SOS对话框
 */

public class ToSoSDialog extends Dialog implements View.OnClickListener {


    private CallBack mCallBack;
    private AnimatorSet mAnimatorSet;

    private DonutProgress donutProgress;

    public interface CallBack {
        void onEntry();
    }

    public static class Builder {
        private Context context;

        private CallBack callBack;

        public ToSoSDialog builder() {
            return new ToSoSDialog(this);
        }

        public Builder(Context context) {
            this.context = context;
        }


        public Builder callBack(CallBack callBack) {
            this.callBack = callBack;
            return this;
        }
    }


    private ToSoSDialog(Builder builder) {
        super(builder.context, R.style.custom_dialog);
        setCanceledOnTouchOutside(true);
        mCallBack = builder.callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_to_sos);
        setDialogFeature();
        initView();
    }


    /**
     * 设置对话框特征
     */
    private void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.TOP;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(wlp);
        }
    }


    private void initView() {
        donutProgress = (DonutProgress) findViewById(R.id.donut_progress);

        findViewById(R.id.btn_cancel).setOnClickListener(this);

        mAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.progress_anim);
        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mCallBack != null) {
                    mCallBack.onEntry();
                }
                dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCallBack = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setTarget(donutProgress);
        mAnimatorSet.start();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_cancel:
                mAnimatorSet.cancel();
                cancel();
                break;
        }
    }
}
