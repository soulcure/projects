package com.taku.safe.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.taku.safe.R;
import com.taku.safe.ui.guide.GuideFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by colin on 2017/5/17.
 */
public class GuideActivity extends AppIntro {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(GuideFragment.newInstance(R.mipmap.img_guide_1, R.string.guide_title_1,
                R.string.guide_content_1));
        addSlide(GuideFragment.newInstance(R.mipmap.img_guide_2, R.string.guide_title_2,
                R.string.guide_content_2));
        addSlide(GuideFragment.newInstance(R.mipmap.img_guide_3, R.string.guide_title_3, R.string.guide_content_3));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(ContextCompat.getColor(this, R.color.color_blue));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        // setVibrate(true);
        // setVibrateIntensity(30);

        setFadeAnimation();

        setSkipText("跳过");
        setDoneText("完成");
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


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }


    @Override
    public void onBackPressed() {
        // do nothing
    }
}
