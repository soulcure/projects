package com.taku.safe.sos;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.taku.safe.BasePermissionActivity;
import com.taku.safe.R;
import com.taku.safe.views.CircleProgressView;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by colin on 2017/6/8.
 */

public class StartSosActivity extends BasePermissionActivity implements View.OnClickListener {

    private AnimatorSet mAnimatorSet;

    private CircleProgressView donutProgress;

    private boolean isCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_start);
        initTitle();
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initTitle() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("");
//
//        TextView tv_title = (TextView) findViewById(R.id.tv_title);
//        tv_title.setText("SOS 紧急救援");
//
//        setSupportActionBar(toolbar);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        TextView tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setTextColor(ContextCompat.getColor(mContext, R.color.color_title));
        tv_title.setText("SOS 紧急救援");

        TextView tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setBackgroundResource(R.drawable.bg_blue_selector);
        tv_confirm.setText("试用");

        tv_confirm.setOnClickListener(this);
    }

    private void initView() {

        donutProgress = (CircleProgressView) findViewById(R.id.donut_progress);
        donutProgress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!mAnimatorSet.isRunning()) {
                            mAnimatorSet.start();
                        }
                        //some code....
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mAnimatorSet.isRunning()) {
                            //mAnimatorSet.end();
                            mAnimatorSet.cancel();
                        }

                        v.performClick();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });


        findViewById(R.id.btn_cancel).setOnClickListener(this);

        mAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.progress_anim);
        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isCancel = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isCancel) {
                    Intent intent = new Intent(mContext, SosInMapActivity.class);
                    intent.putExtra(SosInMapActivity.IS_TRY, false);
                    startActivity(intent);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isCancel = true;
                donutProgress.setProgress(0);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        mAnimatorSet.setTarget(donutProgress);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_confirm:
                Intent intent = new Intent(mContext, SosInMapActivity.class);
                intent.putExtra(SosInMapActivity.IS_TRY, true);
                startActivity(intent);
                break;
        }
    }


}
